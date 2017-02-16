package com.vipkid.rest.dto;

import java.sql.Timestamp;

/**
 * 课程信息DTO定义，用于承载在线教室页面相关的所有数据
 * 定义详细:https://code.vipkid.com.cn/vipkid/teacher-portal/wikis/teacher-classrooms#roomInfo
 * @author zengweilong
 *
 */
public class InfoRoomDto {

	private Long onlineClassId;
	
	private Long studentId;
	
	private String serialNumber;
	
	private String lessonName;
	
	private String studentEnglishName;
	
	private Timestamp scheduleTime;
	
	private Timestamp serverTime;
	
	/*学生创建时间*/
	private Timestamp createTime;
	
	private Boolean isReplay;
	
	private Integer stars;
	
	private String teacherName;
	
	private String classroom;
	
	private String supplierCode;
	
	private String objective;
	
	private String vocabularies;
	
	private String sentencePatterns;
	
	private String sysInfoUrl;
	
	private String microserviceUrl;

	private boolean previp;

	private boolean ua;

	private String courseType;

	public Long getOnlineClassId() {
		return onlineClassId;
	}

	public Long getStudentId() {
		return studentId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getLessonName() {
		return lessonName;
	}

	public String getStudentEnglishName() {
		return studentEnglishName;
	}

	public Timestamp getScheduleTime() {
		return scheduleTime;
	}

	public Timestamp getServerTime() {
		return serverTime;
	}
	
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Boolean getIsReplay() {
		return isReplay;
	}

	public Integer getStars() {
		return stars;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public String getClassroom() {
		return classroom;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public String getObjective() {
		return objective;
	}

	public String getVocabularies() {
		return vocabularies;
	}

	public String getSentencePatterns() {
		return sentencePatterns;
	}

	public void setOnlineClassId(Long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}

	public void setStudentEnglishName(String studentEnglishName) {
		this.studentEnglishName = studentEnglishName;
	}

	public void setScheduleTime(Timestamp scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public void setServerTime(Timestamp serverTime) {
		this.serverTime = serverTime;
	}

	public void setIsReplay(Boolean isReplay) {
		this.isReplay = isReplay;
	}

	public void setStars(Integer stars) {
		this.stars = stars;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public void setVocabularies(String vocabularies) {
		this.vocabularies = vocabularies;
	}

	public void setSentencePatterns(String sentencePatterns) {
		this.sentencePatterns = sentencePatterns;
	}

	public String getSysInfoUrl() {
		return sysInfoUrl;
	}

	public String getMicroserviceUrl() {
		return microserviceUrl;
	}

	public void setSysInfoUrl(String sysInfoUrl) {
		this.sysInfoUrl = sysInfoUrl;
	}

	public void setMicroserviceUrl(String microserviceUrl) {
		this.microserviceUrl = microserviceUrl;
	}

	public boolean isPrevip() {
		return previp;
	}

	public void setPrevip(boolean previp) {
		this.previp = previp;
	}

	public boolean isUa() {
		return ua;
	}

	public void setUa(boolean ua) {
		this.ua = ua;
	}

	public String getCourseType() {
		return courseType;
	}

	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}
}
