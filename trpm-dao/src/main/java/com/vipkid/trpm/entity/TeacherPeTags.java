package com.vipkid.trpm.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.community.dao.support.Entity;

import java.io.Serializable;

public final class TeacherPeTags extends Entity implements Serializable {

    private static final long serialVersionUID = 4941410612309630686L;
    /*  */
    private int id;
    /*  */
    private int applicationId;
    /*  */
    private int tagId;
    
    public int getId() {
        return this.id;
    }

    public TeacherPeTags setId(int id) {
        this.id = id;
        return this;
    }
    
    public int getApplicationId() {
        return this.applicationId;
    }

    public TeacherPeTags setApplicationId(int applicationId) {
        this.applicationId = applicationId;
        return this;
    }
    
    public int getTagId() {
        return this.tagId;
    }

    public TeacherPeTags setTagId(int tagId) {
        this.tagId = tagId;
        return this;
    }

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}