package com.vipkid.trpm.entity.app;

import java.io.Serializable;

public class AppTeacher implements Serializable {

    private static final long serialVersionUID = -6436404517842360613L;
    
    private Long id;
    private String shortName;
    private String fullName;
    private String email;
    private String mobile;
    private String skype;
    private String avatar;
    private Integer gender;
    private String country;
    private String address;
    private String timeZone;
    private String certificates;
    private Integer lifeCycle;
    private String contractDuration;
    private String introduction;
    
    public Long getId() {
        return id;
    }
    public AppTeacher setId(Long id) {
        this.id = id;
        return this;
    }
    public String getShortName() {
        return shortName;
    }
    public AppTeacher setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }
    public String getFullName() {
        return fullName;
    }
    public AppTeacher setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }
    public String getEmail() {
        return email;
    }
    public AppTeacher setEmail(String email) {
        this.email = email;
        return this;
    }
    public String getMobile() {
        return mobile;
    }
    public AppTeacher setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }
    public String getSkype() {
        return skype;
    }
    public AppTeacher setSkype(String skype) {
        this.skype = skype;
        return this;
    }
    public String getAvatar() {
        return avatar;
    }
    public AppTeacher setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }
    public Integer getGender() {
        return gender;
    }
    public AppTeacher setGender(Integer gender) {
        this.gender = gender;
        return this;
    }
    public String getCountry() {
        return country;
    }
    public AppTeacher setCountry(String country) {
        this.country = country;
        return this;
    }
    public String getAddress() {
        return address;
    }
    public AppTeacher setAddress(String address) {
        this.address = address;
        return this;
    }
    public String getTimeZone() {
        return timeZone;
    }
    public AppTeacher setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }
    public String getCertificates() {
        return certificates;
    }
    public AppTeacher setCertificates(String certificates) {
        this.certificates = certificates;
        return this;
    }
    public Integer getLifeCycle() {
        return lifeCycle;
    }
    public AppTeacher setLifeCycle(Integer lifeCycle) {
        this.lifeCycle = lifeCycle;
        return this;
    }
    public String getContractDuration() {
        return contractDuration;
    }
    public AppTeacher setContractDuration(String contractDuration) {
        this.contractDuration = contractDuration;
        return this;
    }
    public String getIntroduction() {
        return introduction;
    }
    public AppTeacher setIntroduction(String introduction) {
        this.introduction = introduction;
        return this;
    }
   
}
