package com.vipkid.rest.dto;

import com.vipkid.rest.validation.annotation.NotNull;

@NotNull
public class PersonlInfoDto {

	/** 从  aws **/
	private String avatar;
	
	private String email;
	
	private String gender;
	
	private String birthday;
	
	private String nationality;
	
	private String phoneType;
	
	private String phoneNationCode;
	
	private String phoneNationId;
	
	private String mobile;
	
	private String skype;
	
	private Integer countryId;
	
	private String countryName;
	
	private Integer stateId;
	
	private String stateName;
	
	private Integer cityId;
	
	private String cityName;
	
	private String streetAddress;
	
	private String zipCode;
	
	private String timezone;
	
	private String university;
	
	private String highestLevelOfEdu;
	
	private String introduction;
	
	private Long teacherId;
	
	private String evaluationBio;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getPhoneNationCode() {
		return phoneNationCode;
	}

	public void setPhoneNationCode(String phoneNationCode) {
		this.phoneNationCode = phoneNationCode;
	}

	public String getPhoneNationId() {
		return phoneNationId;
	}

	public void setPhoneNationId(String phoneNationId) {
		this.phoneNationId = phoneNationId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSkype() {
		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}

	public String getHighestLevelOfEdu() {
		return highestLevelOfEdu;
	}

	public void setHighestLevelOfEdu(String highestLevelOfEdu) {
		this.highestLevelOfEdu = highestLevelOfEdu;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	public String getEvaluationBio() {
		return evaluationBio;
	}

	public void setEvaluationBio(String evaluationBio) {
		this.evaluationBio = evaluationBio;
	}
	
}
