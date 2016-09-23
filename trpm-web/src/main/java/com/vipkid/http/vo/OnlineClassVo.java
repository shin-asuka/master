package com.vipkid.http.vo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.google.api.client.util.Lists;

public class OnlineClassVo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long teacherId;
	private Long lessonId;
	private String teacherName;
	private String teacherEmail;
	private Date scheduledDateTime;
	private String timezone; //时区
	
	List<Long> idList = Lists.newArrayList();
	
	
	public OnlineClassVo() {
	}

	public List<Long> getIdList() {
		if(idList == null){
			idList = Lists.newArrayList();
		}
		return idList;
	}

	public void setIdList(List<Long> idList) {
		this.idList = idList;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	public Long getLessonId() {
		return lessonId;
	}

	public void setLessonId(Long lessonId) {
		this.lessonId = lessonId;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public String getTeacherEmail() {
		return teacherEmail;
	}

	public void setTeacherEmail(String teacherEmail) {
		this.teacherEmail = teacherEmail;
	}

	public Date getScheduledDateTime() {
		return scheduledDateTime;
	}

	public void setScheduledDateTime(Date scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

}
