package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

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
	private java.sql.Timestamp createDateTime;
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
	
	private java.sql.Timestamp firstDateTime;
	
	private java.sql.Timestamp lastDateTime;

	//扩展字段
    private Boolean hasComment; //是否已经填写评语
    
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

	public java.sql.Timestamp getCreateDateTime() {
		return this.createDateTime;
	}

	public TeacherComment setCreateDateTime(java.sql.Timestamp createDateTime) {
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

    public java.sql.Timestamp getFirstDateTime() {
        return firstDateTime;
    }

    public TeacherComment setFirstDateTime(java.sql.Timestamp firstDateTime) {
        this.firstDateTime = firstDateTime;
        return this;
    }

    public java.sql.Timestamp getLastDateTime() {
        return lastDateTime;
    }

    public TeacherComment setLastDateTime(java.sql.Timestamp lastDateTime) {
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
