package com.vipkid.trpm.dao;

import com.google.common.base.Preconditions;
import com.vipkid.trpm.entity.TeacherPeTags;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liuguowen on 2017/1/16.
 */
@Repository
public class TeacherPeTagsDao extends MapperDaoTemplate<TeacherPeTags> {

    @Autowired
    public TeacherPeTagsDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherPeTags.class);
    }

    public int deleteTeacherPeTags(int applicationId) {
        Preconditions.checkArgument(0 != applicationId);
        return super.delete(new TeacherPeTags().setApplicationId(applicationId), "deleteByApplicationId");
    }

    public void saveTeacherPeTags(List<TeacherPeTags> teacherPeTags) {
        super.saveBatch(teacherPeTags);
    }

    public List<TeacherPeTags> getTeacherPeTagsByApplicationId(int applicationId){
        Preconditions.checkArgument(0 != applicationId);
        return super.selectList(new TeacherPeTags().setApplicationId(applicationId));
    }

}
