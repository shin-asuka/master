package com.vipkid.trpm.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.community.dao.support.Entity;

import com.google.common.collect.Lists;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.FormType;



/**
 * 纳税表格Entity
 * @author zouqinghua
 * @version 2016-10-14
 */
public class TeacherTaxpayerForm  extends Entity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long id; //编号
	private Long teacherId;		// 老师ID
	private String teacherName;		// 教师名称
	private Integer formType;		// 上传表单文件类型{ 1 W9 , 2 T4A  }
	private Integer uploaded; //上传：0未上传；1已上传
	private Long uploader;		// 上传操作人
	private Date uploadTime;		// 最新上传时间
	private Integer isNew;		// 是否新上传：0 否；1 是
	private Long taxpayerFormDetailId;		// 有效表格文件记录id
	private String url;		// 文件地址url
	private Date createTime;		// 创建时间
	private Date updateTime;		// 修改时间
	private Long createBy;
	private Long updateBy;		// 
	private String delFlag;
	
	private TeacherTaxpayerFormDetail teacherTaxpayerFormDetail;
	private List<TeacherTaxpayerFormDetail> teacherTaxpayerFormDetailList = Lists.newArrayList();
	
	public TeacherTaxpayerForm() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public Integer getFormType() {
		return formType;
	}

	public void setFormType(Integer formType) {
		this.formType = formType;
	}

	public String getFormTypeName(){
		String formTypeName = "";
		if(formType!=null){
			FormType e = TeacherEnum.getFormTypeById(formType);
			formTypeName = e == null ? formTypeName : e.name();
		}
		return formTypeName;
	}
	public Integer getUploaded() {
		return uploaded;
	}

	public void setUploaded(Integer uploaded) {
		this.uploaded = uploaded;
	}

	public Long getUploader() {
		return uploader;
	}

	public void setUploader(Long uploader) {
		this.uploader = uploader;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Integer getIsNew() {
		return isNew;
	}

	public void setIsNew(Integer isNew) {
		this.isNew = isNew;
	}

	public Long getTaxpayerFormDetailId() {
		return taxpayerFormDetailId;
	}

	public void setTaxpayerFormDetailId(Long taxpayerFormDetailId) {
		this.taxpayerFormDetailId = taxpayerFormDetailId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName(){
		String name = "";
		String url = getUrl();
		if(StringUtils.isNotBlank(url)){
			Integer index = url.lastIndexOf("/");
			name = url.substring(index+1);
			if(name.contains("-")){
				name = name.substring(name.indexOf("-")+1);
			}
		}
		return name;
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
	
	
	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public Long getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Long updateBy) {
		this.updateBy = updateBy;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public TeacherTaxpayerFormDetail getTeacherTaxpayerFormDetail() {
		return teacherTaxpayerFormDetail;
	}

	public void setTeacherTaxpayerFormDetail(TeacherTaxpayerFormDetail teacherTaxpayerFormDetail) {
		this.teacherTaxpayerFormDetail = teacherTaxpayerFormDetail;
	}

	public List<TeacherTaxpayerFormDetail> getTeacherTaxpayerFormDetailList() {
		return teacherTaxpayerFormDetailList;
	}

	public void setTeacherTaxpayerFormDetailList(List<TeacherTaxpayerFormDetail> teacherTaxpayerFormDetailList) {
		this.teacherTaxpayerFormDetailList = teacherTaxpayerFormDetailList;
	}



	/**
     * 删除标记（0：正常；1：删除；）
     */
    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";
}