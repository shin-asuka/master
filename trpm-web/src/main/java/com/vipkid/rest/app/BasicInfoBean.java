package com.vipkid.rest.app;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BasicInfoBean implements Serializable {

    private static final long serialVersionUID = -732744608873189260L;

    private String fullName;
    
    private Integer countryId;
    
    private Integer stateId;
    
    private Integer cityId;
    
    private String street;
    
    private String zipCode;
    
    private String phoneType; //新增字段
    
    private String phoneNationCode;
    
    private String phoneNationId;
    
    private String mobile;
    
    private String timezone;
    
    private String recruitmentChannel;
    
    private String channel;
    
    private String nationality;
    
    private String gender;
    
    private String highestDegree;
    
    private List<Map<String,Integer>> teachingIds;

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

    public String getHighestDegree() {
        return highestDegree;
    }

    public void setHighestDegree(String highestDegree) {
        this.highestDegree = highestDegree;
    }

    public List<Map<String, Integer>> getTeachingIds() {
        return teachingIds;
    }

    public void setTeachingIds(List<Map<String, Integer>> teachingIds) {
        this.teachingIds = teachingIds;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

}
