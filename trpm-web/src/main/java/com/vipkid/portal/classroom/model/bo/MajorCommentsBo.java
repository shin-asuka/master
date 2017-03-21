package com.vipkid.portal.classroom.model.bo;

import java.sql.Timestamp;

/**
 * Created by LP-813 on 2017/2/15.
 */
public class MajorCommentsBo extends FeedbackBo{
    private static final long serialVersionUID = -1L;
    /*  */
    private Long id;
    /*  */
    private Long onlineClassId;
    private Long studentId;
    private Integer teacherId;
    private Timestamp createDateTime;
    /*  */
    private Integer empty;
    /*  */
    private String teacherFeedback;
    /*  */
    private String tipsForOtherTeachers;
    private Timestamp firstDateTime;
    private Timestamp lastDateTime;
    private Integer performance;
    /*  根据performance是否建议调整 1为建议调整*/
    private Integer performanceAdjust;
    /*  */
    private String courseType;
    private String submitSource;
    private String serialNumber;
    private String scheduleDateTime;
    private String trialLevelResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Integer getEmpty() {
        return empty;
    }

    public void setEmpty(Integer empty) {
        this.empty = empty;
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

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getSubmitSource() {
        return submitSource;
    }

    public void setSubmitSource(String submitSource) {
        this.submitSource = submitSource;
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

    public Timestamp getFirstDateTime() {
        return firstDateTime;
    }

    public void setFirstDateTime(Timestamp firstDateTime) {
        this.firstDateTime = firstDateTime;
    }

    public Timestamp getLastDateTime() {
        return lastDateTime;
    }

    public void setLastDateTime(Timestamp lastDateTime) {
        this.lastDateTime = lastDateTime;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Long getOnlineClassId() {
        return onlineClassId;
    }

    public void setOnlineClassId(Long onlineClassId) {
        this.onlineClassId = onlineClassId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getTrialLevelResult() {
        return trialLevelResult;
    }

    public void setTrialLevelResult(String trialLevelResult) {
        this.trialLevelResult = trialLevelResult;
    }
}
