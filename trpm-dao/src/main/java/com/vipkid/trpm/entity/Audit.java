package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class Audit extends Entity implements Serializable {

	private static final long serialVersionUID = -7066188575342690901L;
	/*  */
	private long id;
	/*  */
	private String category;
	/*  */
	private java.sql.Timestamp executeDateTime;
	/*  */
	private String level;
	/*  */
	private String operation;
	/*  */
	private String operator;

	public long getId() {
		return this.id;
	}

	public Audit setId(long id) {
		this.id = id;
		return this;
	}

	public String getCategory() {
		return this.category;
	}

	public Audit setCategory(String category) {
		this.category = category;
		return this;
	}

	public java.sql.Timestamp getExecuteDateTime() {
		return this.executeDateTime;
	}

	public Audit setExecuteDateTime(java.sql.Timestamp executeDateTime) {
		this.executeDateTime = executeDateTime;
		return this;
	}

	public String getLevel() {
		return this.level;
	}

	public Audit setLevel(String level) {
		this.level = level;
		return this;
	}

	public String getOperation() {
		return this.operation;
	}

	public Audit setOperation(String operation) {
		this.operation = operation;
		return this;
	}

	public String getOperator() {
		return this.operator;
	}

	public Audit setOperator(String operator) {
		this.operator = operator;
		return this;
	}

}