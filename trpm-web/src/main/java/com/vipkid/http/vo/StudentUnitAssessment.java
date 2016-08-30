package com.vipkid.http.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zouqinghua
 * @date 2016年8月26日  下午3:00:21
 *
 */
public class StudentUnitAssessment implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id; //编号
	private Integer studentId;		// 学生
	private String studentName;		// 学生名称
	private Long teacherId;		// 教师
	private String teacherName;		// 教师名称
	private Integer score;		// 得分
	private String courseSn;		// 教学方案序列号
	private String courseName;		// 教学方案
	private String lessonSn;		// 所属课程序列号
	private Integer onlineClassId;		// 在线课程
	private Date scheduledDateTime;		// 上课时间
	private Integer finishType;		// 结束类型
	private Integer submitStatus;		// 提交状态 {1 已提交，0 未提交}
	private Integer approvalStatus;		// 审核状态{ 1 未审核 2 审核中 3 已审核 4 审核失败}
	private Long auditorId;		// 审核人
	private String auditorName;		// 审核人名称
	
	public StudentUnitAssessment() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStudentId() {
		return studentId;
	}

	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getCourseSn() {
		return courseSn;
	}

	public void setCourseSn(String courseSn) {
		this.courseSn = courseSn;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getLessonSn() {
		return lessonSn;
	}

	public void setLessonSn(String lessonSn) {
		this.lessonSn = lessonSn;
	}

	public Integer getOnlineClassId() {
		return onlineClassId;
	}

	public void setOnlineClassId(Integer onlineClassId) {
		this.onlineClassId = onlineClassId;
	}

	public Date getScheduledDateTime() {
		return scheduledDateTime;
	}

	public void setScheduledDateTime(Date scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}

	public Integer getFinishType() {
		return finishType;
	}

	public void setFinishType(Integer finishType) {
		this.finishType = finishType;
	}

	public Integer getSubmitStatus() {
		return submitStatus;
	}

	public void setSubmitStatus(Integer submitStatus) {
		this.submitStatus = submitStatus;
	}

	public Integer getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public Long getAuditorId() {
		return auditorId;
	}

	public void setAuditorId(Long auditorId) {
		this.auditorId = auditorId;
	}

	public String getAuditorName() {
		return auditorName;
	}

	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}

	
}
