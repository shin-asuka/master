package com.vipkid.trpm.entity;
import java.io.Serializable;


/**
 * 
 * @Along
 **/
@SuppressWarnings("serial")
public class ShareExamDetail implements Serializable {
	
	
	/****/
	private Long id;

	/****/
	private String questionId;

	/****/
	private Long questionIndex;

	/****/
	private String questionResult;

	/****/
	private java.util.Date startDateTime;

	/****/
	private java.util.Date endDateTime;

	/****/
	private Long activityExamId;

	/**0 未完成  1已完成**/
	private Integer status;


	
	
	public ShareExamDetail setId(Long id){
		this.id = id;
		return this;
	}

	public Long getId(){
		return this.id;
	}

	public ShareExamDetail setQuestionId(String questionId){
		this.questionId = questionId;
		return this;
	}

	public String getQuestionId(){
		return this.questionId;
	}

	public ShareExamDetail setQuestionIndex(Long questionIndex){
		this.questionIndex = questionIndex;
		return this;
	}

	public Long getQuestionIndex(){
		return this.questionIndex;
	}

	public ShareExamDetail setQuestionResult(String questionResult){
		this.questionResult = questionResult;
		return this;
	}

	public String getQuestionResult(){
		return this.questionResult;
	}

	public ShareExamDetail setStartDateTime(java.util.Date startDateTime){
		this.startDateTime = startDateTime;
		return this;
	}

	public java.util.Date getStartDateTime(){
		return this.startDateTime;
	}

	public ShareExamDetail setEndDateTime(java.util.Date endDateTime){
		this.endDateTime = endDateTime;
		return this;
	}

	public java.util.Date getEndDateTime(){
		return this.endDateTime;
	}

	public ShareExamDetail setActivityExamId(Long activityExamId){
		this.activityExamId = activityExamId;
		return this;
	}

	public Long getActivityExamId(){
		return this.activityExamId;
	}

	public ShareExamDetail setStatus(Integer status){
		this.status = status;
		return this;
	}

	public Integer getStatus(){
		return this.status;
	}


}

