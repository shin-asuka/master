package com.vipkid.trpm.dao;

import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.TeacherModule;

@Repository
public class TeacherModuleDao extends MapperDaoTemplate<TeacherModule> {

	@Autowired
	public TeacherModuleDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, TeacherModule.class);
	}

	public List<TeacherModule> findByTeacherPe(long teacherId){
	    if(teacherId == 0){
	        return null;
	    }
	    Map<String,Object> paramsMap = Maps.newHashMap();
	    paramsMap.put("teacherId", teacherId);
	    paramsMap.put("moduleName","PE");
	    return super.selectList(new TeacherModule(), paramsMap);
	}
	
	   public List<TeacherModule> findByTeacherModule(long teacherId){
	        if(teacherId == 0){
	            return null;
	        }
	        Map<String,Object> paramsMap = Maps.newHashMap();
	        paramsMap.put("teacherId", teacherId);
	        return super.selectList(new TeacherModule(), paramsMap);
	    }
}
