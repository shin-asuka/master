package com.vipkid.portal.classroom.model;

import java.util.List;
import java.util.Map;

import com.vipkid.rest.validation.annotation.Ignore;
import com.vipkid.rest.validation.annotation.Length;
import com.vipkid.rest.validation.annotation.NotNull;

@NotNull
public class PeCommentsVo {

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
	
	private Integer formType;

	@Ignore
	private Integer timeManagementScore;
	
	@Ignore
	private Integer accent;
	
	@Ignore
	private Integer positive;
	
	@Ignore
	private Integer engaged;
	
	@Ignore
	private Integer appearance;
	
	@Ignore
	private Integer phonics;
	
	@Ignore
	private List<Map<String,Integer>> levels;
	
	@Ignore
	private List<Map<String,Integer>> tagIds;
	
	private String finishType;
	
	private String result;
	
	@Length(maxLength=3000)
	private String things;
	
	@Length(maxLength=3000)
	private String areas;

	private Integer totalScore;
	
	private String submitType;

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

	public List<Map<String,Integer>> getLevels() {
		return levels;
	}

	public List<Map<String,Integer>> getTagIds() {
		return tagIds;
	}

	public Integer getFormType() {
		return formType;
	}

	public void setFormType(Integer formType) {
		this.formType = formType;
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

	public void setLevels(List<Map<String,Integer>> levels) {
		this.levels = levels;
	}

	public void setTagIds(List<Map<String,Integer>> tagIds) {
		this.tagIds = tagIds;
	}

	public void setFinishType(String finishType) {
		this.finishType = finishType;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getThings() {
		return things;
	}

	public String getAreas() {
		return areas;
	}

	public Integer getTotalScore() {
		return totalScore;
	}

	public String getSubmitType() {
		return submitType;
	}

	public void setThings(String things) {
		this.things = things;
	}

	public void setAreas(String areas) {
		this.areas = areas;
	}

	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}

	public void setSubmitType(String submitType) {
		this.submitType = submitType;
	}
	
}
