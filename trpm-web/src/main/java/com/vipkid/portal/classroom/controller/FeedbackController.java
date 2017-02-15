package com.vipkid.portal.classroom.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.vipkid.mq.message.FinishOnlineClassMessage;
import com.vipkid.portal.classroom.model.ClassRoomVo;
import com.vipkid.portal.classroom.model.TeacherCommentVo;
import com.vipkid.portal.classroom.service.FeedbackService;
import com.vipkid.rest.security.AppContext;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.teachercomment.TeacherComment;
import com.vipkid.trpm.entity.teachercomment.TeacherCommentResult;
import com.vipkid.trpm.entity.teachercomment.TeacherCommentUpdateDto;
import com.vipkid.trpm.service.portal.ReportService;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.RestInterface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RestInterface(lifeCycle=LifeCycle.REGULAR)
@RequestMapping("/portal/comments/")
public class FeedbackController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "previp/save", method = RequestMethod.POST)
    public Map<String, Object> previpSave(HttpServletRequest request, HttpServletResponse response, @RequestBody TeacherCommentVo teacherCommentVo) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String serialNumber = teacherCommentVo.getSerialNumber();
        String scheduledDateTime = teacherCommentVo.getScheduleDateTime();
        logger.info("FeedbackController: previpSave() 参数为：serialNumber={}, scheduledDateTime={}, teacherCommentVo={}", serialNumber, scheduledDateTime, JSON.toJSONString(teacherCommentVo));
        teacherCommentVo.setSubmitSource("PC");
        Map<String, Object> parmMap = feedbackService.submitTeacherComment(teacherCommentVo, loginService.getUser(),serialNumber,scheduledDateTime,false,true);
        long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("执行ReportController: feedbackSubmit()耗时：{} ", millis);
        return ApiResponseUtils.buildSuccessDataResp(parmMap);
    }
}