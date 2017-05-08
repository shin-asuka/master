package com.vipkid.trpm.dao;

import com.google.common.base.Preconditions;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.TeacherPeFeedback;

@Repository
public class TeacherPeFeedbackDao extends MapperDaoTemplate<TeacherPeFeedback> {

    @Autowired
    public TeacherPeFeedbackDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherPeFeedback.class);
    }

    public int saveTeacherPeFeedback(TeacherPeFeedback teacherPeFeedback) {
        return super.save(teacherPeFeedback);
    }

    public boolean hasTeacherPeFeedback(Integer applicationId) {
        Preconditions.checkArgument(null != applicationId);
        TeacherPeFeedback teacherPeFeedback = new TeacherPeFeedback();
        teacherPeFeedback.setApplicationId(applicationId);
        return 0 != super.selectCount(teacherPeFeedback);
    }

}
