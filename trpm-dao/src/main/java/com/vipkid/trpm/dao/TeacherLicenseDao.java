package com.vipkid.trpm.dao;

import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.trpm.entity.TeacherLicense;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class TeacherLicenseDao extends MapperDaoTemplate<TeacherLicense> {

    @Autowired
    public TeacherLicenseDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherLicense.class);
    }

    public TeacherLicense getOne(Integer id) {
        TeacherLicense teacherLicense = new TeacherLicense();
        teacherLicense.setId(id);
        return super.selectOne(teacherLicense);
    }

    public int save(TeacherLicense license) {
        return super.save(license);
    }

}
