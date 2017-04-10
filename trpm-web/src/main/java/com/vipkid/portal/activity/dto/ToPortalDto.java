package com.vipkid.portal.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class ToPortalDto {

	private Integer activityExamId;

	public Integer getActivityExamId() {
		return activityExamId;
	}

	public void setActivityExamId(Integer activityExamId) {
		this.activityExamId = activityExamId;
	}
}
