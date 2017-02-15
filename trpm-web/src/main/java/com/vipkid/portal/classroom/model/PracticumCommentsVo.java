package com.vipkid.portal.classroom.model;

import com.vipkid.rest.validation.annotation.NotNull;

@NotNull
public class PracticumCommentsVo {

	private Integer id;
	
	private Integer onlineClassId;
	
	private Integer delayDays;
	
	private Integer englishLanguageScore;
	
	private Integer grade6TeachingExperience;
	
	private Integer highSchoolTeachingExperience;
	
	private Integer homeCountryTeachingExperience;
	
	private Integer interactionRapportScore;
	
	private Integer kidTeachingExperience;
	
	private Integer kidUnder12TeachingExperience;
	
	private Integer lessonObjectivesScore;
	
	private Integer onlineTeachingExperience;
	
	private Integer preparationPlanningScore;
	
	private Integer studentOutputScore;
	
	private Integer teachingCertificate;
	
	private Integer teachingMethodScore;
	
	private Integer teenagerTeachingExperience;
	
	private Integer teflOrToselCertificate;
	// 以下PE 独有
	private Integer timeManagementScore;
	
	private Integer accent;
	
	private Integer positive;
	
	private Integer engaged;
	
	private Integer appearance;
	
	private Integer phonics;
	// PE 独有 end
	private String levels;
	
	private String tagIds;
	
	private String finishType;
	
	private String result;

	public Integer getId() {
		return id;
	}

	public Integer getOnlineClassId() {
		return onlineClassId;
	}

	public Integer getDelayDays() {
		return delayDays;
	}

	public Integer getEnglishLanguageScore() {
		return englishLanguageScore;
	}

	public Integer getGrade6TeachingExperience() {
		return grade6TeachingExperience;
	}

	public Integer getHighSchoolTeachingExperience() {
		return highSchoolTeachingExperience;
	}

	public Integer getHomeCountryTeachingExperience() {
		return homeCountryTeachingExperience;
	}

	public Integer getInteractionRapportScore() {
		return interactionRapportScore;
	}

	public Integer getKidTeachingExperience() {
		return kidTeachingExperience;
	}

	public Integer getKidUnder12TeachingExperience() {
		return kidUnder12TeachingExperience;
	}

	public Integer getLessonObjectivesScore() {
		return lessonObjectivesScore;
	}

	public Integer getOnlineTeachingExperience() {
		return onlineTeachingExperience;
	}

	public Integer getPreparationPlanningScore() {
		return preparationPlanningScore;
	}

	public Integer getStudentOutputScore() {
		return studentOutputScore;
	}

	public Integer getTeachingCertificate() {
		return teachingCertificate;
	}

	public Integer getTeachingMethodScore() {
		return teachingMethodScore;
	}

	public Integer getTeenagerTeachingExperience() {
		return teenagerTeachingExperience;
	}

	public Integer getTeflOrToselCertificate() {
		return teflOrToselCertificate;
	}

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

	public String getLevels() {
		return levels;
	}

	public String getTagIds() {
		return tagIds;
	}

	public String getFinishType() {
		return finishType;
	}

	public String getResult() {
		return result;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setOnlineClassId(Integer onlineClassId) {
		this.onlineClassId = onlineClassId;
	}

	public void setDelayDays(Integer delayDays) {
		this.delayDays = delayDays;
	}

	public void setEnglishLanguageScore(Integer englishLanguageScore) {
		this.englishLanguageScore = englishLanguageScore;
	}

	public void setGrade6TeachingExperience(Integer grade6TeachingExperience) {
		this.grade6TeachingExperience = grade6TeachingExperience;
	}

	public void setHighSchoolTeachingExperience(Integer highSchoolTeachingExperience) {
		this.highSchoolTeachingExperience = highSchoolTeachingExperience;
	}

	public void setHomeCountryTeachingExperience(
			Integer homeCountryTeachingExperience) {
		this.homeCountryTeachingExperience = homeCountryTeachingExperience;
	}

	public void setInteractionRapportScore(Integer interactionRapportScore) {
		this.interactionRapportScore = interactionRapportScore;
	}

	public void setKidTeachingExperience(Integer kidTeachingExperience) {
		this.kidTeachingExperience = kidTeachingExperience;
	}

	public void setKidUnder12TeachingExperience(Integer kidUnder12TeachingExperience) {
		this.kidUnder12TeachingExperience = kidUnder12TeachingExperience;
	}

	public void setLessonObjectivesScore(Integer lessonObjectivesScore) {
		this.lessonObjectivesScore = lessonObjectivesScore;
	}

	public void setOnlineTeachingExperience(Integer onlineTeachingExperience) {
		this.onlineTeachingExperience = onlineTeachingExperience;
	}

	public void setPreparationPlanningScore(Integer preparationPlanningScore) {
		this.preparationPlanningScore = preparationPlanningScore;
	}

	public void setStudentOutputScore(Integer studentOutputScore) {
		this.studentOutputScore = studentOutputScore;
	}

	public void setTeachingCertificate(Integer teachingCertificate) {
		this.teachingCertificate = teachingCertificate;
	}

	public void setTeachingMethodScore(Integer teachingMethodScore) {
		this.teachingMethodScore = teachingMethodScore;
	}

	public void setTeenagerTeachingExperience(Integer teenagerTeachingExperience) {
		this.teenagerTeachingExperience = teenagerTeachingExperience;
	}

	public void setTeflOrToselCertificate(Integer teflOrToselCertificate) {
		this.teflOrToselCertificate = teflOrToselCertificate;
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

	public void setLevels(String levels) {
		this.levels = levels;
	}

	public void setTagIds(String tagIds) {
		this.tagIds = tagIds;
	}

	public void setFinishType(String finishType) {
		this.finishType = finishType;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
}
