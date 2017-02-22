package com.vipkid.portal.classroom.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.vipkid.mq.service.PayrollMessageService;
import com.vipkid.portal.classroom.model.bo.FeedbackBo;
import com.vipkid.portal.classroom.model.bo.MajorCommentsBo;
import com.vipkid.portal.classroom.util.Convertor;
import com.vipkid.rest.security.AppContext;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.*;
import com.vipkid.trpm.entity.report.DemoReports;
import com.vipkid.trpm.entity.report.ReportLevels;
import com.vipkid.trpm.entity.teachercomment.TeacherComment;
import com.vipkid.trpm.entity.teachercomment.TeacherCommentResult;
import com.vipkid.trpm.entity.teachercomment.TeacherCommentUpdateDto;
import com.vipkid.trpm.service.media.AbstarctMediaService;
import com.vipkid.trpm.service.portal.ReportEmailService;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.util.DateUtils;
import com.vipkid.trpm.util.LessonSerialNumber;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class ClassFeedbackService {

    private static Logger logger = LoggerFactory.getLogger(ClassFeedbackService.class);

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

    public OnlineClass findOnlineClassById(long onlineClassId) {
        if (0 == onlineClassId) {
            return null;
        }
        return onlineClassDao.findById(onlineClassId);
    }

    /**
     * feedback<br/>
     * 根据lessonId 查找 Lesson<br/>
     * 存在则返回本身，不存在则返回null<br/>
     *
     * @param lessonId
     * @return Lesson
     * @Author:ALong
     * @Title: findLessonById
     * @date 2015年12月16日
     */
    public Lesson findLessonById(long lessonId) {
        if (0 == lessonId) {
            return null;
        }
        return lessonDao.findById(lessonId);
    }

    /**
     * feedback<br/>
     * <p/>
     * 根据onlineclassid 和studentid 得到feebback<br/>
     * <p/>
     * 存在则返回本身，错误则返回null，不存在则新建<br/>
     *
     * @param onlineClassId
     * @param studentId
     * @return
     * @Author:ALong
     * @date 2015年12月16日
     */
    public TeacherComment findCFByOnlineClassIdAndStudentIdAndTeacherId(long onlineClassId, long studentId,
                                                                        OnlineClass onlineClass, Lesson lesson) {
        logger.info("onlineClassId：" + onlineClassId + ";studentId:" + studentId);
        if (0 == onlineClassId || 0 == studentId || onlineClass == null || lesson == null) {
            return null;
        }
        logger.info("teacherId：" + onlineClass.getTeacherId() + ";lessonId:" + lesson.getId());
        TeacherCommentUpdateDto tcuDto = new TeacherCommentUpdateDto();
        tcuDto.setStudentId(studentId);
        tcuDto.setOnlineClassId(onlineClassId);
        tcuDto.setTeacherId(onlineClass.getTeacherId());

        tcuDto.setLessonId(lesson.getId());
        tcuDto.setLessonName(lesson.getName());
        tcuDto.setLessonSerialNumber(lesson.getSerialNumber());
        tcuDto.setLearningCycleId(lesson.getLearningCycleId());
        tcuDto.setScheduledDateTime(new Date(onlineClass.getScheduledDateTime().getTime()));
        tcuDto.setCreateTime(new Timestamp(System.currentTimeMillis()));

        Course course = courseDao.findIdsByLessonId(lesson.getId());
        tcuDto.setCourseId(course.getId());
        tcuDto.setCourseType(course.getType());
        tcuDto.setUnitId(course.getUnitId());
        boolean isPrevip = LessonSerialNumber.isPreVipkidLesson(lesson.getSerialNumber());

        tcuDto.setStars(0);
        tcuDto.setEmpty(true);
        //先按teacher_id和student_id和onlineClassId检索,如果有则返回已存在的,如果没有则插入一条新的
        TeacherCommentResult tcResult = teacherService.checkExistOrInsertOne(tcuDto);
        if (tcResult == null) {
            logger.error("进入教室后,老师点击feedback按钮无法获取teacherComment信息!");
            return null;
        } else {
            TeacherComment comment = new TeacherComment(tcResult);
            comment.setPreVip(isPrevip);
            return comment;
        }
    }

    public StudentExam findStudentExamByStudentId(long studentId) {
        return studentExamDao.findStudentExamByStudentId(studentId);
    }


    public StudentExam handleExamLevel(StudentExam studentExam, String serialNum) {
        logger.info("ReportController: handleExamLevel() 参数为：serialNum={}, studentExam={}", serialNum, JSON.toJSONString(studentExam));
        // studentExam 不为空则进行替换逻辑
        if (studentExam != null) {
            // ExamLevel 不为空则进行替换逻辑
            if (studentExam.getExamLevel() != null) {
                String lowerCase = studentExam.getExamLevel().toLowerCase();
                String examLevel = "No Computer Test result.";
                if ("l1u0".equals(lowerCase)) {
                    studentExam.setExamLevel("Computer Test result  is Level 0 Unit 0");
                } else if (lowerCase.equals("l1u1")) {
                    examLevel = "Computer Test result is L1U1(PreVIP).";
                } else if (lowerCase.startsWith("l")) {
                    examLevel = "Computer Test result is " + lowerCase.replaceAll("l", "Level ").replaceAll("u", " Unit ") + ".";
                }
                if (serialNum != null) {
                    switch (serialNum) {
                        case ApplicationConstant.TrailLessonConstants.L0:
                            if (StringUtils.equals(examLevel, "No Computer Test result.")) {
                                examLevel = examLevel + " Please use the PreVIP courseware.";
                            } else {
                                examLevel = " Please ignore the Computer Test result and use the PreVIP courseware.";
                            }
                            break;
                        case ApplicationConstant.TrailLessonConstants.L1:
                            if (StringUtils.equals(examLevel, "No Computer Test result.") || StringUtils.equals(examLevel, "Computer Test result is L1U1(PreVIP).")) {
                                examLevel = examLevel + " Please use the Level 2 Unit 01 courseware.";
                            } else {
                                examLevel = examLevel + " Please use the " + lowerCase.replace("l", "Level ").replace("u", " Unit ") + " courseware.";
                            }
                            break;
                        case ApplicationConstant.TrailLessonConstants.L2:
                            if (StringUtils.equals(examLevel, "No Computer Test result.") || StringUtils.equals(examLevel, "Computer Test result is L1U1(PreVIP).")) {
                                examLevel = examLevel + " Please use the use Level 2 Unit 04 courseware.";
                            } else {
                                examLevel = examLevel + " Please use the " + lowerCase.replace("l", "Level ").replace("u", " Unit ") + " courseware.";
                            }
                            break;
                        case ApplicationConstant.TrailLessonConstants.L3:
                            if (StringUtils.equals(examLevel, "No Computer Test result.") || StringUtils.equals(examLevel, "Computer Test result is L1U1(PreVIP).")) {
                                examLevel = examLevel + " Please use the Level 3 Unit 01 courseware.";
                            } else {
                                examLevel = examLevel + " Please use the " + lowerCase.replace("l", "Level ").replace("u", " Unit ") + " courseware.";
                            }
                            break;
                        case ApplicationConstant.TrailLessonConstants.L4:
                            if (StringUtils.equals(examLevel, "No Computer Test result.") || StringUtils.equals(examLevel, "Computer Test result is L1U1(PreVIP).")) {
                                examLevel = examLevel + " Please use the Level 4 Unit 01 courseware.";
                            } else {
                                examLevel = examLevel + " Please use the " + lowerCase.replace("l", "Level ").replace("u", " Unit ") + " courseware.";
                            }
                            break;
                        default:
                            break;
                    }
                }
                studentExam.setExamLevel(examLevel);
            }
        } else {
            // studentExam 为空则返回空对象
            studentExam = new StudentExam();
            // ExamLevel 为空则根据Lession的SerialNum进行处理
            if (serialNum != null) {
                switch (serialNum) {
                    case ApplicationConstant.TrailLessonConstants.L0:
                        studentExam.setExamLevel("No Computer Test result. Please use the PreVIP courseware.");
                        break;
                    case ApplicationConstant.TrailLessonConstants.L1:
                        studentExam.setExamLevel("No Computer Test result. Please use the Level2 Unit1 courseware.");
                        break;
                    case ApplicationConstant.TrailLessonConstants.L2:
                        studentExam.setExamLevel("No Computer Test result. Please use the Level2 Unit4 courseware.");
                        break;
                    case ApplicationConstant.TrailLessonConstants.L3:
                        studentExam.setExamLevel("No Computer Test result. Please use the Level3 Unit1 courseware.");
                        break;
                    case ApplicationConstant.TrailLessonConstants.L4:
                        studentExam.setExamLevel("No Computer Test result. Please use the Level4 Unit1 courseware.");
                        break;
                    default:
                        break;
                }
            }
        }
        return studentExam;
    }

    //TeacherComment的trialLevelResult, L*U* 换成Level * Unit *
    public String handleTeacherComment(String trialLevelResult) {
        logger.info("ReportController: handleTeacherComment() 参数为： trialLevelResult={}", trialLevelResult);

        // teacherComment 不为空则进行替换逻辑
        if (StringUtils.isNotBlank(trialLevelResult)) {
            String lowerCase = trialLevelResult.toLowerCase();
            if ("l1u0".equals(lowerCase)) {
                return "Level 0 Unit 0";
            } else if (lowerCase.startsWith("l")) {
                return lowerCase.replaceAll("l", "Level ").replaceAll("u", " Unit ");
            }
        }
        return trialLevelResult;
    }

    public Long findTeacherCommentIdByOnlineClassIdAndStudentId(Long onlineClassId,Long studentId){
        OnlineClass onlineClass = findOnlineClassById(onlineClassId);
        Map map = Maps.newHashMap();
        map.put("onlineClass", onlineClass);

        // 查询Lesson
        Lesson lesson = findLessonById(onlineClass.getLessonId());
        map.put("lesson", lesson);

        // 查询FeedBack信息
        TeacherComment teacherComment = findCFByOnlineClassIdAndStudentIdAndTeacherId(onlineClassId, studentId,onlineClass,lesson);
        return teacherComment.getOnlineClassId();
    }
}
