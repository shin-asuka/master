package com.vipkid.trpm.dao;

import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.Lesson;

@Repository
public class LessonDao extends MapperDaoTemplate<Lesson> {

	@Autowired
	public LessonDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, Lesson.class);
	}

	/**
	 * 通过课程类型查询Lesson
	 * 
	 * @param courseType
	 * @return Map<String,Object>
	 */
	public List<Lesson> findByCourseType(String courseType) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("courseType", courseType);

		return selectList("findByCourseType", paramsMap);
	}

	/**
	 * 通过id查lesson
	 * 
	 * @param lessonId
	 * @return
	 */
	public Lesson findById(long lessonId) {
		Lesson lesson = new Lesson();
		lesson.setId(lessonId);

		return selectOne(lesson);
	}

	/**
	 * 通过课程id查询Lesson
	 * 
	 * @param courseId
	 * @return
	 */
	public List<Lesson> findByCourseId(long courseId) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("courseId", courseId);

		return selectList("findByCourseId", paramsMap);
	}

}
