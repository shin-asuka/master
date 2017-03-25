package com.vipkid.portal.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class SubmitHandleDto {

	private Integer activityExamId;
	
	private String questionIndex;
	
	private String questionId;
	
	private String questionResult;

	public Integer getActivityExamId() {
		return activityExamId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public String getQuestionResult() {
		return questionResult;
	}

	public String getQuestionIndex() {
		return questionIndex;
	}

	public void setQuestionIndex(String questionIndex) {
		this.questionIndex = questionIndex;
	}

	public void setActivityExamId(Integer activityExamId) {
		this.activityExamId = activityExamId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public void setQuestionResult(String questionResult) {
		this.questionResult = questionResult;
	}
	
}
