package com.vipkid.trpm.dao;

import java.sql.Timestamp;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.enums.BasicInfoBean;
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

    public void updateOrSaveCurrentAddressId(Teacher teacher,BasicInfoBean bean){
        TeacherAddress teacherAddress = new TeacherAddress();
        if(teacher.getCurrentAddressId() > 0){
            teacherAddress = this.findById(teacher.getCurrentAddressId());
        }
        teacherAddress.setCountryId(bean.getCountryId());
        teacherAddress.setStateId(bean.getStateId());
        teacherAddress.setCity(bean.getCityId());
        teacherAddress.setStreetAddress(bean.getStreetAddress());
        teacherAddress.setZipCode(bean.getZipCode());
        this.updateOrSave(teacherAddress);
        teacher.setCurrentAddressId(teacherAddress.getId());
    }
    
    public int updateOrSave(TeacherAddress teacherAddress) {
        if (0 == teacherAddress.getId()) {
            return super.save(teacherAddress);
        }
        java.util.Date now = new java.util.Date();
        Timestamp ts = new Timestamp(now.getTime());
        teacherAddress.setUpdateTime(ts);
        return super.update(teacherAddress);
    }

}
