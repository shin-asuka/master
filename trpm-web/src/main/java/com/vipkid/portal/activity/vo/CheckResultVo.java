package com.vipkid.portal.activity.vo;

public class CheckResultVo {
	// 0 未完成  1 已完成  2从未考试
	private Integer status;
	
	private Integer activityExamId;
	
	private String questionId;
	//总考试结果进度
	private String examResult;
	
	private Long questionIndex;

	private String pageContent;

	public Integer getStatus() {
		return status;
	}

	public Integer getActivityExamId() {
		return activityExamId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public String getPageContent() {
		return pageContent;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setActivityExamId(Integer activityExamId) {
		this.activityExamId = activityExamId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
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
