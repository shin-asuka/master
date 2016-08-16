package com.vipkid.trpm.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.Student;

@Repository
public class StudentDao extends MapperDaoTemplate<Student> {

	@Autowired
	public StudentDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, Student.class);
	}

	public Student findById(long studentId) {
		return selectOne(new Student().setId(studentId));
	}

	/**
	 * 通过onlineClassId 查询教室所有学生
	 * 
	 * @param onlineClassId
	 * @return
	 */
	public List<Student> findStudentByOnlineClassId(Long onlineClassId) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("onlineClassId", onlineClassId);

		return listEntity("findStudentByOnlineClassId", paramsMap);
	}

	public List<Map<String, Object>> findOrderListByStudentIdAndPaidDateTime(Long studentId, String paidDateTime) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("studentId", studentId);
		paramsMap.put("paidDateTime", paidDateTime);

		return listEntity("findOrderListByStudentIdAndPaidDateTime", paramsMap);
	}

	public List<Map<String, Object>> findWechatBystudentId(long studentId) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("studentId", studentId);
		return listEntity("findWechatBystudentId", paramsMap);
	}

	public List<Map<String, Object>> findOldOrderListByStudentIdAndPaidDateTime(Long studentId, String paidDateTime) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("studentId", studentId);
		paramsMap.put("paidDateTime", paidDateTime);

		return listEntity("findOldOrderListByStudentIdAndPaidDateTime", paramsMap);
	}

	public List<Map<String, Object>> findStudentsBy(String[] ids) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("ids", ids);
		return listEntity("findStudentsByIds", paramsMap);
	}

}
