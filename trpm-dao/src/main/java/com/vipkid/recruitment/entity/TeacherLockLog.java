package com.vipkid.recruitment.entity;

import org.community.dao.support.Entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class TeacherLockLog extends Entity implements Serializable{

    private Long id;

    private Long teacherId;
    
    private String reason;
    
    private String lifeCycle;

    private Integer isUnlocked;

    private Long operatorId;

    private Timestamp createDateTime;

    public TeacherLockLog() {
    }

    public TeacherLockLog(Long teacherId, String reason, String lifeCycle) {
        this(teacherId, reason, lifeCycle, null);
    }

    public TeacherLockLog(Long teacherId, String reason, String lifeCycle, Integer isUnlocked) {
        this.teacherId = teacherId;
        this.reason = reason;
        this.lifeCycle = lifeCycle;
        this.isUnlocked = isUnlocked;
    }


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

    public Integer getIsUnlocked() {
        return isUnlocked;
    }

    public void setIsUnlocked(Integer isUnlocked) {
        this.isUnlocked = isUnlocked;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
    }
}
