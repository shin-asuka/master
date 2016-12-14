package com.vipkid.trpm.controller.pe;

import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.constant.ApplicationConstant.FinishType;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherPe;
import com.vipkid.trpm.service.pe.AppserverPracticumService;
import com.vipkid.trpm.service.pe.PeSupervisorService;
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

    @RequestMapping("/pesupervisor")
    public String peSupervisor(HttpServletRequest request, HttpServletResponse response,
            Model model) {
        model.addAttribute("linePerPage", LINE_PER_PAGE);
        Teacher teacher = loginService.getTeacher();
        model.addAttribute("totalLine", peSupervisorService.totalPe(teacher.getId()));
        return view("classrooms_pe");
    }

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
            model.addAttribute("teacherApplication", teacherApplication);

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
        int peId = ServletRequestUtils.getIntParameter(request, "peId", -1);
        String type = ServletRequestUtils.getStringParameter(request, "type", "");
        String finishType = ServletRequestUtils.getStringParameter(request, "finishType", "");

        if (StringUtils.isEmpty(finishType)) {
            finishType = FinishType.AS_SCHEDULED;
        }
        Map<String, Object> modelMap = peSupervisorService.updateAudit(peSupervisor,
                teacherApplication, type, finishType, peId);
        model.addAllAttributes(modelMap);
        Teacher recruitTeacher = (Teacher) modelMap.get("recruitTeacher");
        // Finish课程
        if ((Boolean) modelMap.get("result")) {
            onlineclassService.finishPracticum(teacherApplication, finishType, peSupervisor, recruitTeacher);
        }

        // 并异步调用AppServer发送邮件及消息
        Long teacherApplicationId = (Long) modelMap.get("teacherApplicationId");
        if (Objects.nonNull(teacherApplicationId) && Objects.nonNull(recruitTeacher)) {
            appserverPracticumService.finishPracticumProcess(teacherApplicationId, recruitTeacher);
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
