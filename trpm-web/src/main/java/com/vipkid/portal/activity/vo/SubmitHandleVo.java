package com.vipkid.portal.activity.vo;

public class SubmitHandleVo {

	private Integer status;
	
	private String questionId;
	
	private Integer questionIndex;
	
	private String examResult;

	public Integer getStatus() {
		return status;
	}

	public String getQuestionId() {
		return questionId;
	}

	public Integer getQuestionIndex() {
		return questionIndex;
	}

	public String getExamResult() {
		return examResult;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public void setQuestionIndex(Integer questionIndex) {
		this.questionIndex = questionIndex;
	}

	public void setExamResult(String examResult) {
		this.examResult = examResult;
	}
	
}
