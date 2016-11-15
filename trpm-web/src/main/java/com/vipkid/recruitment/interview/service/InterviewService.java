package com.vipkid.recruitment.interview.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.recruitment.dao.InterviewDao;
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
     * TODO
     * 1.onlineClassId 必须是AVAILABLE 课
     * 2.约课老师必须是INTERVIEW的待约课老师
     * 3.book的课程在开课前1小时之内不允许book
     * 4.cancel次数小于3次
     * @param onlineClassId
     * @param teacher
     * @return    
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> bookInterviewClass(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
        String dateTime = DateFormatUtils.format(onlineClass.getScheduledDateTime(),"yyyy-MM-dd HH:mm:ss");
        return OnlineClassProxy.doBookRecruitment(teacher.getId(), onlineClass.getId(), ClassType.TEACHER_RECRUITMENT,dateTime);
    }
    
    /***
     * CANCEL INTERVIEW 
     * TODO
     * 1.onlineClassId 必须是AVAILABLE 课
     * 2.约课老师必须是INTERVIEW的待约课老师
     * 3.book的课程在开课前1小时之内不允许book
     * 4.cancel次数小于3次
     * @param onlineClassId
     * @param teacher
     * @return    
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> cancelInterviewClass(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
        //TODO
        return OnlineClassProxy.doCancelRecruitement(teacher.getId(), onlineClass.getId(), ClassType.TEACHER_RECRUITMENT);
    }
    
}
