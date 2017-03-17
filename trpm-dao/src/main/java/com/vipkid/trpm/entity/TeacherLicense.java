package com.vipkid.trpm.entity;

import org.community.dao.support.Entity;

import java.io.Serializable;
import java.util.Date;

public class TeacherLicense extends Entity implements Serializable {

	private static final long serialVersionUID = -131427392030075858L;
	/*  */
	private Integer id;
	/* teacher id */
	private Long teacherId;

	/* driver_license */
	private String driverLicense;

	/* social_no */
	private String socialNo;

	private String driverLicenseType;

	private String driverLicenseIssuingAgency;
	/*  */
	private Date createTime;
	/*  */
	private Date updateTime;

	private Long updateId;

	private Long createId;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	public String getDriverLicense() {
		return driverLicense;
	}

	public void setDriverLicense(String driverLicense) {
		this.driverLicense = driverLicense;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getSocialNo() {
		return socialNo;
	}

	public void setSocialNo(String socialNo) {
		this.socialNo = socialNo;
	}

	public String getDriverLicenseType() {
		return driverLicenseType;
	}

	public void setDriverLicenseType(String driverLicenseType) {
		this.driverLicenseType = driverLicenseType;
	}

	public String getDriverLicenseIssuingAgency() {
		return driverLicenseIssuingAgency;
	}

	public void setDriverLicenseIssuingAgency(String driverLicenseIssuingAgency) {
		this.driverLicenseIssuingAgency = driverLicenseIssuingAgency;
	}

	public Long getUpdateId() {
		return updateId;
	}

	public void setUpdateId(Long updateId) {
		this.updateId = updateId;
	}

	public Long getCreateId() {
		return createId;
	}

	public void setCreateId(Long createId) {
		this.createId = createId;
	}
}