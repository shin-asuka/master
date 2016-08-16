package com.vipkid.trpm.entity.app;

import java.io.Serializable;

public class AppStudent implements Serializable{

    private static final long serialVersionUID = -6817400899030997710L;

    private Long id;
    private String englishName;
    private String avatar;
    private Integer gender;
    public Long getId() {
        return id;
    }
    public AppStudent setId(Long id) {
        this.id = id;
        return this;
    }
    public String getEnglishName() {
        return englishName;
    }
    public AppStudent setEnglishName(String englishName) {
        this.englishName = englishName;
        return this;
    }
    public String getAvatar() {
        return avatar;
    }
    public AppStudent setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }
    public Integer getGender() {
        return gender;
    }
    public AppStudent setGender(Integer gender) {
        this.gender = gender;
        return this;
    }
   
    
    
}
