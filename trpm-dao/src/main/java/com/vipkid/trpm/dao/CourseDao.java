package com.vipkid.trpm.dao;

import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.Course;

@Repository
public class CourseDao extends MapperDaoTemplate<Course> {

	@Autowired
	public CourseDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, Course.class);
	}

	/**
	 * 通过TeacherId查询老师可以教的课程列表
	 * 
	 * @param teacherId
	 * @return List<Course>
	 */
	public List<Course> findByTeacherId(long teacherId) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("teacherId", teacherId);

		return selectList("findByTeacherId", paramsMap);
	}

	/**
	 * 通过LessonId查询课程
	 * 
	 * @param lessonId
	 * @return Course
	 */
	public Course findByLessonId(long lessonId) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("lessonId", lessonId);

		return selectOne("findByLessonId", paramsMap);
	}

	/**
	 * 通过LessonId查询课程
	 *
	 * @param id
	 * @return Course
	 */
	public Course findById(long id) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("id", id);

		return selectOne("findById",paramsMap);
	}

	/**
	 * 通过LessonId查询ids
	 *
	 * @param lessonId
	 * @return Course
	 */
	public Course findIdsByLessonId(long lessonId) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("lessonId", lessonId);

		return selectOne("findIdsByLessonId", paramsMap);
	}

	/**
	 * 根据type找course
	 * @param type
	 * @return
	 */
	public List<Course> findByType(String type){
		return selectList(new Course().setType(type));		
	}

}
