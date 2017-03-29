package com.vipkid.trpm.entity;
import java.io.Serializable;


/**
 * 
 * @Along
 **/
@SuppressWarnings("serial")
public class ShareRecord implements Serializable {
	
	
	/****/
	private Long id;

	/****/
	private Long teacherId;

	/****/
	private String candidateIp;

	/**已经登陆的是teacherId，没有登陆的需要通过分享link完成测试才能分享**/
	private String candidateKey;

	/****/
	private Long shareLevel;

	/****/
	private Long countClick;

	/****/
	private String examVersion;

	/****/
	private Long linkSourceId;

	/****/
	private java.util.Date shareTime;

	/****/
	private Long activityExamId;


	
	
	public ShareRecord setId(Long id){
		this.id = id;
		return this;
	}

	public Long getId(){
		return this.id;
	}

	public ShareRecord setTeacherId(Long teacherId){
		this.teacherId = teacherId;
		return this;
	}

	public Long getTeacherId(){
		return this.teacherId;
	}

	public ShareRecord setCandidateIp(String candidateIp){
		this.candidateIp = candidateIp;
		return this;
	}

	public String getCandidateIp(){
		return this.candidateIp;
	}

	public ShareRecord setCandidateKey(String candidateKey){
		this.candidateKey = candidateKey;
		return this;
	}

	public String getCandidateKey(){
		return this.candidateKey;
	}

	public ShareRecord setShareLevel(Long shareLevel){
		this.shareLevel = shareLevel;
		return this;
	}

	public Long getShareLevel(){
		return this.shareLevel;
	}

	public ShareRecord setCountClick(Long countClick){
		this.countClick = countClick;
		return this;
	}

	public Long getCountClick(){
		return this.countClick;
	}

	public ShareRecord setExamVersion(String examVersion){
		this.examVersion = examVersion;
		return this;
	}

	public String getExamVersion(){
		return this.examVersion;
	}

	public ShareRecord setLinkSourceId(Long linkSourceId){
		this.linkSourceId = linkSourceId;
		return this;
	}

	public Long getLinkSourceId(){
		return this.linkSourceId;
	}

	public ShareRecord setShareTime(java.util.Date shareTime){
		this.shareTime = shareTime;
		return this;
	}

	public java.util.Date getShareTime(){
		return this.shareTime;
	}

	public ShareRecord setActivityExamId(Long activityExamId){
		this.activityExamId = activityExamId;
		return this;
	}

	public Long getActivityExamId(){
		return this.activityExamId;
	}


}

