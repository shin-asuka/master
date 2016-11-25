package com.vipkid.recruitment.dao;

import com.vipkid.recruitment.entity.TeacherLockLog;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TeacherLockLogDao extends MapperDaoTemplate<TeacherLockLog>{

    @Autowired
    public TeacherLockLogDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherLockLog.class);
    }

    @Override
    public int save(TeacherLockLog teacherLockLog) {
        return super.save(teacherLockLog);
    }

    public int count(TeacherLockLog teacherLockLog){
        return super.selectCount(teacherLockLog);
    }
}
