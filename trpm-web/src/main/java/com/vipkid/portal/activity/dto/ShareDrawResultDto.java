package com.vipkid.portal.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class ShareDrawResultDto {
	
	private Long drawRecordId;

	public Long getDrawRecordId() {
		return drawRecordId;
	}

	public void setDrawRecordId(Long drawRecordId) {
		this.drawRecordId = drawRecordId;
	}
	
}
