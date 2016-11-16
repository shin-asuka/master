package com.vipkid.recruitment.practicum.service;

import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.recruitment.dao.PracticumDao;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.proxy.OnlineClassProxy;
import com.vipkid.trpm.proxy.OnlineClassProxy.ClassType;
import com.vipkid.trpm.util.DateUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PracticumService {
    
    private static Logger logger = LoggerFactory.getLogger(PracticumService.class);
    @Autowired
    private PracticumDao practicumDao;
    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    @Autowired
    private OnlineClassDao onlineClassDao;

    public List<Map<String,Object>> findTimeList(){
        String fromTime = LocalDateTime.now().plusHours(1).format(DateUtils.FMT_YMD_HMS);
        String toTime = LocalDateTime.now().plusDays(2).withHour(23).withMinute(59).withSecond(59).format(DateUtils.FMT_YMD_HMS);
        logger.info("findTimeListByPracticum parameter fromTime:{}, toTime:{}",fromTime, toTime);
        return practicumDao.findTimeList(fromTime, toTime);
    }


    public Map<String,Object> getClassRoomUrl(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
        Map<String,Object> result = OnlineClassProxy.generateRoomEnterUrl(teacher.getId()+"", teacher.getRealName(),onlineClass.getClassroom(), OnlineClassProxy.RoomRole.TEACHER, onlineClass.getSupplierCode());
        return result;
    }


    public Map<String,Object> bookClass(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
        //课程没有找到，无法取消
        if(onlineClass == null || OnlineClassEnum.Status.REMOVED.toString().equals(onlineClass.getStatus())){
            return ResponseUtils.responseFail("The online class doesn't exist: "+onlineClassId+". Please refresh your page.",this);
        }
        //上课时间
        long sTime = onlineClass.getScheduledDateTime().getTime();
        long cTime = System.currentTimeMillis();
        long count = sTime - cTime;
        if(count < 3600000){
            return ResponseUtils.responseFail("The online class is expired (1h). Please refresh your page.",this);
        }
        //teacherApplication判断，是否已经booked
        List<TeacherApplication> teacherApplications = this.teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(teacherApplications != null && teacherApplications.size() > 0){
            TeacherApplication teacherApplication = teacherApplications.get(0);
            if(teacherApplication.getOnlineClassId() > 0 && StringUtils.isEmpty(teacherApplication.getResult())){
                return ResponseUtils.responseFail("You have booked a class already. Please refresh your page.",this);
            }
        }

        String dateTime = DateFormatUtils.format(onlineClass.getScheduledDateTime(),"yyyy-MM-dd HH:mm:ss");
        return OnlineClassProxy.doBookRecruitment(teacher.getId(), onlineClass.getId(), ClassType.TEACHER_RECRUITMENT,dateTime);
    }

    public Map<String,Object> cancelClass(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
        //课程没有找到，无法取消
        if(onlineClass == null){
            return ResponseUtils.responseFail("The online class doesn't exist:"+onlineClassId,this);
        }
        //离上课时间不到1小时,不能取消课程
        if(System.currentTimeMillis() > onlineClass.getScheduledDateTime().getTime() - 60*60*1000){
            return ResponseUtils.responseFail("The online class will begin, can't rescheduled"+onlineClassId,this);
        }
        //课程必须是当前步骤中的数据
        List<TeacherApplication> teacherApplications = this.teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(teacherApplications != null && teacherApplications.size() > 0){
            TeacherApplication teacherApplication = teacherApplications.get(0);
            if(onlineClassId != teacherApplication.getOnlineClassId()){
                return ResponseUtils.responseFail("You have already cancelled this class. Please refresh your page.",this);
            }
        }
        return OnlineClassProxy.doCancelRecruitement(teacher.getId(), onlineClass.getId(), ClassType.TEACHER_RECRUITMENT);
    }
}
