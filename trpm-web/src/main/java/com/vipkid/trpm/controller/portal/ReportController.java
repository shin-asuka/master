package com.vipkid.trpm.controller.portal;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.dao.StudentExamDao;
import com.vipkid.trpm.entity.AssessmentReport;
import com.vipkid.trpm.entity.DemoReport;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.StudentExam;
import com.vipkid.trpm.entity.teachercomment.SubmitTeacherCommentDto;
import com.vipkid.trpm.entity.teachercomment.TeacherComment;
import com.vipkid.trpm.entity.teachercomment.TeacherCommentResult;
import com.vipkid.trpm.service.portal.ReportService;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.util.DateUtils;

/**
 * 1.主要负责UAReport、DemoReport、FeebBack等模块的参数接收和页面跳转<br>
 * 
 * UAReport对应表 AssessmentsReport DemoReport对应表 Demoreport FeedBack对应表TeacherComment
 * 
 * @Title: ReportController.java
 * @Package com.vipkid.trpm.controller.portal
 * @author ALong
 * @date 2015年12月16日 下午8:56:43
 */
@Controller
public class ReportController extends AbstractPortalController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private StudentExamDao studentExamDao;
    
    @Autowired
    private LoginService loginService;

    @Autowired
    private TeacherService teacherService;
    
    /**
     * UA报告上传页面进入
     * 
     * @Author:ALong
     * @Title: uploadPage
     * @param report
     * @param model
     * @return String
     * @date 2015年12月12日
     */
    @RequestMapping("/uploadPage")
    public String uaReport(AssessmentReport report, HttpServletRequest request, Model model) {
        logger.info("ReportController: uaReport() 参数为：AssessmentReport={}", JsonUtils.toJSONString(report));
        model = this.uploadData(report, request, model, "0");
        return view("ua_report_upload");
    }

    @RequestMapping("/uaReportShow")
    public String uaReportShow(AssessmentReport report, Long onlineClassId, HttpServletRequest request, Model model) {
        logger.info("ReportController: uaReportShow() 参数为：AssessmentReport={}, onlineClassId={}", JsonUtils.toJSONString(report), onlineClassId);

        model.addAttribute("onlineClassId", onlineClassId);

        return view("ua_report_show");
    }

    /**
     * PracticumReport报告上传页面进入
     * 
     * @Author:ALong
     * @Title: uploadPracticum
     * @param report
     * @param model
     * @return String
     * @date 2015年12月12日
     */
    @RequestMapping("/uploadPracticum")
    public String practicumReport(AssessmentReport report, HttpServletRequest request, Model model) {
        logger.info("ReportController: practicumReport() 参数为：AssessmentReport={}", JsonUtils.toJSONString(report));
        model = this.uploadData(report, request, model, "1");
        return view("practicum_report_upload");
    }

    /**
     * UA报告上传实现
     * 
     * @Author:ALong
     * @Title: upload
     * @param request
     * @param response
     * @param report
     * @param file
     * @return String
     * @date 2015年12月12日
     */
    @RequestMapping(value = "/uploadReport", method = RequestMethod.POST)
    public String uaReportSubmit(MultipartHttpServletRequest request, HttpServletResponse response,
            AssessmentReport report, @RequestParam("file") MultipartFile file) {
        logger.info("ReportController: uaReportSubmit() 参数为：AssessmentReport={}", JsonUtils.toJSONString(report));

        String score = request.getParameter("score");
        String onlineClassId = request.getParameter("onlineClassId");
        Map<String, Object> map = reportService.saveUAReport(report, file, request.getFile(file.getName()).getSize(),
        		loginService.getUser(), score, onlineClassId);

        return jsonView(response, map);
    }

    /**
     * Practicum 报告上传实现
     * 
     * @Author:ALong
     * @Title: upload
     * @param request
     * @param response
     * @param report
     * @param file
     * @return String
     * @date 2015年12月12日
     */
    @RequestMapping(value = "/uploadPracticumReport", method = RequestMethod.POST)
    public String practicumReportSubmit(MultipartHttpServletRequest request, HttpServletResponse response,
            AssessmentReport report, @RequestParam("file") MultipartFile file) {
        logger.info("ReportController: practicumReportSubmit() 参数为：AssessmentReport={}", JsonUtils.toJSONString(report));

        String score = request.getParameter("score");
        String onlineClassId = request.getParameter("onlineClassId");
        Map<String, Object> map = reportService.savePracticumReport(report, file,
                request.getFile(file.getName()).getSize(), loginService.getUser(), score, onlineClassId);

        return jsonView(response, map);
    }

    /**
     * 
     * 进入DemoReport页面
     * 
     * @Author:ALong
     * @Title: demoReport
     * @param response
     * @param onlineClassId
     * @param studentId
     * @return String
     * @date 2015年12月12日
     */
    @RequestMapping("/demoReport")
    public String demoReport(HttpServletResponse response, String serialNumber, long onlineClassId, long studentId,
            Model model) {
        logger.info("ReportController: demoReport() 参数为：serialNumber={}, onlineClassId={}, studentId={}", serialNumber, onlineClassId, studentId);

        /* 根据参数查询当前的DemoReport */
        initData(response, serialNumber, onlineClassId, studentId, model);
        return view("demo_report_page");
    }

    /**
     * 
     * 从教室里面进入DemoReport页面，右边滑动效果
     * 
     * @Author:ALong
     * @Title: demoReport
     * @param response
     * @param onlineClassId
     * @param studentId
     * @return String
     * @date 2015年12月12日
     */
    @RequestMapping("/demoReportRoom")
    public String demoReportRoom(HttpServletResponse response, String serialNumber, long onlineClassId, long studentId,
            Model model) {
        logger.info("ReportController: demoReportRoom() 参数为：serialNumber={}, onlineClassId={}, studentId={}", serialNumber, onlineClassId, studentId);

        /* 根据参数查询当前的DemoReport */
        initData(response, serialNumber, onlineClassId, studentId, model);
        return view("demo_report_room");
    }

    /**
     * 保存或提交DemoReport
     * 
     * @Author:ALong @Title: reportSubmit @param response @param demoReport @param isSubmited @return String @date
     * 2015年12月14日 @throws
     */
    @RequestMapping("/reportSubmit")
    public String demoReportSubmit(HttpServletRequest request, HttpServletResponse response, DemoReport demoReport,
            boolean isSubmited) {
        logger.info("ReportController: demoReportSubmit() 参数为：demoReport={}, isSubmited={}", JsonUtils.toJSONString(demoReport), isSubmited);

        Map<String, Object> map = reportService.saveOrSubmitDemoReport(demoReport, isSubmited,
        		loginService.getUser());

        return jsonView(response, map);
    }

    /**
     * 教室右侧Feedback 进入
     * 
     * @param request
     * @param response
     * @param onlineClassId
     * @param studentId
     * @param model
     * @return
     */
    @RequestMapping("/feedback")
    public String feedback(HttpServletRequest request, HttpServletResponse response, @RequestParam long onlineClassId,
            @RequestParam long studentId, Model model) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        logger.info("ReportController: feedback() 参数为：onlineClassId={}, studentId={}", onlineClassId, studentId);

        // 查询课程信息
        OnlineClass onlineClass = reportService.findOnlineClassById(onlineClassId);
        model.addAttribute("onlineClass", onlineClass);

        // 查询Lesson
        Lesson lesson = reportService.findLessonById(onlineClass.getLessonId());
        model.addAttribute("lesson", lesson);

        // 查询FeedBack信息
        TeacherComment teacherComment = reportService.findCFByOnlineClassIdAndStudentIdAndTeacherId(onlineClassId, studentId,onlineClass,lesson);
        if(teacherComment!=null){
            String trialLevelResultDisplay = reportService.handleTeacherComment(teacherComment.getTrialLevelResult());
            teacherComment.setTrialLevelResult(trialLevelResultDisplay);
        }
        model.addAttribute("teacherComment", teacherComment);
        //查询StudentExam信息
        StudentExam studentExam = studentExamDao.findStudentExamByStudentId(studentId);
        model.addAttribute("studentExam", reportService.handleExamLevel(studentExam, lesson.getSerialNumber()));

        model.addAttribute("studentId", studentId);

        long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("执行ReportController: feedback()耗时：{} ", millis);

        if(teacherComment!=null && teacherComment.getPreVip()!=null && teacherComment.getPreVip() ){
            //判断是否unit<4
            Integer unitNo=0;
            String [] arrays = StringUtils.split(lesson.getSerialNumber().toLowerCase(),"-");
            for(String arr : arrays){
                if(arr.indexOf("u")>-1){
                    unitNo = NumberUtils.toInt(arr.replace("u", "").trim());
                }
            }
            if(unitNo < 4){
                model.addAttribute("isBelowU4",true);
            }
            return view("online_class_feedback_previp");
        }else{
            return view("online_class_feedback");
        }

    }

    @RequestMapping("/unitAssessment")
    public String unitAssessment(@RequestParam long onlineClassId, HttpServletRequest request,
            HttpServletResponse response, Model model) {
        logger.info("ReportController: unitAssessment() 参数为：onlineClassId={}", onlineClassId);

        model.addAttribute("onlineClassId", onlineClassId);
        return view("online_class_unitAssessment");
    }

    /**
     * feedback保存，任何时候都可以保存 2016-5-10 修改feedback 只允许提交一次，因为要通知家长老师有反馈
     * 
     * @Author:ALong @Title: commentSubmit @param request @param response @param teacherComment @return String @date
     * 2015年12月16日 @throws
     */
    @RequestMapping("/commentSubmit")
    public String feedbackSubmit(HttpServletRequest request, HttpServletResponse response,
        SubmitTeacherCommentDto teacherComment, Model model) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String serialNumber = request.getParameter("serialNumber");
        String scheduledDateTime = request.getParameter("scheduledDateTime");
        logger.info("ReportController: feedbackSubmit() 参数为：serialNumber={}, scheduledDateTime={}, teacherComment={}", serialNumber, scheduledDateTime, JsonUtils.toJSONString(teacherComment));
        teacherComment.setSubmitSource("PC");
        Map<String, Object> parmMap = reportService.submitTeacherComment(teacherComment, loginService.getUser(),serialNumber,scheduledDateTime,false);

        long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("执行ReportController: feedbackSubmit()耗时：{} ", millis);

        return jsonView(response, parmMap);
    }

    @RequestMapping("/getComment")
    public String getComment(HttpServletRequest request, HttpServletResponse response, @RequestParam long id) {
        logger.info("ReportController: getComment() 参数为：id={}", id);
        Map<String, Object> parmMap = Maps.newHashMap();
        // 查询FeedBack信息
        TeacherCommentResult tcFromApi = teacherService.findByTeacherCommentId(String.valueOf(id));
        if(null == tcFromApi){
            parmMap.put("status", false);
            return jsonView(response, parmMap);
        }
        TeacherComment teacherComment = new TeacherComment(tcFromApi);

        if (Objects.nonNull(teacherComment) && Objects.nonNull(teacherComment.getFirstDateTime())) {
            parmMap.put("status", true);
            parmMap.put("teacherComment", teacherComment);
        } else {
            parmMap.put("status", false);
        }
        return jsonView(response, parmMap);
    }

    /**
     * info 展示 学生基本信息 / 学生测试信息 / 教师feedback
     * 
     * @Author:ALong @Title: openInfo @param request @param response @param studentId @param model @return String @date
     * 2015年12月18日 @throws
     */
    @RequestMapping("/openInfo")
    public String openInfo(HttpServletRequest request, HttpServletResponse response, long studentId, String serialNum,
            Model model) {
        logger.info("ReportController: openInfo() 参数为：studentId={}, serialNum={}", studentId, serialNum);

        // 查询学生个人信息
        model.addAttribute("student", reportService.findStudentById(studentId));

        // 查询学生考试情况
        StudentExam studentExam = reportService.findStudentExamByStudentId(studentId);

        // 处理考试名称
        model.addAttribute("studentExam", reportService.handleExamLevel(studentExam, serialNum));

        // 查询教师评价
        model.addAttribute("teacherComments",
                teacherService.findTCByStudentIdAndGroupByOnlineClassId(String.valueOf(studentId)));

        return view("online_class_info");
    }

    @RequestMapping("/findServerTime")
    public String findServerTime(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("serverTime", DateUtils.getThisYearMonth(TimeZone.getDefault().getID()));
        return jsonView(response, map);
    }

    /**
     * DemoReport
     * 
     * 根据参数查询当前的DemoReport
     * 
     * @Author:ALong
     * @Title: initData
     * @param response
     * @param serialNumber
     * @param onlineClassId
     * @param studentId
     * @param model
     * @return void
     * @date 2015年12月23日
     */
    private void initData(HttpServletResponse response, String serialNumber, long onlineClassId, long studentId,
            Model model) {
        logger.info("ReportController: initData() 参数为：serialNumber={}, onlineClassId={}, studentId={}", serialNumber, onlineClassId, studentId);

        DemoReport currentReport = reportService.getDemoReport(studentId, onlineClassId);
        if (currentReport == null)
            currentReport = new DemoReport();
        model.addAttribute("onlineClassId", onlineClassId);
        model.addAttribute("currentReport", currentReport);

        model.addAttribute("demoReports", reportService.getDemoReports());
        model.addAttribute("reportLevels", reportService.getReportLevels());
    }


    private Model uploadData(AssessmentReport report, HttpServletRequest request, Model model, String classType) {
        logger.info("ReportController: uploadData() 参数为：report={}, classType={}", JsonUtils.toJSONString(report), classType);

        model.addAttribute("template", report);

        String onlineClassId = request.getParameter("onlineClassId");
        OnlineClass onlineClass = reportService.findOnlineClassById(Long.valueOf(onlineClassId));
        model.addAttribute("onlineClass", onlineClass);
        if (onlineClass.getScheduledDateTime().before(new Timestamp(System.currentTimeMillis()))
                && !OnlineClassEnum.ClassStatus.INVALID.toString().equals(onlineClass.getStatus())) {
            model.addAttribute("mark", onlineClass.getScheduledDateTime());
        }

        AssessmentReport reports = null;

        /** 主修课按照student和lessonSN查询 */
        /** 主修课优先按照onlineClass 查询，如果没有则按照student和lessonSN查询 */
        if ("0".equals(classType)) {
            if (DateUtils.isSearchById(onlineClass.getScheduledDateTime().getTime())) {
                reports = reportService.findReportByClassId(Long.valueOf(onlineClassId));
            } else {
                reports = reportService.findReportByStudentIdAndName(report.getName(), report.getStudentId());
            }
            /** Practicum课程按照classId查询 */
        } else if ("1".equals(classType)) {
            reports = reportService.findReportByClassId(Long.valueOf(onlineClassId));
        }

        model.addAttribute("bean", reports);

        /* 如果报告不等于空则需要对其名称进行显示处理 */
        if (reports != null && reports.getUrl() != null) {
            String complete = reports.getUrl().substring(reports.getUrl().lastIndexOf("/") + 1);
            model.addAttribute("uploadName", complete);
        }
        return model;
    }


}
