package com.vipkid.portal.classroom.model;

import com.vipkid.rest.validation.annotation.NotNull;

/**
 * Created by LP-813 on 2017/2/20.
 */
@NotNull
public class PrevipCommentsVo {

    private Long onlineClassId;
    private Long studentId;
    private Integer teacherId;
    private String teacherFeedback;
    private String tipsForOtherTeachers;
    private String vocabularyRetention;
    private String pronunciation;
    private String alphabetSkills;
    private String phonologicalAwareness;
    private String followsInstructions;
    private String participatesActively;
    private String speaksClearly;
    private String mouseTouchpadActivities;
    private String degreeCompletion;
    private String performance;
    private Boolean needParentSupport;
    private String serialNumber;
    private String scheduleDateTime;

    public String getScheduleDateTime() {
        return scheduleDateTime;
    }

    public void setScheduleDateTime(String scheduleDateTime) {
        this.scheduleDateTime = scheduleDateTime;
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

    public String getVocabularyRetention() {
        return vocabularyRetention;
    }

    public void setVocabularyRetention(String vocabularyRetention) {
        this.vocabularyRetention = vocabularyRetention;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getAlphabetSkills() {
        return alphabetSkills;
    }

    public void setAlphabetSkills(String alphabetSkills) {
        this.alphabetSkills = alphabetSkills;
    }

    public String getPhonologicalAwareness() {
        return phonologicalAwareness;
    }

    public void setPhonologicalAwareness(String phonologicalAwareness) {
        this.phonologicalAwareness = phonologicalAwareness;
    }

    public String getFollowsInstructions() {
        return followsInstructions;
    }

    public void setFollowsInstructions(String followsInstructions) {
        this.followsInstructions = followsInstructions;
    }

    public String getParticipatesActively() {
        return participatesActively;
    }

    public void setParticipatesActively(String participatesActively) {
        this.participatesActively = participatesActively;
    }

    public String getSpeaksClearly() {
        return speaksClearly;
    }

    public void setSpeaksClearly(String speaksClearly) {
        this.speaksClearly = speaksClearly;
    }

    public String getMouseTouchpadActivities() {
        return mouseTouchpadActivities;
    }

    public void setMouseTouchpadActivities(String mouseTouchpadActivities) {
        this.mouseTouchpadActivities = mouseTouchpadActivities;
    }

    public String getDegreeCompletion() {
        return degreeCompletion;
    }

    public void setDegreeCompletion(String degreeCompletion) {
        this.degreeCompletion = degreeCompletion;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public Boolean getNeedParentSupport() {
        return needParentSupport;
    }

    public void setNeedParentSupport(Boolean needParentSupport) {
        this.needParentSupport = needParentSupport;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }
}
