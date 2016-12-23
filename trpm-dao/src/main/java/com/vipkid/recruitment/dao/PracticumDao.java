package com.vipkid.recruitment.dao;

import com.google.common.collect.Maps;
import com.vipkid.recruitment.entity.Practicum;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class PracticumDao extends MapperDaoTemplate<Practicum> {

    @Autowired
    public PracticumDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, Practicum.class);
    }  
  
    public List<Map<String,Object>> findTimeList(String fromTime,String toTime){
        Map<String,Object> paramsMap = Maps.newHashMap();
        paramsMap.put("fromTime", fromTime);
        paramsMap.put("toTime", toTime);
        return listEntity("findTimeListByPracticum", paramsMap);
    }
}
