package com.vipkid.rest.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class SubmitHandleDto {

	private Integer activityExamId;
	
	private String questionOrder;
	
	private String questionId;
	
	private String questionResult;

	public Integer getActivityExamId() {
		return activityExamId;
	}

	public String getQuestionOrder() {
		return questionOrder;
	}

	public String getQuestionId() {
		return questionId;
	}

	public String getQuestionResult() {
		return questionResult;
	}

	public void setActivityExamId(Integer activityExamId) {
		this.activityExamId = activityExamId;
	}

	public void setQuestionOrder(String questionOrder) {
		this.questionOrder = questionOrder;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public void setQuestionResult(String questionResult) {
		this.questionResult = questionResult;
	}
	
}
