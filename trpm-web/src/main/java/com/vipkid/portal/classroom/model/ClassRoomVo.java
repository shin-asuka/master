package com.vipkid.portal.classroom.model;

import com.vipkid.rest.validation.annotation.NotNull;

@NotNull
public class ClassRoomVo {

	private Long onlineClassIdLong;
	
	private Long studentId;

	public Long getOnlineClassIdLong() {
		return onlineClassIdLong;
	}

	public void setOnlineClassIdLong(Long onlineClassIdLong) {
		this.onlineClassIdLong = onlineClassIdLong;
	}

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}
}
