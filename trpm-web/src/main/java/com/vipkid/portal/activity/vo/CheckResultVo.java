package com.vipkid.portal.activity.vo;

import com.alibaba.fastjson.JSONObject;

public class CheckResultVo {
	// 0 未完成  1 已完成  2从未考试
	private Integer status;
	
	private Integer activityExamId;
	
	private String questionId;
	//总考试结果进度
	private String examResult;
	
	private Long questionIndex;

	private JSONObject pageContent;

	public Integer getStatus() {
		return status;
	}

	public Integer getActivityExamId() {
		return activityExamId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public JSONObject getPageContent() {
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

	public void setPageContent(JSONObject pageContent) {
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
