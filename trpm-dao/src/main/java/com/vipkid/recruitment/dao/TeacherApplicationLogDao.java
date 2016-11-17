package com.vipkid.recruitment.dao;

import java.util.List;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.recruitment.entity.TeacherApplicationLog;

@Repository
public class TeacherApplicationLogDao extends MapperDaoTemplate<TeacherApplicationLog>{

    @Autowired
    public TeacherApplicationLogDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherApplicationLog.class);
    }
 
    public List<TeacherApplicationLog> selectList(TeacherApplicationLog bean){
        return super.selectList(bean);
    }
}
