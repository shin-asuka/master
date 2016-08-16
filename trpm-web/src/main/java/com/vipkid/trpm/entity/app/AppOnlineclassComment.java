package com.vipkid.trpm.entity.app;

import java.io.Serializable;

public class AppOnlineclassComment implements Serializable{

    private static final long serialVersionUID = -4158638923962563844L;

    private Long id;
    private Long onlineClassId;
    private Long studentId;
    private Long teacherId;
    private String comment;
    private Long createTime;
    private Integer stars;
    public Long getId() {
        return id;
    }
    public AppOnlineclassComment setId(Long id) {
        this.id = id;
        return this;
    }
    public Long getOnlineClassId() {
        return onlineClassId;
    }
    public AppOnlineclassComment setOnlineClassId(Long onlineClassId) {
        this.onlineClassId = onlineClassId;
        return this;
    }
    public Long getStudentId() {
        return studentId;
    }
    public AppOnlineclassComment setStudentId(Long studentId) {
        this.studentId = studentId;
        return this;
    }
    public Long getTeacherId() {
        return teacherId;
    }
    public AppOnlineclassComment setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
        return this;
    }
    public String getComment() {
        return comment;
    }
    public AppOnlineclassComment setComment(String comment) {
        this.comment = comment;
        return this;
    }
    public Long getCreateTime() {
        return createTime;
    }
    public AppOnlineclassComment setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }
    public Integer getStars() {
        return stars;
    }
    public AppOnlineclassComment setStars(Integer stars) {
        this.stars = stars;
        return this;
    }
    
}
