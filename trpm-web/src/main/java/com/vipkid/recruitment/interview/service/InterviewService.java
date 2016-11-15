package com.vipkid.recruitment.interview.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.recruitment.dao.InterviewDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.proxy.ClassroomProxy;
import com.vipkid.trpm.util.DateUtils;

@Service
public class InterviewService {
    
    @Autowired
    private InterviewDao interviewDao;
    
    @Autowired
    private OnlineClassDao onlineClassDao;
    
    private static Logger logger = LoggerFactory.getLogger(InterviewService.class);
    
    public List<Map<String,Object>> findlistByInterview(){
        String fromTime = LocalDateTime.now().plusHours(1).format(DateUtils.FMT_YMD_HMS);
        String toTime = LocalDateTime.now().plusDays(2).withHour(23).withMinute(59).withSecond(59).format(DateUtils.FMT_YMD_HMS);
        logger.info("findlistByInterview parameter fromTime:{}, toTime:{}",fromTime, toTime);
        return interviewDao.findlistByInterview(fromTime, toTime);
    }
    
    public Map<String,Object> getClassRoomUrl(long onlineClassId,Teacher teacher){
       Map<String,Object> result = Maps.newHashMap();
       OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
       String url = ClassroomProxy.generateRoomEnterUrl(teacher.getId()+"", teacher.getRealName(),onlineClass.getClassroom(), ClassroomProxy.RoomRole.TEACHER, onlineClass.getSupplierCode());
       if(StringUtils.isBlank(url)){
           result.put("status", false);
       }else{
           result.put("url", url);
           result.put("status", true);
       }
       return result;
    }
    
}
