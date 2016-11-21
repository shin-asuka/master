package com.vipkid.rest.dto;

import com.vipkid.rest.validation.annotation.NotNull;

public class RegisterDto {
    
    @NotNull
    private String email;
    
    @NotNull
    private String password;
    
    private Integer refereeId;
    
    private Integer partnerId;
    
    @NotNull
    private String key;
    
    @NotNull
    private String imageCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;

    }

    public Integer getRefereeId() {
        return refereeId;
    }

    public void setRefereeId(Integer refereeId) {
        this.refereeId = refereeId;

    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;

    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;

    }
    
    
    
}
