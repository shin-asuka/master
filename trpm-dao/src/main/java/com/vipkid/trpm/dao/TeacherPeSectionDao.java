package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.TeacherPeSection;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeacherPeSectionDao extends MapperDaoTemplate<TeacherPeSection> {

    @Autowired
    public TeacherPeSectionDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherPeSection.class);
    }

    public List<TeacherPeSection> listTeacherPeSection(Integer rubricId) {
        TeacherPeSection teacherPeSection = new TeacherPeSection();
        teacherPeSection.setRubricId(rubricId);
        return super.selectList(teacherPeSection);
    }

}
