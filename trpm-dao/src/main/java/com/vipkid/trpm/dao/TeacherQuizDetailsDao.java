package com.vipkid.trpm.dao;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.TeacherQuizDetails;

@Repository
public class TeacherQuizDetailsDao extends MapperDaoTemplate<TeacherQuizDetails>{

    @Autowired
    public TeacherQuizDetailsDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherQuizDetails.class);
    }
    
    public int save(TeacherQuizDetails t){
        return super.save(t);
    }
}
