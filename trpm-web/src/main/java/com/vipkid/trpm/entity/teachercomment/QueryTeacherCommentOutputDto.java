package com.vipkid.trpm.entity.teachercomment;

import java.util.Date;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/11/16 下午4:22
 */
public class QueryTeacherCommentOutputDto {

    //本次课程信息
    private String classTime;

    private String topic;

    private String courseType;

    private String courseDisplayName;

    private String classNumber;

    private String studentName;

    //tc信息
    private String teacherCommentId;

    private String teacherFeedback;

    private String tipsForOtherTeachers;

    private String levelOfdifficulty;

    private boolean suggestAdjustment;

    private String trialLevelResult;

    private boolean empty;

    private Date createDate;

    private int stars;

    //preVip的Major课特有字段 START
    private String vocabularyRetention;
    private String pronunciation;
    private String alphabetSkills;
    private String phonologicalAwareness;
    private String followsInstructions;
    private String participatesActively;
    private String speaksClearly;
    private String mouseTouchpadActivities;
    private String degreeCompletion;
    private Boolean needParentSupport;
    //preVip的Major课特有字段 END
    private boolean isPreVip;

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

    public Boolean getNeedParentSupport() {
        return needParentSupport;
    }

    public void setNeedParentSupport(Boolean needParentSupport) {
        this.needParentSupport = needParentSupport;
    }

    public boolean isPreVip() {
        return isPreVip;
    }

    public void setPreVip(boolean preVip) {
        isPreVip = preVip;
    }

    public String getCourseDisplayName() {
        return courseDisplayName;
    }

    public void setCourseDisplayName(String courseDisplayName) {
        this.courseDisplayName = courseDisplayName;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
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


    public String getLevelOfdifficulty() {
        return levelOfdifficulty;
    }

    public void setLevelOfdifficulty(String levelOfdifficulty) {
        this.levelOfdifficulty = levelOfdifficulty;
    }

    public boolean isSuggestAdjustment() {
        return suggestAdjustment;
    }

    public void setSuggestAdjustment(boolean suggestAdjustment) {
        this.suggestAdjustment = suggestAdjustment;
    }

    public String getTrialLevelResult() {
        return trialLevelResult;
    }

    public void setTrialLevelResult(String trialLevelResult) {
        this.trialLevelResult = trialLevelResult;
    }

    public String getTeacherCommentId() {
        return teacherCommentId;
    }

    public void setTeacherCommentId(String teacherCommentId) {
        this.teacherCommentId = teacherCommentId;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
