package com.vipkid.portal.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.Ignore;
import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class StartHandleDto {

	private Long shareRecordId;
	
	private Long linkSourceId;
	
	@Ignore
	private String candidateKey;

	public Long getShareRecordId() {
		return shareRecordId;
	}

	public String getCandidateKey() {
		return candidateKey;
	}

	public void setShareRecordId(Long shareRecordId) {
		this.shareRecordId = shareRecordId;
	}

	public void setCandidateKey(String candidateKey) {
		this.candidateKey = candidateKey;
	}

	public Long getLinkSourceId() {
		return linkSourceId;
	}

	public void setLinkSourceId(Long linkSourceId) {
		this.linkSourceId = linkSourceId;
	}
	
}
