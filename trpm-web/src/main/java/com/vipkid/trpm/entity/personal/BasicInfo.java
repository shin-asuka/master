package com.vipkid.trpm.entity.personal;

public class BasicInfo {

	private String address;

	private String country;

	private String email;
	// from user.
	private String gender;

	private String introduction;
	
	private String evaluationBio;

	private String mobile;

	private String skype;

	private String timezone;

	private String graduatedFrom;
	
	private String highestLevelOfEdu;//新增

	private String birthday;

	private String name;

	private int phoneType;
	
	private String phoneNationCode;

	private int phoneNationId;

	private int currentAddressId;

	public int getPhoneNationId() {
		return phoneNationId;
	}

	public void setPhoneNationId(int phoneNationId) {
		this.phoneNationId = phoneNationId;
	}

	public int getCurrentAddressId() {
		return currentAddressId;
	}

	public void setCurrentAddressId(int currentAddressId) {
		this.currentAddressId = currentAddressId;
	}

	public String getPhoneNationCode() {
		return phoneNationCode;
	}

	public void setPhoneNationCode(String phoneNationCode) {
		this.phoneNationCode = phoneNationCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
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

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getGraduatedFrom() {
		return graduatedFrom;
	}

	public void setGraduatedFrom(String graduatedFrom) {
		this.graduatedFrom = graduatedFrom;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHighestLevelOfEdu() {
		return highestLevelOfEdu;
	}

	public void setHighestLevelOfEdu(String highestLevelOfEdu) {
		this.highestLevelOfEdu = highestLevelOfEdu;
	}

    public String getEvaluationBio() {
        return evaluationBio;
    }

    public void setEvaluationBio(String evaluationBio) {
        this.evaluationBio = evaluationBio;
    }

    public int getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(int phoneType) {
        this.phoneType = phoneType;
    }	
	
}
