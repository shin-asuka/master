package com.vipkid.rest.portal.model;

/**
 * Created by LP-813 on 2017/1/10.
 */
public class StudentComment {

    private Long id;
    private Integer grade;
    private String comment;
    private String lessonSn;
    private Long onlineClassId;
    private Long hasTranslated;
    private String translation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLessonSn() {
        return lessonSn;
    }

    public void setLessonSn(String lessonSn) {
        this.lessonSn = lessonSn;
    }

    public Long getOnlineClassId() {
        return onlineClassId;
    }

    public void setOnlineClassId(Long onlineClassId) {
        this.onlineClassId = onlineClassId;
    }

    public Long getHasTranslated() {
        return hasTranslated;
    }

    public void setHasTranslated(Long hasTranslated) {
        this.hasTranslated = hasTranslated;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
