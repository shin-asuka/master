package com.vipkid.rest.portal.vo;

import java.util.ArrayList;

/**
 * Created by LP-813 on 2017/1/11.
 */
public class StudentCommentVo {

    private Integer id;
    private Integer	teacher_id;
    private Integer student_id;
    private String content;
    private Integer clt_id;
    private Integer course_id;
    private Integer class_id;
    private String lessonSn;
    private Integer status;
    private Integer rating;
    private String create_time;
    private String studentName;
    private String studentAvatar;
    private String scheduleDateTime;
    private String onlineClassName;
    private String transaltion;
    private String ocToken;
    private String tags[];
    private String tagsEn[];


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(Integer teacher_id) {
        this.teacher_id = teacher_id;
    }

    public Integer getStudent_id() {
        return student_id;
    }

    public void setStudent_id(Integer student_id) {
        this.student_id = student_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getClt_id() {
        return clt_id;
    }

    public void setClt_id(Integer clt_id) {
        this.clt_id = clt_id;
    }

    public Integer getCourse_id() {
        return course_id;
    }

    public void setCourse_id(Integer course_id) {
        this.course_id = course_id;
    }

    public Integer getClass_id() {
        return class_id;
    }

    public void setClass_id(Integer class_id) {
        this.class_id = class_id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getScheduleDateTime() {
        return scheduleDateTime;
    }

    public void setScheduleDateTime(String scheduleDateTime) {
        this.scheduleDateTime = scheduleDateTime;
    }

    public String getOnlineClassName() {
        return onlineClassName;
    }

    public void setOnlineClassName(String onlineClassName) {
        this.onlineClassName = onlineClassName;
    }

    public String getStudentAvatar() {
        return studentAvatar;
    }

    public void setStudentAvatar(String studentAvatar) {
        this.studentAvatar = studentAvatar;
    }

    public String getTransaltion() {
        return transaltion;
    }

    public void setTransaltion(String transaltion) {
        this.transaltion = transaltion;
    }

    public String getLessonSn() {
        return lessonSn;
    }

    public void setLessonSn(String lessonSn) {
        this.lessonSn = lessonSn;
    }

    public String getOcToken() {
        return ocToken;
    }

    public void setOcToken(String ocToken) {
        this.ocToken = ocToken;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getTagsEn() {
        return tagsEn;
    }

    public void setTagsEn(String[] tagsEn) {
        this.tagsEn = tagsEn;
    }
}
