package com.vipkid.portal.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class ShareHandleDto {

	private Long linkSourceId;
	
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
	
}
