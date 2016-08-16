package com.vipkid.trpm.dao;

import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.TeacherCertificatedCourse;

@Repository
public class TeacherCertificatedCourseDao extends MapperDaoTemplate<TeacherCertificatedCourse> {

	@Autowired
	public TeacherCertificatedCourseDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, TeacherCertificatedCourse.class);
	}

	public int save(TeacherCertificatedCourse teacherCertificatedCourse) {
		return super.save(teacherCertificatedCourse);
	}

	public List<Map<String, Object>> findCertificatedCourseNameByTeacherId(long teacherId) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("teacherId", teacherId);

		List<Map<String, Object>> listCourseNames = listEntity("findByTeacherIdDao", paramsMap);

		return listCourseNames;
	}

	public TeacherCertificatedCourse findCertificatedCourseByTeacherIdAndCourseId(long teacherId,
			long courseId) {
		TeacherCertificatedCourse certificatedCourse = null;

		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("courseId", courseId);

		certificatedCourse = selectOne(paramsMap);
		return certificatedCourse;
	}

}
