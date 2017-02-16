package com.vipkid.portal.classroom.model;

import com.vipkid.rest.validation.annotation.NotNull;

@NotNull
public class PeCommentsVo extends PesCommentsVo {


	private Integer timeManagementScore;
	
	private Integer accent;
	
	private Integer positive;
	
	private Integer engaged;
	
	private Integer appearance;
	
	private Integer phonics;

	public Integer getTimeManagementScore() {
		return timeManagementScore;
	}

	public Integer getAccent() {
		return accent;
	}

	public Integer getPositive() {
		return positive;
	}

	public Integer getEngaged() {
		return engaged;
	}

	public Integer getAppearance() {
		return appearance;
	}

	public Integer getPhonics() {
		return phonics;
	}

	public void setTimeManagementScore(Integer timeManagementScore) {
		this.timeManagementScore = timeManagementScore;
	}

	public void setAccent(Integer accent) {
		this.accent = accent;
	}

	public void setPositive(Integer positive) {
		this.positive = positive;
	}

	public void setEngaged(Integer engaged) {
		this.engaged = engaged;
	}

	public void setAppearance(Integer appearance) {
		this.appearance = appearance;
	}

	public void setPhonics(Integer phonics) {
		this.phonics = phonics;
	}
	
}
