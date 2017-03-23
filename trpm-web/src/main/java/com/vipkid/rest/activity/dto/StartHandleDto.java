package com.vipkid.rest.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.Ignore;
import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class StartHandleDto {

	private Integer shareRecordId;
	
	@Ignore
	private String candidateKey;

	public Integer getShareRecordId() {
		return shareRecordId;
	}

	public String getCandidateKey() {
		return candidateKey;
	}

	public void setShareRecordId(Integer shareRecordId) {
		this.shareRecordId = shareRecordId;
	}

	public void setCandidateKey(String candidateKey) {
		this.candidateKey = candidateKey;
	}
	
}
