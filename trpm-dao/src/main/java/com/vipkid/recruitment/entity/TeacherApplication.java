package com.vipkid.recruitment.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

/**
 * 版本排序 默认1(旧版本) 0.INTERVIEW 2.RACTICUM新版使用  3.BasicInfo新版使用
 * @author Along(ZengWeiLong)
 * @ClassName: TeacherApplication 
 * @date 2016年10月18日 下午5:55:55 
 *
 */
public class TeacherApplication extends Entity implements Serializable {

    private static final long serialVersionUID = 1139367434501991471L;
    private long id;
    private int abroadTeachingExperience = -1;
    private int appearanceScore = -1;
    private java.sql.Timestamp applyDateTime;
    private java.sql.Timestamp auditDateTime;
    private float basePay;
    private String comments;
    private String contractUrl;
    private int current = -1;
    private int delayDays = -1;
    private int englishLanguageScore = -1;
    private String failedReason;
    private int grade6TeachingExperience = -1;
    private int highSchoolTeachingExperience = -1;
    private int homeCountryTeachingExperience = -1;
    private int interactionRapportScore = -1;
    private int kidTeachingExperience = -1;
    private int kidUnder12TeachingExperience = -1;
    private int lessonObjectivesScore = -1;
    private int onlineTeachingExperience = -1;
    private int preparationPlanningScore = -1;
    private String result;
    private String status;
    private int studentOutputScore = -1;
    private int teachingCertificate = -1;
    private int teachingMethodScore = -1;
    private int teenagerTeachingExperience = -1;
    private int teflOrToselCertificate = -1;
    private int timeManagementScore = -1;
    private long auditorId;
    private long stduentId;
    private long teacherId;
    private long onlineClassId;
    private int accent = -1;
    private int positive = -1;
    private int engaged = -1;
    private int appearance = -1;
    private int phonics = -1;
    private int version = -1;

    public long getId() {
        return this.id;
    }

    public TeacherApplication setId(long id) {
        this.id = id;
        return this;
    }

    public int getAbroadTeachingExperience() {
        return this.abroadTeachingExperience;
    }

    public TeacherApplication setAbroadTeachingExperience(int abroadTeachingExperience) {
        this.abroadTeachingExperience = abroadTeachingExperience;
        return this;
    }

    public int getAppearanceScore() {
        return this.appearanceScore;
    }

    public TeacherApplication setAppearanceScore(int appearanceScore) {
        this.appearanceScore = appearanceScore;
        return this;
    }

    public java.sql.Timestamp getApplyDateTime() {
        return this.applyDateTime;
    }

    public TeacherApplication setApplyDateTime(java.sql.Timestamp applyDateTime) {
        this.applyDateTime = applyDateTime;
        return this;
    }

    public java.sql.Timestamp getAuditDateTime() {
        return this.auditDateTime;
    }

    public TeacherApplication setAuditDateTime(java.sql.Timestamp auditDateTime) {
        this.auditDateTime = auditDateTime;
        return this;
    }

    public float getBasePay() {
        return this.basePay;
    }

    public TeacherApplication setBasePay(float basePay) {
        this.basePay = basePay;
        return this;
    }

    public String getComments() {
        return this.comments;
    }

    public TeacherApplication setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public String getContractUrl() {
        return this.contractUrl;
    }

    public TeacherApplication setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
        return this;
    }

    public int getCurrent() {
        return this.current;
    }

    public TeacherApplication setCurrent(int current) {
        this.current = current;
        return this;
    }

    public int getDelayDays() {
        return this.delayDays;
    }

    public TeacherApplication setDelayDays(int delayDays) {
        this.delayDays = delayDays;
        return this;
    }

    public int getEnglishLanguageScore() {
        return this.englishLanguageScore;
    }

    public TeacherApplication setEnglishLanguageScore(int englishLanguageScore) {
        this.englishLanguageScore = englishLanguageScore;
        return this;
    }

    public String getFailedReason() {
        return this.failedReason;
    }

    public TeacherApplication setFailedReason(String failedReason) {
        this.failedReason = failedReason;
        return this;
    }

    public int getGrade6TeachingExperience() {
        return this.grade6TeachingExperience;
    }

    public TeacherApplication setGrade6TeachingExperience(int grade6TeachingExperience) {
        this.grade6TeachingExperience = grade6TeachingExperience;
        return this;
    }

    public int getHighSchoolTeachingExperience() {
        return this.highSchoolTeachingExperience;
    }

    public TeacherApplication setHighSchoolTeachingExperience(int highSchoolTeachingExperience) {
        this.highSchoolTeachingExperience = highSchoolTeachingExperience;
        return this;
    }

    public int getHomeCountryTeachingExperience() {
        return this.homeCountryTeachingExperience;
    }

    public TeacherApplication setHomeCountryTeachingExperience(int homeCountryTeachingExperience) {
        this.homeCountryTeachingExperience = homeCountryTeachingExperience;
        return this;
    }

    public int getInteractionRapportScore() {
        return this.interactionRapportScore;
    }

    public TeacherApplication setInteractionRapportScore(int interactionRapportScore) {
        this.interactionRapportScore = interactionRapportScore;
        return this;
    }

    public int getKidTeachingExperience() {
        return this.kidTeachingExperience;
    }

    public TeacherApplication setKidTeachingExperience(int kidTeachingExperience) {
        this.kidTeachingExperience = kidTeachingExperience;
        return this;
    }

    public int getKidUnder12TeachingExperience() {
        return this.kidUnder12TeachingExperience;
    }

    public TeacherApplication setKidUnder12TeachingExperience(int kidUnder12TeachingExperience) {
        this.kidUnder12TeachingExperience = kidUnder12TeachingExperience;
        return this;
    }

    public int getLessonObjectivesScore() {
        return this.lessonObjectivesScore;
    }

    public TeacherApplication setLessonObjectivesScore(int lessonObjectivesScore) {
        this.lessonObjectivesScore = lessonObjectivesScore;
        return this;
    }

    public int getOnlineTeachingExperience() {
        return this.onlineTeachingExperience;
    }

    public TeacherApplication setOnlineTeachingExperience(int onlineTeachingExperience) {
        this.onlineTeachingExperience = onlineTeachingExperience;
        return this;
    }

    public int getPreparationPlanningScore() {
        return this.preparationPlanningScore;
    }

    public TeacherApplication setPreparationPlanningScore(int preparationPlanningScore) {
        this.preparationPlanningScore = preparationPlanningScore;
        return this;
    }

    public String getResult() {
        return this.result;
    }

    public TeacherApplication setResult(String result) {
        this.result = result;
        return this;
    }

    public String getStatus() {
        return this.status;
    }

    public TeacherApplication setStatus(String status) {
        this.status = status;
        return this;
    }

    public int getStudentOutputScore() {
        return this.studentOutputScore;
    }

    public TeacherApplication setStudentOutputScore(int studentOutputScore) {
        this.studentOutputScore = studentOutputScore;
        return this;
    }

    public int getTeachingCertificate() {
        return this.teachingCertificate;
    }

    public TeacherApplication setTeachingCertificate(int teachingCertificate) {
        this.teachingCertificate = teachingCertificate;
        return this;
    }

    public int getTeachingMethodScore() {
        return this.teachingMethodScore;
    }

    public TeacherApplication setTeachingMethodScore(int teachingMethodScore) {
        this.teachingMethodScore = teachingMethodScore;
        return this;
    }

    public int getTeenagerTeachingExperience() {
        return this.teenagerTeachingExperience;
    }

    public TeacherApplication setTeenagerTeachingExperience(int teenagerTeachingExperience) {
        this.teenagerTeachingExperience = teenagerTeachingExperience;
        return this;
    }

    public int getTeflOrToselCertificate() {
        return this.teflOrToselCertificate;
    }

    public TeacherApplication setTeflOrToselCertificate(int teflOrToselCertificate) {
        this.teflOrToselCertificate = teflOrToselCertificate;
        return this;
    }

    public int getTimeManagementScore() {
        return this.timeManagementScore;
    }

    public TeacherApplication setTimeManagementScore(int timeManagementScore) {
        this.timeManagementScore = timeManagementScore;
        return this;
    }

    public long getAuditorId() {
        return this.auditorId;
    }

    public TeacherApplication setAuditorId(long auditorId) {
        this.auditorId = auditorId;
        return this;
    }

    public long getStduentId() {
        return this.stduentId;
    }

    public TeacherApplication setStduentId(long stduentId) {
        this.stduentId = stduentId;
        return this;
    }

    public long getTeacherId() {
        return this.teacherId;
    }

    public TeacherApplication setTeacherId(long teacherId) {
        this.teacherId = teacherId;
        return this;
    }

    public long getOnlineClassId() {
        return this.onlineClassId;
    }

    public TeacherApplication setOnlineClassId(long onlineClassId) {
        this.onlineClassId = onlineClassId;
        return this;
    }

    public int getAccent() {
        return this.accent;
    }

    public TeacherApplication setAccent(int accent) {
        this.accent = accent;
        return this;
    }

    public int getPositive() {
        return this.positive;
    }

    public TeacherApplication setPositive(int positive) {
        this.positive = positive;
        return this;
    }

    public int getEngaged() {
        return this.engaged;
    }

    public TeacherApplication setEngaged(int engaged) {
        this.engaged = engaged;
        return this;
    }

    public int getAppearance() {
        return this.appearance;
    }

    public TeacherApplication setAppearance(int appearance) {
        this.appearance = appearance;
        return this;
    }

    public int getPhonics() {
        return this.phonics;
    }

    public TeacherApplication setPhonics(int phonics) {
        this.phonics = phonics;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
