package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class TeacherNationalityCode extends Entity implements Serializable {

    private static final long serialVersionUID = 2309535848119858478L;
    /*  */
    private int id;
    /*  */
    private String country;
    /*  */
    private String code;
    /*  */
    private String background;
        
    public int getId() {
        return this.id;
    }

    public TeacherNationalityCode setId(int id) {
        this.id = id;
        return this;
    }
        
    public String getCountry() {
        return this.country;
    }

    public TeacherNationalityCode setCountry(String country) {
        this.country = country;
        return this;
    }
        
    public String getCode() {
        return this.code;
    }

    public TeacherNationalityCode setCode(String code) {
        this.code = code;
        return this;
    }
        
    public String getBackground() {
        return this.background;
    }

    public TeacherNationalityCode setBackground(String background) {
        this.background = background;
        return this;
    }

}