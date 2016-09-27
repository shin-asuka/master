package com.vipkid.trpm.dao;

import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.Staff;
import com.vipkid.trpm.entity.User;

@Repository
public class StaffDao extends MapperDaoTemplate<Staff> {

	@Autowired
	public StaffDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, Staff.class);
	}
	public Staff findById(Long id) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("id", id);
		return selectOne("findById", paramsMap);
	}

}
