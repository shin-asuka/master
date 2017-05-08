package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.TeacherPeCriteria;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeacherPeCriteriaDao extends MapperDaoTemplate<TeacherPeCriteria> {

    @Autowired
    public TeacherPeCriteriaDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherPeCriteria.class);
    }

    public List<TeacherPeCriteria> listTeacherPeCriteria(Integer sectionId) {
        TeacherPeCriteria teacherPeCriteria = new TeacherPeCriteria();
        teacherPeCriteria.setSectionId(sectionId);
        return super.selectList(teacherPeCriteria);
    }

}
