package com.vipkid.trpm.dao;

import com.google.common.base.Preconditions;
import com.vipkid.trpm.entity.TeacherPeComments;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by liuguowen on 2017/1/16.
 */
@Repository
public class TeacherPeCommentsDao extends MapperDaoTemplate<TeacherPeComments> {

    @Autowired
    public TeacherPeCommentsDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherPeComments.class);
    }

    public int deleteTeacherPeComments(int applicationId) {
        Preconditions.checkArgument(0 != applicationId);
        TeacherPeComments teacherPeComments = new TeacherPeComments();
        teacherPeComments.setApplicationId(applicationId);
        return super.delete(teacherPeComments, "deleteByApplicationId");
    }

    public void saveTeacherPeComments(TeacherPeComments teacherPeComments) {
        super.save(teacherPeComments);
    }

    public TeacherPeComments getTeacherPeComments(int applicationId) {
        Preconditions.checkArgument(0 != applicationId);
        TeacherPeComments teacherPeComments = new TeacherPeComments();
        teacherPeComments.setApplicationId(applicationId);
        return super.selectOne(teacherPeComments);
    }

    public void updateTeacherPeComments(TeacherPeComments teacherPeComments) {
        super.update(teacherPeComments, "updateByApplicationId");
    }

}
