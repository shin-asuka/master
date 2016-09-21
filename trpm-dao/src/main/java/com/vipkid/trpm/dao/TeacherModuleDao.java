package com.vipkid.trpm.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
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

    public List<TeacherModule> findByTeacherModuleName(long teacherId,String moduleName) {
        if (teacherId == 0) {
            return null;
        }
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", teacherId);
        paramsMap.put("moduleName", moduleName);
        return super.selectList(new TeacherModule(), paramsMap);
    }

    public String findByTeacherModule(long teacherId) {
        if (teacherId == 0) {
            return null;
        }
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", teacherId);
        List<TeacherModule> modulelist = super.selectList(new TeacherModule(), paramsMap);
        logger.info("select list:" + modulelist);
        String result = ",";
        if(CollectionUtils.isNotEmpty(modulelist)){
            result = modulelist.stream().parallel().map(bean -> (String)bean.getModuleName()).collect(Collectors.joining(",")) + ",";
        }        
        return result;
    }
}
