package com.vipkid.recruitment.dao;

import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.recruitment.entity.Interview;

@Repository
public class InterviewDao extends MapperDaoTemplate<Interview> {
    
    @Autowired
    public InterviewDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, Interview.class);
    }  
  
    public List<Map<String,Object>> findlistByInterview(String fromTime,String toTime){
        Map<String,Object> paramsMap = Maps.newHashMap();
        paramsMap.put("fromTime", fromTime);
        paramsMap.put("toTime", toTime);
        return listEntity("listByInterview", paramsMap);
    }

    public List<Map<String,Object>> findlistByBookedCount(String scheduleTime, String fromTime,String toTime){
        Map<String,Object> paramsMap = Maps.newHashMap();
        paramsMap.put("scheduleTime", scheduleTime);
        paramsMap.put("fromTime", fromTime);
        paramsMap.put("toTime", toTime);
        return listEntity("listByInterviewBookedCount", paramsMap);
    }
}
