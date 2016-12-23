package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class TeacherCertificatedCourse  extends Entity implements Serializable {

	private static final long serialVersionUID = 2955715884766555124L;
	private long teacherId;
	private long courseId;

	// 2015-12-16 添加course name
	private String courseName;
	
	public long getTeacherId() {
		return this.teacherId;
	}

	public TeacherCertificatedCourse setTeacherId(long teacherId) {
		this.teacherId = teacherId;
		return this;
	}

	public long getCourseId() {
		return this.courseId;
	}

	public TeacherCertificatedCourse setCourseId(long courseId) {
		this.courseId = courseId;
		return this;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

}

