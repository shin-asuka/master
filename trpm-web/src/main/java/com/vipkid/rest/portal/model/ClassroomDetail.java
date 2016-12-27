package com.vipkid.rest.portal.model;

/*classrooms页面的接口返回的每节课的数据模型*/
public class ClassroomDetail{
	private Integer id;
	private Long studentId;
	private String studentName;
	private Long lessonId;
	private String lessonName;
	private String lessonSerialNumber;
	private Long teacherId;
	private Integer shortNotice;
	private Long learningCycleId;
	private Long onlineClassId;
	private String scheduledDateTime;
	private String status;
	private Integer isPaidTrail;
	private String finishType;
	private Integer reportType;
	private Integer reportStatus;
	public Long getStudentId() {
		return studentId;
	}
	public String getStudentName() {
		return studentName;
	}
	public Long getLessonId() {
		return lessonId;
	}
	public String getLessonName() {
		return lessonName;
	}
	public String getLessonSerialNumber() {
		return lessonSerialNumber;
	}
	public Long getTeacherId() {
		return teacherId;
	}
	public Integer getShortNotice() {
		return shortNotice;
	}
	public Long getLearningCycleId() {
		return learningCycleId;
	}
	public Long getOnlineClassId() {
		return onlineClassId;
	}
	public String getScheduledDateTime() {
		return scheduledDateTime;
	}
	public String getStatus() {
		return status;
	}
	public Integer getIsPaidTrail() {
		return isPaidTrail;
	}
	public String getFinishType() {
		return finishType;
	}
	public Integer getReportType() {
		return reportType;
	}
	public Integer getReportStatus() {
		return reportStatus;
	}
	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public void setLessonId(Long lessonId) {
		this.lessonId = lessonId;
	}
	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}
	public void setLessonSerialNumber(String lessonSerialNumber) {
		this.lessonSerialNumber = lessonSerialNumber;
	}
	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}
	public void setShortNotice(Integer shortNotice) {
		this.shortNotice = shortNotice;
	}
	public void setLearningCycleId(Long learningCycleId) {
		this.learningCycleId = learningCycleId;
	}
	public void setOnlineClassId(Long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}
	public void setScheduledDateTime(String scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setIsPaidTrail(Integer isPaidTrail) {
		this.isPaidTrail = isPaidTrail;
	}
	public void setFinishType(String finishType) {
		this.finishType = finishType;
	}
	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}
	public void setReportStatus(Integer reportStatus) {
		this.reportStatus = reportStatus;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
}
