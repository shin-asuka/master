package com.vipkid.recruitment.entity;

import org.community.dao.support.Entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class TeacherLockLog extends Entity implements Serializable{

    private long id;

    private long teacherId;
    
    private String reason;
    
    private String lifeCycle;

    private long operatorId;

    private Timestamp createDateTime;

    public TeacherLockLog() {
    }

    public TeacherLockLog(long teacherId, String reason, String lifeCycle) {
        this.teacherId = teacherId;
        this.reason = reason;
        this.lifeCycle = lifeCycle;
    }

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(String lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
    }
}
