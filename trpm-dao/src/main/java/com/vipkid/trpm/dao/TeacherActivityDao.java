package com.vipkid.trpm.dao;

import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.TeacherActivity;

@Repository
public class TeacherActivityDao extends MapperDaoTemplate<TeacherActivity> {
    
    @Autowired
    public TeacherActivityDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherActivity.class);
    }
    
    /**
     * 查询老师在一年内上了多少次课
     * @Author:ALong (ZengWeiLong)
     * @param id
     * @param year
     * @return    
     * List<Map<String,Object>>
     * @date 2016年3月18日
     */
    public int countClassByTeacherId(long id,String yearmd){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", id);
        paramsMap.put("yearmd", yearmd);
        return selectCount("countClassByTeacherId", paramsMap);
    }
       /**
     * 查询老师在一年内教了多少学生
     * @Author:ALong (ZengWeiLong)
     * @param id
     * @param year
     * @return    
     * List<Map<String,Object>>
     * @date 2016年3月18日
     */
    public List<Map<String, Object>> countStudentByTeacherId(long id,String yearmd){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", id);
        paramsMap.put("yearmd", yearmd);        
        return listEntity("countStudentByTeacherId", paramsMap);
    }
    
    /**
     * 查询上的课程最多的学生 
     * @Author:ALong (ZengWeiLong)
     * @param id
     * @param year  @return    
     * List<Map<String,Object>>
     * @date 2016年3月18日
     */
    public List<Map<String, Object>> countStudentByMax(long id,String yearmd){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", id);
        paramsMap.put("yearmd", yearmd);        
        return listEntity("countStudentByMax", paramsMap);
    }

}
