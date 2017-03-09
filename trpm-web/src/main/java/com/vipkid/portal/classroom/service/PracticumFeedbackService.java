package com.vipkid.portal.classroom.service;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.portal.classroom.model.PeCommentsVo;
import com.vipkid.portal.classroom.model.PeSupervisorCommentsVo;
import com.vipkid.portal.classroom.util.BeanUtils;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.trpm.constant.ApplicationConstant.FinishType;
import com.vipkid.trpm.dao.DemoReportDao;
import com.vipkid.trpm.dao.TeacherPeCommentsDao;
import com.vipkid.trpm.dao.TeacherPeLevelsDao;
import com.vipkid.trpm.dao.TeacherPeTagsDao;
import com.vipkid.trpm.entity.DemoReport;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherPeComments;
import com.vipkid.trpm.entity.TeacherPeLevels;
import com.vipkid.trpm.entity.TeacherPeTags;
import com.vipkid.trpm.entity.report.DemoReports;
import com.vipkid.trpm.entity.report.ReportLevels;
import com.vipkid.trpm.service.pe.AppserverPracticumService;
import com.vipkid.trpm.service.pe.PeSupervisorService;
import com.vipkid.trpm.service.pe.TeacherPeCommentsService;
import com.vipkid.trpm.service.pe.TeacherPeLevelsService;
import com.vipkid.trpm.service.pe.TeacherPeTagsService;
import com.vipkid.trpm.service.portal.OnlineClassService;
import com.vipkid.trpm.util.FilesUtils;

@Service
public class PracticumFeedbackService {

	private static Logger logger = LoggerFactory.getLogger(PracticumFeedbackService.class);
	
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
    
    @Autowired
    private TeacherPeTagsDao teacherPeTagsDao;
    
    @Autowired
    private TeacherPeLevelsDao teacherPeLevelsDao;
    
    @Autowired
    private TeacherPeCommentsDao teacherPeCommentsDao;
    
    @Autowired
    private DemoReportDao demoReportDao;
    
    private static DemoReports demoReports = null;

    private static ReportLevels reportLevels = null;

	
    /**
     * Pe 保存 / 提交
     * @param pe
     * @param bean
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     */
	public Map<String, Object> saveDoPeAudit(Teacher pe, PeCommentsVo bean) throws IllegalAccessException, InvocationTargetException{
		
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
        
        teacherApplication = BeanUtils.copyPropertys(bean,teacherApplication);
        
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
                }else{
                	throw new RuntimeException(resultMap.get("msg")+"");
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
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public Map<String, Object> saveDoPeSupervisorAudit(Teacher peSupervisor, PeSupervisorCommentsVo bean) throws IllegalAccessException, InvocationTargetException{
		
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
        
        teacherApplication = BeanUtils.copyPropertys(bean,teacherApplication);
        
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
                //发邮件 email
                logger.info("需要发送邮件");
                resultMap.put("recruitTeacher", recruitTeacher);
                resultMap.put("applicationResult", teacherApplication.getResult());
            }else{
            	throw new RuntimeException(resultMap.get("msg")+"");
            }
            // 并异步调用AppServer发送邮件及消息
            Long teacherApplicationId = (Long) resultMap.get("teacherApplicationId");
            if (Objects.nonNull(teacherApplicationId) && Objects.nonNull(recruitTeacher)) {
                appserverPracticumService.finishPracticumProcess(teacherApplicationId, recruitTeacher);
            }
        }
		return resultMap;
	}
	
	
	public PeCommentsVo findPeFromByAppId(Integer applicationId) throws IllegalAccessException, InvocationTargetException{
		PeCommentsVo bean = new PeCommentsVo();
		
		TeacherApplication teacherApplication = this.teacherApplicationDao.findApplictionById(applicationId);
	
		BeanUtils.copyPropertys(teacherApplication,bean);
		
		List<TeacherPeTags> tagsList = teacherPeTagsDao.getTeacherPeTagsByApplicationId(applicationId);
		List<Map<String,Integer>> taglist = Lists.newArrayList();
		tagsList.stream().forEach(tagpe -> {Map<String, Integer> maps = Maps.newHashMap();maps.put("id", tagpe.getTagId()); taglist.add(maps);});
		bean.setTagIds(taglist);
		
		List<TeacherPeLevels> levelsList = teacherPeLevelsDao.getTeacherPeLevelsByApplicationId(applicationId);
		List<Map<String,Integer>> levellist = Lists.newArrayList();
		levelsList.stream().forEach(level -> {Map<String, Integer> maps = Maps.newHashMap();maps.put("id", level.getLevel()); levellist.add(maps);});
		bean.setLevels(levellist);
        
		TeacherPeComments teacherPeComments = teacherPeCommentsDao.getTeacherPeComments(applicationId);
		if(teacherPeComments != null){
			bean.setThings(teacherPeComments.getThingsDidWell());
			bean.setAreas(teacherPeComments.getAreasImprovement());
			bean.setTotalScore(teacherPeComments.getTotalScore());
			bean.setSubmitType(teacherPeComments.getStatus()); 
		}
		return bean;
	}
	
	
	public PeSupervisorCommentsVo findPeSupervisorFromByAppId(Integer applicationId) throws IllegalAccessException, InvocationTargetException{
		PeSupervisorCommentsVo bean = new PeSupervisorCommentsVo();
		
		TeacherApplication teacherApplication = this.teacherApplicationDao.findApplictionById(applicationId);

		BeanUtils.copyPropertys(teacherApplication,bean);
		
		List<TeacherPeTags> tagsList = teacherPeTagsDao.getTeacherPeTagsByApplicationId(applicationId);
		List<Map<String,Integer>> taglist = Lists.newArrayList();
		tagsList.stream().forEach(tagpe -> {Map<String, Integer> maps = Maps.newHashMap();maps.put("id", tagpe.getTagId()); taglist.add(maps);});
		bean.setTagIds(taglist);
		
		List<TeacherPeLevels> levelsList = teacherPeLevelsDao.getTeacherPeLevelsByApplicationId(applicationId);
		List<Map<String,Integer>> levellist = Lists.newArrayList();
		levelsList.stream().forEach(level -> {Map<String, Integer> maps = Maps.newHashMap();maps.put("id", level.getLevel()); levellist.add(maps);});
		bean.setLevels(levellist);
        
		TeacherPeComments teacherPeComments = teacherPeCommentsDao.getTeacherPeComments(applicationId);
		if(teacherPeComments != null){
			bean.setThings(teacherPeComments.getThingsDidWell());
			bean.setAreas(teacherPeComments.getAreasImprovement());
			bean.setTotalScore(teacherPeComments.getTotalScore());
			bean.setSubmitType(teacherPeComments.getStatus()); 
		}
		return bean;
	}
	
	/**
     * DemoReport<br/>
     *
     * 根据studentId,onlineclassId获取DemoReport对象<br/>
     *
     * @Author:ALong
     * @Title: getDemoReport
     * @param studentId
     * @param onlineClassId
     * @return DemoReport
     * @date 2015年12月12日
     */
    public DemoReport getDemoReport(long studentId, long onlineClassId) {
        return demoReportDao.findByStudentIdAndOnlineClassId(studentId, onlineClassId);
    }

    /**
     * DemoReport<br/>
     *
     * 从文件中读取JSON数据<br/>
     *
     * @Author:ALong
     * @Title: getDemoReports
     * @return DemoReports
     * @date 2015年12月12日
     */
    public DemoReports getDemoReports() {
        if (demoReports == null) {
            String contentJson = FilesUtils.readContent(this.getClass().getResourceAsStream("data/demoReports.json"),
                    StandardCharsets.UTF_8);
            demoReports = JsonTools.readValue(contentJson, DemoReports.class);
        }

        return demoReports;
    }

    /**
     * DemoReport<br/>
     *
     * 从文件中读取JSON数据<br/>
     *
     * @Author:ALong
     * @Title: getReportLevels
     * @return ReportLevels
     * @date 2015年12月12日
     */
    public ReportLevels getReportLevels() {
        if (reportLevels == null) {
            String contentJson = FilesUtils.readContent(this.getClass().getResourceAsStream("data/levels.json"),
                    StandardCharsets.UTF_8);
            reportLevels = JsonTools.readValue(contentJson, ReportLevels.class);
        }

        return reportLevels;
    }
    
 }
