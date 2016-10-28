package com.vipkid.trpm.entity;

import java.io.Serializable;
import java.util.Date;

import org.community.dao.support.Entity;



/**
 * 纳税表格详情Entity
 * 
 * @author zouqinghua
 * @version 2016-10-14
 */
public class TeacherTaxpayerFormDetail  extends Entity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long id; //编号
	private Long taxpayerFormId;		// 教师上传记录id
	private Long teacherId;		// teacherID
	private String formName;		// 上传表单文件名
	private Integer formType;		// 上传表单文件类型{ 1 W9 , 2 T4A  }
	private String uploaderName;		// 上传操作人名称
	private Long uploader;		// 操作人员ID
	private Integer isNew;		// 是否新上传：0 否；1 是
	private String url;		// 文件地址url
	private Date createTime;		// 创建时间
	private Long createBy;
	private String delFlag;
	
	public TeacherTaxpayerFormDetail() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTaxpayerFormId() {
		return taxpayerFormId;
	}

	public void setTaxpayerFormId(Long taxpayerFormId) {
		this.taxpayerFormId = taxpayerFormId;
	}

	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public Integer getFormType() {
		return formType;
	}

	public void setFormType(Integer formType) {
		this.formType = formType;
	}

	public String getUploaderName() {
		return uploaderName;
	}

	public void setUploaderName(String uploaderName) {
		this.uploaderName = uploaderName;
	}

	public Long getUploader() {
		return uploader;
	}

	public void setUploader(Long uploader) {
		this.uploader = uploader;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public Integer getIsNew() {
		return isNew;
	}

	public void setIsNew(Integer isNew) {
		this.isNew = isNew;
	}

	/**
     * 删除标记（0：正常；1：删除；）
     */
    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";
	
}