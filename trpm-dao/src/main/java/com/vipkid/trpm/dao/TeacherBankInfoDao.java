package com.vipkid.trpm.dao;

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

}
