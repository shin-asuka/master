package com.vipkid.trpm.dao;

import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;

@Repository
public class TeacherAddressDao extends MapperDaoTemplate<TeacherAddress> {

    @Autowired
    public TeacherAddressDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherAddress.class);
    }

    public TeacherAddress getTeacherAddress(int id) {
        if (0 == id) {
            return null;
        }
        return super.selectOne(new TeacherAddress().setId(id));
    }

    public TeacherAddress findById(int id) {
        return super.selectOne(new TeacherAddress().setId(id));
    }

    public TeacherAddress updateOrSaveCurrentAddressId(Teacher teacher, int countryId, int stateId, int cityId, String streetAddress, String zipCode) {
        TeacherAddress teacherAddress = null;
        if (teacher.getCurrentAddressId() > 0) {
            teacherAddress = this.findById(teacher.getCurrentAddressId());
        }
        teacherAddress = teacherAddress == null ? new TeacherAddress() : teacherAddress;
        teacherAddress.setTeacherId(teacher.getId());
        teacherAddress.setCountryId(countryId);
        teacherAddress.setStateId(stateId);
        teacherAddress.setCity(cityId);
        if (StringUtils.isNotBlank(streetAddress)) {
            teacherAddress.setStreetAddress(streetAddress);
        }
        if (StringUtils.isNotBlank(zipCode)) {
            teacherAddress.setZipCode(zipCode);
        }
        this.updateOrSave(teacherAddress);
        teacher.setCurrentAddressId(teacherAddress.getId());
        return teacherAddress;
    }

    public int updateOrSave(TeacherAddress teacherAddress) {
        if (teacherAddress.getId() == null || teacherAddress.getId() == 0) {
            return super.save(teacherAddress);
        }
        java.util.Date now = new java.util.Date();
        Timestamp ts = new Timestamp(now.getTime());
        teacherAddress.setUpdateTime(ts);
        return super.update(teacherAddress);
    }

}
