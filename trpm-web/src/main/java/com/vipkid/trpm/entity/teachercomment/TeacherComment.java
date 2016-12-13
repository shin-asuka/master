package com.vipkid.trpm.entity.teachercomment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.community.dao.support.Entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class TeacherComment extends Entity implements Serializable {

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

    public TeacherComment(){

    }

    public TeacherComment(TeacherCommentResult teacherCommentResult) {
        if(teacherCommentResult.getId()!=null){
            id = teacherCommentResult.getId();
        }
        if(teacherCommentResult.getAbilityToFollowInstructions()!=null){
            abilityToFollowInstructions = teacherCommentResult.getAbilityToFollowInstructions();
        }
        if(teacherCommentResult.getActivelyInteraction()!=null){
            activelyInteraction = teacherCommentResult.getActivelyInteraction();
        }
        if(teacherCommentResult.getClearPronunciation()!=null){
            clearPronunciation = teacherCommentResult.getClearPronunciation();
        }

        if (teacherCommentResult.getCreateTime() != null) {
            createDateTime = new Timestamp(teacherCommentResult.getCreateTime().getTime());
        }
        if(teacherCommentResult.getEmpty()!=null){
            empty = teacherCommentResult.getEmpty() ? 1 : 0;
        }
        if(teacherCommentResult.getReadingSkills()!=null){
            readingSkills = teacherCommentResult.getReadingSkills();
        }
        if(teacherCommentResult.getRepetition()!=null){
            repetition = teacherCommentResult.getRepetition();
        }


        reportIssues = teacherCommentResult.getReportIssues();

        if (teacherCommentResult.getSpellingAccuracy() != null) {
            spellingAccuracy = teacherCommentResult.getSpellingAccuracy();
        }

        if(teacherCommentResult.getStars()!=null){
            stars = teacherCommentResult.getStars();
        }

        teacherFeedback = teacherCommentResult.getTeacherFeedback();
        feedbackTranslation = teacherCommentResult.getFeedbackTranslation();
        tipsForOtherTeachers = teacherCommentResult.getTipsForOtherTeachers();

        if(teacherCommentResult.getUrgent()!=null){
            urgent = teacherCommentResult.getUrgent() ? 1 : 0;
        }

        if(teacherCommentResult.getPerformance()!=null){
            performance = teacherCommentResult.getPerformance();
        }

        if(teacherCommentResult.getPerformanceAdjust()!=null){
            performanceAdjust = teacherCommentResult.getPerformanceAdjust();
        }

        if(teacherCommentResult.getCurrentPerformance()!=null){
            currentPerformance = teacherCommentResult.getCurrentPerformance();
        }

        if(teacherCommentResult.getOnlineClassId()!=null){
            onlineClassId = teacherCommentResult.getOnlineClassId();
        }

        if(teacherCommentResult.getStudentId()!=null){
            studentId = teacherCommentResult.getStudentId();
        }

        if(teacherCommentResult.getTeacherId()!=null){
            teacherId = teacherCommentResult.getTeacherId();
        }

        if(teacherCommentResult.getCreateBy()!=null){
            operatorId = teacherCommentResult.getCreateBy();
        }

        trialLevelResult = teacherCommentResult.getTrialLevelResult();

        if (teacherCommentResult.getSubmitDateTime() != null) {
            firstDateTime = new Timestamp(teacherCommentResult.getSubmitDateTime().getTime());
        }
        if (teacherCommentResult.getUpdateTime() != null) {
            lastDateTime = new Timestamp(teacherCommentResult.getUpdateTime().getTime());
        }

        hasComment = StringUtils.isNotBlank(teacherCommentResult.getTeacherFeedback()) ? true : false;

        courseType = teacherCommentResult.getCourseType();
    }

    public TeacherComment(SubmitTeacherCommentInputDto inputDto) {

        id = Integer.valueOf(inputDto.getTeacherCommentId());
        empty = 0;
        teacherFeedback = inputDto.getTeacherFeedback();
        tipsForOtherTeachers = inputDto.getTipsForOtherTeachers();
        if(NumberUtils.isNumber(inputDto.getLevelOfdifficulty())){
            performance = Integer.valueOf(inputDto.getLevelOfdifficulty());
        }
        if(inputDto.isSuggestAdjustment()!=null && inputDto.isSuggestAdjustment()){
            performanceAdjust = 1;
        }
        trialLevelResult = inputDto.getTrialLevelResult();
    }

    public String getSubmitSource() {
        return submitSource;
    }

    public void setSubmitSource(String submitSource) {
        this.submitSource = submitSource;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public long getId() {
        return this.id;
    }

    public TeacherComment setId(long id) {
        this.id = id;
        return this;
    }

    public int getAbilityToFollowInstructions() {
        return this.abilityToFollowInstructions;
    }

    public TeacherComment setAbilityToFollowInstructions(int abilityToFollowInstructions) {
        this.abilityToFollowInstructions = abilityToFollowInstructions;
        return this;
    }

    public int getActivelyInteraction() {
        return this.activelyInteraction;
    }

    public TeacherComment setActivelyInteraction(int activelyInteraction) {
        this.activelyInteraction = activelyInteraction;
        return this;
    }

    public int getClearPronunciation() {
        return this.clearPronunciation;
    }

    public TeacherComment setClearPronunciation(int clearPronunciation) {
        this.clearPronunciation = clearPronunciation;
        return this;
    }

    public Timestamp getCreateDateTime() {
        return this.createDateTime;
    }

    public TeacherComment setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
        return this;
    }

    public int getEmpty() {
        return this.empty;
    }

    public TeacherComment setEmpty(int empty) {
        this.empty = empty;
        return this;
    }

    public int getReadingSkills() {
        return this.readingSkills;
    }

    public TeacherComment setReadingSkills(int readingSkills) {
        this.readingSkills = readingSkills;
        return this;
    }

    public int getRepetition() {
        return this.repetition;
    }

    public TeacherComment setRepetition(int repetition) {
        this.repetition = repetition;
        return this;
    }

    public String getReportIssues() {
        return this.reportIssues;
    }

    public TeacherComment setReportIssues(String reportIssues) {
        this.reportIssues = reportIssues;
        return this;
    }

    public int getSpellingAccuracy() {
        return this.spellingAccuracy;
    }

    public TeacherComment setSpellingAccuracy(int spellingAccuracy) {
        this.spellingAccuracy = spellingAccuracy;
        return this;
    }

    public int getStars() {
        return this.stars;
    }

    public TeacherComment setStars(int stars) {
        this.stars = stars;
        return this;
    }

    public String getTeacherFeedback() {
        return this.teacherFeedback;
    }

    public TeacherComment setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
        return this;
    }

    public String getFeedbackTranslation() {
        return this.feedbackTranslation;
    }

    public TeacherComment setFeedbackTranslation(String feedbackTranslation) {
        this.feedbackTranslation = feedbackTranslation;
        return this;
    }

    public String getTipsForOtherTeachers() {
        return this.tipsForOtherTeachers;
    }

    public TeacherComment setTipsForOtherTeachers(String tipsForOtherTeachers) {
        this.tipsForOtherTeachers = tipsForOtherTeachers;
        return this;
    }

    public int getUrgent() {
        return this.urgent;
    }

    public TeacherComment setUrgent(int urgent) {
        this.urgent = urgent;
        return this;
    }

    public int getPerformance() {
        return this.performance;
    }

    public TeacherComment setPerformance(int performance) {
        this.performance = performance;
        return this;
    }


    public int getPerformanceAdjust() {
        return this.performanceAdjust;
    }

    public TeacherComment setPerformanceAdjust(int performanceAdjust) {
        this.performanceAdjust = performanceAdjust;
        return this;
    }

    public String getCurrentPerformance() {
        return this.currentPerformance;
    }

    public TeacherComment setCurrentPerformance(String currentPerformance) {
        this.currentPerformance = currentPerformance;
        return this;
    }

    public long getOnlineClassId() {
        return this.onlineClassId;
    }

    public TeacherComment setOnlineClassId(long onlineClassId) {
        this.onlineClassId = onlineClassId;
        return this;
    }

    public long getStudentId() {
        return this.studentId;
    }

    public TeacherComment setStudentId(long studentId) {
        this.studentId = studentId;
        return this;
    }

    public long getTeacherId() {
        return this.teacherId;
    }

    public TeacherComment setTeacherId(long teacherId) {
        this.teacherId = teacherId;
        return this;
    }

    public long getOperatorId() {
        return this.operatorId;
    }

    public TeacherComment setOperatorId(long operatorId) {
        this.operatorId = operatorId;
        return this;
    }

    public String getTrialLevelResult() {
        return this.trialLevelResult;
    }

    public TeacherComment setTrialLevelResult(String trialLevelResult) {
        this.trialLevelResult = trialLevelResult;
        return this;
    }

    public Timestamp getFirstDateTime() {
        return firstDateTime;
    }

    public TeacherComment setFirstDateTime(Timestamp firstDateTime) {
        this.firstDateTime = firstDateTime;
        return this;
    }

    public Timestamp getLastDateTime() {
        return lastDateTime;
    }

    public TeacherComment setLastDateTime(Timestamp lastDateTime) {
        this.lastDateTime = lastDateTime;
        return this;
    }

    public Boolean getHasComment() {
        return hasComment;
    }

    public void setHasComment(Boolean hasComment) {
        this.hasComment = hasComment;
    }

}
