package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class Course extends Entity implements Serializable {

	private static final long serialVersionUID = -6221962091842405166L;
	/*  */
	private long id;
	/*  */
	private float baseClassSalary;
	/*  */
	private java.sql.Timestamp createDateTime;
	/*  */
	private String description;
	/*  */
	private int free;
	/*  */
	private String mode;
	/*  */
	private String name;
	/*  */
	private int needBackupTeacher;
	/*  */
	private int sequential;
	/*  */
	private String serialNumber;
	/*  */
	private String type;
	/*  */
	private long entryUnitId;
	/*  */
	private String showName;
	/* 下级节点类型,e.g. level,unit */
	private String childtype;


	private long unitId;
	private long learningCycleId;

	public long getUnitId() {
		return unitId;
	}

	public void setUnitId(long unitId) {
		this.unitId = unitId;
	}

	public long getLearningCycleId() {
		return learningCycleId;
	}

	public void setLearningCycleId(long learningCycleId) {
		this.learningCycleId = learningCycleId;
	}

	public long getId() {
		return this.id;
	}

	public Course setId(long id) {
		this.id = id;
		return this;
	}

	public float getBaseClassSalary() {
		return this.baseClassSalary;
	}

	public Course setBaseClassSalary(float baseClassSalary) {
		this.baseClassSalary = baseClassSalary;
		return this;
	}

	public java.sql.Timestamp getCreateDateTime() {
		return this.createDateTime;
	}

	public Course setCreateDateTime(java.sql.Timestamp createDateTime) {
		this.createDateTime = createDateTime;
		return this;
	}

	public String getDescription() {
		return this.description;
	}

	public Course setDescription(String description) {
		this.description = description;
		return this;
	}

	public int getFree() {
		return this.free;
	}

	public Course setFree(int free) {
		this.free = free;
		return this;
	}

	public String getMode() {
		return this.mode;
	}

	public Course setMode(String mode) {
		this.mode = mode;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public Course setName(String name) {
		this.name = name;
		return this;
	}

	public int getNeedBackupTeacher() {
		return this.needBackupTeacher;
	}

	public Course setNeedBackupTeacher(int needBackupTeacher) {
		this.needBackupTeacher = needBackupTeacher;
		return this;
	}

	public int getSequential() {
		return this.sequential;
	}

	public Course setSequential(int sequential) {
		this.sequential = sequential;
		return this;
	}

	public String getSerialNumber() {
		return this.serialNumber;
	}

	public Course setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
		return this;
	}

	public String getType() {
		return this.type;
	}

	public Course setType(String type) {
		this.type = type;
		return this;
	}

	public long getEntryUnitId() {
		return this.entryUnitId;
	}

	public Course setEntryUnitId(long entryUnitId) {
		this.entryUnitId = entryUnitId;
		return this;
	}

	public String getShowName() {
		return this.showName;
	}

	public Course setShowName(String showName) {
		this.showName = showName;
		return this;
	}

	public String getChildtype() {
		return this.childtype;
	}

	public Course setChildtype(String childtype) {
		this.childtype = childtype;
		return this;
	}

}
