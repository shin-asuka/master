package com.vipkid.trpm.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.community.dao.support.Entity;

import java.io.Serializable;

public final class TeacherPeLevels extends Entity implements Serializable {

    private static final long serialVersionUID = -7859682963175313790L;
    /*  */
    private int id;
    /*  */
    private int applicationId;
    /*  */
    private int level;
    
    public int getId() {
        return this.id;
    }

    public TeacherPeLevels setId(int id) {
        this.id = id;
        return this;
    }
    
    public int getApplicationId() {
        return this.applicationId;
    }

    public TeacherPeLevels setApplicationId(int applicationId) {
        this.applicationId = applicationId;
        return this;
    }
    
    public int getLevel() {
        return this.level;
    }

    public TeacherPeLevels setLevel(int level) {
        this.level = level;
        return this;
    }

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}