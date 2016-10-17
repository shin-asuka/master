package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class TeacherModule extends Entity implements Serializable {

	private static final long serialVersionUID = -8299090898831130710L;
	/*  */
	private int id;
	/*  */
	private int teacherId;
	/*  */
	private String moduleName;

	public int getId() {
		return this.id;
	}

	public TeacherModule setId(int id) {
		this.id = id;
		return this;
	}

	public int getTeacherId() {
		return this.teacherId;
	}

	public TeacherModule setTeacherId(int teacherId) {
		this.teacherId = teacherId;
		return this;
	}

	public String getModuleName() {
		return this.moduleName;
	}

	public TeacherModule setModuleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}

}