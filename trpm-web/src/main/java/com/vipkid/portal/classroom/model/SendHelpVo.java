package com.vipkid.portal.classroom.model;

import com.vipkid.rest.validation.annotation.NotNull;

@NotNull
public class SendHelpVo {

	private Long onlineClassId;
	
	private String scheduleTime;

	public Long getOnlineClassId() {
		return onlineClassId;
	}

	public String getScheduleTime() {
		return scheduleTime;
	}

	public void setOnlineClassId(Long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}

	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	
	
}

