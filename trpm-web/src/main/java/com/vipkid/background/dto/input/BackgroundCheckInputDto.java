package com.vipkid.background.dto.input;


import java.io.Serializable;

public class BackgroundCheckInputDto implements Serializable{
    private static final long serialVersionUID = 2932585128429730715L;

    private Long teacherId;

    private String middleName;

    private String maidenName;

    private String birthDay;

    /* 最新国家id */
    private Integer latestCountryId;
    /* 最新state id */
    private Integer latestStateId;
    /* 最新city id */
    private Integer latestCity;
    /* 最新街道名称 */
    private String latestStreet;
    /* 最新zipcode */
    private String latestZipCode;

    /* 当前国家id */
    private Integer currentCountryId;
    /* 当前state id */
    private Integer currentStateId;
    /* 当前city id */
    private Integer currentCity;
    /* 当前街道名称 */
    private String currentStreet;
    /* 当前zipCode */
    private String currentZipCode;
    /* 默认为最新地址*/
    private Integer addressType = 1;

    private String driverLicenseNumber;

    private String driverLicenseType;

    private String driverLicenseAgency;

    private String socialSecurityNumber;

    private String fileUrl;

    private String operateType;

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

    public Integer getAddressType() {
        return addressType;
    }

    public void setAddressType(Integer addressType) {
        this.addressType = addressType;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
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

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
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
}
