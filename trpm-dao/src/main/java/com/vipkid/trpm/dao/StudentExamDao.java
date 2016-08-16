package com.vipkid.trpm.dao;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.StudentExam;

@Repository
public class StudentExamDao extends MapperDaoTemplate<StudentExam> {

    @Autowired
    public StudentExamDao(SqlSessionTemplate sqlSessionTemplate){
        super(sqlSessionTemplate,StudentExam.class);
    }

	/**
	 * 通过studentId查询StudentExam<br/>
	 * @Author:ALong
	 * @Title: findStudentExamByStudentId 
	 * @param studentId
	 * @return StudentExam
	 * @date 2015年12月18日
	 */
    public StudentExam findStudentExamByStudentId(long studentId){
		StudentExam studentExam = new StudentExam().setStudentId(studentId);
		studentExam.setOrderString("id DESC");
		studentExam.setStatus(1);
		try {
			return super.selectLimit(studentExam, 0, 1).get(0);
		} catch (Exception e) {
			return null;
		}
    }
    
}
