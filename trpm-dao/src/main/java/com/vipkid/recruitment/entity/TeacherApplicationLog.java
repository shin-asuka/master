package com.vipkid.recruitment.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.community.dao.support.Entity;

public class TeacherApplicationLog extends Entity implements Serializable{

    private static final long serialVersionUID = 5434286284784271582L;

    private Long id;

    private Long teacherId;
    
    private Long teacherApplicationId;
    
    private Long onlineClassId;
    
    private Timestamp scheduleDateTime;
    
    private String status;
    
    private String result;
    
    private Timestamp createTime;
    
    private Timestamp updateTime;
    
    private Long createId;
    
    private Long updateId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;

    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;

    }

    public Long getTeacherApplicationId() {
        return teacherApplicationId;
    }

    public void setTeacherApplicationId(Long teacherApplicationId) {
        this.teacherApplicationId = teacherApplicationId;

    }

    public Long getOnlineClassId() {
        return onlineClassId;
    }

    public void setOnlineClassId(Long onlineClassId) {
        this.onlineClassId = onlineClassId;

    }

    public Timestamp getScheduleDateTime() {
        return scheduleDateTime;
    }

    public void setScheduleDateTime(Timestamp scheduleDateTime) {
        this.scheduleDateTime = scheduleDateTime;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;

    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;

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

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;

    }

    public Long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Long updateId) {
        this.updateId = updateId;

    }
}
