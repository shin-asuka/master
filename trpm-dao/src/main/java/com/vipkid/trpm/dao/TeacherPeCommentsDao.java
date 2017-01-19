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
        return super.delete(new TeacherPeComments().setApplicationId(applicationId), "deleteByApplicationId");
    }

    public void saveTeacherPeComments(TeacherPeComments teacherPeComments) {
        super.save(teacherPeComments);
    }

    public TeacherPeComments getTeacherPeComments(int applicationId){
        Preconditions.checkArgument(0 != applicationId);
        return super.selectOne(new TeacherPeComments().setApplicationId(applicationId));
    }

}
