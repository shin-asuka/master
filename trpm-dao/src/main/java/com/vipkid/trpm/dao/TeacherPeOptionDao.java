package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.TeacherPeOption;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeacherPeOptionDao extends MapperDaoTemplate<TeacherPeOption> {

    @Autowired
    public TeacherPeOptionDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherPeOption.class);
    }

    public List<TeacherPeOption> listTeacherPeOption(Integer criteriaId) {
        TeacherPeOption teacherPeOption = new TeacherPeOption();
        teacherPeOption.setCriteriaId(criteriaId);
        return super.selectList(teacherPeOption);
    }

}
