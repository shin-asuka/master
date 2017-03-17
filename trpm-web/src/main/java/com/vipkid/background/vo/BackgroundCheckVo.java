package com.vipkid.background.vo;


import java.io.Serializable;

public class BackgroundCheckVo implements Serializable{

    private static final long serialVersionUID = -2533960462834871624L;
    private Long teacherId;

    private String firstName;

    private String middleName;

    private String lastName;

    private String maidenName;

    private String birthDay;

    /* 国家id */
    private Integer latestCountryId;
    /* state id */
    private Integer latestStateId;
    /* city id */
    private Integer latestCity;

    private String latestStreet;

    private String latestZipCode;

    /* 国家id */
    private Integer currentCountryId;
    /* state id */
    private Integer currentStateId;
    /* city id */
    private Integer currentCity;

    private String currentStreet;

    private String currentZipCode;

    private String driverLicenseNumber;

    private String driverLicenseType;

    private String driverLicenseAgency;

    private String socialSecurityNumber;

    private String fileUrl;

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public Integer getLatestCountryId() {
        return latestCountryId;
    }

    public void setLatestCountryId(Integer latestCountryId) {
        this.latestCountryId = latestCountryId;
    }

    public Integer getLatestStateId() {
        return latestStateId;
    }

    public void setLatestStateId(Integer latestStateId) {
        this.latestStateId = latestStateId;
    }

    public Integer getLatestCity() {
        return latestCity;
    }

    public void setLatestCity(Integer latestCity) {
        this.latestCity = latestCity;
    }

    public String getLatestStreet() {
        return latestStreet;
    }

    public void setLatestStreet(String latestStreet) {
        this.latestStreet = latestStreet;
    }

    public String getLatestZipCode() {
        return latestZipCode;
    }

    public void setLatestZipCode(String latestZipCode) {
        this.latestZipCode = latestZipCode;
    }

    public Integer getCurrentCountryId() {
        return currentCountryId;
    }

    public void setCurrentCountryId(Integer currentCountryId) {
        this.currentCountryId = currentCountryId;
    }

    public Integer getCurrentStateId() {
        return currentStateId;
    }

    public void setCurrentStateId(Integer currentStateId) {
        this.currentStateId = currentStateId;
    }

    public Integer getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(Integer currentCity) {
        this.currentCity = currentCity;
    }

    public String getCurrentStreet() {
        return currentStreet;
    }

    public void setCurrentStreet(String currentStreet) {
        this.currentStreet = currentStreet;
    }

    public String getCurrentZipCode() {
        return currentZipCode;
    }

    public void setCurrentZipCode(String currentZipCode) {
        this.currentZipCode = currentZipCode;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getDriverLicenseType() {
        return driverLicenseType;
    }

    public void setDriverLicenseType(String driverLicenseType) {
        this.driverLicenseType = driverLicenseType;
    }

    public String getDriverLicenseAgency() {
        return driverLicenseAgency;
    }

    public void setDriverLicenseAgency(String driverLicenseAgency) {
        this.driverLicenseAgency = driverLicenseAgency;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
