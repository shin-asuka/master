package com.vipkid.trpm.service.rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.trpm.dao.TeacherQuizDao;

@Service
public class EvaluationService {

    
    @Autowired
    private TeacherQuizDao teacherQuizDao;
    
    /**
     * 获取所有Tag 
     * @Author:ALong (ZengWeiLong)
     * @return    
     * Map<String,Object>
     * @date 2016年9月19日
     */
    public Map<String,Object> findTags(){
        Map<String,Object> resultMap = Maps.newHashMap();
        List<Map<String,Object>> list = this.teacherQuizDao.findTags();
        if(CollectionUtils.isNotEmpty(list)){
            List<Map<String,Object>> listGroup = list.stream().parallel().filter(map -> map.get("type").equals(1)).collect(Collectors.toList());
            List<Map<String,Object>> listTags = list.stream().parallel().filter(map -> map.get("type").equals(2)).collect(Collectors.toList());
            resultMap.put("listGroup", listGroup);
            resultMap.put("listTags", listTags);
        }
        return resultMap;
    }
}
