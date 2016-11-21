package com.vipkid.trpm.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.TeacherNationalityCode;

@Repository
public class TeacherNationalityCodeDao extends MapperDaoTemplate<TeacherNationalityCode> {

	@Autowired
	public TeacherNationalityCodeDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, TeacherNationalityCode.class);
	}
	
	public List<TeacherNationalityCode> getTeacherNationalityCodes() {
        return super.selectList(new TeacherNationalityCode());
    }

	public TeacherNationalityCode getTeacherNationalityCode(int id, String code) {
		if (StringUtils.isEmpty(code) || 0 == id) {
			return null;
		}
		return super.selectOne(new TeacherNationalityCode().setId(id).setCode(code));
	}

}
