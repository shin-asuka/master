package com.vipkid.trpm.dao;

import java.util.List;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;
import com.vipkid.trpm.entity.TeacherLocation;

@Repository
public class TeacherLocationDao extends MapperDaoTemplate<TeacherLocation> {

	@Autowired
	public TeacherLocationDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, TeacherLocation.class);
	}

	public List<TeacherLocation> findByParentId(int parentId, int level) {
		Preconditions.checkArgument(-1 != parentId);

		TeacherLocation teacherLocation = new TeacherLocation().setParentId(parentId);
		teacherLocation.setLevel(level);
		teacherLocation.setOrderString("name ASC");
		return super.selectList(teacherLocation);
	}

	public TeacherLocation findById(int id) {
		Preconditions.checkArgument(0 != id);
		return super.selectOne(new TeacherLocation().setId(id));
	}

	public List<TeacherLocation> getCountrys() {
		return super.selectList(new TeacherLocation().setParentId(0));
	}

}
