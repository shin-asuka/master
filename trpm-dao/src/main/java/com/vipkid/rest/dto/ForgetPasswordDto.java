package com.vipkid.rest.dto;

import com.vipkid.rest.validation.annotation.NotNull;

/**
 * Created by luning on 2017/3/7.
 */
public class ForgetPasswordDto {
    @NotNull
    private String email;

    private String key;

    private String imageCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
