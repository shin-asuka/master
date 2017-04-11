package com.vipkid.portal.classroom.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.portal.classroom.model.PrevipCommentsVo;
import com.vipkid.portal.classroom.model.bo.PrevipCommentsBo;
import com.vipkid.portal.classroom.service.ClassFeedbackService;
import com.vipkid.portal.classroom.service.FeedbackService;
import com.vipkid.portal.classroom.service.PrevipFeedbackService;
import com.vipkid.portal.classroom.util.Convertor;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.dao.StudentExamDao;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.StudentExam;
import com.vipkid.trpm.entity.teachercomment.TeacherComment;
import com.vipkid.trpm.service.portal.OnlineClassService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
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
    private ClassFeedbackService classFeedbackService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private StudentExamDao studentExamDao;
    @Autowired
    @Qualifier("previpFeedbackService")
    private FeedbackService feedbackService;
    @Autowired
    private OnlineClassService onlineClassService;

    @RequestMapping("/previp/save")
    public Map<String,Object> feedbackSubmit(HttpServletRequest request, HttpServletResponse response,
                                  @RequestBody PrevipCommentsVo teacherCommentVo) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        PrevipCommentsBo teacherComment = Convertor.toPrevipCommentsBo(teacherCommentVo);
        String serialNumber = teacherComment.getSerialNumber();
        OnlineClass onlineClass = onlineClassService.getOnlineClassById(teacherCommentVo.getOnlineClassId());
        String scheduledDateTime = "";
        if(null != onlineClass) {
            scheduledDateTime = DateFormatUtils.format(onlineClass.getScheduledDateTime(),"yyyy-MM-dd HH:mm:ss");
        }

        logger.info("ReportController: feedbackSubmit() 参数为：serialNumber={}, scheduledDateTime={}, teacherComment={}", serialNumber, scheduledDateTime, JSON.toJSONString(teacherComment));
        teacherComment.setSubmitSource("PC");

        Map<String, Object> parmMap = feedbackService.submitTeacherComment(teacherComment, loginService.getUser(),serialNumber,scheduledDateTime,false);
        long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("执行ReportController: feedbackSubmit()耗时：{} ", millis);
        return parmMap;
    }

    @RequestMapping("/previp/view")
    public Map<String,Object> feedbackView(HttpServletRequest request, HttpServletResponse response,
                                             @RequestParam Long onlineClassId ,@RequestParam Integer studentId) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        logger.info("ReportController: feedbackView() 参数为：onlineClassId={}, studentId={}", onlineClassId, studentId);
        Map map = Maps.newHashMap();
        // 查询课程信息
        OnlineClass onlineClass = classFeedbackService.findOnlineClassById(onlineClassId);
        map.put("onlineClass", onlineClass);

        // 查询Lesson
        Lesson lesson = classFeedbackService.findLessonById(onlineClass.getLessonId());
        map.put("lesson", lesson);

        // 查询FeedBack信息
        TeacherComment teacherComment = classFeedbackService.findCFByOnlineClassIdAndStudentIdAndTeacherId(onlineClassId, studentId,onlineClass,lesson);
        if(teacherComment!=null){
            String trialLevelResultDisplay = classFeedbackService.handleTeacherComment(teacherComment.getTrialLevelResult());
            teacherComment.setTrialLevelResult(trialLevelResultDisplay);
        }
        map.put("teacherComment", teacherComment);
        //查询StudentExam信息
        StudentExam studentExam = studentExamDao.findStudentExamByStudentId(studentId);
        map.put("studentExam", classFeedbackService.handleExamLevel(studentExam, lesson.getSerialNumber()));

        map.put("studentId", studentId);

        long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("执行ReportController: feedbackView()耗时：{} ", millis);
        return map;
    }


}
