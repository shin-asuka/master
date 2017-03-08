package com.vipkid.trpm.controller.portal;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.event.AuditEvent;
import com.vipkid.recruitment.event.AuditEventHandler;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.constant.ApplicationConstant.FinishType;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class OnlineClassController extends AbstractPortalController {

    private static Logger logger = LoggerFactory.getLogger(OnlineClassController.class);

    @Autowired
    private OnlineClassService onlineclassService;

    @Autowired
    private PeSupervisorService peSupervisorService;

    @Autowired
    private AppserverPracticumService appserverPracticumService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private TeacherPeTagsService teacherPeTagsService;

    @Autowired
    private TeacherPeLevelsService teacherPeLevelsService;

    @Autowired
    private TeacherPeCommentsService teacherPeCommentsService;

    @Autowired
    private AuditEventHandler auditEventHandler;

    /**
     * 进入教室
     *
     * @param request
     * @param response
     * @param onlineClassId
     * @param studentId
     * @param lessonId
     * @param model
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping("/classroom/{onlineClassId}-{studentId}-{lessonId}")
    public String classroom(HttpServletRequest request, HttpServletResponse response, @PathVariable long onlineClassId,
                    @PathVariable long studentId, @PathVariable long lessonId, Integer submitStatus, Model model)
                    throws IOException {
        if (StringUtils.equalsIgnoreCase(request.getHeader("x-forwarded-proto"), "https")) {
            String url = request.getRequestURL().toString();
            String httpUrl = url.replace("https:", "http:");
            String queryString = "";
            if (StringUtils.isNotBlank(request.getQueryString())){
                queryString = "?" + request.getQueryString();
            }
            String finalUrl = httpUrl + queryString;
            logger.info("Enter Classroom change https to http redirect -> header: {}; url: {}; httpUrl: {}; queryString: {}; finalUrl: {}",
                    request.getHeader("x-forwarded-proto"), url, httpUrl, queryString, finalUrl);
            response.sendRedirect(finalUrl);
            return finalUrl;
        }
        model.addAttribute("submitStatus", submitStatus);
        Teacher teacher = loginService.getTeacher();
        User user = loginService.getUser();
        String errorHTML = "You cannot enter this classroom!";
        // 登陆判断
        if (teacher == null || user == null) {
            errorHTML = "Please log in again!";
            model.addAttribute("info", errorHTML);
            return "error/info";
        }

        request.setAttribute("TRPM_TEACHER", teacher);
        request.setAttribute("TRPM_USER", user);
        // 参数判断1
        Lesson lesson = onlineclassService.getLesson(lessonId);
        if (lesson == null) {
            logger.error("teacherId:{},没有权限进入教室，原因:lesson is null,lessonId:{}", user.getId(), lessonId);
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        // 参数判断
        OnlineClass onlineClass = onlineclassService.getOnlineClassById(onlineClassId);
        if (onlineClass == null) {
            logger.error("teacherId:{},没有权限进入教室，原因:onlineClass is null,onlineClassId:{},LessonId:{}", user.getId(),
                            onlineClassId, lessonId);
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        // 检查teacherId 与当前登录Id是否匹配
        if (onlineClass.getTeacherId() != teacher.getId()) {
            logger.error("teacherId:{},没有权限进入教室，原因:teacherId 与当前登录Id不匹配,onlineClassId:{},teacherId:{}", user.getId(),
                            onlineClassId, teacher.getId());
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        // 检查lessonId是否匹配 onlineClassId
        if (onlineClass.getLessonId() != lessonId) {
            logger.error("teacherId:{},没有权限进入教室，原因:lessonId 与 onlineClassId不匹配,onlineClassId:{},lessonId:{}",
                            user.getId(), onlineClassId, lessonId);
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        // 检查onlineClassId是否匹配studentId
        if (!onlineclassService.checkStudentIdClassId(onlineClassId, studentId)) {
            logger.error("teacherId:{},没有权限进入教室，原因:onlineClassId 与 studentId不匹配,onlineClassId:{},studentId:{}",
                            user.getId(), onlineClassId, studentId);
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        // INVALID不允许进入教室
        if (OnlineClassEnum.ClassStatus.INVALID.toString().equals(onlineClass.getStatus())) {
            logger.error("teacherId:{},没有权限进入教室，原因:onlineClass 状态为：INVALID,onlineClassId:{}", user.getId(),
                            onlineClassId);
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        // TEACHER_NO_SHOW不允许进入教室
        if ("TEACHER_NO_SHOW".equals(onlineClass.getFinishType())) {
            logger.error("teacherId:{},没有权限进入教室，原因:onlineClass FinishType为：TEACHER_NO_SHOW,onlineClassId:{}",
                            user.getId(), onlineClassId);
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        String isTrial = "0";
        if (lesson.getSerialNumber() != null && lesson.getSerialNumber().startsWith("T1-")) {
            isTrial = "1"; // 区分是否Trial课程
        }
        model.addAttribute("isTrial", isTrial);
        logger.info("TeacherId:{},成功进入教室(INTO),onlineClassId:{},studentId:{}", user.getId(), onlineClassId, studentId);

        model.addAttribute("lesson", lesson);
        // 是否已经开始上课
        if (System.currentTimeMillis() > onlineClass.getScheduledDateTime().getTime()) {
            model.addAttribute("isStarted", true);
        } else {
            model.addAttribute("isStarted", false);
        }
        if (lesson.getSerialNumber().startsWith("P")) {
            model.addAllAttributes(onlineclassService.enterPracticum(onlineClass, studentId, teacher, lesson));
            return view("online_class_practicum");
        } else if (lesson.getSerialNumber().startsWith("OPEN")) {
            model.addAllAttributes(onlineclassService.enterOpen(onlineClass, studentId, teacher, lesson));
            return view("online_class_open");
        } else {
            /** 是否需要打开feedbackd */
            if ("feedback".equals(request.getParameter("from"))) {
                model.addAttribute("from", "feedback");
            }
            //只有在Major课老师进教室时，才创建TeacherComment
            //学生进教室的时候创建CF记录
            //onlineclassService.createTeacherCommentByEnterClassroom(studentId,teacher.getId(),onlineClass,lesson);
            model.addAllAttributes(
                    onlineclassService.enterMajor(onlineClass, studentId, teacher, lesson));
            return view("online_class_major");
        }
    }

    /**
     * 退出非OPEN课教室
     *
     * @Author:ALong
     * @Title: exitClassroom
     * @param request
     * @param response
     * @param onlineClassId
     * @return String
     * @date 2016年1月8日
     */
    @RequestMapping("/exitClassroom")
    public String exitClassroom(HttpServletRequest request, HttpServletResponse response, long onlineClassId) {
        Teacher teacher = loginService.getTeacher();
        onlineclassService.exitclassroom(onlineClassId, teacher);

        return "redirect:/classrooms";
    }

    @RequestMapping("/exitClassroomPage")
    public String exitClassroomPage(HttpServletRequest request, HttpServletResponse response,
                    @RequestParam("onlineClassId") Long onlineClassId) {
        logger.info("教师退出在线教室 exitClassroomPage onlineClassId = {}", onlineClassId);
        Map<String, Object> modelMap = Maps.newHashMap();
        Integer status = 0;
        String message = "";
        try {
            Teacher teacher = loginService.getTeacher();
            onlineclassService.exitclassroom(onlineClassId, teacher);
            status = 1;
        } catch (Exception e) {
            status = 0;
            message = "退出在线教室失败";
            logger.error("退出在线教室失败", e);
        }
        modelMap.put("status", status);
        modelMap.put("message", message);
        return jsonView(response, modelMap);
    }

    /**
     * 退出OPEN课程教室
     *
     * @param request
     * @param response
     * @param onlineClassId
     * @param model
     * @return
     */
    @RequestMapping("/endThisClass")
    public String endThisClass(HttpServletRequest request, HttpServletResponse response,
                    @RequestParam long onlineClassId, Model model) {
        onlineclassService.exitOpenclass(onlineClassId);
        return "redirect:/classrooms.shtml";
    }

    /**
     * 提示服务器端已经进教室
     *
     * @param request
     * @param response
     * @param onlineClassId
     * @param model
     * @return
     */
    @RequestMapping("/sendTeacherInClassroom")
    public String sendTeacherInClassroom(HttpServletRequest request, HttpServletResponse response,
                    @RequestParam("onlineClassId") long onlineClassId, Model model) {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("onlineClassId", String.valueOf(onlineClassId));
        Teacher teacher = loginService.getTeacher();
        if (teacher != null) {
            logger.info("teacher : " + teacher.getId() + ",into classroomId:" + onlineClassId);
        }
        Map<String, Object> modelMap = onlineclassService.sendTeacherInClassroom(requestParams, teacher);
        model.addAllAttributes(modelMap);
        return jsonView(response, modelMap);
    }

    /**
     * audit 课程
     *
     * @Author:ALong
     * @Title: doAudit
     * @param teacherApplication 申请
     * @return String
     * @date 2016年1月11日
     */
    @RequestMapping("/doAudit")
    public String doAudit(HttpServletRequest request, HttpServletResponse response,
                    TeacherApplication teacherApplication, Model model) {
        Teacher pe = loginService.getTeacher();
        String type = ServletRequestUtils.getStringParameter(request, "type", "");
        String finishType = ServletRequestUtils.getStringParameter(request, "finishType", "");

        // 新增 tags 逻辑
        int[] tags = ServletRequestUtils.getIntParameters(request, "tags");
        String things = ServletRequestUtils.getStringParameter(request, "things", null);
        String areas = ServletRequestUtils.getStringParameter(request, "areas", null);
        int[] levels = ServletRequestUtils.getIntParameters(request, "level");
        int totalScore = ServletRequestUtils.getIntParameter(request, "totalScore", 0);
        String submitType = ServletRequestUtils.getStringParameter(request, "submitType", null);

        Map<String, Object> modelMap = Maps.newHashMap();
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

        if ("SAVE".endsWith(submitType)){
            modelMap.put("result", onlineclassService.updateApplications(teacherApplication));
        } else {
            if (type.startsWith(Result.TBD.toString())) {
                modelMap = peSupervisorService.doPracticumForPE(pe, teacherApplication, type);
            } else {
                if (StringUtils.isEmpty(finishType)) {
                    finishType = FinishType.AS_SCHEDULED;
                }
                modelMap = onlineclassService.updateAudit(pe, teacherApplication, type, finishType);
                Teacher recruitTeacher = (Teacher) modelMap.get("recruitTeacher");

                // Finish课程
                if ((Boolean) modelMap.get("result")) {
                    onlineclassService.finishPracticum(teacherApplication, finishType, pe, recruitTeacher);
                    //发邮件
                    auditEventHandler.onAuditEvent(new AuditEvent(recruitTeacher.getId(), TeacherEnum.LifeCycle.PRACTICUM.toString(), teacherApplication.getResult()));
                }
            }

            // 并异步调用AppServer发送邮件及消息
            Long teacherApplicationId = (Long) modelMap.get("teacherApplicationId");
            Teacher recruitTeacher = (Teacher) modelMap.get("recruitTeacher");
            if (Objects.nonNull(teacherApplicationId) && Objects.nonNull(recruitTeacher)) {
                appserverPracticumService.finishPracticumProcess(teacherApplicationId, recruitTeacher);
            }
        }

        return jsonView(response, modelMap);
    }

    /**
     * 获取TeacherComment状态
     *
     * @param request
     * @param response
     * @param onlineClassId
     * @param studentId
     * @param model
     * @return
     */
    @RequestMapping("/isEmpty")
    public String isEmpty(HttpServletRequest request, HttpServletResponse response, @RequestParam long onlineClassId,
                    @RequestParam long studentId, Model model) {
        /* 判断当前课程是否为公开课，如果是则设置不显示退出教室提示 */
        Map<String, Object> modelMap = onlineclassService.isEmpty(onlineClassId, studentId);
        model.addAllAttributes(modelMap);
        return jsonView(response, modelMap);
    }

    /**
     * 获取DemoReport状态
     *
     * @param request
     * @param response
     * @param onlineClassId
     * @param studentId
     * @param model
     * @return
     */
    @RequestMapping("/getLifeCycle")
    public String getLifeCycle(HttpServletRequest request, HttpServletResponse response,
                    @RequestParam long onlineClassId, @RequestParam long studentId, Model model) {
        DemoReport demoReport = onlineclassService.getDemoReport(onlineClassId, studentId);
        if (null != demoReport) {
            model.addAttribute("lifeCycle", demoReport.getLifeCycle());
        } else {
            model.addAttribute("lifeCycle", "");
        }
        return jsonView();
    }

    /**
     * 发送帮助
     *
     * @param request
     * @param response
     * @param scheduleTime
     * @param model
     * @return
     */
    @RequestMapping("/sendHelp")
    public String sendHelp(HttpServletRequest request, HttpServletResponse response, @RequestParam String scheduleTime,
                    @RequestParam long onlineClassId, Model model) {
        /* 计算服务器时间毫秒 */
        Teacher teacher = loginService.getTeacher();
        Map<String, Object> modelMap = onlineclassService.sendHelp(scheduleTime, onlineClassId, teacher);
        model.addAllAttributes(modelMap);
        return jsonView(response, modelMap);
    }

    /**
     * 记录发送星星日志
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping("/sendStarLogs")
    public String sendStarLogs(HttpServletRequest request, HttpServletResponse response, @RequestParam boolean send,
                    @RequestParam long studentId, @RequestParam long onlineClassId, Model model) {
        Teacher teacher = loginService.getTeacher();
        onlineclassService.sendStarlogs(send, studentId, onlineClassId, teacher);
        Map<String, Object> modelMap;
        if(send){
            logger.info("Teacher {} send star to {} in class {}", teacher.getId(), studentId, onlineClassId);
            modelMap = onlineclassService.updateStarNum(onlineClassId, teacher.getId(), studentId, 1);
        }else{
            logger.info("Teacher {} remove star to {} in class {}", teacher.getId(), studentId, onlineClassId);
            modelMap = onlineclassService.updateStarNum(onlineClassId, teacher.getId(), studentId, -1);
        }
        return jsonView(response, modelMap);
    }
  //FAQ静态页面的controler
  	@RequestMapping("/faq")
  	public String showFAQ(HttpServletRequest request, HttpServletResponse response, Model model) {
  		return view("faq");
  	}
}
