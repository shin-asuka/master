package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class OnlineClass extends Entity implements Serializable {

	private static final long serialVersionUID = 26363943647614059L;
	/*  */
	private long id;
	/*  */
	private java.sql.Timestamp ableToEnterClassroomDateTime;
	/*  */
	private int archived;
	/*  */
	private int attatchdocumentsucess;
	/*  */
	private int backup;
	/*  */
	private java.sql.Timestamp bookDateTime;
	/*  */
	private int canUndoFinish;
	/*  */
	private String classroom;
	/*  */
	private String comments;
	/*  */
	private int consumeClassHour;
	/*  */
	private String finishType;
	/*  */
	private java.sql.Timestamp lastEditDateTime;
	/*  */
	private int maxStudentNumber;
	/*  */
	private int minStudentNumber;
	/*  */
	private java.sql.Timestamp scheduledDateTime;
	/*  */
	private String serialNumber;
	/*  */
	private String status;
	/*  */
	private java.sql.Timestamp studentEnterClassroomDateTime;
	/*  */
	private java.sql.Timestamp teacherEnterClassroomDateTime;
	/*  */
	private String wxtCourseId;
	/*  */
	private long createrId;
	/*  */
	private long lastEditorId;
	/*  */
	private long lessonId;
	/*  */
	private long teacherId;
	/*  */
	private String dbyDocument;
	/*  */
	private int shortNotice;
	/*  */
	private float unitPrice;
	/*  */
	private int isPaidTrail;

	private String supplierCode;

	private int classType = -1;

	public long getId() {
		return this.id;
	}

	public OnlineClass setId(long id) {
		this.id = id;
		return this;
	}

	public java.sql.Timestamp getAbleToEnterClassroomDateTime() {
		return this.ableToEnterClassroomDateTime;
	}

	public OnlineClass setAbleToEnterClassroomDateTime(java.sql.Timestamp ableToEnterClassroomDateTime) {
		this.ableToEnterClassroomDateTime = ableToEnterClassroomDateTime;
		return this;
	}

	public int getArchived() {
		return this.archived;
	}

	public OnlineClass setArchived(int archived) {
		this.archived = archived;
		return this;
	}

	public int getAttatchdocumentsucess() {
		return this.attatchdocumentsucess;
	}

	public OnlineClass setAttatchdocumentsucess(int attatchdocumentsucess) {
		this.attatchdocumentsucess = attatchdocumentsucess;
		return this;
	}

	public int getBackup() {
		return this.backup;
	}

	public OnlineClass setBackup(int backup) {
		this.backup = backup;
		return this;
	}

	public java.sql.Timestamp getBookDateTime() {
		return this.bookDateTime;
	}

	public OnlineClass setBookDateTime(java.sql.Timestamp bookDateTime) {
		this.bookDateTime = bookDateTime;
		return this;
	}

	public int getCanUndoFinish() {
		return this.canUndoFinish;
	}

	public OnlineClass setCanUndoFinish(int canUndoFinish) {
		this.canUndoFinish = canUndoFinish;
		return this;
	}

	public String getClassroom() {
		return this.classroom;
	}

	public OnlineClass setClassroom(String classroom) {
		this.classroom = classroom;
		return this;
	}

	public String getComments() {
		return this.comments;
	}

	public OnlineClass setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public int getConsumeClassHour() {
		return this.consumeClassHour;
	}

	public OnlineClass setConsumeClassHour(int consumeClassHour) {
		this.consumeClassHour = consumeClassHour;
		return this;
	}

	public String getFinishType() {
		return this.finishType;
	}

	public OnlineClass setFinishType(String finishType) {
		this.finishType = finishType;
		return this;
	}

	public java.sql.Timestamp getLastEditDateTime() {
		return this.lastEditDateTime;
	}

	public OnlineClass setLastEditDateTime(java.sql.Timestamp lastEditDateTime) {
		this.lastEditDateTime = lastEditDateTime;
		return this;
	}

	public int getMaxStudentNumber() {
		return this.maxStudentNumber;
	}

	public OnlineClass setMaxStudentNumber(int maxStudentNumber) {
		this.maxStudentNumber = maxStudentNumber;
		return this;
	}

	public int getMinStudentNumber() {
		return this.minStudentNumber;
	}

	public OnlineClass setMinStudentNumber(int minStudentNumber) {
		this.minStudentNumber = minStudentNumber;
		return this;
	}

	public java.sql.Timestamp getScheduledDateTime() {
		return this.scheduledDateTime;
	}

	public OnlineClass setScheduledDateTime(java.sql.Timestamp scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
		return this;
	}

	public String getSerialNumber() {
		return this.serialNumber;
	}

	public OnlineClass setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
		return this;
	}

	public String getStatus() {
		return this.status;
	}

	public OnlineClass setStatus(String status) {
		this.status = status;
		return this;
	}

	public java.sql.Timestamp getStudentEnterClassroomDateTime() {
		return this.studentEnterClassroomDateTime;
	}

	public OnlineClass setStudentEnterClassroomDateTime(java.sql.Timestamp studentEnterClassroomDateTime) {
		this.studentEnterClassroomDateTime = studentEnterClassroomDateTime;
		return this;
	}

	public java.sql.Timestamp getTeacherEnterClassroomDateTime() {
		return this.teacherEnterClassroomDateTime;
	}

	public OnlineClass setTeacherEnterClassroomDateTime(java.sql.Timestamp teacherEnterClassroomDateTime) {
		this.teacherEnterClassroomDateTime = teacherEnterClassroomDateTime;
		return this;
	}

	public String getWxtCourseId() {
		return this.wxtCourseId;
	}

	public OnlineClass setWxtCourseId(String wxtCourseId) {
		this.wxtCourseId = wxtCourseId;
		return this;
	}

	public long getCreaterId() {
		return this.createrId;
	}

	public OnlineClass setCreaterId(long createrId) {
		this.createrId = createrId;
		return this;
	}

	public long getLastEditorId() {
		return this.lastEditorId;
	}

	public OnlineClass setLastEditorId(long lastEditorId) {
		this.lastEditorId = lastEditorId;
		return this;
	}

	public long getLessonId() {
		return this.lessonId;
	}

	public OnlineClass setLessonId(long lessonId) {
		this.lessonId = lessonId;
		return this;
	}

	public long getTeacherId() {
		return this.teacherId;
	}

	public OnlineClass setTeacherId(long teacherId) {
		this.teacherId = teacherId;
		return this;
	}

	public String getDbyDocument() {
		return this.dbyDocument;
	}

	public OnlineClass setDbyDocument(String dbyDocument) {
		this.dbyDocument = dbyDocument;
		return this;
	}

	public int getShortNotice() {
		return this.shortNotice;
	}

	public OnlineClass setShortNotice(int shortNotice) {
		this.shortNotice = shortNotice;
		return this;
	}

	public float getUnitPrice() {
		return this.unitPrice;
	}

	public OnlineClass setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice;
		return this;
	}

	public int getIsPaidTrail() {
		return this.isPaidTrail;
	}

	public OnlineClass setIsPaidTrail(int isPaidTrail) {
		this.isPaidTrail = isPaidTrail;
		return this;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public int getClassType() {
		return classType;
	}

	public void setClassType(int classType) {
		this.classType = classType;
	}

}