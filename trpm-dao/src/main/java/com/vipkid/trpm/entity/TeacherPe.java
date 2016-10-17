package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class TeacherPe extends Entity implements Serializable {

    private static final long serialVersionUID = -8900851024204371273L;
    /*  */
    private int id;
    /*  */
    private long onlineClassId;
    /*  */
    private long teacherId;
    /*  */
    private String teacherName;
    /*  */
    private long peId = -1;
    /*  */
    private String serialNumber;
    /*  */
    private String lessonName;
    /*  */
    private java.sql.Timestamp scheduleTime;
    /*  */
    private String studentName;
    /*  */
    private long studentId;
    /*  */
    private String teacherComment;
    /*  */
    private String peComment;
    /*  */
    private java.sql.Timestamp creationTime;
    /*  */
    private java.sql.Timestamp operatorTime;
    /* 1.passs;2.fail;3.replay,4.practicum2; */
    private int status;

    private String url;

    /* 1:Fail, 2.TBD */
    private int teacherAction;

    private long expiredMillis;

    private int expiredRemind = -1;

    private java.sql.Timestamp operatorStartTime;

    public java.sql.Timestamp getOperatorStartTime() {
        return operatorStartTime;
    }

    public void setOperatorStartTime(java.sql.Timestamp operatorStartTime) {
        this.operatorStartTime = operatorStartTime;
    }

    public int getExpiredRemind() {
        return expiredRemind;
    }

    public void setExpiredRemind(int expiredRemind) {
        this.expiredRemind = expiredRemind;
    }

    public long getExpiredMillis() {
        return expiredMillis;
    }

    public void setExpiredMillis(long expiredMillis) {
        this.expiredMillis = expiredMillis;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return this.id;
    }

    public TeacherPe setId(int id) {
        this.id = id;
        return this;
    }

    public long getOnlineClassId() {
        return this.onlineClassId;
    }

    public TeacherPe setOnlineClassId(long onlineClassId) {
        this.onlineClassId = onlineClassId;
        return this;
    }

    public long getTeacherId() {
        return this.teacherId;
    }

    public TeacherPe setTeacherId(long teacherId) {
        this.teacherId = teacherId;
        return this;
    }

    public String getTeacherName() {
        return this.teacherName;
    }

    public TeacherPe setTeacherName(String teacherName) {
        this.teacherName = teacherName;
        return this;
    }

    public long getPeId() {
        return this.peId;
    }

    public TeacherPe setPeId(long peId) {
        this.peId = peId;
        return this;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public TeacherPe setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public String getLessonName() {
        return this.lessonName;
    }

    public TeacherPe setLessonName(String lessonName) {
        this.lessonName = lessonName;
        return this;
    }

    public java.sql.Timestamp getScheduleTime() {
        return this.scheduleTime;
    }

    public TeacherPe setScheduleTime(java.sql.Timestamp scheduleTime) {
        this.scheduleTime = scheduleTime;
        return this;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public TeacherPe setStudentName(String studentName) {
        this.studentName = studentName;
        return this;
    }

    public long getStudentId() {
        return this.studentId;
    }

    public TeacherPe setStudentId(long studentId) {
        this.studentId = studentId;
        return this;
    }

    public String getTeacherComment() {
        return this.teacherComment;
    }

    public TeacherPe setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
        return this;
    }

    public String getPeComment() {
        return this.peComment;
    }

    public TeacherPe setPeComment(String peComment) {
        this.peComment = peComment;
        return this;
    }

    public java.sql.Timestamp getCreationTime() {
        return this.creationTime;
    }

    public TeacherPe setCreationTime(java.sql.Timestamp creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public java.sql.Timestamp getOperatorTime() {
        return this.operatorTime;
    }

    public TeacherPe setOperatorTime(java.sql.Timestamp operatorTime) {
        this.operatorTime = operatorTime;
        return this;
    }

    public int getStatus() {
        return this.status;
    }

    public TeacherPe setStatus(int status) {
        this.status = status;
        return this;
    }

    public int getTeacherAction() {
        return teacherAction;
    }

    public void setTeacherAction(int teacherAction) {
        this.teacherAction = teacherAction;
    }
}
