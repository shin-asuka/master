package com.vipkid.trpm.entity;

import java.io.Serializable;

public class AppOnlineClass implements Serializable{

    private static final long serialVersionUID = 7990631628179351716L;
    
    private Long id;
    private Long teacherId;
    private Long studentId;
    private Long startTime; 
    private Long endTime; 
    private String status;
    private String statusInfo;
    private Long courseId;
    private String courseName;
    private String courseType;
    private Long lessonId;
    private String lessonName;
    private String serialNumber;
    private String objective;
    private String sentencePatterns;
    private String topic;
    private String vocabularies;
    private String goal;
    private String grammar;
    private Integer hasFeedback;
    private Integer unitAssessmentStatus;
    
    public Long getId() {
        return id;
    }
    public AppOnlineClass setId(Long id) {
        this.id = id;
        return this;
    }
    public Long getTeacherId() {
        return teacherId;
    }
    public AppOnlineClass setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
        return this;
    }
    public Long getStudentId() {
        return studentId;
    }
    public AppOnlineClass setStudentId(Long studentId) {
        this.studentId = studentId;
        return this;
    }
    public Long getStartTime() {
        return startTime;
    }
    public AppOnlineClass setStartTime(Long startTime) {
        this.startTime = startTime;
        return this;
    }
    public Long getEndTime() {
        return endTime;
    }
    public AppOnlineClass setEndTime(Long endTime) {
        this.endTime = endTime;
        return this;
    }
    public String getStatus() {
        return status;
    }
    public AppOnlineClass setStatus(String status) {
        this.status = status;
        return this;
    }
    public String getStatusInfo() {
        return statusInfo;
    }
    public AppOnlineClass setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
        return this;
    }
    public Long getCourseId() {
        return courseId;
    }
    public AppOnlineClass setCourseId(Long courseId) {
        this.courseId = courseId;
        return this;
    }
    public String getCourseName() {
        return courseName;
    }
    public AppOnlineClass setCourseName(String courseName) {
        this.courseName = courseName;
        return this;
    }
    public String getCourseType() {
        return courseType;
    }
    public AppOnlineClass setCourseType(String courseType) {
        this.courseType = courseType;
        return this;
    }
    public Long getLessonId() {
        return lessonId;
    }
    public AppOnlineClass setLessonId(Long lessonId) {
        this.lessonId = lessonId;
        return this;
    }
    public String getLessonName() {
        return lessonName;
    }
    public AppOnlineClass setLessonName(String lessonName) {
        this.lessonName = lessonName;
        return this;
    }
    public String getSerialNumber() {
        return serialNumber;
    }
    public AppOnlineClass setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }
    public String getObjective() {
        return objective;
    }
    public AppOnlineClass setObjective(String objective) {
        this.objective = objective;
        return this;
    }
    public String getSentencePatterns() {
        return sentencePatterns;
    }
    public AppOnlineClass setSentencePatterns(String sentencePatterns) {
        this.sentencePatterns = sentencePatterns;
        return this;
    }
    public String getTopic() {
        return topic;
    }
    public AppOnlineClass setTopic(String topic) {
        this.topic = topic;
        return this;
    }
    public String getVocabularies() {
        return vocabularies;
    }
    public AppOnlineClass setVocabularies(String vocabularies) {
        this.vocabularies = vocabularies;
        return this;
    }
    public String getGoal() {
        return goal;
    }
    public AppOnlineClass setGoal(String goal) {
        this.goal = goal;
        return this;
    }
    public String getGrammar() {
        return grammar;
    }
    public AppOnlineClass setGrammar(String grammar) {
        this.grammar = grammar;
        return this;
    }
    public Integer getHasFeedback() {
        return hasFeedback;
    }
    public AppOnlineClass setHasFeedback(Integer hasFeedback) {
        this.hasFeedback = hasFeedback;
        return this;
    }
    public Integer getUnitAssessmentStatus() {
        return unitAssessmentStatus;
    }
    public AppOnlineClass setUnitAssessmentStatus(Integer unitAssessmentStatus) {
        this.unitAssessmentStatus = unitAssessmentStatus;
        return this;
    }    
}
