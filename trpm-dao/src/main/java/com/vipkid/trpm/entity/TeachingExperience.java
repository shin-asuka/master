package com.vipkid.trpm.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.community.dao.support.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TeachingExperience extends Entity implements Serializable{

    private static final long serialVersionUID = 8649458262820806559L;

    private long id;
    
    @JsonIgnore
    private long teacherId;
    
    private String organisationName;
    
    private String jobTitle;
    
    private Timestamp timePeriodStart;
    
    private Timestamp timePeriodEnd;
    
    private float hoursWeek;
    
    @JsonIgnore
    private float totalHours;
    
    private String jobDescription;
    
    @JsonIgnore
    private Timestamp createTime;
    
    @JsonIgnore
    private Timestamp updateTime;

    @JsonIgnore
    private long createId;
    
    @JsonIgnore
    private long updateId;
    
    @JsonIgnore
    private int status;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
        
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
        
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        
    }

    public Timestamp getTimePeriodStart() {
        return timePeriodStart;
    }

    public void setTimePeriodStart(Timestamp timePeriodStart) {
        this.timePeriodStart = timePeriodStart;
        
    }

    public Timestamp getTimePeriodEnd() {
        return timePeriodEnd;
    }

    public void setTimePeriodEnd(Timestamp timePeriodEnd) {
        this.timePeriodEnd = timePeriodEnd;
        
    }

    public float getHoursWeek() {
        return hoursWeek;
    }

    public void setHoursWeek(float hoursWeek) {
        this.hoursWeek = hoursWeek;
        
    }

    public float getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(float totalHours) {
        this.totalHours = totalHours;
        
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
        
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
        
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
        
    }

    public long getCreateId() {
        return createId;
    }

    public void setCreateId(long createId) {
        this.createId = createId;
        
    }

    public long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(long updateId) {
        this.updateId = updateId;
        
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        
    }

}
