package com.vipkid.portal.classroom.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.vipkid.mq.message.FinishOnlineClassMessage;
import com.vipkid.mq.service.PayrollMessageService;
import com.vipkid.portal.classroom.model.bo.MajorCommentsBo;
import com.vipkid.portal.classroom.util.Convertor;
import com.vipkid.rest.security.AppContext;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.entity.report.DemoReports;
import com.vipkid.trpm.entity.report.ReportLevels;
import com.vipkid.trpm.entity.teachercomment.TeacherComment;
import com.vipkid.trpm.entity.teachercomment.TeacherCommentResult;
import com.vipkid.trpm.entity.teachercomment.TeacherCommentUpdateDto;
import com.vipkid.trpm.service.media.AbstarctMediaService;
import com.vipkid.trpm.service.portal.ReportEmailService;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class MajorFeedbackService {

    private static Logger logger = LoggerFactory.getLogger(FeedbackService.class);

    private static final Executor executor = Executors.newFixedThreadPool(10);

    private static final Executor sendEmailExecutor = Executors.newFixedThreadPool(10);

    private static DemoReports demoReports = null;

    private static ReportLevels reportLevels = null;

    @Autowired
    private ReportEmailService emailService;

    @Autowired
    private AbstarctMediaService mediaService;

    @Autowired
    private AssessmentReportDao assessmentReportDao;

    @Autowired
    private DemoReportDao demoReportDao;

    @Autowired
    private OnlineClassDao onlineClassDao;

    @Autowired
    private LessonDao lessonDao;

    @Autowired
    private StudentExamDao studentExamDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private AuditDao auditDao;

    @Autowired
    private PayrollMessageService payrollMessageService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private LoginService loginService;

    public Map<String, Object> submitTeacherComment(MajorCommentsBo teacherComment, User user, String serialNumber,
                                                    String scheduledDateTime, boolean isFromH5) {
        // 如果ID为0 则抛出异常并回滚
        checkArgument(teacherComment.getId() != null && 0 != teacherComment.getId(), "Argument teacherComment id equals 0");

        teacherComment.setEmpty(0);
        // 日志记录参数准备
        TeacherCommentResult oldtcFromAPI = teacherService
                .findByTeacherCommentId(String.valueOf(teacherComment.getId()));
        if (oldtcFromAPI == null) {
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("result", false);
            paramMap.put("msg", "You submit a wrong feedback.");
            return paramMap;
        }
        TeacherComment oldtc = new TeacherComment(oldtcFromAPI);
        if (isFromH5) {
            //从APP的h5页面过来的
            Teacher teacher = AppContext.getTeacher();
            scheduledDateTime = DateUtils
                    .formatTo(oldtcFromAPI.getScheduledDateTime().toInstant(), teacher.getTimezone(),
                            DateUtils.FMT_YMD_HMS);
        }

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("teacherId", user.getId());
        paramMap.put("onlineClassId", oldtc.getOnlineClassId());
        paramMap.put("studentId", oldtc.getStudentId());

        // 如果已经提交过，则不允许保存
        if (StringUtils.isNotBlank(oldtc.getTeacherFeedback())) {
            paramMap.put("result", false);
            paramMap.put("msg", "You have already submitted feedback.");
            return paramMap;
        }
        // 如果firstDateTime为空则新增
        if (oldtc.getFirstDateTime() == null) {
            teacherComment.setFirstDateTime(new Timestamp(System.currentTimeMillis()));
        }
        teacherComment.setLastDateTime(new Timestamp(System.currentTimeMillis()));

        // 更新后并返回影响的行数
        //int status = teacherCommentDao.update(teacherComment);
        TeacherCommentUpdateDto tcuDto = new TeacherCommentUpdateDto(Convertor.toSubmitTeacherCommentDto(teacherComment));
        boolean success = teacherService.updateTeacherComment(tcuDto);

        if (success) {
            logger.info("FEEDBACK_SAVE_OK,paramMap = {},teacherName = {},teacherComment ={}",
                    JSON.toJSONString(paramMap), user.getUsername(), teacherComment);
            paramMap.put("result", true);
        } else {
            logger.error("FEEDBACK_SAVE_FAIL,paramMap = {},teacherName = {},teacherComment ={}",
                    JSON.toJSONString(paramMap), user.getUsername(), teacherComment);
            paramMap.put("result", false);
        }

        // 填写评语发送消息
        Long onlineClassId = oldtc.getOnlineClassId();
        if (teacherComment != null && onlineClassId != null && onlineClassId > 0
                && teacherComment.getTeacherFeedback() != null) {
            logger.info("填写评语发送消息  onlineClassId = {} ", onlineClassId);

            executor.execute(() -> payrollMessageService.sendFinishOnlineClassMessage(Convertor.toSubmitTeacherCommentDto(teacherComment), onlineClassId,
                    FinishOnlineClassMessage.OperatorType.ADD_TEACHER_COMMENTS));
        }

        //判断PerformanceAdjust给CLT发邮件
        if (teacherComment.getPerformanceAdjust() != null && teacherComment.getPerformance() != null
                && (teacherComment.getPerformanceAdjust() == 1 && teacherComment.getPerformance() != 0)) {
            logger.info("判断PerformanceAdjust给CLT发邮件: studentId = {}, serialNumber = {}, scheduledDateTime = {} ",
                    oldtc.getStudentId(), serialNumber, scheduledDateTime);
            final String finalScheduledDateTime = scheduledDateTime;
            sendEmailExecutor.execute(() -> {
                emailService.sendEmail4PerformanceAdjust2CLT(oldtc.getStudentId(), serialNumber,
                        finalScheduledDateTime, teacherComment.getPerformance());
            });
        }

        if (teacherComment.getPerformance() != null && (teacherComment.getPerformance() == 1 || teacherComment.getPerformance() == 5)) {
            logger.info("检查Performance判断是否给CLT发邮件: studentId = {}, serialNumber = {} ", oldtc.getStudentId(), serialNumber);
            sendEmailExecutor.execute(() -> {
                emailService.sendEmail4Performance2CLT(oldtc.getStudentId(), serialNumber);
            });
        }

        return paramMap;
    }

}
