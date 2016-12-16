package com.vipkid.recruitment.practicum.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherEnum.Type;
import com.vipkid.enums.TeacherLockLogEnum.Reason;
import com.vipkid.enums.TeacherQuizEnum.Version;
import com.vipkid.enums.UserEnum;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.dao.PracticumDao;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeacherApplicationLogDao;
import com.vipkid.recruitment.dao.TeacherLockLogDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeacherLockLog;
import com.vipkid.recruitment.practicum.PracticumConstant;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherQuizDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherQuiz;
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
    private UserDao userDao;
    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    @Autowired
    private TeacherApplicationLogDao teacherApplicationLogDao;
    @Autowired
    private TeacherLockLogDao teacherLockLogDao;
    @Autowired
    private RecruitmentService recruitmentService;
    @Autowired
    private TeacherQuizDao teacherQuizDao;

    private static Logger logger = LoggerFactory.getLogger(PracticumService.class);

    public List<Map<String,Object>> findTimeList(Teacher teacher){
        LocalDateTime now = LocalDateTime.now();
        String fromTime24 = now.plusHours(1).format(DateUtils.FMT_YMD_HMS);
        String toTime24 = now.plusDays(1).format(DateUtils.FMT_YMD_HMS);
        String fromTime = now.plusDays(1).plusSeconds(1).format(DateUtils.FMT_YMD_HMS);
        String toTime = now.plusDays(3).withHour(23).withMinute(59).withSecond(59).format(DateUtils.FMT_YMD_HMS);
        logger.info("findTimeListByPracticum parameter fromTime:{}, toTime:{}",fromTime24, toTime);

        //1. 查24小时内
        List<Map<String,Object>> list24 = practicumDao.findTimeList(fromTime24, toTime24);
        List<String> ids24 = new ArrayList<>();
        Map<String,Object> idsTimes24 = new HashedMap();
        list24.forEach(x -> {
            String id = x.get("id").toString();
            ids24.add(id);
            idsTimes24.put(id, x.get("scheduledDateTime"));
        });
        //2. 查24小时可用
        List<String> ids24Available = OnlineClassProxy.get24HourClass(teacher.getId(), ids24);
        //3. 查24小时以外
        List<Map<String,Object>> list = practicumDao.findTimeList(fromTime, toTime);
        ids24Available.forEach(x -> {
            Map<String,Object> map = new HashedMap();
            map.put("id", Integer.valueOf(x));
            map.put("scheduledDateTime", idsTimes24.get(x));
            list.add(map);
        });

        if(CollectionUtils.isNotEmpty(list)){
            Collections.shuffle(list);
        }
        return list;
    }


    public Map<String,Object> getClassRoomUrl(long onlineClassId,Teacher teacher){
        
        if(teacher == null || teacher.getId() == 0 || StringUtils.isBlank(teacher.getRealName())){
            return ReturnMapUtils.returnFail("This account does not exist.");
        }
        
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);

        //课程没有找到，无法book
        if(onlineClass == null){
            return ReturnMapUtils.returnFail("The online class doesn't exist: "+onlineClassId);
        }
        
        String logpix = "onlineclassId:"+onlineClassId+";teacherId:"+teacher.getId();
        
        //判断教室是否创建好
        if(StringUtils.isBlank(onlineClass.getClassroom())){
            return ReturnMapUtils.returnFail("The classroom without creating",logpix);
        }
        //课程必须是当前步骤中的数据
        List<TeacherApplication> listEntity = this.teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isEmpty(listEntity)){
            return ReturnMapUtils.returnFail("You cannot enter this classroom!",logpix);
        }
        //进教室权限判断
        if(listEntity.get(0).getOnlineClassId() != onlineClassId){
            return ReturnMapUtils.returnFail("You cannot enter this classroom!",logpix);
        }

        Map<String,Object> result = OnlineClassProxy.generateRoomEnterUrl(teacher.getId()+"", teacher.getRealName(),onlineClass.getClassroom(), OnlineClassProxy.RoomRole.TEACHER, onlineClass.getSupplierCode(),onlineClassId,OnlineClassProxy.ClassType.PRACTICUM);
        return result;
    }

    public Map<String,Object> bookClass(long onlineClassId,Teacher teacher){
       
        if(teacher == null || teacher.getId() == 0 || StringUtils.isBlank(teacher.getRealName())){
            return ReturnMapUtils.returnFail("This account does not exist.");
        }
        
        String logpix = "onlineclassId:"+onlineClassId+";teacherId:"+teacher.getId();
        
        if(recruitmentService.teacherIsApplicationFinished(teacher)){
            return ReturnMapUtils.returnFail("Your recruitment process is over already, Please refresh your page !","PRACTICUM:"+teacher.getId());
        }
                
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
        
        //课程没有找到，无法book
        if(onlineClass == null){
            return ReturnMapUtils.returnFail("This online class does not exist.",logpix);
        }


        //onlineClassId 必须是AVAILABLE课
        if(!OnlineClassEnum.ClassStatus.AVAILABLE.toString().equalsIgnoreCase(onlineClass.getStatus())){
            return ReturnMapUtils.returnFail("Oops, someone else just booked this time slot. Please select another.",logpix);
        }
        //book的课程在开课前1小时之内不允许book
        if(System.currentTimeMillis() + PracticumConstant.BOOK_TIME > onlineClass.getScheduledDateTime().getTime()){
            return ReturnMapUtils.returnFail("Oops, someone else just booked this time slot. Please select another.",logpix);
        }
        //约课老师必须是Practicum的待约课老师
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isNotEmpty(listEntity)){
            TeacherApplication teacherApplication = listEntity.get(0);
            //存在步骤，但步骤中已经存在待审核的课程 不允许继续book
            if(teacherApplication.getOnlineClassId() != 0 && StringUtils.isBlank(teacherApplication.getResult())){
                return ReturnMapUtils.returnFail("You have booked a class already. Please refresh your page !",logpix);
            }
        }
        //判断剩余可取消次数
        if(recruitmentService.getRemainRescheduleTimes(teacher, Status.PRACTICUM.toString(), Result.CANCEL.toString(), false) <= 0){
            userDao.doLock(teacher.getId());
            teacherLockLogDao.save(new TeacherLockLog(teacher.getId(), Reason.RESCHEDULE.toString(), LifeCycle.PRACTICUM.toString()));
            return ReturnMapUtils.returnFail("There are no more cancellations allowed for your account. Contact us at teachvip@vipkid.com.cn for more information.",logpix);
        }
        //执行BOOK逻辑
        String dateTime = DateFormatUtils.format(onlineClass.getScheduledDateTime(),"yyyy-MM-dd HH:mm:ss");
        Map<String,Object> result = OnlineClassProxy.doBookRecruitment(teacher.getId(), onlineClass.getId(), ClassType.PRACTICUM,dateTime);
        if(ReturnMapUtils.isFail(result)){
            //一旦失败，抛出异常回滚
            throw new RuntimeException("Booking failed! Please try again." + result.get("info"));
        }
        if(ReturnMapUtils.isSuccess(result)){
            logger.info("teacher:{} book Practicum success send email",teacher.getId());
            List<TeacherApplication> list = teacherApplicationDao.findApplictionForStatusResult(teacher.getId(), null, Result.PRACTICUM2.name());
            if(CollectionUtils.isNotEmpty(list)){
                EmailUtils.sendEmail4Practicum2Book(teacher,onlineClass);
            }else{
                EmailUtils.sendEmail4PracticumBook(teacher,onlineClass);
            }
        }

        return result;
    }

    public Map<String,Object> cancelClass(long onlineClassId,Teacher teacher){
        
        if(teacher == null || teacher.getId() == 0 || StringUtils.isBlank(teacher.getRealName())){
            return ReturnMapUtils.returnFail("This account does not exist.");
        }

        String logpix = "onlineclassId:"+onlineClassId+";teacherId:"+teacher.getId();
        
        //课程没有找到，无法取消
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
        if(onlineClass == null){
            return ReturnMapUtils.returnFail("This online class does not exist.",logpix);
        }

        //class already start, can't cancel error
        if(System.currentTimeMillis() > onlineClass.getScheduledDateTime().getTime()){
            return ReturnMapUtils.returnFail("Sorry, you can't cancel after the start time has passed.",logpix);
        }

        List<TeacherApplication> listEntity = this.teacherApplicationDao.findCurrentApplication(teacher.getId());
        //如果步骤中无数据则不允许cancel
        if(CollectionUtils.isEmpty(listEntity)){
            return ReturnMapUtils.returnFail("You do not have permission to cancel this course",logpix);
        }else{
            TeacherApplication teacherApplication = listEntity.get(0);
            //如果步骤中有数据并且数据不是本次cancel的课程 则不允许cancel
            if(teacherApplication.getOnlineClassId() != onlineClass.getId()){
                return ReturnMapUtils.returnFail("You have already cancelled this class. Please refresh your page !",logpix);
            }else{
                //如果步骤中有数据并且数据不是本次cancel的课程 但管理端已经审核，不允许cancel
                if(StringUtils.isNotBlank(teacherApplication.getResult())){
                    return ReturnMapUtils.returnFail("This class already audited. Please refresh your page !",logpix);
                }
            }
        }

        //保存cancel记录
        this.teacherApplicationLogDao.saveCancel(teacher.getId(), listEntity.get(0).getId(), Status.PRACTICUM, Result.CANCEL, onlineClass);

        if (!UserEnum.Status.isLocked(userDao.findById(teacher.getId()).getStatus())) {
            int count = recruitmentService.getRemainRescheduleTimes(teacher, Status.PRACTICUM.toString(), Result.CANCEL.toString(), true);
            if (count <= 0) {
                userDao.doLock(teacher.getId());
                teacherLockLogDao.save(new TeacherLockLog(teacher.getId(), Reason.RESCHEDULE.toString(), LifeCycle.PRACTICUM.toString()));
            }
        }

        //执行Cancel逻辑
        Map<String,Object> result = OnlineClassProxy.doCancelRecruitement(teacher.getId(), onlineClass.getId(), ClassType.PRACTICUM);
        if(ReturnMapUtils.isFail(result)){
            //一旦失败，抛出异常回滚
            throw new RuntimeException(""+result.get("info"));
        }
        return result;
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
            return ReturnMapUtils.returnFail("You don't have permission to enter into next phase!","teacherId;"+teacher.getId());
        }
        TeacherApplication teacherApplication = listEntity.get(0);
        //执行逻辑 只有在Practicum的PASS状态才能进入
        if(Status.PRACTICUM.toString().equals(teacherApplication.getStatus())
                && Result.PASS.toString().equals(teacherApplication.getResult())){
            //按照新流程 该步骤将老师的LifeCycle改变为Practicum -to-Contract
            List<TeacherApplication> list = teacherApplicationDao.findApplictionForStatusResult(teacher.getId(),Status.SIGN_CONTRACT.toString(), Result.PASS.toString());

            if(CollectionUtils.isNotEmpty(list)){
                // 1.教师状态更新
                teacher.setLifeCycle(LifeCycle.REGULAR.toString());
                // 2.新增教师入职时间
                teacher.setEntryDate(new Date());
                // 3.新增教师类型
                teacher.setType(Type.PART_TIME.toString());


                // 4.如果是PASS操作，则ta状态修改为FINISH，教师状态修改为REGULAR
                teacherApplication.setStatus(Status.FINISHED.toString());
                teacherApplicationDao.update(teacherApplication);
                // 5.增加quiz的考试记录
                List<TeacherQuiz> quizslist = teacherQuizDao.findAllQuiz(teacher.getId());
                if (CollectionUtils.isEmpty(quizslist)) {
                    teacherQuizDao.insertQuiz(teacher.getId(), teacher.getId(),Version.ADMIN_QUIZ);
                }
            } else {
                teacher.setLifeCycle(LifeCycle.CONTRACT_INFO.toString());
            }

            this.teacherDao.insertLifeCycleLog(teacher.getId(), LifeCycle.PRACTICUM, LifeCycle.valueOf(teacher.getLifeCycle()), teacher.getId());
            this.teacherDao.update(teacher);

            Map<String,Object> resultMap = Maps.newHashMap();
            resultMap.put("lifeCycle",teacher.getLifeCycle());
            return ReturnMapUtils.returnSuccess(resultMap);
        }
        return ReturnMapUtils.returnFail("You don't have permission to enter into next phase!","teacherId;"+teacher.getId());
    }

}
