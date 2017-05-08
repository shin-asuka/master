package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.TeacherPeTemplate;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TeacherPeTemplateDao extends MapperDaoTemplate<TeacherPeTemplate> {

    @Autowired
    public TeacherPeTemplateDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherPeTemplate.class);
    }

    public TeacherPeTemplate getCurrentPeTemplate() {
        TeacherPeTemplate teacherPeTemplate = new TeacherPeTemplate();
        teacherPeTemplate.setCurrent(1);
        return super.selectOne(teacherPeTemplate);
    }

    public TeacherPeTemplate getPeTemplate(Integer id) {
        TeacherPeTemplate teacherPeTemplate = new TeacherPeTemplate();
        teacherPeTemplate.setId(id);
        return super.selectOne(teacherPeTemplate);
    }

}
