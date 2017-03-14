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

	/* security_no */
	private String securityNo;

	private String type;

	private String issuingAgency;
	/*  */
	private Date createTime;
	/*  */
	private Date updateTime;


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

	public String getSecurityNo() {
		return securityNo;
	}

	public void setSecurityNo(String securityNo) {
		this.securityNo = securityNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIssuingAgency() {
		return issuingAgency;
	}

	public void setIssuingAgency(String issuingAgency) {
		this.issuingAgency = issuingAgency;
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
}