package com.vipkid.portal.activity.vo;

import com.alibaba.fastjson.JSONObject;

public class StartHandleVo {

	private Long activityExamID;
	
	private String candidateKey;
	
	private JSONObject pageContent;
	
	private String questionId;
	
	private Integer questionIndex;

	public Long getActivityExamID() {
		return activityExamID;
	}

	public String getCandidateKey() {
		return candidateKey;
	}

	public JSONObject getPageContent() {
		return pageContent;
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

	public void setPageContent(JSONObject pageContent) {
		this.pageContent = pageContent;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public void setQuestionIndex(Integer questionIndex) {
		this.questionIndex = questionIndex;
	}
	
	
}
