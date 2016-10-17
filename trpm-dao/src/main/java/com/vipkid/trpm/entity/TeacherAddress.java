package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class TeacherAddress extends Entity implements Serializable {

	private static final long serialVersionUID = 8200096962489484236L;
	/*  */
	private int id;
	/* teacher id */
	private int teacherId;
	/* 国家id */
	private int countryId;
	/* state id */
	private int stateId;
	/* city id */
	private int city;
	/* 地址 */
	private String streetAddress;
	/* 邮编 */
	private String zipCode;
	/*  */
	private java.sql.Timestamp createTime;
	/*  */
	private java.sql.Timestamp updateTime;

	public int getId() {
		return this.id;
	}

	public TeacherAddress setId(int id) {
		this.id = id;
		return this;
	}

	public int getTeacherId() {
		return this.teacherId;
	}

	public TeacherAddress setTeacherId(int teacherId) {
		this.teacherId = teacherId;
		return this;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public int getStateId() {
		return this.stateId;
	}

	public TeacherAddress setStateId(int stateId) {
		this.stateId = stateId;
		return this;
	}

	public int getCity() {
		return this.city;
	}

	public TeacherAddress setCity(int city) {
		this.city = city;
		return this;
	}

	public String getStreetAddress() {
		return this.streetAddress;
	}

	public TeacherAddress setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
		return this;
	}

	public String getZipCode() {
		return this.zipCode;
	}

	public TeacherAddress setZipCode(String zipCode) {
		this.zipCode = zipCode;
		return this;
	}

	public java.sql.Timestamp getCreateTime() {
		return this.createTime;
	}

	public TeacherAddress setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
		return this;
	}

	public java.sql.Timestamp getUpdateTime() {
		return this.updateTime;
	}

	public TeacherAddress setUpdateTime(java.sql.Timestamp updateTime) {
		this.updateTime = updateTime;
		return this;
	}

}