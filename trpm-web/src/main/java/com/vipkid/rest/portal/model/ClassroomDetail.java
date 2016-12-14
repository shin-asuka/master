package com.vipkid.rest.portal.model;

/*classrooms页面的接口返回的每节课的数据模型*/
public class ClassroomDetail{
	private long studentId;
	private String studentName;
	private long lessonId;
	private String lessonName;
	private String lessonSerialNumber;
	private long teacherId;
	private int shortNotice;
	private long learningCycleId;
	private long onlineClassId;
	private String scheduledDateTime;
	private String status;
	private int isPaidTrail;
	private String finishType;
	private int reportType;
	private int reportStatus;
	public long getStudentId() {
		return studentId;
	}
	public String getStudentName() {
		return studentName;
	}
	public long getLessonId() {
		return lessonId;
	}
	public String getLessonName() {
		return lessonName;
	}
	public String getLessonSerialNumber() {
		return lessonSerialNumber;
	}
	public long getTeacherId() {
		return teacherId;
	}
	public int getShortNotice() {
		return shortNotice;
	}
	public long getLearningCycleId() {
		return learningCycleId;
	}
	public long getOnlineClassId() {
		return onlineClassId;
	}
	public String getScheduledDateTime() {
		return scheduledDateTime;
	}
	public String getStatus() {
		return status;
	}
	public int getIsPaidTrail() {
		return isPaidTrail;
	}
	public String getFinishType() {
		return finishType;
	}
	public int getReportType() {
		return reportType;
	}
	public int getReportStatus() {
		return reportStatus;
	}
	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public void setLessonId(long lessonId) {
		this.lessonId = lessonId;
	}
	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}
	public void setLessonSerialNumber(String lessonSerialNumber) {
		this.lessonSerialNumber = lessonSerialNumber;
	}
	public void setTeacherId(long teacherId) {
		this.teacherId = teacherId;
	}
	public void setShortNotice(int shortNotice) {
		this.shortNotice = shortNotice;
	}
	public void setLearningCycleId(long learningCycleId) {
		this.learningCycleId = learningCycleId;
	}
	public void setOnlineClassId(long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}
	public void setScheduledDateTime(String scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setIsPaidTrail(int isPaidTrail) {
		this.isPaidTrail = isPaidTrail;
	}
	public void setFinishType(String finishType) {
		this.finishType = finishType;
	}
	public void setReportType(int reportType) {
		this.reportType = reportType;
	}
	public void setReportStatus(int reportStatus) {
		this.reportStatus = reportStatus;
	}
}
