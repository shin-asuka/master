package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class PeakTime extends Entity implements Serializable {

	private static final long serialVersionUID = -1108376803184659965L;
	/*  */
	private long id;
	/*  */
	private java.sql.Timestamp timePoint;
	/*  */
	private String type;

	public long getId() {
		return this.id;
	}

	public PeakTime setId(long id) {
		this.id = id;
		return this;
	}

	public java.sql.Timestamp getTimePoint() {
		return this.timePoint;
	}

	public PeakTime setTimePoint(java.sql.Timestamp timePoint) {
		this.timePoint = timePoint;
		return this;
	}

	public String getType() {
		return this.type;
	}

	public PeakTime setType(String type) {
		this.type = type;
		return this;
	}

}