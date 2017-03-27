package com.vipkid.portal.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class SubmitHandleDto {

	private Long activityExamId;
	
	private Integer questionIndex;
	
	private String questionId;
	
	private String questionResult;

	public Long getActivityExamId() {
		return activityExamId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public String getQuestionResult() {
		return questionResult;
	}

	public Integer getQuestionIndex() {
		return questionIndex;
	}

	public void setQuestionIndex(Integer questionIndex) {
		this.questionIndex = questionIndex;
	}

	public void setActivityExamId(Long activityExamId) {
		this.activityExamId = activityExamId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public void setQuestionResult(String questionResult) {
		this.questionResult = questionResult;
	}
	
}
