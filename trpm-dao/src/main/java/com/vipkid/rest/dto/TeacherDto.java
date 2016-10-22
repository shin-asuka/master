package com.vipkid.rest.dto;

import java.io.Serializable;

import com.vipkid.rest.validation.annotation.Verify;

@Verify
public class TeacherDto implements Serializable {

    private static final long serialVersionUID = -732744608873189260L;

    private String fullName;
    
    private Integer countryId;    
   
    private Integer stateId;    
   
    private Integer cityId;    
   
    private String streetAddress;    
   
    private String zipCode;    
   
    private Integer phoneType; //新增字段    
   
    private String phoneNationCode;    
   
    private Integer phoneNationId;    
   
    private String mobile;    
   
    private String timezone;    
   
    private String recruitmentChannel;    
   
    private String channel;    
   
    private String nationality;    
   
    private String gender;    
    
    private String highestLevelOfEdu;
  
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getRecruitmentChannel() {
        return recruitmentChannel;
    }

    public void setRecruitmentChannel(String recruitmentChannel) {
        this.recruitmentChannel = recruitmentChannel;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public Integer getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(Integer phoneType) {
        this.phoneType = phoneType;
    }

    public String getPhoneNationCode() {
        return phoneNationCode;
    }

    public void setPhoneNationCode(String phoneNationCode) {
        this.phoneNationCode = phoneNationCode;
    }

    public Integer getPhoneNationId() {
        return phoneNationId;
    }

    public void setPhoneNationId(Integer phoneNationId) {
        this.phoneNationId = phoneNationId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHighestLevelOfEdu() {
        return highestLevelOfEdu;
    }

    public void setHighestLevelOfEdu(String highestLevelOfEdu) {
        this.highestLevelOfEdu = highestLevelOfEdu;
    }
}
