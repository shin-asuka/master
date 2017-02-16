package com.vipkid.portal.classroom.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.portal.classroom.model.PeCommentsVo;
import com.vipkid.portal.classroom.model.PeSupervisorCommentsVo;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.trpm.constant.ApplicationConstant.FinishType;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherPeComments;
import com.vipkid.trpm.entity.TeacherPeLevels;
import com.vipkid.trpm.entity.TeacherPeTags;
import com.vipkid.trpm.service.pe.AppserverPracticumService;
import com.vipkid.trpm.service.pe.PeSupervisorService;
import com.vipkid.trpm.service.pe.TeacherPeCommentsService;
import com.vipkid.trpm.service.pe.TeacherPeLevelsService;
import com.vipkid.trpm.service.pe.TeacherPeTagsService;
import com.vipkid.trpm.service.portal.OnlineClassService;

@Service
public class FeedbackService {

	private static Logger logger = LoggerFactory.getLogger(FeedbackService.class);
	
	@Autowired
	private TeacherApplicationDao teacherApplicationDao;
	
    @Autowired
    private OnlineClassService onlineclassService;
    
    @Autowired
    private PeSupervisorService peSupervisorService;

    @Autowired
    private AppserverPracticumService appserverPracticumService;
	
    @Autowired
    private TeacherPeTagsService teacherPeTagsService;

    @Autowired
    private TeacherPeLevelsService teacherPeLevelsService;

    @Autowired
    private TeacherPeCommentsService teacherPeCommentsService;
	
    /**
     * Pe 保存 / 提交
     * @param pe
     * @param bean
     * @return
     */
	public Map<String, Object> saveDoPeAudit(Teacher pe, PeCommentsVo bean){
		
		Map<String, Object> resultMap = Maps.newHashMap();
        
		resultMap.put("submitType", bean.getSubmitType());
        
		int applicationId = bean.getId();
		
        if(!StringUtils.equalsIgnoreCase(bean.getResult(),Result.REAPPLY.toString())) {
            // 处理 tags 相关逻辑
            List<TeacherPeTags> teacherPeTags = Lists.newArrayList();
            for (Map<String, Integer> tag : bean.getTagIds()) {
                TeacherPeTags teacherPeTag = new TeacherPeTags();
                teacherPeTag.setApplicationId(applicationId);
                teacherPeTag.setTagId(tag.get("id"));
                teacherPeTags.add(teacherPeTag);
            }            
            teacherPeTagsService.updatePeTags(applicationId, teacherPeTags);
            
            List<TeacherPeLevels> teacherPeLevels = Lists.newArrayList();
            for (Map<String, Integer> level : bean.getLevels()) {
                TeacherPeLevels teacherPeLevel = new TeacherPeLevels();
                teacherPeLevel.setApplicationId(applicationId);
                teacherPeLevel.setLevel(level.get("id"));
                teacherPeLevels.add(teacherPeLevel);
            }
            teacherPeLevelsService.updateTeacherPeLevels(applicationId, teacherPeLevels);

            TeacherPeComments teacherPeComment = new TeacherPeComments();
            teacherPeComment.setApplicationId(applicationId);
            teacherPeComment.setThingsDidWell(bean.getThings());
            teacherPeComment.setAreasImprovement(bean.getAreas());
            teacherPeComment.setTotalScore(bean.getTotalScore());
            teacherPeComment.setStatus(bean.getSubmitType());
            teacherPeCommentsService.updateTeacherPeComments(applicationId, teacherPeComment);
        }
        
        TeacherApplication teacherApplication = this.teacherApplicationDao.findApplictionById(applicationId);
        
        teacherApplication = setPropertiesForPe(teacherApplication, bean);
        
        logger.info("PE:{} , 页面传入的结果result:{}", bean.getSubmitType(), bean.getResult());
        
        if ("SAVE".endsWith(bean.getSubmitType())){ 
            resultMap.put("result", onlineclassService.updateApplications(teacherApplication));
        } else {
            if (bean.getResult().startsWith(Result.TBD.toString())) {
                resultMap = peSupervisorService.doPracticumForPE(pe, teacherApplication, bean.getResult());
            } else {
                if (StringUtils.isBlank(bean.getFinishType())) {
                	bean.setFinishType(FinishType.AS_SCHEDULED);
                }
                resultMap = onlineclassService.updateAudit(pe, teacherApplication, bean.getResult(), bean.getFinishType());
                Teacher recruitTeacher = (Teacher) resultMap.get("recruitTeacher");

                // Finish课程
                if ((Boolean) resultMap.get("result")) {
                    onlineclassService.finishPracticum(teacherApplication, bean.getFinishType(), pe, recruitTeacher);
                    //发邮件 email
                    logger.info("需要发送邮件");
                    resultMap.put("recruitTeacher", recruitTeacher);
                    resultMap.put("applicationResult", teacherApplication.getResult());
                }
            }

            // 并异步调用AppServer 通知管理端发送消息
            Long teacherApplicationId = (Long) resultMap.get("teacherApplicationId");
            Teacher recruitTeacher = (Teacher) resultMap.get("recruitTeacher");
            if (Objects.nonNull(teacherApplicationId) && Objects.nonNull(recruitTeacher)) {
                appserverPracticumService.finishPracticumProcess(teacherApplicationId, recruitTeacher);
            }
        }        
        return resultMap;
	}
	
	/**
	 * Pes 保存/提交
	 * @param peSupervisor
	 * @param bean
	 * @return
	 */
	public Map<String, Object> saveDoPeSupervisorAudit(Teacher peSupervisor, PeSupervisorCommentsVo bean){
		
		Map<String, Object> resultMap = Maps.newHashMap();
		
		resultMap.put("submitType", bean.getSubmitType());
        
		int applicationId = bean.getId();
        
		if(!StringUtils.equalsIgnoreCase(bean.getResult(),Result.REAPPLY.toString())) {
        	// 处理 tags 相关逻辑
            List<TeacherPeTags> teacherPeTags = Lists.newArrayList();
            for (Map<String, Integer> tag : bean.getTagIds()) {
                TeacherPeTags teacherPeTag = new TeacherPeTags();
                teacherPeTag.setApplicationId(applicationId);
                teacherPeTag.setTagId(tag.get("id"));
                teacherPeTags.add(teacherPeTag);
            }            
            teacherPeTagsService.updatePeTags(applicationId, teacherPeTags);
            
            List<TeacherPeLevels> teacherPeLevels = Lists.newArrayList();
            for (Map<String, Integer> level : bean.getLevels()) {
                TeacherPeLevels teacherPeLevel = new TeacherPeLevels();
                teacherPeLevel.setApplicationId(applicationId);
                teacherPeLevel.setLevel(level.get("id"));
                teacherPeLevels.add(teacherPeLevel);
            }
            teacherPeLevelsService.updateTeacherPeLevels(applicationId, teacherPeLevels);

            TeacherPeComments teacherPeComment = new TeacherPeComments();
            teacherPeComment.setApplicationId(applicationId);
            teacherPeComment.setThingsDidWell(bean.getThings());
            teacherPeComment.setAreasImprovement(bean.getAreas());
            teacherPeComment.setTotalScore(bean.getTotalScore());
            teacherPeComment.setStatus(bean.getSubmitType());
            teacherPeCommentsService.updateTeacherPeComments(applicationId, teacherPeComment);
        }

        TeacherApplication teacherApplication = this.teacherApplicationDao.findApplictionById(applicationId);
        
        teacherApplication = setPropertiesForPeSupervisor(teacherApplication, bean);
		
        logger.info("PES:{} , 页面传入的结果result:{}", bean.getSubmitType(), bean.getResult());
        
		if ("SAVE".endsWith(bean.getSubmitType())){ 
            resultMap.put("result", onlineclassService.updateApplications(teacherApplication));
        } else {
            if (StringUtils.isBlank(bean.getFinishType())) {
            	bean.setFinishType(FinishType.AS_SCHEDULED);
            }
            resultMap = peSupervisorService.updateAudit(peSupervisor,teacherApplication, bean.getResult(), bean.getFinishType(), bean.getPeId());

            Teacher recruitTeacher = (Teacher) resultMap.get("recruitTeacher");
            // Finish课程
            if ((Boolean) resultMap.get("result")) {
                onlineclassService.finishPracticum(teacherApplication, bean.getFinishType(), peSupervisor, recruitTeacher);
                //发邮件
                //发邮件 email
                logger.info("需要发送邮件");
                resultMap.put("recruitTeacher", recruitTeacher);
                resultMap.put("applicationResult", teacherApplication.getResult());
            }
            // 并异步调用AppServer发送邮件及消息
            Long teacherApplicationId = (Long) resultMap.get("teacherApplicationId");
            if (Objects.nonNull(teacherApplicationId) && Objects.nonNull(recruitTeacher)) {
                appserverPracticumService.finishPracticumProcess(teacherApplicationId, recruitTeacher);
            }
        }
		return resultMap;
	}
	
	
	private TeacherApplication setPropertiesForPe(TeacherApplication teacherApplication,PeCommentsVo bean){
		teacherApplication.setDelayDays(bean.getDelayDays());
		teacherApplication.setEnglishLanguageScore(bean.getEnglishLanguageScore());
		teacherApplication.setGrade6TeachingExperience(bean.getGrade6TeachingExperience());
		teacherApplication.setHighSchoolTeachingExperience(bean.getHighSchoolTeachingExperience());
		teacherApplication.setHomeCountryTeachingExperience(bean.getHomeCountryTeachingExperience());
		teacherApplication.setInteractionRapportScore(bean.getInteractionRapportScore());
		teacherApplication.setKidTeachingExperience(bean.getKidTeachingExperience());
		teacherApplication.setKidUnder12TeachingExperience(bean.getKidUnder12TeachingExperience());
		teacherApplication.setLessonObjectivesScore(bean.getLessonObjectivesScore());
		teacherApplication.setOnlineTeachingExperience(bean.getOnlineTeachingExperience());
		teacherApplication.setPreparationPlanningScore(bean.getPreparationPlanningScore());
		teacherApplication.setStudentOutputScore(bean.getStudentOutputScore());
		teacherApplication.setTeachingCertificate(bean.getTeachingCertificate());
		teacherApplication.setTeachingMethodScore(bean.getTeachingMethodScore());
		teacherApplication.setTeenagerTeachingExperience(bean.getTeenagerTeachingExperience());
		teacherApplication.setTeflOrToselCertificate(bean.getTeflOrToselCertificate());
		teacherApplication.setTimeManagementScore(bean.getTimeManagementScore());
		teacherApplication.setAccent(bean.getAccent());
		teacherApplication.setPositive(bean.getPositive());
		teacherApplication.setEngaged(bean.getEngaged());
		teacherApplication.setAppearance(bean.getAppearance());
		teacherApplication.setPhonics(bean.getPhonics());
		teacherApplication.setResult(bean.getResult());	
		return teacherApplication;
	}
	
	
	private TeacherApplication setPropertiesForPeSupervisor(TeacherApplication teacherApplication,PeSupervisorCommentsVo bean){
		teacherApplication.setDelayDays(bean.getDelayDays());
		teacherApplication.setEnglishLanguageScore(bean.getEnglishLanguageScore());
		teacherApplication.setGrade6TeachingExperience(bean.getGrade6TeachingExperience());
		teacherApplication.setHighSchoolTeachingExperience(bean.getHighSchoolTeachingExperience());
		teacherApplication.setHomeCountryTeachingExperience(bean.getHomeCountryTeachingExperience());
		teacherApplication.setInteractionRapportScore(bean.getInteractionRapportScore());
		teacherApplication.setKidTeachingExperience(bean.getKidTeachingExperience());
		teacherApplication.setKidUnder12TeachingExperience(bean.getKidUnder12TeachingExperience());
		teacherApplication.setLessonObjectivesScore(bean.getLessonObjectivesScore());
		teacherApplication.setOnlineTeachingExperience(bean.getOnlineTeachingExperience());
		teacherApplication.setPreparationPlanningScore(bean.getPreparationPlanningScore());
		teacherApplication.setStudentOutputScore(bean.getStudentOutputScore());
		teacherApplication.setTeachingCertificate(bean.getTeachingCertificate());
		teacherApplication.setTeachingMethodScore(bean.getTeachingMethodScore());
		teacherApplication.setTeenagerTeachingExperience(bean.getTeenagerTeachingExperience());
		teacherApplication.setTeflOrToselCertificate(bean.getTeflOrToselCertificate());
		teacherApplication.setResult(bean.getResult());	
		return teacherApplication;
	}
	
 }
