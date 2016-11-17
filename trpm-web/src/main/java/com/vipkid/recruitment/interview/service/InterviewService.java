package com.vipkid.recruitment.interview.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.recruitment.dao.InterviewDao;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeacherApplicationLogDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeacherApplicationLog;
import com.vipkid.recruitment.interview.ConstantInterview;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.proxy.OnlineClassProxy;
import com.vipkid.trpm.proxy.OnlineClassProxy.ClassType;
import com.vipkid.trpm.util.DateUtils;

@Service
public class InterviewService {
    
    @Autowired
    private InterviewDao interviewDao;
    
    @Autowired
    private OnlineClassDao onlineClassDao;
    
    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    
    @Autowired
    private TeacherApplicationLogDao teacherApplicationLogDao;
    
    private static Logger logger = LoggerFactory.getLogger(InterviewService.class);
    
    /**
     * 可用INTERVIEW课程列表查询
     * TODO
     * 1.处于Interview阶段的待约课老师才能约课
     * @return    
     * List&lt;Map&lt;String,Object&gt;&gt;
     */
    public List<Map<String,Object>> findlistByInterview(){
        String fromTime = LocalDateTime.now().plusHours(1).format(DateUtils.FMT_YMD_HMS);
        String toTime = LocalDateTime.now().plusDays(2).withHour(23).withMinute(59).withSecond(59).format(DateUtils.FMT_YMD_HMS);
        logger.info("findlistByInterview parameter fromTime:{}, toTime:{}",fromTime, toTime);
        return interviewDao.findlistByInterview(fromTime, toTime);
    }
    
    /**
     * 用户interview进教室
     * TODO
     * 1.开课前1小时以后可以获取教室URL
     * 2.必须是处于Interview的待上课的老师可以获取URL
     * 3.课程合法性验证
     * @param onlineClassId
     * @param teacher
     * @return    
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> getClassRoomUrl(long onlineClassId,Teacher teacher){
       Map<String,Object> result = Maps.newHashMap();
       OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
       result = OnlineClassProxy.generateRoomEnterUrl(teacher.getId()+"", teacher.getRealName(),onlineClass.getClassroom(), OnlineClassProxy.RoomRole.TEACHER, onlineClass.getSupplierCode());
       return result;
    }
    
    /***
     * BOOK INTERVIEW 
     * 1.onlineClassId 必须是OPEN 课
     * 2.book的课程在开课前1小时之内不允许book
     * 3.约课老师必须是INTERVIEW的待约课老师
     * 4.cancel次数小于3次
     * @param onlineClassId
     * @param teacher
     * @return    
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> bookInterviewClass(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
        //onlineClassId 必须是OPEN 课
        if(onlineClass == null || OnlineClassEnum.Status.OPEN.toString().equalsIgnoreCase(onlineClass.getStatus())){
            return ResponseUtils.responseFail("This class("+onlineClassId+") is empty or anyone else has been booked !", this);
        }
        //book的课程在开课前1小时之内不允许book
        if(System.currentTimeMillis() + ConstantInterview.CANCEL_TIME > onlineClass.getScheduledDateTime().getTime()){
            return ResponseUtils.responseFail("Class is about to start is not allowed to book !", this);
        }
        //约课老师必须是INTERVIEW的待约课老师
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(listEntity != null && listEntity.size() > 0){
            TeacherApplication teacherApplication = listEntity.get(0);
            if(teacherApplication.getOnlineClassId() > 0 && StringUtils.isBlank(teacherApplication.getResult())){
                return ResponseUtils.responseFail("You have booked a class already. Please refresh your page !"+onlineClassId, this);
            }
        }
        //cancel次数必须小于3次
        TeacherApplicationLog bean = new TeacherApplicationLog();
        bean.setTeacherId(teacher.getId());
        bean.setStatus(TeacherApplicationDao.Status.INTERVIEW.toString());
        bean.setResult(TeacherApplicationDao.Result.REAPPLY.toString());
        List<TeacherApplicationLog> listLog = teacherApplicationLogDao.selectList(bean);
        if(CollectionUtils.isNotEmpty(listLog) && listLog.size() > ConstantInterview.CANCEL_NUM){
            return ResponseUtils.responseFail("You cancel the course number has finished can't book the class !", this);
        }
        //执行BOOK逻辑
        String dateTime = DateFormatUtils.format(onlineClass.getScheduledDateTime(),"yyyy-MM-dd HH:mm:ss");
        Map<String,Object> result = OnlineClassProxy.doBookRecruitment(teacher.getId(), onlineClass.getId(), ClassType.TEACHER_RECRUITMENT,dateTime);
        if(ResponseUtils.isFail(result)){
           throw new RuntimeException("The a book class result is fail!"+result.get("info"));
        }
        return result;
    }
    
    /***
     * CANCEL INTERVIEW 
     * TODO
     * 1.课程合法性验证
     * 2.开课前1小时不允许取消课程
     * 3.必须是处于Interview的带上课的老师可以取消课程
     * 4.记录cancel记录
     * @param onlineClassId
     * @param teacher
     * @return    
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> cancelInterviewClass(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
        //课程没有找到，无法取消
        if(onlineClass == null){
            return ResponseUtils.responseFail("The online class not exis:"+onlineClassId,this);
        }
        //离上课时间不到1小时,不能取消课程
        if(System.currentTimeMillis() > onlineClass.getScheduledDateTime().getTime() - 60*60*1000){
            return ResponseUtils.responseFail("The online class will begin,can't rescheduled"+onlineClassId,this);
        }
        //课程必须是当前步骤中的数据
        List<TeacherApplication> listEntity = this.teacherApplicationDao.findCurrentApplication(teacher.getId());       
        if(listEntity != null && listEntity.size() > 0){
            TeacherApplication teacherApplication = listEntity.get(0);
            if(onlineClassId != teacherApplication.getOnlineClassId()){     
                return ResponseUtils.responseFail("You have already cancelled this class. Please refresh your page.",this);
            }
        }
        return OnlineClassProxy.doCancelRecruitement(teacher.getId(), onlineClass.getId(), ClassType.TEACHER_RECRUITMENT);
    }
    
}
