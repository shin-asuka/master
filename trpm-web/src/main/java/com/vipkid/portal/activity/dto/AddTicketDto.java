package com.vipkid.portal.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class AddTicketDto {

	private Long onlineClassId;

	public Long getOnlineClassId() {
		return onlineClassId;
	}

	public void setOnlineClassId(Long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}
}
