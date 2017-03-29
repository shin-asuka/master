package com.vipkid.portal.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class ShareHandleDto {

	private Long linkSourceId;
	
	private Long activityExamId;
	
	private String candidateKey;

	public Long getLinkSourceId() {
		return linkSourceId;
	}

	public String getCandidateKey() {
		return candidateKey;
	}

	public void setLinkSourceId(Long linkSourceId) {
		this.linkSourceId = linkSourceId;
	}

	public void setCandidateKey(String candidateKey) {
		this.candidateKey = candidateKey;
	}

	public Long getActivityExamId() {
		return activityExamId;
	}

	public void setActivityExamId(Long activityExamId) {
		this.activityExamId = activityExamId;
	}
	
}
