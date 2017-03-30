package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class TeacherAddress extends Entity implements Serializable {

	private static final long serialVersionUID = 8200096962489484236L;
	/*  */
	private Integer id;
	/* teacher id */
	private Long teacherId;
	/* 国家id */
	private Integer countryId;
	/* state id */
	private Integer stateId;
	/* city id */
	private Integer city;
	/* 地址 */
	private String streetAddress;
	/* 邮编 */
	private String zipCode;
	/*  */
	private java.sql.Timestamp createTime;
	/*  */
	private java.sql.Timestamp updateTime;

	private Integer type;

	public Integer getId() {
		return this.id;
	}

	public TeacherAddress setId(Integer id) {
		this.id = id;
		return this;
	}

	public Long getTeacherId() {
		return this.teacherId;
	}

	public TeacherAddress setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
		return this;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public Integer getStateId() {
		return this.stateId;
	}

	public TeacherAddress setStateId(Integer stateId) {
		this.stateId = stateId;
		return this;
	}

	public Integer getCity() {
		return this.city;
	}

	public TeacherAddress setCity(Integer city) {
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}