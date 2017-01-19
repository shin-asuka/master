package com.vipkid.trpm.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.community.dao.support.Entity;

import java.io.Serializable;

public final class TeacherPeComments extends Entity implements Serializable {

    private static final long serialVersionUID = 6611553613271690756L;
    /*  */
    private int id;
    /*  */
    private int applicationId;
    /*  */
    private String thingsDidWell;
    /*  */
    private String areasImprovement;
    /*  */
    private int totalScore;
    /*  */
    private String status;
    
    public int getId() {
        return this.id;
    }

    public TeacherPeComments setId(int id) {
        this.id = id;
        return this;
    }
    
    public int getApplicationId() {
        return this.applicationId;
    }

    public TeacherPeComments setApplicationId(int applicationId) {
        this.applicationId = applicationId;
        return this;
    }
    
    public String getThingsDidWell() {
        return this.thingsDidWell;
    }

    public TeacherPeComments setThingsDidWell(String thingsDidWell) {
        this.thingsDidWell = thingsDidWell;
        return this;
    }
    
    public String getAreasImprovement() {
        return this.areasImprovement;
    }

    public TeacherPeComments setAreasImprovement(String areasImprovement) {
        this.areasImprovement = areasImprovement;
        return this;
    }
    
    public int getTotalScore() {
        return this.totalScore;
    }

    public TeacherPeComments setTotalScore(int totalScore) {
        this.totalScore = totalScore;
        return this;
    }
    
    public String getStatus() {
        return this.status;
    }

    public TeacherPeComments setStatus(String status) {
        this.status = status;
        return this;
    }

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}