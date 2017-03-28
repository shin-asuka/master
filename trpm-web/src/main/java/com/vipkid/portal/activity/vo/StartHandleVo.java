package com.vipkid.portal.activity.vo;


public class StartHandleVo {

	private Long activityExamID;
	
	private String candidateKey;
	
	private String questionId;
	
	private Integer questionIndex;
	
	/**
	 * 推荐人 顶层Id
	 */
	private Long refereeId;

	public Long getActivityExamID() {
		return activityExamID;
	}

	public String getCandidateKey() {
		return candidateKey;
	}

	public String getQuestionId() {
		return questionId;
	}

	public Integer getQuestionIndex() {
		return questionIndex;
	}

	public void setActivityExamID(Long activityExamID) {
		this.activityExamID = activityExamID;
	}

	public void setCandidateKey(String candidateKey) {
		this.candidateKey = candidateKey;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public void setQuestionIndex(Integer questionIndex) {
		this.questionIndex = questionIndex;
	}

	public Long getRefereeId() {
		return refereeId;
	}

	public void setRefereeId(Long refereeId) {
		this.refereeId = refereeId;
	}
	
	
}
