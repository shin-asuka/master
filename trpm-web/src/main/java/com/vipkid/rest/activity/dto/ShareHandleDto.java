package com.vipkid.rest.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class ShareHandleDto {

	private Integer linkSourceId;
	
	private String candidateKey;

	public Integer getLinkSourceId() {
		return linkSourceId;
	}

	public String getCandidateKey() {
		return candidateKey;
	}

	public void setLinkSourceId(Integer linkSourceId) {
		this.linkSourceId = linkSourceId;
	}

	public void setCandidateKey(String candidateKey) {
		this.candidateKey = candidateKey;
	}
	
}
