package com.vipkid.trpm.entity.teachercomment;

import com.vipkid.portal.classroom.model.TeacherCommentVo;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/11/22 下午7:03
 */
public class TeacherCommentUpdateDto {

    private static final long serialVersionUID = -1L;

    /**
     * 实体编号（唯一标识）
     */
    protected Integer id;

    // 能够跟随教师指导
    //ability_to_follow_instructions
    private Integer abilityToFollowInstructions;

    // 积极互动
    //actively_interaction
    private Integer activelyInteraction;

    // 发音清晰
    //clear_pronunciation
    private Integer clearPronunciation;

    // 标记教师评语是否为空0:有,1:无
    //empty
    private Boolean empty;

    // 阅读技巧
    //reading_skills
    private Integer readingSkills;

    // 能够正确复述
    //repetition
    private Integer repetition;

    // 报告问题
    //report_issues
    private String reportIssues;

    // 拼写正确
    //spelling_accuracy
    private Integer spellingAccuracy;

    // 本节课获得星星币
    //stars
    private Integer stars;

    // 教师反馈
    //teacher_feedback
    private String teacherFeedback;

    //feedback_translation
    private String feedbackTranslation;

    // 给其它教师的小贴士
    //tips_for_other_teachers
    private String tipsForOtherTeachers;

    // 标记是否紧急
    //urgent
    private Boolean urgent;

    /**
     * 本次课程中的performance 得分
     * 0 - no comments	1 -very diff	2 - diff	3- average	4 - easy	5 - very easy
     */
    //performance
    private Integer performance;

    /**
     * 2015-06-29 该学生的performance表现：
     * 计算得出： Above，OnTarget, Below
     */
    //current_performance
    private String currentPerformance;

    private Long onlineClassId;

    private Long studentId;

    private Long teacherId;

    private Long courseId;

    private Long unitId;

    private Long learningCycleId;

    private Long lessonId;

    private String lessonSerialNumber;

    private String lessonName;

    // 2015-08-29 comment对应的课类型：trial 类型需要显示 trial level result
    private String courseType;

    // 2015-08-29 trial 水平测试的结果
    //trial_level_result
    private String trialLevelResult;

    //教师评价当前level是否符合该学生水平
    //performance_adjust
    private Integer performanceAdjust;

    //提交来源PC还是APP
    private String submitSource;

    //预约时间
    private Date scheduledDateTime;

    //老师点击添加teacherComment按钮时刻
    private Date clickDateTime;

    //提交teacherComment时间
    private Date submitDateTime;

    //查询条件:最多返回结果条数
    private Integer limit;

    //扩展字段
    //private Boolean hasComment; //是否已经填写评语




    private String remarks; // 备注

    private Integer createBy; // 创建者

    private Date createTime; // 创建日期

    private Integer updateBy; // 更新者

    private Date updateTime; // 更新日期

    private String delFlag; // 删除标记（0：正常；1：删除）废弃,现在是物理删除


    //preVip的Major课特有字段 START
    private String vocabularyRetention;
    private String pronunciation;
    private String alphabetSkills;
    private String phonologicalAwareness;
    private String followsInstructions;
    private String participatesActively;
    private String speaksClearly;
    private String mouseTouchpadActivities;
    private String degreeCompletion;
    private Boolean needParentSupport;
    //preVip的Major课特有字段 END

//    public TeacherCommentUpdateDto(SubmitTeacherCommentInputDto inputDto) {
//        if(NumberUtils.isNumber(inputDto.getTeacherCommentId())){
//            id = Integer.valueOf(inputDto.getTeacherCommentId());
//        }
//        if(StringUtils.isNotBlank(inputDto.getTeacherFeedback())){
//            teacherFeedback = inputDto.getTeacherFeedback();
//        }
//        if(NumberUtils.isNumber(inputDto.getLevelOfdifficulty())){
//            performance = Integer.valueOf(inputDto.getLevelOfdifficulty());
//        }
//        if(StringUtils.isNotBlank(inputDto.getTipsForOtherTeachers())){
//            tipsForOtherTeachers = inputDto.getTipsForOtherTeachers();
//        }
//        if(StringUtils.isNotBlank(inputDto.getTrialLevelResult())){
//            trialLevelResult = inputDto.getTrialLevelResult();
//        }
//        if(inputDto.isSuggestAdjustment()){
//            //建议调整
//            performanceAdjust = 1;
//        }else{
//            //不建议调整
//            performanceAdjust = 0;
//        }
//
//    }



    public String getVocabularyRetention() {
        return vocabularyRetention;
    }

    public void setVocabularyRetention(String vocabularyRetention) {
        this.vocabularyRetention = vocabularyRetention;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getAlphabetSkills() {
        return alphabetSkills;
    }

    public void setAlphabetSkills(String alphabetSkills) {
        this.alphabetSkills = alphabetSkills;
    }

    public String getPhonologicalAwareness() {
        return phonologicalAwareness;
    }

    public void setPhonologicalAwareness(String phonologicalAwareness) {
        this.phonologicalAwareness = phonologicalAwareness;
    }

    public String getFollowsInstructions() {
        return followsInstructions;
    }

    public void setFollowsInstructions(String followsInstructions) {
        this.followsInstructions = followsInstructions;
    }

    public String getParticipatesActively() {
        return participatesActively;
    }

    public void setParticipatesActively(String participatesActively) {
        this.participatesActively = participatesActively;
    }

    public String getSpeaksClearly() {
        return speaksClearly;
    }

    public void setSpeaksClearly(String speaksClearly) {
        this.speaksClearly = speaksClearly;
    }

    public String getMouseTouchpadActivities() {
        return mouseTouchpadActivities;
    }

    public void setMouseTouchpadActivities(String mouseTouchpadActivities) {
        this.mouseTouchpadActivities = mouseTouchpadActivities;
    }

    public String getDegreeCompletion() {
        return degreeCompletion;
    }

    public void setDegreeCompletion(String degreeCompletion) {
        this.degreeCompletion = degreeCompletion;
    }

    public Boolean getNeedParentSupport() {
        return needParentSupport;
    }

    public void setNeedParentSupport(Boolean needParentSupport) {
        this.needParentSupport = needParentSupport;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAbilityToFollowInstructions() {
        return abilityToFollowInstructions;
    }

    public void setAbilityToFollowInstructions(Integer abilityToFollowInstructions) {
        this.abilityToFollowInstructions = abilityToFollowInstructions;
    }

    public Integer getActivelyInteraction() {
        return activelyInteraction;
    }

    public void setActivelyInteraction(Integer activelyInteraction) {
        this.activelyInteraction = activelyInteraction;
    }

    public Integer getClearPronunciation() {
        return clearPronunciation;
    }

    public void setClearPronunciation(Integer clearPronunciation) {
        this.clearPronunciation = clearPronunciation;
    }

    public Boolean getEmpty() {
        return empty;
    }

    public void setEmpty(Boolean empty) {
        this.empty = empty;
    }

    public Integer getReadingSkills() {
        return readingSkills;
    }

    public void setReadingSkills(Integer readingSkills) {
        this.readingSkills = readingSkills;
    }

    public Integer getRepetition() {
        return repetition;
    }

    public void setRepetition(Integer repetition) {
        this.repetition = repetition;
    }

    public String getReportIssues() {
        return reportIssues;
    }

    public void setReportIssues(String reportIssues) {
        this.reportIssues = reportIssues;
    }

    public Integer getSpellingAccuracy() {
        return spellingAccuracy;
    }

    public void setSpellingAccuracy(Integer spellingAccuracy) {
        this.spellingAccuracy = spellingAccuracy;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }

    public String getFeedbackTranslation() {
        return feedbackTranslation;
    }

    public void setFeedbackTranslation(String feedbackTranslation) {
        this.feedbackTranslation = feedbackTranslation;
    }

    public String getTipsForOtherTeachers() {
        return tipsForOtherTeachers;
    }

    public void setTipsForOtherTeachers(String tipsForOtherTeachers) {
        this.tipsForOtherTeachers = tipsForOtherTeachers;
    }

    public Boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(Boolean urgent) {
        this.urgent = urgent;
    }

    public Integer getPerformance() {
        return performance;
    }

    public void setPerformance(Integer performance) {
        this.performance = performance;
    }

    public String getCurrentPerformance() {
        return currentPerformance;
    }

    public void setCurrentPerformance(String currentPerformance) {
        this.currentPerformance = currentPerformance;
    }

    public Long getOnlineClassId() {
        return onlineClassId;
    }

    public void setOnlineClassId(Long onlineClassId) {
        this.onlineClassId = onlineClassId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getLearningCycleId() {
        return learningCycleId;
    }

    public void setLearningCycleId(Long learningCycleId) {
        this.learningCycleId = learningCycleId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getLessonSerialNumber() {
        return lessonSerialNumber;
    }

    public void setLessonSerialNumber(String lessonSerialNumber) {
        this.lessonSerialNumber = lessonSerialNumber;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getTrialLevelResult() {
        return trialLevelResult;
    }

    public void setTrialLevelResult(String trialLevelResult) {
        this.trialLevelResult = trialLevelResult;
    }

    public Integer getPerformanceAdjust() {
        return performanceAdjust;
    }

    public void setPerformanceAdjust(Integer performanceAdjust) {
        this.performanceAdjust = performanceAdjust;
    }

    public String getSubmitSource() {
        return submitSource;
    }

    public void setSubmitSource(String submitSource) {
        this.submitSource = submitSource;
    }

    public Date getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(Date scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public Date getClickDateTime() {
        return clickDateTime;
    }

    public void setClickDateTime(Date clickDateTime) {
        this.clickDateTime = clickDateTime;
    }

    public Date getSubmitDateTime() {
        return submitDateTime;
    }

    public void setSubmitDateTime(Date submitDateTime) {
        this.submitDateTime = submitDateTime;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public TeacherCommentUpdateDto(){

    }

    public TeacherCommentUpdateDto(SubmitTeacherCommentDto teacherComment) {

        //理论上不会有比int大的
        if (teacherComment.getId() !=null) {
            id = Integer.valueOf(String.valueOf(teacherComment.getId()));
        }

        if (teacherComment.getAbilityToFollowInstructions() !=null) {
            abilityToFollowInstructions = teacherComment.getAbilityToFollowInstructions();
        }
        if (teacherComment.getActivelyInteraction() !=null) {
            activelyInteraction = teacherComment.getActivelyInteraction();
        }
        if (teacherComment.getClearPronunciation() !=null) {
            clearPronunciation = teacherComment.getClearPronunciation();
        }

        if (teacherComment.getCreateDateTime() !=null) {
            createTime = new Date(teacherComment.getCreateDateTime().getTime());
        }
        if (teacherComment.getEmpty() !=null && teacherComment.getEmpty() == 0) {
//            empty	teacher_feedback
//            0	jjjjjjjjjjlllll
//            1	None
            empty = false;
        } else {
            empty = true;
        }
        if (teacherComment.getLastDateTime() != null) {
            submitDateTime = new Date(teacherComment.getLastDateTime().getTime());
        }
        if (teacherComment.getLastDateTime() != null) {
            updateTime = new Date(teacherComment.getLastDateTime().getTime());
        }

        if (teacherComment.getReadingSkills() !=null) {
            readingSkills = teacherComment.getReadingSkills();
        }
        if (teacherComment.getRepetition() !=null) {
            repetition = teacherComment.getRepetition();
        }


        reportIssues = teacherComment.getReportIssues();

        if (teacherComment.getSpellingAccuracy() !=null) {
            spellingAccuracy = teacherComment.getSpellingAccuracy();
        }

        if (teacherComment.getStars() !=null) {
            stars = teacherComment.getStars();
        }

        teacherFeedback = teacherComment.getTeacherFeedback();
        feedbackTranslation = teacherComment.getFeedbackTranslation();
        tipsForOtherTeachers = teacherComment.getTipsForOtherTeachers();

        if (teacherComment.getUrgent()!=null && teacherComment.getUrgent() > 0) {
            urgent = true;
        } else {
            urgent = false;
        }

        if (teacherComment.getPerformance() !=null) {
            performance = teacherComment.getPerformance();
        }

        if (teacherComment.getPerformanceAdjust() !=null) {
            performanceAdjust = teacherComment.getPerformanceAdjust();
        }

        currentPerformance = teacherComment.getCurrentPerformance();


        if (teacherComment.getOnlineClassId() !=null) {
            onlineClassId = teacherComment.getOnlineClassId();
        }

        if (teacherComment.getStudentId()!=null) {
            studentId = teacherComment.getStudentId();
        }

        if (teacherComment.getTeacherId() !=null) {
            teacherId = teacherComment.getTeacherId();
        }

        if (teacherComment.getOperatorId() !=null) {
            createBy = Integer.valueOf(String.valueOf(teacherComment.getOperatorId()));
        }

        trialLevelResult = teacherComment.getTrialLevelResult();

        if(StringUtils.isNotBlank(teacherComment.getSubmitSource())){
            submitSource = teacherComment.getSubmitSource();
        }


        //preVip的Major课特有字段 START
        //preVip特有字段

        needParentSupport = teacherComment.getNeedParentSupport()==null?false:teacherComment.getNeedParentSupport();

        if(StringUtils.isNotBlank(teacherComment.getVocabularyRetention())){
            vocabularyRetention = teacherComment.getVocabularyRetention();
        }
        if(StringUtils.isNotBlank(teacherComment.getPronunciation())){
            pronunciation = teacherComment.getPronunciation();
        }
        if(StringUtils.isNotBlank(teacherComment.getAlphabetSkills())){
            alphabetSkills = teacherComment.getAlphabetSkills();
        }
        if(StringUtils.isNotBlank(teacherComment.getPhonologicalAwareness())){
            phonologicalAwareness = teacherComment.getPhonologicalAwareness();
        }
        if(StringUtils.isNotBlank(teacherComment.getFollowsInstructions())){
            followsInstructions = teacherComment.getFollowsInstructions();
        }
        if(StringUtils.isNotBlank(teacherComment.getParticipatesActively())){
            participatesActively = teacherComment.getParticipatesActively();
        }
        if(StringUtils.isNotBlank(teacherComment.getSpeaksClearly())){
            speaksClearly = teacherComment.getSpeaksClearly();
        }
        if(StringUtils.isNotBlank(teacherComment.getMouseTouchpadActivities())){
            mouseTouchpadActivities = teacherComment.getMouseTouchpadActivities();
        }
        if(StringUtils.isNotBlank(teacherComment.getDegreeCompletion())){
            degreeCompletion = teacherComment.getDegreeCompletion();
        }
        //preVip的Major课特有字段 END

    }
}
