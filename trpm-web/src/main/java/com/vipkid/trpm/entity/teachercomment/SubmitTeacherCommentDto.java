package com.vipkid.trpm.entity.teachercomment;

import org.community.dao.support.Entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/12/22 下午7:56
 */
public class SubmitTeacherCommentDto extends Entity implements Serializable {


    private static final long serialVersionUID = -1L;
    /*  */
    private long id;
    /*  */
    private int abilityToFollowInstructions;
    /*  */
    private int activelyInteraction;
    /*  */
    private int clearPronunciation;
    /*  */
    private Timestamp createDateTime;
    /*  */
    private int empty;
    /*  */
    private int readingSkills;
    /*  */
    private int repetition;
    /*  */
    private String reportIssues;
    /*  */
    private int spellingAccuracy;
    /*  */
    private int stars;
    /*  */
    private String teacherFeedback;
    /*  */
    private String feedbackTranslation;
    /*  */
    private String tipsForOtherTeachers;
    /*  */
    private int urgent;
    /*  */
    private int performance;
    /*  根据performance是否建议调整 1为建议调整*/
    private int performanceAdjust;
    /*  */
    private String currentPerformance;
    /*  */
    private long onlineClassId;
    /*  */
    private long studentId;
    /*  */
    private long teacherId;
    /*  */
    private long operatorId;
    /*  */
    private String trialLevelResult;

    private Timestamp firstDateTime;

    private Timestamp lastDateTime;

    //扩展字段
    private Boolean hasComment; //是否已经填写评语

    private String courseType;

    private String submitSource;


    //previp-小孩上课是否需要家长参与
    private boolean needParentSupport;



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

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public boolean isNeedParentSupport() {
        return needParentSupport;
    }

    public void setNeedParentSupport(boolean needParentSupport) {
        this.needParentSupport = needParentSupport;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAbilityToFollowInstructions() {
        return abilityToFollowInstructions;
    }

    public void setAbilityToFollowInstructions(int abilityToFollowInstructions) {
        this.abilityToFollowInstructions = abilityToFollowInstructions;
    }

    public int getActivelyInteraction() {
        return activelyInteraction;
    }

    public void setActivelyInteraction(int activelyInteraction) {
        this.activelyInteraction = activelyInteraction;
    }

    public int getClearPronunciation() {
        return clearPronunciation;
    }

    public void setClearPronunciation(int clearPronunciation) {
        this.clearPronunciation = clearPronunciation;
    }

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
    }

    public int getEmpty() {
        return empty;
    }

    public void setEmpty(int empty) {
        this.empty = empty;
    }

    public int getReadingSkills() {
        return readingSkills;
    }

    public void setReadingSkills(int readingSkills) {
        this.readingSkills = readingSkills;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public String getReportIssues() {
        return reportIssues;
    }

    public void setReportIssues(String reportIssues) {
        this.reportIssues = reportIssues;
    }

    public int getSpellingAccuracy() {
        return spellingAccuracy;
    }

    public void setSpellingAccuracy(int spellingAccuracy) {
        this.spellingAccuracy = spellingAccuracy;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
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

    public int getUrgent() {
        return urgent;
    }

    public void setUrgent(int urgent) {
        this.urgent = urgent;
    }

    public int getPerformance() {
        return performance;
    }

    public void setPerformance(int performance) {
        this.performance = performance;
    }

    public int getPerformanceAdjust() {
        return performanceAdjust;
    }

    public void setPerformanceAdjust(int performanceAdjust) {
        this.performanceAdjust = performanceAdjust;
    }

    public String getCurrentPerformance() {
        return currentPerformance;
    }

    public void setCurrentPerformance(String currentPerformance) {
        this.currentPerformance = currentPerformance;
    }

    public long getOnlineClassId() {
        return onlineClassId;
    }

    public void setOnlineClassId(long onlineClassId) {
        this.onlineClassId = onlineClassId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
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
}
