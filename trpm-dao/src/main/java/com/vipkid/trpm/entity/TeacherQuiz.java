package com.vipkid.trpm.entity;

import java.io.Serializable;
import java.util.Date;

import org.community.dao.support.Entity;

public class TeacherQuiz extends Entity implements Serializable {

    /**  
    */ 
    private static final long serialVersionUID = 9141017337423618823L;

    
    private int id;
    
    private int quizScore;
    
    private Date creationTime;
    
    private Date startQuizTime;
    
    private long quizTime;
    
    private Date updateTime;
    
    private long teacherId;
    
    private long updateId;
    
    private int status;
    
    private String andwhere;

    public int getId() {
        return id;
    }

    public TeacherQuiz setId(int id) {
        this.id = id;
        return this;
    }

    public int getQuizScore() {
        return quizScore;
    }

    public TeacherQuiz setQuizScore(int quizScore) {
        this.quizScore = quizScore;
        return this;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public TeacherQuiz setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public TeacherQuiz setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public TeacherQuiz setTeacherId(long teacherId) {
        this.teacherId = teacherId;
        return this;
    }

    public long getUpdateId() {
        return updateId;
    }

    public TeacherQuiz setUpdateId(long updateId) {
        this.updateId = updateId;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public TeacherQuiz setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getAndwhere() {
        return andwhere;
    }

    public TeacherQuiz setAndwhere(String andwhere) {
        this.andwhere = andwhere;
        return this;
    }

    public Date getStartQuizTime() {
        return startQuizTime;
    }

    public TeacherQuiz setStartQuizTime(Date startQuizTime) {
        this.startQuizTime = startQuizTime;
        return this;
    }

    public long getQuizTime() {
        return quizTime;
    }

    public TeacherQuiz setQuizTime(long quizTime) {
        this.quizTime = quizTime;
        return this;
    } 
}
