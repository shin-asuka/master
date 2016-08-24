package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class Lesson extends Entity implements Serializable {

	private static final long serialVersionUID = -9129149437992455245L;
	/*  */
	private long id;
	/*  */
	private String dbyDocument;
	/*  */
	private String domain;
	/*  */
	private String goal;
	/*  */
	private String lssTarget;
	/*  */
	private String mathTarget;
	/*  */
	private String name;
	/*  */
	private String objective;
	/*  */
	private String reviewTarget;
	/*  */
	private String sentencePatterns;
	/*  */
	private int sequence;
	/*  */
	private String serialNumber;
	/*  */
	private String topic;
	/*  */
	private String vocabularies;
	/*  */
	private long learningCycleId;
	/*  */
	private String number;

	/**
	 * 是否生成UA报告
	 */
	private Integer isUnitAssessment; 
	
	public long getId() {
		return this.id;
	}

	public Lesson setId(long id) {
		this.id = id;
		return this;
	}

	public String getDbyDocument() {
		return this.dbyDocument;
	}

	public Lesson setDbyDocument(String dbyDocument) {
		this.dbyDocument = dbyDocument;
		return this;
	}

	public String getDomain() {
		return this.domain;
	}

	public Lesson setDomain(String domain) {
		this.domain = domain;
		return this;
	}

	public String getGoal() {
		return this.goal;
	}

	public Lesson setGoal(String goal) {
		this.goal = goal;
		return this;
	}

	public String getLssTarget() {
		return this.lssTarget;
	}

	public Lesson setLssTarget(String lssTarget) {
		this.lssTarget = lssTarget;
		return this;
	}

	public String getMathTarget() {
		return this.mathTarget;
	}

	public Lesson setMathTarget(String mathTarget) {
		this.mathTarget = mathTarget;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public Lesson setName(String name) {
		this.name = name;
		return this;
	}

	public String getObjective() {
		return this.objective;
	}

	public Lesson setObjective(String objective) {
		this.objective = objective;
		return this;
	}

	public String getReviewTarget() {
		return this.reviewTarget;
	}

	public Lesson setReviewTarget(String reviewTarget) {
		this.reviewTarget = reviewTarget;
		return this;
	}

	public String getSentencePatterns() {
		return this.sentencePatterns;
	}

	public Lesson setSentencePatterns(String sentencePatterns) {
		this.sentencePatterns = sentencePatterns;
		return this;
	}

	public int getSequence() {
		return this.sequence;
	}

	public Lesson setSequence(int sequence) {
		this.sequence = sequence;
		return this;
	}

	public String getSerialNumber() {
		return this.serialNumber;
	}

	public Lesson setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
		return this;
	}

	public String getTopic() {
		return this.topic;
	}

	public Lesson setTopic(String topic) {
		this.topic = topic;
		return this;
	}

	public String getVocabularies() {
		return this.vocabularies;
	}

	public Lesson setVocabularies(String vocabularies) {
		this.vocabularies = vocabularies;
		return this;
	}

	public long getLearningCycleId() {
		return this.learningCycleId;
	}

	public Lesson setLearningCycleId(long learningCycleId) {
		this.learningCycleId = learningCycleId;
		return this;
	}

	public String getNumber() {
		return this.number;
	}

	public Lesson setNumber(String number) {
		this.number = number;
		return this;
	}

	public Integer getIsUnitAssessment() {
		return isUnitAssessment;
	}

	public void setIsUnitAssessment(Integer isUnitAssessment) {
		this.isUnitAssessment = isUnitAssessment;
	}

}