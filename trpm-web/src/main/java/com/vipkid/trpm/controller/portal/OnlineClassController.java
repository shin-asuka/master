package com.vipkid.trpm.controller.portal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.google.common.collect.Maps;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.trpm.constant.ApplicationConstant.FinishType;
import com.vipkid.trpm.constant.ApplicationConstant.RecruitmentResult;
import com.vipkid.trpm.entity.DemoReport;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherApplication;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.passport.IndexService;
import com.vipkid.trpm.service.pe.AppserverPracticumService;
import com.vipkid.trpm.service.pe.PeSupervisorService;
import com.vipkid.trpm.service.portal.OnlineClassService;

@Controller
public class OnlineClassController extends AbstractPortalController {

    private static Logger logger = LoggerFactory.getLogger(OnlineClassController.class);

    @Autowired
    private OnlineClassService onlineclassService;

    @Autowired
    private IndexService indexService;

    @Autowired
    private PeSupervisorService peSupervisorService;

    @Autowired
    private AppserverPracticumService appserverPracticumService;

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
    public String classroom(HttpServletRequest request, HttpServletResponse response,
            @PathVariable long onlineClassId, @PathVariable long studentId,
            @PathVariable long lessonId, Model model) throws IOException {
        Teacher teacher = indexService.getTeacher(request);
        User user = indexService.getUser(request);

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
            logger.error("没有权限进入教室，原因:lesson is null,lessonId:{}", lessonId);
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        // 参数判断
        OnlineClass onlineClass = onlineclassService.getOnlineClassById(onlineClassId);
        if (onlineClass == null) {
            logger.error("没有权限进入教室，原因:onlineClass is null,onlineClassId:{},LessonId:{}",
                    onlineClassId, lessonId);
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        // 检查teacherId 与当前登录Id是否匹配
        if (onlineClass.getTeacherId() != teacher.getId()) {
            logger.error("没有权限进入教室，原因:teacherId 与当前登录Id不匹配,onlineClassId:{},teacherId:{}",
                    onlineClassId, teacher.getId());
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        // 检查lessonId是否匹配 onlineClassId
        if (onlineClass.getLessonId() != lessonId) {
            logger.error("没有权限进入教室，原因:lessonId 与 onlineClassId不匹配,onlineClassId:{},lessonId:{}",
                    onlineClassId, lessonId);
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        // 检查onlineClassId是否匹配studentId
        if (!onlineclassService.checkStudentIdClassId(onlineClassId, studentId)) {
            logger.error("没有权限进入教室，原因:onlineClassId 与 studentId不匹配,onlineClassId:{},studentId:{}",
                    onlineClassId, studentId);
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        // INVALID不允许进入教室
        if (OnlineClassEnum.Status.INVALID.toString().equals(onlineClass.getStatus())) {
            logger.error("没有权限进入教室，原因:onlineClass 状态为：INVALID,onlineClassId:{}", onlineClassId);
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        // TEACHER_NO_SHOW不允许进入教室
        if ("TEACHER_NO_SHOW".equals(onlineClass.getFinishType())) {
            logger.error("没有权限进入教室，原因:onlineClass FinishType为：TEACHER_NO_SHOW,onlineClassId:{}",
                    onlineClassId);
            model.addAttribute("info", errorHTML);
            return "error/info";
        }
        String isTrial = "0";
        if(lesson.getSerialNumber()!=null && lesson.getSerialNumber().startsWith("T1-")){
        	isTrial = "1"; //区分是否Trial课程
        }
        model.addAttribute("isTrial", isTrial);
        
        model.addAttribute("lesson", lesson);
        if (lesson.getSerialNumber().startsWith("P")) {
            model.addAllAttributes(
                    onlineclassService.enterPracticum(onlineClass, studentId, teacher, lesson));
            return view("online_class_practicum");
        } else if (lesson.getSerialNumber().startsWith("OPEN")) {
            model.addAllAttributes(
                    onlineclassService.enterOpen(onlineClass, studentId, teacher, lesson));
            return view("online_class_open");
        } else {
            /** 是否需要打开feedbackd */
            if ("feedback".equals(request.getParameter("from"))) {
                model.addAttribute("from", "feedback");
            }
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
    public String exitClassroom(HttpServletRequest request, HttpServletResponse response,
            long onlineClassId) {
        Teacher teacher = indexService.getTeacher(request);
        onlineclassService.exitclassroom(onlineClassId, teacher);

        return "redirect:/classrooms.shtml";
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
     * @param scheduleTime
     * @param model
     * @return
     */
    @RequestMapping("/sendTeacherInClassroom")
    public String sendTeacherInClassroom(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("onlineClassId") long onlineClassId, Model model) {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("onlineClassId", String.valueOf(onlineClassId));
        Teacher teacher = indexService.getTeacher(request);
        if (teacher != null) {
            logger.info("teacher : " + teacher.getId() + ",into classroomId:" + onlineClassId);
        }
        Map<String, Object> modelMap =
                onlineclassService.sendTeacherInClassroom(requestParams, teacher);
        model.addAllAttributes(modelMap);
        return jsonView(response, modelMap);
    }

    /**
     * audit 课程
     *
     * @Author:ALong
     * @Title: doAudit
     * @param type 完成类型
     * @param onlineClassId 课程Id
     * @param studentId 学生Id
     * @param comments 备注
     * @param finishType 完成类型
     * @return String
     * @date 2016年1月11日
     */
    @RequestMapping("/doAudit")
    public String doAudit(HttpServletRequest request, HttpServletResponse response,
            TeacherApplication teacherApplication, Model model) {
        Teacher pe = indexService.getTeacher(request);
        String type = ServletRequestUtils.getStringParameter(request, "type", "");
        String finishType = ServletRequestUtils.getStringParameter(request, "finishType", "");

        Map<String, Object> modelMap = Maps.newHashMap();
        if (type.startsWith(RecruitmentResult.TBD)) {
            modelMap = peSupervisorService.doPracticumForPE(pe, teacherApplication, type);
        } else {
            if (StringUtils.isEmpty(finishType)) {
                finishType = FinishType.AS_SCHEDULED;
            }
            modelMap = onlineclassService.updateAudit(pe, teacherApplication, type, finishType);

            // Finish课程
            if ((Boolean) modelMap.get("result")) {
                onlineclassService.finishPracticum(teacherApplication.getOnlineClassId(),
                        finishType);
            }
        }

        // 并异步调用AppServer发送邮件及消息
        Long teacherApplicationId = (Long) modelMap.get("teacherApplicationId");
        Teacher recruitTeacher = (Teacher) modelMap.get("recruitTeacher");
        if (Objects.nonNull(teacherApplicationId) && Objects.nonNull(recruitTeacher)) {
            appserverPracticumService.finishPracticumProcess(teacherApplicationId, recruitTeacher);
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
    public String isEmpty(HttpServletRequest request, HttpServletResponse response,
            @RequestParam long onlineClassId, @RequestParam long studentId, Model model) {
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
    public String sendHelp(HttpServletRequest request, HttpServletResponse response,
            @RequestParam String scheduleTime, @RequestParam long onlineClassId, Model model) {
        /* 计算服务器时间毫秒 */
        Teacher teacher = indexService.getTeacher(request);
        Map<String, Object> modelMap =
                onlineclassService.sendHelp(scheduleTime, onlineClassId, teacher);
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
    public String sendStarLogs(HttpServletRequest request, HttpServletResponse response,
            @RequestParam boolean send, @RequestParam long studentId,
            @RequestParam long onlineClassId, Model model) {
        Teacher teacher = indexService.getTeacher(request);
        onlineclassService.sendStarlogs(send, studentId, onlineClassId, teacher);
        return jsonView();
    }

}
