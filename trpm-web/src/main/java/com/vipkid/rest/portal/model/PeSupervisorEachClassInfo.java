package com.vipkid.rest.portal.model;

public class PeSupervisorEachClassInfo {
	private long id;
	private long onlineClassId;
	private String lessonName;
	private String serialNumber;
	private String scheduleTime;
	private String teacherName;
	private int status;
	
	public long getId() {
		return id;
	}
	public long getOnlineClassId() {
		return onlineClassId;
	}
	public String getLessonName() {
		return lessonName;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public String getScheduleTime() {
		return scheduleTime;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public int getStatus() {
		return status;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setOnlineClassId(long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}
	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
