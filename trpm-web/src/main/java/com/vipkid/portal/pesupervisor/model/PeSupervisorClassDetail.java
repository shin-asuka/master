package com.vipkid.portal.pesupervisor.model;

public class PeSupervisorClassDetail {
	private long id;
	private long onlineClassId;
	private String lessonName;
	private String serialNumber;
	private String scheduleTime;
	private String teacherName;
	private long teacehrId;
	private long studentId;
	private String studentName;
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
	public long getTeacehrId() {
		return teacehrId;
	}
	public void setTeacehrId(long teacehrId) {
		this.teacehrId = teacehrId;
	}
	public long getStudentId() {
		return studentId;
	}
	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
}
