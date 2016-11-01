package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class TeacherPageLogin extends Entity implements Serializable {

	private static final long serialVersionUID = -465663132875049821L;
	/*  */
	private long id;
	/*  */
	private long userId;
	/* 0：classrooms，1：schedule 2:adminQuiz 3:evaluation*/
	private int loginType = -1;

	public long getId() {
		return this.id;
	}

	public TeacherPageLogin setId(long id) {
		this.id = id;
		return this;
	}

	public long getUserId() {
		return this.userId;
	}

	public TeacherPageLogin setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public int getLoginType() {
		return this.loginType;
	}

	public TeacherPageLogin setLoginType(int loginType) {
		this.loginType = loginType;
		return this;
	}

}