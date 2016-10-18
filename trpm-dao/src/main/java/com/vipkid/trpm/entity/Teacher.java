package com.vipkid.trpm.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.community.dao.support.Entity;

public class Teacher extends Entity implements Serializable {

	private static final long serialVersionUID = 7422401967408523712L;
	/*  */
	private long id;
	/*  */
	private String address;
	/*  */
	private String avatar;
	/*  */
	private String bankAccountName;
	/*  */
	private String bankAddress;
	/*  */
	private String bankCardNumber;
	/*  */
	private String bankName;
	/*  */
	private String bankSwiftCode;
	/*  */
	private String bankABARoutingNumber;
	/*  */
	private String bankACHNumber;
	/*  */
	private java.sql.Date birthday;
	/*  */
	private String certificates;
	/*  */
	private java.sql.Date contractEndDate;
	/*  */
	private java.sql.Date contractStartDate;
	/* */
	private Date entryDate;
	/*  */
	private String country;
	/*  */
	private String currency;
	/*  */
	private String email;
	/*  */
	private float extraClassSalary;
	/*  */
	private String hide;
	/*  */
	private String introduction;
	/*  */
	private String job;
	/*  */
	private String lifeCycle;
	/*  */
	private String linkedin;
	/*  */
	private String mobile;
	/*  */
	private long noShowTime;
	/*  */
	private String notes;
	/*  */
	private float overTimeClassSalary;
	/*  */
	private String paypalAccount;
	/*  */
	private String recruitmentChannel;
	/*  */
	private String serialNumber;
	/*  */
	private String skype;
	/*  */
	private String summary;
	/*  */
	private int teachingExperience;
	/*  */
	private String timezone;
	/*  */
	private String type;
	/*  */
	private String qq;
	/*  */
	private String realName;
	/*  */
	private String additionalDiplomas;
	/*  */
	private String bachelorDiploma;
	/*  */
	private String certificateFiles;
	/*  */
	private String lifePicture1;
	/*  */
	private String lifePicture2;
	/*  */
	private String referee;
	/*  */
	private String resume;
	/*  */
	private String shortVideo;
	/*  */
	private long partnerId;
	/*  */
	private String contract;
	/*  */
	private String passport;
	/*  */
	private String photos;
	/*  */
	private String recruitmentId;
	/*  */
	private int hasTested;
	/*  */
	private String introductionZh;
	/*  */
	private String graduatedFrom;
	/*  */
	private String highestLevelOfEdu;//新增
	/*  */
	private String teacherTags;
	/*  */
	private String vipkidRemarks;
	/* 对应的manager */
	private long manager;
	//TODO  generated from mybatis
	private String contractType;
	//TODO  generated from mybatis
	private Timestamp createDateTime;

	// 2016-05-24 新添加teacher 属性 --start
	private int identityType;
	private String identityNumber;
	private int issuanceCountry;
	private String phoneNationCode;
	private int phoneNationId;
	private int currentAddressId;
	private int beneficiaryBankAddressId;
	private int beneficiaryAddressId;

	// 2016-05-24 新添加teacher 属性 --end
	private String evaluationBio;
	
	private int phoneType; //新增电话类型

	public long getId() {
		return this.id;
	}

	public Teacher setId(long id) {
		this.id = id;
		return this;
	}

	public String getAddress() {
		return this.address;
	}

	public Teacher setAddress(String address) {
		this.address = address;
		return this;
	}

	public String getAvatar() {
		return this.avatar;
	}

	public Teacher setAvatar(String avatar) {
		this.avatar = avatar;
		return this;
	}

	public String getBankAccountName() {
		return this.bankAccountName;
	}

	public Teacher setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
		return this;
	}

	public String getBankAddress() {
		return this.bankAddress;
	}

	public Teacher setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
		return this;
	}

	public String getBankCardNumber() {
		return this.bankCardNumber;
	}

	public Teacher setBankCardNumber(String bankCardNumber) {
		this.bankCardNumber = bankCardNumber;
		return this;
	}

	public String getBankName() {
		return this.bankName;
	}

	public Teacher setBankName(String bankName) {
		this.bankName = bankName;
		return this;
	}

	public String getBankSwiftCode() {
		return this.bankSwiftCode;
	}

	public Teacher setBankSwiftCode(String bankSwiftCode) {
		this.bankSwiftCode = bankSwiftCode;
		return this;
	}

	public String getBankABARoutingNumber() {
		return bankABARoutingNumber;
	}

	public Teacher setBankABARoutingNumber(String bankABARoutingNumber) {
		this.bankABARoutingNumber = bankABARoutingNumber;
		return this;
	}

	public String getBankACHNumber() {
		return bankACHNumber;
	}

	public Teacher setBankACHNumber(String bankACHNumber) {
		this.bankACHNumber = bankACHNumber;
		return this;
	}
	
	public java.sql.Date getBirthday() {
		return this.birthday;
	}

	public Teacher setBirthday(java.sql.Date birthday) {
		this.birthday = birthday;
		return this;
	}

	public String getCertificates() {
		return this.certificates;
	}

	public Teacher setCertificates(String certificates) {
		this.certificates = certificates;
		return this;
	}

	public java.sql.Date getContractEndDate() {
		return this.contractEndDate;
	}

	public Teacher setContractEndDate(java.sql.Date contractEndDate) {
		this.contractEndDate = contractEndDate;
		return this;
	}

	public java.sql.Date getContractStartDate() {
		return this.contractStartDate;
	}

	public Teacher setContractStartDate(java.sql.Date contractStartDate) {
		this.contractStartDate = contractStartDate;
		return this;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public Teacher setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
		return this;
	}

	public String getCountry() {
		return this.country;
	}

	public Teacher setCountry(String country) {
		this.country = country;
		return this;
	}

	public String getCurrency() {
		return this.currency;
	}

	public Teacher setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public String getEmail() {
		return this.email;
	}

	public Teacher setEmail(String email) {
		this.email = email;
		return this;
	}

	public float getExtraClassSalary() {
		return this.extraClassSalary;
	}

	public Teacher setExtraClassSalary(float extraClassSalary) {
		this.extraClassSalary = extraClassSalary;
		return this;
	}

	public String getHide() {
		return this.hide;
	}

	public Teacher setHide(String hide) {
		this.hide = hide;
		return this;
	}

	public String getIntroduction() {
		return this.introduction;
	}

	public Teacher setIntroduction(String introduction) {
		this.introduction = introduction;
		return this;
	}

	public String getJob() {
		return this.job;
	}

	public Teacher setJob(String job) {
		this.job = job;
		return this;
	}

	public String getLifeCycle() {
		return this.lifeCycle;
	}

	public Teacher setLifeCycle(String lifeCycle) {
		this.lifeCycle = lifeCycle;
		return this;
	}

	public String getLinkedin() {
		return this.linkedin;
	}

	public Teacher setLinkedin(String linkedin) {
		this.linkedin = linkedin;
		return this;
	}

	public String getMobile() {
		return this.mobile;
	}

	public Teacher setMobile(String mobile) {
		this.mobile = mobile;
		return this;
	}

	public long getNoShowTime() {
		return this.noShowTime;
	}

	public Teacher setNoShowTime(long noShowTime) {
		this.noShowTime = noShowTime;
		return this;
	}

	public String getNotes() {
		return this.notes;
	}

	public Teacher setNotes(String notes) {
		this.notes = notes;
		return this;
	}

	public float getOverTimeClassSalary() {
		return this.overTimeClassSalary;
	}

	public Teacher setOverTimeClassSalary(float overTimeClassSalary) {
		this.overTimeClassSalary = overTimeClassSalary;
		return this;
	}

	public String getPaypalAccount() {
		return this.paypalAccount;
	}

	public Teacher setPaypalAccount(String paypalAccount) {
		this.paypalAccount = paypalAccount;
		return this;
	}

	public String getRecruitmentChannel() {
		return this.recruitmentChannel;
	}

	public Teacher setRecruitmentChannel(String recruitmentChannel) {
		this.recruitmentChannel = recruitmentChannel;
		return this;
	}

	public String getSerialNumber() {
		return this.serialNumber;
	}

	public Teacher setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
		return this;
	}

	public String getSkype() {
		return this.skype;
	}

	public Teacher setSkype(String skype) {
		this.skype = skype;
		return this;
	}

	public String getSummary() {
		return this.summary;
	}

	public Teacher setSummary(String summary) {
		this.summary = summary;
		return this;
	}

	public int getTeachingExperience() {
		return this.teachingExperience;
	}

	public Teacher setTeachingExperience(int teachingExperience) {
		this.teachingExperience = teachingExperience;
		return this;
	}

	public String getTimezone() {
		return this.timezone;
	}

	public Teacher setTimezone(String timezone) {
		this.timezone = timezone;
		return this;
	}

	public String getType() {
		return this.type;
	}

	public Teacher setType(String type) {
		this.type = type;
		return this;
	}

	public String getQq() {
		return this.qq;
	}

	public Teacher setQq(String qq) {
		this.qq = qq;
		return this;
	}

	public String getRealName() {
		return this.realName;
	}

	public Teacher setRealName(String realName) {
		this.realName = realName;
		return this;
	}

	public String getAdditionalDiplomas() {
		return this.additionalDiplomas;
	}

	public Teacher setAdditionalDiplomas(String additionalDiplomas) {
		this.additionalDiplomas = additionalDiplomas;
		return this;
	}

	public String getBachelorDiploma() {
		return this.bachelorDiploma;
	}

	public Teacher setBachelorDiploma(String bachelorDiploma) {
		this.bachelorDiploma = bachelorDiploma;
		return this;
	}

	public String getCertificateFiles() {
		return this.certificateFiles;
	}

	public Teacher setCertificateFiles(String certificateFiles) {
		this.certificateFiles = certificateFiles;
		return this;
	}

	public String getLifePicture1() {
		return this.lifePicture1;
	}

	public Teacher setLifePicture1(String lifePicture1) {
		this.lifePicture1 = lifePicture1;
		return this;
	}

	public String getLifePicture2() {
		return this.lifePicture2;
	}

	public Teacher setLifePicture2(String lifePicture2) {
		this.lifePicture2 = lifePicture2;
		return this;
	}

	public String getReferee() {
		return this.referee;
	}

	public Teacher setReferee(String referee) {
		this.referee = referee;
		return this;
	}

	public String getResume() {
		return this.resume;
	}

	public Teacher setResume(String resume) {
		this.resume = resume;
		return this;
	}

	public String getShortVideo() {
		return this.shortVideo;
	}

	public Teacher setShortVideo(String shortVideo) {
		this.shortVideo = shortVideo;
		return this;
	}

	public long getPartnerId() {
		return this.partnerId;
	}

	public Teacher setPartnerId(long partnerId) {
		this.partnerId = partnerId;
		return this;
	}

	public String getContract() {
		return this.contract;
	}

	public Teacher setContract(String contract) {
		this.contract = contract;
		return this;
	}

	public String getPassport() {
		return this.passport;
	}

	public Teacher setPassport(String passport) {
		this.passport = passport;
		return this;
	}

	public String getPhotos() {
		return this.photos;
	}

	public Teacher setPhotos(String photos) {
		this.photos = photos;
		return this;
	}

	public String getRecruitmentId() {
		return this.recruitmentId;
	}

	public Teacher setRecruitmentId(String recruitmentId) {
		this.recruitmentId = recruitmentId;
		return this;
	}

	public int getHasTested() {
		return this.hasTested;
	}

	public Teacher setHasTested(int hasTested) {
		this.hasTested = hasTested;
		return this;
	}

	public String getIntroductionZh() {
		return this.introductionZh;
	}

	public Teacher setIntroductionZh(String introductionZh) {
		this.introductionZh = introductionZh;
		return this;
	}

	public String getGraduatedFrom() {
		return this.graduatedFrom;
	}

	public Teacher setGraduatedFrom(String graduatedFrom) {
		this.graduatedFrom = graduatedFrom;
		return this;
	}

	public String getTeacherTags() {
		return this.teacherTags;
	}

	public Teacher setTeacherTags(String teacherTags) {
		this.teacherTags = teacherTags;
		return this;
	}

	public String getVipkidRemarks() {
		return this.vipkidRemarks;
	}

	public Teacher setVipkidRemarks(String vipkidRemarks) {
		this.vipkidRemarks = vipkidRemarks;
		return this;
	}

	public long getManager() {
		return this.manager;
	}

	public Teacher setManager(long manager) {
		this.manager = manager;
		return this;
	}

	public int getIdentityType() {
		return identityType;
	}

	public void setIdentityType(int identityType) {
		this.identityType = identityType;
	}

	public String getIdentityNumber() {
		return identityNumber;
	}

	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}

	public int getIssuanceCountry() {
		return issuanceCountry;
	}

	public void setIssuanceCountry(int issuanceCountry) {
		this.issuanceCountry = issuanceCountry;
	}

	public String getPhoneNationCode() {
		return phoneNationCode;
	}

	public void setPhoneNationCode(String phoneNationCode) {
		this.phoneNationCode = phoneNationCode;
	}

	public int getCurrentAddressId() {
		return currentAddressId;
	}

	public void setCurrentAddressId(int currentAddressId) {
		this.currentAddressId = currentAddressId;
	}

	public int getBeneficiaryBankAddressId() {
		return beneficiaryBankAddressId;
	}

	public void setBeneficiaryBankAddressId(int beneficiaryBankAddressId) {
		this.beneficiaryBankAddressId = beneficiaryBankAddressId;
	}

	public int getBeneficiaryAddressId() {
		return beneficiaryAddressId;
	}

	public void setBeneficiaryAddressId(int beneficiaryAddressId) {
		this.beneficiaryAddressId = beneficiaryAddressId;
	}

	public int getPhoneNationId() {
		return phoneNationId;
	}

	public void setPhoneNationId(int phoneNationId) {
		this.phoneNationId = phoneNationId;
	}
	
	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public Timestamp getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Timestamp createDateTime) {
		this.createDateTime = createDateTime;
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