package com.vipkid.portal.classroom.model;

import com.vipkid.rest.validation.annotation.NotNull;

@NotNull
public class SendHelpVo {

	private Long onlineClassId;
	
	private Long scheduleTime;

	public Long getOnlineClassId() {
		return onlineClassId;
	}

	public Long getScheduleTime() {
		return scheduleTime;
	}

	public void setOnlineClassId(Long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}

	public void setScheduleTime(Long scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	
	
}

