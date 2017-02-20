package com.vipkid.portal.classroom.model;

import com.vipkid.rest.validation.annotation.NotNull;

/**
 * Created by LP-813 on 2017/2/20.
 */
@NotNull
public class MajorCommentsVo {
    private Long id;
    private Long onlineClassId;
    private Integer studentId;
    private Integer teacherId;
    private String teacherFeedback;
    private String tipsForOtherTeachers;
    private Integer performance;
    private Integer performanceAdjust;
    private String serialNumber;
    private String scheduleDateTime;

    public Long getOnlineClassId() {
        return onlineClassId;
    }

    public void setOnlineClassId(Long onlineClassId) {
        this.onlineClassId = onlineClassId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }

    public String getTipsForOtherTeachers() {
        return tipsForOtherTeachers;
    }

    public void setTipsForOtherTeachers(String tipsForOtherTeachers) {
        this.tipsForOtherTeachers = tipsForOtherTeachers;
    }

    public Integer getPerformance() {
        return performance;
    }

    public void setPerformance(Integer performance) {
        this.performance = performance;
    }

    public Integer getPerformanceAdjust() {
        return performanceAdjust;
    }

    public void setPerformanceAdjust(Integer performanceAdjust) {
        this.performanceAdjust = performanceAdjust;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getScheduleDateTime() {
        return scheduleDateTime;
    }

    public void setScheduleDateTime(String scheduleDateTime) {
        this.scheduleDateTime = scheduleDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }
}
