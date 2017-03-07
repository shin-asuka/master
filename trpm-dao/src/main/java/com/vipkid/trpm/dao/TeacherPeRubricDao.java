package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.TeacherPeRubric;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeacherPeRubricDao extends MapperDaoTemplate<TeacherPeRubric> {

    @Autowired
    public TeacherPeRubricDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherPeRubric.class);
    }

    public List<TeacherPeRubric> listTeacherPeRubric(Integer templateId){
        TeacherPeRubric teacherPeRubric = new TeacherPeRubric();
        teacherPeRubric.setTemplateId(templateId);
        return super.selectList(teacherPeRubric);
    }

}
