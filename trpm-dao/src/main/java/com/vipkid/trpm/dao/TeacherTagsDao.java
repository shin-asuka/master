package com.vipkid.trpm.dao;

import com.google.common.base.Preconditions;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.TeacherTags;

import java.util.List;

/**
 * Created by liuguowen on 2017/3/21.
 */
@Repository
public class TeacherTagsDao extends MapperDaoTemplate<TeacherTags> {

    @Autowired
    public TeacherTagsDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherTags.class);
    }

    public List<TeacherTags> listTeacherTagsByTeacherId(Integer teacherId) {
        Preconditions.checkNotNull(teacherId);
        TeacherTags teacherTags = new TeacherTags();
        teacherTags.setTeacherId(teacherId);
        return super.selectList(teacherTags);
    }

    public void saveTeacherTags(List<TeacherTags> teacherTagsList) {
        super.saveBatch(teacherTagsList);
    }

}
