package com.vipkid.trpm.dao;

import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.trpm.entity.TeacherLicense;
import org.apache.commons.collections.CollectionUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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

    public int insert(TeacherLicense license) {
        return super.save(license);
    }

    public int updateByTeacherId(TeacherLicense license){
        license.setUpdateTime(new Timestamp(new Date().getTime()));
        return super.update(license, "updateByTeacherId");
    }

    public TeacherLicense findByTeacherId(Long teacherId){
        TeacherLicense license = new TeacherLicense();
        license.setTeacherId(teacherId);

        List<TeacherLicense> list = super.selectList(license, "findByTeacherId");
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

}
