package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class StudentComment extends Entity implements Serializable {

    private static final long serialVersionUID = -1L;
    private long id;
    private long onlineClassId;
    private long studentId;
    private int scores;
    private String comment;
    private java.sql.Timestamp createTime;
        
    public long getId() {
        return this.id;
    }

    public StudentComment setId(long id) {
        this.id = id;
        return this;
    }
        
    public long getOnlineClassId() {
        return this.onlineClassId;
    }

    public StudentComment setOnlineClassId(long onlineClassId) {
        this.onlineClassId = onlineClassId;
        return this;
    }
        
    public long getStudentId() {
        return this.studentId;
    }

    public StudentComment setStudentId(long studentId) {
        this.studentId = studentId;
        return this;
    }
        
    public int getScores() {
        return this.scores;
    }

    public StudentComment setScores(int scores) {
        this.scores = scores;
        return this;
    }
        
    public String getComment() {
        return this.comment;
    }

    public StudentComment setComment(String comment) {
        this.comment = comment;
        return this;
    }
        
    public java.sql.Timestamp getCreateTime() {
        return this.createTime;
    }

    public StudentComment setCreateTime(java.sql.Timestamp createTime) {
        this.createTime = createTime;
        return this;
    }

}
