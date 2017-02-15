package com.vipkid.portal.classroom.model;

import com.vipkid.rest.validation.annotation.Ignore;
import com.vipkid.rest.validation.annotation.NotNull;

@NotNull
public class ClassRoomVo {

	private Long onlineClassId;
	
	private Long studentId;
	
	@Ignore
	private String scheduleTime;
	
	@Ignore
	private String classroom;

	public Long getOnlineClassId() {
		return onlineClassId;
	}

	public void setOnlineClassId(Long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public String getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public String getClassroom() {
		return classroom;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}

}
