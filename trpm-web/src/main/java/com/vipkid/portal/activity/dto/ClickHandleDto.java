package com.vipkid.portal.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.Ignore;
import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class ClickHandleDto {

	private Long linkSourceId;
	
	@Ignore
	private Long shareRecordId;

	public Long getLinkSourceId() {
		return linkSourceId;
	}

	public Long getShareRecordId() {
		return shareRecordId;
	}

	public void setLinkSourceId(Long linkSourceId) {
		this.linkSourceId = linkSourceId;
	}

	public void setShareRecordId(Long shareRecordId) {
		this.shareRecordId = shareRecordId;
	}
	
}
