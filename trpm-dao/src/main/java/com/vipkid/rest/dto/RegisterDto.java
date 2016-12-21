package com.vipkid.rest.dto;

import com.vipkid.rest.validation.annotation.NotNull;

public class RegisterDto {
    
    @NotNull
    private String email;
    
    @NotNull
    private String password;
    
    private Long refereeId;
    
    private Long partnerId;
    
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

    public Long getRefereeId() {
        return refereeId;
    }

    public void setRefereeId(Long refereeId) {
        this.refereeId = refereeId;

    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
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
