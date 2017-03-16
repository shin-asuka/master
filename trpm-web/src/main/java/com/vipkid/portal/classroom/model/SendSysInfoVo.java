package com.vipkid.portal.classroom.model;

import com.vipkid.rest.validation.annotation.NotNull;

@NotNull
public class SendSysInfoVo {

	private String classroom;
	
	private Long onlineClassId;

	public String getClassroom() {
		return classroom;
	}

	public Long getOnlineClassId() {
		return onlineClassId;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}

	public void setOnlineClassId(Long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}
	
}
