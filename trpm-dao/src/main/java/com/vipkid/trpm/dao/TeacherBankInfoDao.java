package com.vipkid.trpm.dao;

import com.google.common.base.Preconditions;
import com.vipkid.trpm.entity.TeacherBankInfo;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TeacherBankInfoDao extends MapperDaoTemplate<TeacherBankInfo> {

    @Autowired
    public TeacherBankInfoDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherBankInfo.class);
    }

    public TeacherBankInfo getTeacherBankInfo(long teacherId) {
        Preconditions.checkArgument(0 != teacherId);
        TeacherBankInfo teacherBankInfo = new TeacherBankInfo();
        teacherBankInfo.setTeacherId(teacherId);
        return selectOne(teacherBankInfo);
    }

    public void updateTeacherBankInfo(long teacherId, TeacherBankInfo teacherBankInfo) {
        Preconditions.checkArgument(0 != teacherId);
        Preconditions.checkNotNull(teacherBankInfo);

        TeacherBankInfo oldBankInfo = new TeacherBankInfo();
        oldBankInfo.setTeacherId(teacherId);
        oldBankInfo = getTeacherBankInfo(teacherId);

        if (null != oldBankInfo) {
            teacherBankInfo.setId(oldBankInfo.getId());
            update(teacherBankInfo);
        } else {
            teacherBankInfo.setTeacherId(teacherId);
            save(teacherBankInfo);
        }
    }

}
