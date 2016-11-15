package com.vipkid.recruitment.interview.service;

import java.util.List;
import java.util.Map;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.recruitment.dao.InterviewDao;

@Service
public class InterviewService {
    
    @Autowired
    private InterviewDao interviewDao;
    
    private static Logger logger = LoggerFactory.getLogger(InterviewService.class);
    
    public List<Map<String,Object>> findlistByInterview(){
        String fromTime = LocalDateTime.now().plusHours(1).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        String toTime = LocalDateTime.now().plusDays(2).withTime(0, 0, 0, 0).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("findlistByInterview parameter fromTime:{}, toTime:{}",fromTime, toTime);
        return interviewDao.findlistByInterview(fromTime, toTime);
    }
    
}
