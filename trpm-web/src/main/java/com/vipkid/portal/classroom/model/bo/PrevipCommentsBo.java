package com.vipkid.portal.classroom.model.bo;

import com.vipkid.trpm.entity.teachercomment.SubmitTeacherCommentDto;

import java.sql.Timestamp;

/**
 * Created by LP-813 on 2017/2/15.
 */
public class PrevipCommentsBo {
    private static final long serialVersionUID = -1L;
    /*  */
    private Long id;
    /*  */
    private Integer abilityToFollowInstructions;
    /*  */
    private Integer activelyInteraction;
    /*  */
    private Integer clearPronunciation;
    /*  */
    private Timestamp createDateTime;
    /*  */
    private Integer empty;
    /*  */
    private Integer readingSkills;
    /*  */
    private Integer repetition;
    /*  */
    private String reportIssues;
    /*  */
    private Integer spellingAccuracy;
    /*  */
    private Integer stars;
    /*  */
    private String teacherFeedback;
    /*  */
    private String feedbackTranslation;
    /*  */
    private String tipsForOtherTeachers;
    /*  */
    private Integer urgent;
    /*  */
    private Integer performance;
    /*  根据performance是否建议调整 1为建议调整*/
    private Integer performanceAdjust;
    /*  */
    private String currentPerformance;
    /*  */
    private Long onlineClassId;
    /*  */
    private Long studentId;
    /*  */
    private Long teacherId;
    /*  */
    private Long operatorId;
    /*  */
    private String trialLevelResult;

    private Timestamp firstDateTime;

    private Timestamp lastDateTime;

    //扩展字段
    private Boolean hasComment; //是否已经填写评语

    private String courseType;

    private String submitSource;






    /**
     h5页面的原submitdto字段
     //    private String teacherCommentId;//id
     //    private String teacherFeedback;
     //    private String tipsForOtherTeachers;

     //Major特有字段
     //    private String levelOfdifficulty;//performance
     //    private Boolean suggestAdjustment;//performanceAdjust,1为调整

     //trial特有字段
     //    private String trialLevelResult;

     private String classNumber;

     */
    private String classNumber;


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

    private String serialNumber;
    private String scheduleDateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAbilityToFollowInstructions() {
        return abilityToFollowInstructions;
    }

    public void setAbilityToFollowInstructions(Integer abilityToFollowInstructions) {
        this.abilityToFollowInstructions = abilityToFollowInstructions;
    }

    public Integer getActivelyInteraction() {
        return activelyInteraction;
    }

    public void setActivelyInteraction(Integer activelyInteraction) {
        this.activelyInteraction = activelyInteraction;
    }

    public Integer getClearPronunciation() {
        return clearPronunciation;
    }

    public void setClearPronunciation(Integer clearPronunciation) {
        this.clearPronunciation = clearPronunciation;
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

    public Integer getReadingSkills() {
        return readingSkills;
    }

    public void setReadingSkills(Integer readingSkills) {
        this.readingSkills = readingSkills;
    }

    public Integer getRepetition() {
        return repetition;
    }

    public void setRepetition(Integer repetition) {
        this.repetition = repetition;
    }

    public String getReportIssues() {
        return reportIssues;
    }

    public void setReportIssues(String reportIssues) {
        this.reportIssues = reportIssues;
    }

    public Integer getSpellingAccuracy() {
        return spellingAccuracy;
    }

    public void setSpellingAccuracy(Integer spellingAccuracy) {
        this.spellingAccuracy = spellingAccuracy;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }

    public String getFeedbackTranslation() {
        return feedbackTranslation;
    }

    public void setFeedbackTranslation(String feedbackTranslation) {
        this.feedbackTranslation = feedbackTranslation;
    }

    public String getTipsForOtherTeachers() {
        return tipsForOtherTeachers;
    }

    public void setTipsForOtherTeachers(String tipsForOtherTeachers) {
        this.tipsForOtherTeachers = tipsForOtherTeachers;
    }

    public Integer getUrgent() {
        return urgent;
    }

    public void setUrgent(Integer urgent) {
        this.urgent = urgent;
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

    public String getCurrentPerformance() {
        return currentPerformance;
    }

    public void setCurrentPerformance(String currentPerformance) {
        this.currentPerformance = currentPerformance;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getTrialLevelResult() {
        return trialLevelResult;
    }

    public void setTrialLevelResult(String trialLevelResult) {
        this.trialLevelResult = trialLevelResult;
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

    public Boolean getHasComment() {
        return hasComment;
    }

    public void setHasComment(Boolean hasComment) {
        this.hasComment = hasComment;
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

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
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

    public String getScheduleDateTime() {
        return scheduleDateTime;
    }

    public void setScheduleDateTime(String scheduleDateTime) {
        this.scheduleDateTime = scheduleDateTime;
    }


}
