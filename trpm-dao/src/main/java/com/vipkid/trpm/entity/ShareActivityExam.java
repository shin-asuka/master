package com.vipkid.trpm.entity;
import java.io.Serializable;


/**
 * 
 * @Along
 **/
@SuppressWarnings("serial")
public class ShareActivityExam implements Serializable {
	
	
	/****/
	private Long id;

	/****/
	private String examVersion;

	/****/
	private String examResult;

	/****/
	private java.util.Date startDateTime;

	/****/
	private java.util.Date endDateTime;

	/****/
	private String candidateIp;

	/****/
	private String candidateKey;

	/****/
	private Long teacherId;

	/****/
	private Long linkSourceId;

	/****/
	private Long shareRecordId;

	/**0,未完成  1已完成**/
	private Integer status;
	
	/** 点击次数 */
	private Integer toPortal;


	
	
	public ShareActivityExam setId(Long id){
		this.id = id;
		return this;
	}

	public Long getId(){
		return this.id;
	}

	public ShareActivityExam setExamVersion(String examVersion){
		this.examVersion = examVersion;
		return this;
	}

	public String getExamVersion(){
		return this.examVersion;
	}

	public ShareActivityExam setExamResult(String examResult){
		this.examResult = examResult;
		return this;
	}

	public String getExamResult(){
		return this.examResult;
	}

	public ShareActivityExam setStartDateTime(java.util.Date startDateTime){
		this.startDateTime = startDateTime;
		return this;
	}

	public java.util.Date getStartDateTime(){
		return this.startDateTime;
	}

	public ShareActivityExam setEndDateTime(java.util.Date endDateTime){
		this.endDateTime = endDateTime;
		return this;
	}

	public java.util.Date getEndDateTime(){
		return this.endDateTime;
	}

	public ShareActivityExam setCandidateIp(String candidateIp){
		this.candidateIp = candidateIp;
		return this;
	}

	public String getCandidateIp(){
		return this.candidateIp;
	}

	public ShareActivityExam setCandidateKey(String candidateKey){
		this.candidateKey = candidateKey;
		return this;
	}

	public String getCandidateKey(){
		return this.candidateKey;
	}

	public ShareActivityExam setTeacherId(Long teacherId){
		this.teacherId = teacherId;
		return this;
	}

	public Long getTeacherId(){
		return this.teacherId;
	}

	public ShareActivityExam setLinkSourceId(Long linkSourceId){
		this.linkSourceId = linkSourceId;
		return this;
	}

	public Long getLinkSourceId(){
		return this.linkSourceId;
	}

	public ShareActivityExam setShareRecordId(Long shareRecordId){
		this.shareRecordId = shareRecordId;
		return this;
	}

	public Long getShareRecordId(){
		return this.shareRecordId;
	}

	public ShareActivityExam setStatus(Integer status){
		this.status = status;
		return this;
	}

	public Integer getStatus(){
		return this.status;
	}

	public Integer getToPortal() {
		return toPortal;
	}

	public void setToPortal(Integer toPortal) {
		this.toPortal = toPortal;
	}
}

