package com.vipkid.trpm.entity;

import java.io.Serializable;
import java.util.Date;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.FormType;
import com.vipkid.enums.TeacherEnum.ISNew;
import com.vipkid.enums.TeacherEnum.UploadStatus;

/**
 * @author zouqinghua
 * @date 2016年10月17日 下午3:22:00
 *
 */
public class TeacherTaxpayerView implements Serializable{

	private static final long serialVersionUID = 3823944543089143579L;
	
	private Long teacherId; // 老师ID
	private String name; // 教师名称
	private String type;
	private String gender;
	private String nationality; //
	
	private Long countryId;
	private String country;
	private Long stateId;
	private String state;
	private Long cityId;
	private String city;
	private Long taxpayerId;
	private Long taxpayerDetailId;
	private Integer uploaded; // 0 未上传 ；1 已上传
	private Long uploader; // 上传操作人
	private Date uploadTime; // 最新上传时间
	private Integer formType; // 上传表单文件类型{ 1 W9 , 2 T4A }
	private Integer isNew; // 是否新上传：0 否；1 是
	private String url; // 文件url

	public TeacherTaxpayerView() {
	}

	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getUploaded() {
		return uploaded;
	}

	public Long getTaxpayerId() {
		return taxpayerId;
	}

	public void setTaxpayerId(Long taxpayerId) {
		this.taxpayerId = taxpayerId;
	}

	public void setUploaded(Integer uploaded) {
		this.uploaded = uploaded;
	}

	public String getUploadedName(){
		String name = "";
		if(uploaded!=null){
			UploadStatus e = TeacherEnum.getUploadStatusById(uploaded);
			name = e == null ? name : e.name();
		}
		return name;
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

	public Integer getFormType() {
		return formType;
	}

	public void setFormType(Integer formType) {
		this.formType = formType;
	}

	public String getFormTypeName(){
		String name = "";
		if(formType!=null){
			FormType e = TeacherEnum.getFormTypeById(formType);
			name = e == null ? name : e.name();
		}
		return name;
	}

	public Integer getIsNew() {
		return isNew;
	}

	public void setIsNew(Integer isNew) {
		this.isNew = isNew;
	}

	public String getISNewName(){
		String name = "";
		if(isNew!=null){
			ISNew e = TeacherEnum.getNewById(isNew);
			name = e == null ? name : e.name();
		}
		return name;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public Long getTaxpayerDetailId() {
		return taxpayerDetailId;
	}

	public void setTaxpayerDetailId(Long taxpayerDetailId) {
		this.taxpayerDetailId = taxpayerDetailId;
	}

}
