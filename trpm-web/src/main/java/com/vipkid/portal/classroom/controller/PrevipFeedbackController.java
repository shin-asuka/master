package com.vipkid.portal.classroom.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.portal.classroom.model.PrevipCommentsVo;
import com.vipkid.portal.classroom.service.PrevipFeedbackService;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.dao.StudentExamDao;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.StudentExam;
import com.vipkid.trpm.entity.teachercomment.TeacherComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by LP-813 on 2017/2/15.
 */
@RestController
@RestInterface(lifeCycle= TeacherEnum.LifeCycle.REGULAR)
@RequestMapping("/portal/comments/")
public class PrevipFeedbackController {

    private static Logger logger = LoggerFactory.getLogger(ClassroomController.class);

    @Autowired
    private PrevipFeedbackService previpFeedbackService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private StudentExamDao studentExamDao;

    @RequestMapping("/previp/save")
    public Map<String,Object> feedbackSubmit(HttpServletRequest request, HttpServletResponse response,
                                 @RequestBody PrevipCommentsVo teacherComment, Model model) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String serialNumber = teacherComment.getSerialNumber();
        String scheduledDateTime = teacherComment.getScheduleDateTime();
        logger.info("ReportController: feedbackSubmit() 参数为：serialNumber={}, scheduledDateTime={}, teacherComment={}", serialNumber, scheduledDateTime, JSON.toJSONString(teacherComment));
        teacherComment.setSubmitSource("PC");
        Map<String, Object> parmMap = previpFeedbackService.submitTeacherComment(teacherComment, loginService.getUser(),serialNumber,scheduledDateTime,false,true);

        long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("执行ReportController: feedbackSubmit()耗时：{} ", millis);
        return ApiResponseUtils.buildSuccessDataResp(parmMap);
    }

    @RequestMapping("/previp/view")
    public Map<String,Object> feedbackView(HttpServletRequest request, HttpServletResponse response,
                                             @RequestParam Long onlineClassId ,@RequestParam Integer studentId) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        logger.info("ReportController: feedbackView() 参数为：onlineClassId={}, studentId={}", onlineClassId, studentId);
        Map map = Maps.newHashMap();
        // 查询课程信息
        OnlineClass onlineClass = previpFeedbackService.findOnlineClassById(onlineClassId);
        map.put("onlineClass", onlineClass);

        // 查询Lesson
        Lesson lesson = previpFeedbackService.findLessonById(onlineClass.getLessonId());
        map.put("lesson", lesson);

        // 查询FeedBack信息
        TeacherComment teacherComment = previpFeedbackService.findCFByOnlineClassIdAndStudentIdAndTeacherId(onlineClassId, studentId,onlineClass,lesson);
        if(teacherComment!=null){
            String trialLevelResultDisplay = previpFeedbackService.handleTeacherComment(teacherComment.getTrialLevelResult());
            teacherComment.setTrialLevelResult(trialLevelResultDisplay);
        }
        map.put("teacherComment", teacherComment);
        //查询StudentExam信息
        StudentExam studentExam = studentExamDao.findStudentExamByStudentId(studentId);
        map.put("studentExam", previpFeedbackService.handleExamLevel(studentExam, lesson.getSerialNumber()));

        map.put("studentId", studentId);

        long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("执行ReportController: feedbackView()耗时：{} ", millis);
        return ApiResponseUtils.buildSuccessDataResp(map);
    }


}
