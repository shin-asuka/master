package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class User extends Entity implements Serializable {

	private static final long serialVersionUID = -1061654536497032817L;
	/*  */
	private long id;
	/*  */
	private String dtype;
	/*  */
	private java.sql.Timestamp createDateTime;
	/*  */
	private String gender;
	/*  */
	private java.sql.Timestamp lastEditDateTime;
	/*  */
	private java.sql.Timestamp lastLoginDateTime;
	/*  */
	private String name;
	/*  */
	private String password;
	/*  */
	private java.sql.Timestamp registerDateTime;
	/*  */
	private String roles;
	/*  */
	private String status;
	/*  */
	private String token;
	/*  */
	private String username;
	/*  */
	private long createrId;
	/*  */
	private long lastEditorId;
	/*  */
	private String initPassword;
	/*  */
	private String accountType;
	/* 是否第一次登录 */
	private String isfirstlogin;

	public long getId() {
		return this.id;
	}

	public User setId(long id) {
		this.id = id;
		return this;
	}

	public String getDtype() {
		return this.dtype;
	}

	public User setDtype(String dtype) {
		this.dtype = dtype;
		return this;
	}

	public java.sql.Timestamp getCreateDateTime() {
		return this.createDateTime;
	}

	public User setCreateDateTime(java.sql.Timestamp createDateTime) {
		this.createDateTime = createDateTime;
		return this;
	}

	public String getGender() {
		return this.gender;
	}

	public User setGender(String gender) {
		this.gender = gender;
		return this;
	}

	public java.sql.Timestamp getLastEditDateTime() {
		return this.lastEditDateTime;
	}

	public User setLastEditDateTime(java.sql.Timestamp lastEditDateTime) {
		this.lastEditDateTime = lastEditDateTime;
		return this;
	}

	public java.sql.Timestamp getLastLoginDateTime() {
		return this.lastLoginDateTime;
	}

	public User setLastLoginDateTime(java.sql.Timestamp lastLoginDateTime) {
		this.lastLoginDateTime = lastLoginDateTime;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	public String getPassword() {
		return this.password;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	public java.sql.Timestamp getRegisterDateTime() {
		return this.registerDateTime;
	}

	public User setRegisterDateTime(java.sql.Timestamp registerDateTime) {
		this.registerDateTime = registerDateTime;
		return this;
	}

	public String getRoles() {
		return this.roles;
	}

	public User setRoles(String roles) {
		this.roles = roles;
		return this;
	}

	public String getStatus() {
		return this.status;
	}

	public User setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getToken() {
		return this.token;
	}

	public User setToken(String token) {
		this.token = token;
		return this;
	}

	public String getUsername() {
		return this.username;
	}

	public User setUsername(String username) {
		this.username = username;
		return this;
	}

	public long getCreaterId() {
		return this.createrId;
	}

	public User setCreaterId(long createrId) {
		this.createrId = createrId;
		return this;
	}

	public long getLastEditorId() {
		return this.lastEditorId;
	}

	public User setLastEditorId(long lastEditorId) {
		this.lastEditorId = lastEditorId;
		return this;
	}

	public String getInitPassword() {
		return this.initPassword;
	}

	public User setInitPassword(String initPassword) {
		this.initPassword = initPassword;
		return this;
	}

	public String getAccountType() {
		return this.accountType;
	}

	public User setAccountType(String accountType) {
		this.accountType = accountType;
		return this;
	}

	public String getIsfirstlogin() {
		return this.isfirstlogin;
	}

	public User setIsfirstlogin(String isfirstlogin) {
		this.isfirstlogin = isfirstlogin;
		return this;
	}

}