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
	private Long questionOrder;

	/****/
	private String questionResult;

	/****/
	private java.util.Date startDateTime;

	/****/
	private java.util.Date endDateTime;

	/****/
	private Long activityExamId;


	
	
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

	public ShareExamDetail setQuestionOrder(Long questionOrder){
		this.questionOrder = questionOrder;
		return this;
	}

	public Long getQuestionOrder(){
		return this.questionOrder;
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


}

