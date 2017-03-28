package com.vipkid.portal.activity.vo;


public class CheckResultVo {
	// 0 未完成  1 已完成  2从未考试
	private Integer status;
	
	private Long activityExamId;
	
	private String questionId;
	//总考试结果进度
	private String examResult;
	
	private Long questionIndex;

	public Integer getStatus() {
		return status;
	}

	public Long getActivityExamId() {
		return activityExamId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setActivityExamId(Long activityExamId) {
		this.activityExamId = activityExamId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getExamResult() {
		return examResult;
	}

	public void setExamResult(String examResult) {
		this.examResult = examResult;
	}

	public Long getQuestionIndex() {
		return questionIndex;
	}

	public void setQuestionIndex(Long questionIndex) {
		this.questionIndex = questionIndex;
	}
	
}
