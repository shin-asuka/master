package com.vipkid.rest;

import java.util.Date;

/**
 * Created by LP-813 on 2016/11/28.
 */
public class OnlineClassVo {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long teacherId;
    private Integer studentId;
    private Long lessonId;
    private String teacherName;
    private String teacherEmail;
    private Date scheduledDateTime;
    private Date submitDateTime;
    private String lessonSn;
    private String course;
    private String courseName;
    private Integer courseId;
    private String finishType;
    private String studentName;
    private String studentEnglishName;
    private String timezone; //时区
    private Integer auditorId;
    private String auditorName;
    private Date auditTime;
    private Integer refillinOpId;
    private String refillinOpName;
    private String from;
    private String to;
    private String extra;
    private Integer total;
    private Integer hasAudited;
    private Date updateTime;

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

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public Date getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(Date scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public Date getSubmitDateTime() {
        return submitDateTime;
    }

    public void setSubmitDateTime(Date submitDateTime) {
        this.submitDateTime = submitDateTime;
    }

    public String getLessonSn() {
        return lessonSn;
    }

    public void setLessonSn(String lessonSn) {
        this.lessonSn = lessonSn;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getFinishType() {
        return finishType;
    }

    public void setFinishType(String finishType) {
        this.finishType = finishType;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEnglishName() {
        return studentEnglishName;
    }

    public void setStudentEnglishName(String studentEnglishName) {
        this.studentEnglishName = studentEnglishName;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Integer auditorId) {
        this.auditorId = auditorId;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Integer getRefillinOpId() {
        return refillinOpId;
    }

    public void setRefillinOpId(Integer refillinOpId) {
        this.refillinOpId = refillinOpId;
    }

    public String getRefillinOpName() {
        return refillinOpName;
    }

    public void setRefillinOpName(String refillinOpName) {
        this.refillinOpName = refillinOpName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getHasAudited() {
        return hasAudited;
    }

    public void setHasAudited(Integer hasAudited) {
        this.hasAudited = hasAudited;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
