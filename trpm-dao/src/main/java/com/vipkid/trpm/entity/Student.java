package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class Student extends Entity implements Serializable {

    private static final long serialVersionUID = -6839362604180532154L;
    private long id;
    private String attendedActivities;
    private String avatar;
    private java.sql.Date birthday;
    private int customerStage;
    private String englishName;
    private String grade;
    private String knowTheStudent;
    private int learnedEnglish;
    private String lifeCycle;
    private String notes;
    private String personality;
    private String school;
    private String source;
    private int stars;
    private String trainingSchools;
    private int welcome;
    private String currentPerformance;
    private long chineseLeadTeacherId;
    private long familyId;
    private long foreignLeadTeacherId;
    private long salesId;
    private String qq;
    private java.sql.Timestamp assignedToSalesDateTimeTestTwo;
    private java.sql.Timestamp assignedToSalesDateTime;
    private long marketingActivityId;
    private long inventionCodeId;
    private java.sql.Timestamp preContractEndTime;
    private int targetClassesPerWeek;
    private String studentType;
    private long channelId;
    private String channelKeyword;
    
    //扩展属性
    private String name;
    private java.sql.Timestamp createDateTime;
    
    public long getId() {
        return this.id;
    }

    public Student setId(long id) {
        this.id = id;
        return this;
    }
        
    public String getAttendedActivities() {
        return this.attendedActivities;
    }

    public Student setAttendedActivities(String attendedActivities) {
        this.attendedActivities = attendedActivities;
        return this;
    }
        
    public String getAvatar() {
        return this.avatar;
    }

    public Student setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }
        
    public java.sql.Date getBirthday() {
        return this.birthday;
    }

    public Student setBirthday(java.sql.Date birthday) {
        this.birthday = birthday;
        return this;
    }
        
    public int getCustomerStage() {
        return this.customerStage;
    }

    public Student setCustomerStage(int customerStage) {
        this.customerStage = customerStage;
        return this;
    }
        
    public String getEnglishName() {
        return this.englishName;
    }

    public Student setEnglishName(String englishName) {
        this.englishName = englishName;
        return this;
    }
        
    public String getGrade() {
        return this.grade;
    }

    public Student setGrade(String grade) {
        this.grade = grade;
        return this;
    }
        
    public String getKnowTheStudent() {
        return this.knowTheStudent;
    }

    public Student setKnowTheStudent(String knowTheStudent) {
        this.knowTheStudent = knowTheStudent;
        return this;
    }
        
    public int getLearnedEnglish() {
        return this.learnedEnglish;
    }

    public Student setLearnedEnglish(int learnedEnglish) {
        this.learnedEnglish = learnedEnglish;
        return this;
    }
        
    public String getLifeCycle() {
        return this.lifeCycle;
    }

    public Student setLifeCycle(String lifeCycle) {
        this.lifeCycle = lifeCycle;
        return this;
    }
        
    public String getNotes() {
        return this.notes;
    }

    public Student setNotes(String notes) {
        this.notes = notes;
        return this;
    }
        
    public String getPersonality() {
        return this.personality;
    }

    public Student setPersonality(String personality) {
        this.personality = personality;
        return this;
    }
        
    public String getSchool() {
        return this.school;
    }

    public Student setSchool(String school) {
        this.school = school;
        return this;
    }
        
    public String getSource() {
        return this.source;
    }

    public Student setSource(String source) {
        this.source = source;
        return this;
    }
        
    public int getStars() {
        return this.stars;
    }

    public Student setStars(int stars) {
        this.stars = stars;
        return this;
    }
        
    public String getTrainingSchools() {
        return this.trainingSchools;
    }

    public Student setTrainingSchools(String trainingSchools) {
        this.trainingSchools = trainingSchools;
        return this;
    }
        
    public int getWelcome() {
        return this.welcome;
    }

    public Student setWelcome(int welcome) {
        this.welcome = welcome;
        return this;
    }
        
    public String getCurrentPerformance() {
        return this.currentPerformance;
    }

    public Student setCurrentPerformance(String currentPerformance) {
        this.currentPerformance = currentPerformance;
        return this;
    }
        
    public long getChineseLeadTeacherId() {
        return this.chineseLeadTeacherId;
    }

    public Student setChineseLeadTeacherId(long chineseLeadTeacherId) {
        this.chineseLeadTeacherId = chineseLeadTeacherId;
        return this;
    }
        
    public long getFamilyId() {
        return this.familyId;
    }

    public Student setFamilyId(long familyId) {
        this.familyId = familyId;
        return this;
    }
        
    public long getForeignLeadTeacherId() {
        return this.foreignLeadTeacherId;
    }

    public Student setForeignLeadTeacherId(long foreignLeadTeacherId) {
        this.foreignLeadTeacherId = foreignLeadTeacherId;
        return this;
    }
        
    public long getSalesId() {
        return this.salesId;
    }

    public Student setSalesId(long salesId) {
        this.salesId = salesId;
        return this;
    }
        
    public String getQq() {
        return this.qq;
    }

    public Student setQq(String qq) {
        this.qq = qq;
        return this;
    }
        
    public java.sql.Timestamp getAssignedToSalesDateTimeTestTwo() {
        return this.assignedToSalesDateTimeTestTwo;
    }

    public Student setAssignedToSalesDateTimeTestTwo(java.sql.Timestamp assignedToSalesDateTimeTestTwo) {
        this.assignedToSalesDateTimeTestTwo = assignedToSalesDateTimeTestTwo;
        return this;
    }
        
    public java.sql.Timestamp getAssignedToSalesDateTime() {
        return this.assignedToSalesDateTime;
    }

    public Student setAssignedToSalesDateTime(java.sql.Timestamp assignedToSalesDateTime) {
        this.assignedToSalesDateTime = assignedToSalesDateTime;
        return this;
    }
        
    public long getMarketingActivityId() {
        return this.marketingActivityId;
    }

    public Student setMarketingActivityId(long marketingActivityId) {
        this.marketingActivityId = marketingActivityId;
        return this;
    }
        
    public long getInventionCodeId() {
        return this.inventionCodeId;
    }

    public Student setInventionCodeId(long inventionCodeId) {
        this.inventionCodeId = inventionCodeId;
        return this;
    }
        
    public java.sql.Timestamp getPreContractEndTime() {
        return this.preContractEndTime;
    }

    public Student setPreContractEndTime(java.sql.Timestamp preContractEndTime) {
        this.preContractEndTime = preContractEndTime;
        return this;
    }
        
    public int getTargetClassesPerWeek() {
        return this.targetClassesPerWeek;
    }

    public Student setTargetClassesPerWeek(int targetClassesPerWeek) {
        this.targetClassesPerWeek = targetClassesPerWeek;
        return this;
    }
        
    public String getStudentType() {
        return this.studentType;
    }

    public Student setStudentType(String studentType) {
        this.studentType = studentType;
        return this;
    }
        
    public long getChannelId() {
        return this.channelId;
    }

    public Student setChannelId(long channelId) {
        this.channelId = channelId;
        return this;
    }
        
    public String getChannelKeyword() {
        return this.channelKeyword;
    }

    public Student setChannelKeyword(String channelKeyword) {
        this.channelKeyword = channelKeyword;
        return this;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public java.sql.Timestamp getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(java.sql.Timestamp createDateTime) {
		this.createDateTime = createDateTime;
	}
    
    
}
