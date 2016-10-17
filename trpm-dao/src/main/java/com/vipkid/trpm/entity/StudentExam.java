package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class StudentExam extends Entity implements Serializable {

    private static final long serialVersionUID = 1751955044430874636L;
    private long id;
    private long studentId;
    private long familyId;
    private String examComment;
    private String examLevel;
    private int examScore;
    private String recordUuid;
    private java.sql.Timestamp createDatetime;
    private java.sql.Timestamp endDatetime;
    private int status;
        
    public long getId() {
        return this.id;
    }

    public StudentExam setId(long id) {
        this.id = id;
        return this;
    }
        
    public long getStudentId() {
        return this.studentId;
    }

    public StudentExam setStudentId(long studentId) {
        this.studentId = studentId;
        return this;
    }
        
    public long getFamilyId() {
        return this.familyId;
    }

    public StudentExam setFamilyId(long familyId) {
        this.familyId = familyId;
        return this;
    }
        
    public String getExamComment() {
        return this.examComment;
    }

    public StudentExam setExamComment(String examComment) {
        this.examComment = examComment;
        return this;
    }
        
    public String getExamLevel() {
        return this.examLevel;
    }

    public StudentExam setExamLevel(String examLevel) {
        this.examLevel = examLevel;
        return this;
    }
        
    public int getExamScore() {
        return this.examScore;
    }

    public StudentExam setExamScore(int examScore) {
        this.examScore = examScore;
        return this;
    }
        
    public String getRecordUuid() {
        return this.recordUuid;
    }

    public StudentExam setRecordUuid(String recordUuid) {
        this.recordUuid = recordUuid;
        return this;
    }
        
    public java.sql.Timestamp getCreateDatetime() {
        return this.createDatetime;
    }

    public StudentExam setCreateDatetime(java.sql.Timestamp createDatetime) {
        this.createDatetime = createDatetime;
        return this;
    }
        
    public java.sql.Timestamp getEndDatetime() {
        return this.endDatetime;
    }

    public StudentExam setEndDatetime(java.sql.Timestamp endDatetime) {
        this.endDatetime = endDatetime;
        return this;
    }
        
    public int getStatus() {
        return this.status;
    }

    public StudentExam setStatus(int status) {
        this.status = status;
        return this;
    }

}
