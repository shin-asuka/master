package com.vipkid.trpm.controller.pe;

import com.google.api.client.util.Lists;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.event.AuditEvent;
import com.vipkid.recruitment.event.AuditEventHandler;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.constant.ApplicationConstant.FinishType;
import com.vipkid.trpm.dao.TagsDao;
import com.vipkid.trpm.dao.TeacherPeCommentsDao;
import com.vipkid.trpm.dao.TeacherPeLevelsDao;
import com.vipkid.trpm.dao.TeacherPeTagsDao;
import com.vipkid.trpm.entity.*;
import com.vipkid.trpm.service.pe.*;
import com.vipkid.trpm.service.portal.OnlineClassService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class PeSupervisorController extends AbstractPeController {

    private static Logger logger = LoggerFactory.getLogger(PeSupervisorController.class);

    @Autowired
    private PeSupervisorService peSupervisorService;

    @Autowired
    private OnlineClassService onlineclassService;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    @Autowired
    private AppserverPracticumService appserverPracticumService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private TagsDao tagsDao;

    @Autowired
    private TeacherPeTagsDao teacherPeTagsDao;

    @Autowired
    private TeacherPeLevelsDao teacherPeLevelsDao;

    @Autowired
    private TeacherPeCommentsDao teacherPeCommentsDao;

    @Autowired
    private TeacherPeTagsService teacherPeTagsService;

    @Autowired
    private TeacherPeLevelsService teacherPeLevelsService;

    @Autowired
    private TeacherPeCommentsService teacherPeCommentsService;

    @Autowired
    private AuditEventHandler auditEventHandler;

    @Deprecated
    @RequestMapping("/pesupervisor")
    public String peSupervisor(HttpServletRequest request, HttpServletResponse response,
            Model model) {
        model.addAttribute("linePerPage", LINE_PER_PAGE);
        Teacher teacher = loginService.getTeacher();
        model.addAttribute("totalLine", peSupervisorService.totalPe(teacher.getId()));
        return view("classrooms_pe");
    }

    @Deprecated
    @RequestMapping("/pe/classList")
    public String classList(HttpServletRequest request, HttpServletResponse response, Model model) {
        int curPage = ServletRequestUtils.getIntParameter(request, "curPage", 1);
        Teacher teacher = loginService.getTeacher();
        model.addAllAttributes(
                peSupervisorService.classList(teacher.getId(), curPage, LINE_PER_PAGE));
        return jsonView();
    }

    @RequestMapping("/pereview")
    public String peReview(HttpServletRequest request, HttpServletResponse response, Model model) {
        if (StringUtils.equalsIgnoreCase(request.getHeader("x-forwarded-proto"), "https")) {
            try {
                response.sendRedirect(request.getRequestURL().toString().replace("https:", "http:")+"?"+request.getQueryString());
            } catch (IOException e) {
                logger.error("Enter pereview Classroom ", e);
            }
            logger.info("Enter pereview Classroom change https to http redirect -> header: {}",
                    request.getHeader("x-forwarded-proto"));
            return null;
        }

        Teacher teacher = this.loginService.getTeacher();
        int id = ServletRequestUtils.getIntParameter(request, "id", 0);
        long onlineClassId = ServletRequestUtils.getLongParameter(request, "classId", 0);
        TeacherPe teacherPe = this.peSupervisorService.getTeacherPe(id);

        if (teacherPe != null && teacher.getId() == teacherPe.getPeId()) {
            logger.info("PE(" + teacher.getId() + "):进入onloineClassId:" + onlineClassId);
            model.addAttribute("url", this.peSupervisorService.getClassRoomUrl(teacherPe));
            model.addAttribute("teacherPe", teacherPe);

            TeacherApplication teacherApplication =
                    peSupervisorService.getTeacherApplication(teacherPe.getTeacherId());

            // 更新审核开始时间
            if (null == teacherPe.getOperatorStartTime()) {
                peSupervisorService.updatePeSupervisorStartTime(teacherPe.getId());
            }

            List<TeacherApplication> list = teacherApplicationDao
                    .findApplictionForStatusResult(teacherApplication.getTeacherId(),Status.PRACTICUM.toString(),Result.PRACTICUM2.toString());
            if (list != null && list.size() > 0) {
                model.addAttribute("practicum2", true);
            } else {
                model.addAttribute("practicum2", false);
            }

            int applicationId = Long.valueOf(teacherApplication.getId()).intValue();
            model.addAttribute("tags", tagsDao.getTags());
            model.addAttribute("teacherPeTags", teacherPeTagsDao.getTeacherPeTagsByApplicationId(applicationId));
            model.addAttribute("teacherPeLevels", teacherPeLevelsDao.getTeacherPeLevelsByApplicationId(applicationId));
            TeacherPeComments teacherPeComments = teacherPeCommentsDao.getTeacherPeComments(applicationId);
            model.addAttribute("teacherPeComments", teacherPeComments);

            if(0==teacherApplication.getAuditorId() && null==teacherPeComments){
                teacherApplicationDao.initApplicationAnswer(teacherApplication);
                model.addAttribute("teacherApplication", teacherApplicationDao.findApplictionById(applicationId));
            }else{
                model.addAttribute("teacherApplication", teacherApplication);
            }

            return view("online_class_pe");
        } else {
            logger.error("PE(" + teacher.getId() + "):没有权限进入该class Review");
            String errorHTML = "You cannot enter this classroom!";
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
    }

    /**
     * audit 课程
     *
     * @Author:ALong
     * @Title: doAudit
     * @return String
     * @date 2016年1月11日
     */
    @RequestMapping("/pe/doAudit")
    public String peDoAudit(HttpServletRequest request, HttpServletResponse response,
            TeacherApplication teacherApplication, Model model) {
        Teacher peSupervisor = loginService.getTeacher();
        Map<String, Object> modelMap = Maps.newHashMap();
        int peId = ServletRequestUtils.getIntParameter(request, "peId", -1);
        String type = ServletRequestUtils.getStringParameter(request, "type", "");
        String finishType = ServletRequestUtils.getStringParameter(request, "finishType", "");

        // 新增 tags 逻辑
        int[] tags = ServletRequestUtils.getIntParameters(request, "tags");
        String things = ServletRequestUtils.getStringParameter(request, "things", null);
        String areas = ServletRequestUtils.getStringParameter(request, "areas", null);

        //validate things and areas'length
        try{
            //Preconditions.checkArgument(com.vipkid.file.utils.StringUtils.isNotBlank(things), "things content can not be null!");
            //Preconditions.checkArgument(com.vipkid.file.utils.StringUtils.isNotBlank(areas), "areas content can not be null!");

            Preconditions.checkArgument(things.length() <= 3000 , "The length of things content must be less than 3000!");
            Preconditions.checkArgument(areas.length() <= 3000, "The length of areas content must be less than 3000!");

        }catch(IllegalArgumentException e){
            logger.warn("IllegalArgumentException at /doAudit, errorMessage="+e.getMessage(), e);
            modelMap.put("result", false);
            modelMap.put("msg", e.getMessage());
            return jsonView(response, modelMap);
        }

        int[] levels = ServletRequestUtils.getIntParameters(request, "level");
        int totalScore = ServletRequestUtils.getIntParameter(request, "totalScore", 0);
        String submitType = ServletRequestUtils.getStringParameter(request, "submitType", null);


        modelMap.put("submitType", submitType);
        if(!StringUtils.equalsIgnoreCase(type,Result.REAPPLY.toString())) {
            // 处理 tags 相关逻辑
            int applicationId = Long.valueOf(teacherApplication.getId()).intValue();

            List<TeacherPeTags> teacherPeTags = Lists.newArrayList();
            for (int tagId : tags) {
                TeacherPeTags teacherPeTag = new TeacherPeTags();
                teacherPeTag.setApplicationId(applicationId);
                teacherPeTag.setTagId(tagId);
                teacherPeTags.add(teacherPeTag);
            }
            teacherPeTagsService.updatePeTags(applicationId, teacherPeTags);

            List<TeacherPeLevels> teacherPeLevels = Lists.newArrayList();
            for (int level : levels) {
                TeacherPeLevels teacherPeLevel = new TeacherPeLevels();
                teacherPeLevel.setApplicationId(applicationId);
                teacherPeLevel.setLevel(level);
                teacherPeLevels.add(teacherPeLevel);
            }
            teacherPeLevelsService.updateTeacherPeLevels(applicationId, teacherPeLevels);

            TeacherPeComments teacherPeComment = new TeacherPeComments();
            teacherPeComment.setApplicationId(applicationId);
            teacherPeComment.setThingsDidWell(things);
            teacherPeComment.setAreasImprovement(areas);
            teacherPeComment.setTotalScore(totalScore);
            teacherPeComment.setStatus(submitType);
            teacherPeCommentsService.updateTeacherPeComments(applicationId, teacherPeComment);
        }

        if ("SAVE".endsWith(submitType)) {
            modelMap.put("result", onlineclassService.updateApplications(teacherApplication));
        } else {
            if (StringUtils.isEmpty(finishType)) {
                finishType = FinishType.AS_SCHEDULED;
            }
            modelMap = peSupervisorService.updateAudit(peSupervisor,teacherApplication, type, finishType, peId);
            model.addAllAttributes(modelMap);
            Teacher recruitTeacher = (Teacher) modelMap.get("recruitTeacher");
            // Finish课程
            if ((Boolean) modelMap.get("result")) {
                onlineclassService.finishPracticum(teacherApplication, finishType, peSupervisor, recruitTeacher);
                //发邮件
                auditEventHandler.onAuditEvent(new AuditEvent(recruitTeacher.getId(), TeacherEnum.LifeCycle.PRACTICUM.toString(), teacherApplication.getResult()));

            }

            // 并异步调用AppServer发送邮件及消息
            Long teacherApplicationId = (Long) modelMap.get("teacherApplicationId");
            if (Objects.nonNull(teacherApplicationId) && Objects.nonNull(recruitTeacher)) {
                appserverPracticumService.finishPracticumProcess(teacherApplicationId, recruitTeacher);
            }
        }
        return jsonView(response, modelMap);
    }

    @RequestMapping("/peExitClass")
    public String peExitClass(HttpServletRequest request, HttpServletResponse response,
            long teacherApplicationId, Model model) {
        Map<String, Object> modelMap = peSupervisorService.peExitClass(teacherApplicationId);
        return jsonView(response, modelMap);
    }

}
