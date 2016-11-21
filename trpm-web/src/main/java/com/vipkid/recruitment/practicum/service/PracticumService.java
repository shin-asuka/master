package com.vipkid.recruitment.practicum.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.recruitment.dao.PracticumDao;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeacherApplicationLogDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.practicum.PracticumConstant;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.proxy.OnlineClassProxy;
import com.vipkid.trpm.proxy.OnlineClassProxy.ClassType;
import com.vipkid.trpm.util.DateUtils;

@Service
public class PracticumService {
    @Autowired
    private PracticumDao practicumDao;
    @Autowired
    private OnlineClassDao onlineClassDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    @Autowired
    private TeacherApplicationLogDao teacherApplicationLogDao;

    private static Logger logger = LoggerFactory.getLogger(PracticumService.class);

    public List<Map<String,Object>> findTimeList(){
        String fromTime = LocalDateTime.now().plusHours(1).format(DateUtils.FMT_YMD_HMS);
        String toTime = LocalDateTime.now().plusDays(3).withHour(23).withMinute(59).withSecond(59).format(DateUtils.FMT_YMD_HMS);
        logger.info("findTimeListByPracticum parameter fromTime:{}, toTime:{}",fromTime, toTime);
        List<Map<String,Object>> list = practicumDao.findTimeList(fromTime, toTime);
        if(CollectionUtils.isNotEmpty(list)){
            Collections.shuffle(list);
        }
        return list;
    }


    public Map<String,Object> getClassRoomUrl(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);

        //课程没有找到，无法book
        if(onlineClass == null){
            return ResponseUtils.responseFail("The online class doesn't exist: "+onlineClassId,this);
        }
        //判断教室是否创建好
        if(StringUtils.isBlank(onlineClass.getClassroom())){
            return ResponseUtils.responseFail("The classroom is empty: "+onlineClassId,this);
        }
        //课程必须是当前步骤中的数据
        List<TeacherApplication> listEntity = this.teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isEmpty(listEntity)){
            return ResponseUtils.responseFail("You can't enter this classroom!",this);
        }
        //进教室权限判断
        if(listEntity.get(0).getOnlineClassId() != onlineClassId){
            return ResponseUtils.responseFail("You can't enter this classroom!",this);
        }

        Map<String,Object> result = OnlineClassProxy.generateRoomEnterUrl(teacher.getId()+"", teacher.getRealName(),onlineClass.getClassroom(), OnlineClassProxy.RoomRole.TEACHER, onlineClass.getSupplierCode());
        return result;
    }

    public Map<String,Object> bookClass(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);

        //课程没有找到，无法book
        if(onlineClass == null){
            return ResponseUtils.responseFail("The online class doesn't exist: "+onlineClassId,this);
        }

        //onlineClassId 必须是AVAILABLE课
        if(OnlineClassEnum.ClassStatus.AVAILABLE.toString().equalsIgnoreCase(onlineClass.getStatus())){
            return ResponseUtils.responseFail("This class ("+onlineClassId+") is empty or has been booked by anyone else!", this);
        }
        //book的课程在开课前1小时之内不允许book
        if(System.currentTimeMillis() + PracticumConstant.CANCEL_TIME > onlineClass.getScheduledDateTime().getTime()){
            return ResponseUtils.responseFail("Class is too close to start and not allowed to book!", this);
        }
        //约课老师必须是Practicum的待约课老师
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isNotEmpty(listEntity)){
            TeacherApplication teacherApplication = listEntity.get(0);
            //存在步骤，但步骤中已经存在待审核的课程 不允许继续book
            if(teacherApplication.getOnlineClassId() != 0 && StringUtils.isNotBlank(teacherApplication.getResult())){
                return ResponseUtils.responseFail("You have booked a class already. Please refresh your page! "+onlineClassId, this);
            }
        }
        //cancel次数最多3次，如果已经cancel 4次了说明这个老师已经Fail了不允许book
        if(getCancelNum(teacher) > PracticumConstant.CANCEL_NUM){
            return ResponseUtils.responseFail("You have canceled classes too much and can't book class again!", this);
        }
        //执行BOOK逻辑
        String dateTime = DateFormatUtils.format(onlineClass.getScheduledDateTime(),"yyyy-MM-dd HH:mm:ss");
        Map<String,Object> result = OnlineClassProxy.doBookRecruitment(teacher.getId(), onlineClass.getId(), ClassType.TEACHER_RECRUITMENT,dateTime);
        if(ResponseUtils.isFail(result)){
            //一旦失败，抛出异常回滚
            throw new RuntimeException("Class booking is fail!" + result.get("info"));
        }
        return result;
    }

    @Transactional
    public Map<String,Object> cancelClass(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);

        //课程没有找到，无法取消
        if(onlineClass == null){
            return ResponseUtils.responseFail("The online class doesn't exist: "+onlineClassId,this);
        }

        //book的课程在开课前1小时之内不允许cancel
        if(System.currentTimeMillis() + PracticumConstant.CANCEL_TIME > onlineClass.getScheduledDateTime().getTime()){
            return ResponseUtils.responseFail("The online class will begin, can't be rescheduled: "+onlineClassId,this);
        }

        List<TeacherApplication> listEntity = this.teacherApplicationDao.findCurrentApplication(teacher.getId());
        //如果步骤中无数据则不允许cancel
        if(CollectionUtils.isEmpty(listEntity)){
            return ResponseUtils.responseFail("You don't have permission to cancel this course: "+onlineClassId,this);
        }else{
            TeacherApplication teacherApplication = listEntity.get(0);
            //如果步骤中有数据并且数据不是本次cancel的课程 则不允许cancel
            if(teacherApplication.getOnlineClassId() != onlineClass.getId()){
                return ResponseUtils.responseFail("You have already cancelled this class. Please refresh your page!",this);
            }else{
                //如果步骤中有数据并且数据不是本次cancel的课程 但管理端已经审核，不允许cancel
                if(StringUtils.isNotBlank(teacherApplication.getResult())){
                    return ResponseUtils.responseFail("This class is already audited. Please refresh your page!",this);
                }
            }
        }

        //Cancel次数已经CANCEL2次了 则直接将老师Fail
        int count = getCancelNum(teacher);

        //如果cancel 次数已经等于3次或者4次等...当然不可能
        if( count > PracticumConstant.CANCEL_NUM){
            return ResponseUtils.responseFail("You have canceled too many times!",this);
        }

        //如果cancel次数已经等于2次将无法cancel
        if(count == PracticumConstant.CANCEL_NUM){
            TeacherApplication teacherApplication = listEntity.get(0);
            teacherApplication.setResult(TeacherApplicationDao.Result.FAIL.toString());
            teacherApplication.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
            teacherApplication.setAuditorId(RestfulConfig.SYSTEM_USER_ID);
            teacherApplication.setFailedReason("Cancel too many times!");
        }

        //保存cancel记录
        this.teacherApplicationLogDao.saveCancel(teacher.getId(), listEntity.get(0).getId(), TeacherApplicationDao.Status.PRACTICUM,
                TeacherApplicationDao.Result.CANCEL, onlineClass);

        //执行Cancel逻辑
        Map<String,Object> result = OnlineClassProxy.doCancelRecruitement(teacher.getId(), onlineClass.getId(), ClassType.TEACHER_RECRUITMENT);
        if(ResponseUtils.isFail(result)){
            //一旦失败，抛出异常回滚
            throw new RuntimeException("Class canceling is fail! "+result.get("info"));
        }
        result.put("count", PracticumConstant.CANCEL_NUM - (count+1));
        return result;
    }

    /**
     * 获取老师对Practicum 课程的Cancel 次数
     * @param teacher
     * @return int
     */
    public int getCancelNum(Teacher teacher){
        return this.teacherApplicationLogDao.getCancelNum(teacher.getId(),TeacherApplicationDao.Status.PRACTICUM,
                TeacherApplicationDao.Result.CANCEL);
    }

    /**
     * 进入下一步骤
     * @param teacher
     * @return
     * Map<String,Object>
     */
    public Map<String,Object> toContract(Teacher teacher){
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isEmpty(listEntity)){
            return ResponseUtils.responseFail("You don't have permission to enter into next phase!",this);
        }

        //执行逻辑 只有在Practicum的PASS状态才能进入
        if(TeacherApplicationDao.Status.PRACTICUM.toString().equals(listEntity.get(0).getStatus())
                && TeacherApplicationDao.Result.PASS.toString().equals(listEntity.get(0).getResult())){
            //按照新流程 该步骤将老师的LifeCycle改变为Practicum -to-Contract
            teacher.setLifeCycle(LifeCycle.CONTRACT.toString());
            this.teacherDao.insertLifeCycleLog(teacher.getId(),LifeCycle.PRACTICUM,LifeCycle.CONTRACT, teacher.getId());
            this.teacherDao.update(teacher);
            return ResponseUtils.responseSuccess();
        }
        return ResponseUtils.responseFail("You don't have permission to enter into next phase!",this);
    }

}
