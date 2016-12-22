package com.vipkid.trpm.service.portal;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.vipkid.mq.message.FinishOnlineClassMessage.OperatorType;
import com.vipkid.mq.service.PayrollMessageService;
import com.vipkid.rest.security.AppContext;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.MediaType;
import com.vipkid.trpm.constant.ApplicationConstant.ReportLifeCycle;
import com.vipkid.trpm.constant.ApplicationConstant.UaReportStatus;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.*;
import com.vipkid.trpm.entity.media.UploadResult;
import com.vipkid.trpm.entity.report.DemoReports;
import com.vipkid.trpm.entity.report.ReportLevels;
import com.vipkid.trpm.entity.teachercomment.SubmitTeacherCommentDto;
import com.vipkid.trpm.entity.teachercomment.TeacherComment;
import com.vipkid.trpm.entity.teachercomment.TeacherCommentResult;
import com.vipkid.trpm.entity.teachercomment.TeacherCommentUpdateDto;
import com.vipkid.trpm.service.media.AbstarctMediaService;
import com.vipkid.trpm.util.DateUtils;
import com.vipkid.trpm.util.FilesUtils;
import com.vipkid.trpm.util.IpUtils;
import com.vipkid.trpm.util.LessonSerialNumber;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 1.主要负责UAReport、DemoReport、FeebBack等模块的查询和保存、以及UAReport的文件上传<br/>
 *
 * @Title: ReportService.java
 * @Package com.vipkid.trpm.service.portal
 * @author ALong
 * @date 2015年12月16日 下午8:54:35
 */
@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

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

    /**
     * uaReport<br/>
     *
     * 保存saveReport<br/>
     *
     * 1.检查是否已有Report <BR/>
     * 2.未有Report则新增 <BR/>
     * 3.已有Report判断Report审核状态，已审核不允许更新，未审核则更新 <BR/>
     *
     * @Author:ALong
     * @param report
     * @param file
     * @param size
     * @param user
     * @param score 分数(该功能暂且保留) TODO
     * @return Map<String,Object>
     * @date 2015年12月10日
     */
    public Map<String, Object> saveUAReport(AssessmentReport report, MultipartFile file, long size, User user,
            String score, String onlineClassId) {
        AssessmentReport resultReport;
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("result", false);

        // 日志记录参数准备
        Map<String, Object> parmMap = Maps.newHashMap();
        parmMap.put("studentId", report.getStudentId());
        parmMap.put("reportName", report.getName());
        parmMap.put("fileName", file.getOriginalFilename());
        parmMap.put("userId", user.getId());
        parmMap.put("score", score);
        parmMap.put("onlineClassId", onlineClassId);

        if (onlineClassId == null || !LessonSerialNumber.isInt(onlineClassId)) {
            resultMap.put("result", "This class is a little small problem, please contact our technical support!");
            return resultMap;
        }

        OnlineClass onlineClass = this.onlineClassDao.findById(Long.valueOf(onlineClassId));
        if (onlineClass == null) {
            resultMap.put("result", "This class is a little small problem, please contact our technical support!");
            return resultMap;
        }
        // 文件上传后获取得到URL和上传文件相关属性 该处暂时保留接口
        UploadResult upload = mediaService.handleUpload(file, MediaType.REPORT, String.valueOf(file.getSize()),
                file.getOriginalFilename());
        if (!upload.isResult()) {
            resultMap.put("result", upload.getMsg());
            return resultMap;
        }

        if (DateUtils.isSearchById(onlineClass.getScheduledDateTime().getTime())) {
            // 根据名称和studentId去匹配，匹配唯条
            resultReport = assessmentReportDao.findReportByClassId(Long.valueOf(onlineClassId));
        } else {
            resultReport = assessmentReportDao.findReportByStudentIdAndName(report.getName(), report.getStudentId());
        }

        logger.info(" upload ua Report url:{},teacherId:{},resultReport:{},score:{}", upload.getUrl(), user.getId(),
                resultReport, score);

        // 如果报告不存在，则新建
        if (resultReport == null) {
            resultReport = new AssessmentReport();
            BeanUtils.copyProperties(report, resultReport);
            resultReport.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
            resultReport.setUrl(upload.getUrl());
            resultReport.setReaded(UaReportStatus.NEWADD);
            resultReport.setOnlineClassId(onlineClass.getId());
            if (!StringUtils.isEmpty(score)) {
                resultReport.setScore(Integer.valueOf(score));
            }
            resultReport.setUploadDateTime(new Timestamp(System.currentTimeMillis())); // 记录上传时间
            assessmentReportDao.save(resultReport);

            // 日志记录
            String content = FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.REPORT_UA_CREATE, parmMap);
            auditDao.saveAudit(ApplicationConstant.AuditCategory.REPORT_UA_CREATE, "INFO", content, user.getName(),
                    resultReport, IpUtils.getRemoteIP());

            resultMap.put("result", true);
            resultMap.put("msg", "Upload Successful");
        } else {
            // 存在则检查是否审核
            // 未审核,或者被驳回, 则更新
            if (UaReportStatus.NEWADD == resultReport.getReaded()
                    || UaReportStatus.RESUBMIT == resultReport.getReaded()) {
                // 日志记录
                String content = FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.REPORT_UA_UPDATE,
                        parmMap);
                content += "【" + resultReport.getUrl() + " Update to " + upload.getUrl() + "】";
                auditDao.saveAudit(ApplicationConstant.AuditCategory.REPORT_UA_UPDATE, "INFO", content, user.getName(),
                        resultReport, IpUtils.getRemoteIP());
                resultReport.setUrl(upload.getUrl());
                resultReport.setReaded(UaReportStatus.NEWADD);
                resultReport.setOnlineClassId(Long.valueOf(onlineClassId));
                if (!StringUtils.isEmpty(score)) {
                    resultReport.setScore(Integer.valueOf(score));
                }
                resultReport.setUploadDateTime(new Timestamp(System.currentTimeMillis())); // 记录上传时间
                assessmentReportDao.update(resultReport);

                resultMap.put("result", true);
                resultMap.put("msg", "Upload Successful");
            } else {
                // 已审核则提示信息 //TODO
                resultMap.put("msg",
                        "This ua report has been sent to the parent already so you cannot make changes to it now.");
            }
        }

        final AssessmentReport finalResultReport = resultReport;
        // 上传报告发送消息
        if (resultReport != null && (resultReport.getReaded() == UaReportStatus.REVIEWED
                || resultReport.getReaded() == UaReportStatus.NEWADD) && resultReport.getOnlineClassId() > 0) {
            logger.info("上传报告发送消息  onlineClassId = {} ", resultReport.getOnlineClassId());
            long ocId = resultReport.getOnlineClassId();

            executor.execute(() -> payrollMessageService.sendFinishOnlineClassMessage(finalResultReport, ocId,
                    OperatorType.ADD_UNIT_ASSESSMENT));
        }

        return resultMap;
    }

    /**
     * practicumReport<br/>
     *
     * 保存savePracticumReport<br/>
     *
     * 1.检查是否已有Report <BR/>
     * 2.未有Report则新增 <BR/>
     * 3.已有Report判断Report审核状态，已审核不允许更新，未审核则更新 <BR/>
     *
     * @Author:ALong
     * @param report
     * @param file
     * @param size
     * @param user
     * @param score 分数(该功能暂且保留) TODO
     * @return Map<String,Object>
     * @date 2015年12月10日
     */
    public Map<String, Object> savePracticumReport(AssessmentReport report, MultipartFile file, long size, User user,
            String score, String onlineClassId) {
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("result", false);

        // 日志记录参数准备
        Map<String, Object> parmMap = Maps.newHashMap();
        parmMap.put("studentId", report.getStudentId());
        parmMap.put("reportName", report.getName());
        parmMap.put("fileName", file.getOriginalFilename());
        parmMap.put("userId", user.getId());
        parmMap.put("score", score);
        parmMap.put("onlineClassId", report.getOnlineClassId());

        OnlineClass onlineClass = this.onlineClassDao.findById(Long.valueOf(onlineClassId));

        // 文件上传后获取得到URL和上传文件相关属性 该处暂时保留接口
        UploadResult upload = mediaService.handleUpload(file, MediaType.REPORT, String.valueOf(file.getSize()),
                file.getOriginalFilename());
        if (!upload.isResult()) {
            resultMap.put("result", upload.getMsg());
            return resultMap;
        }

        // 根据report的name 和 studentId查找report
        AssessmentReport resultReport = assessmentReportDao.findReportByClassId(onlineClass.getId());

        logger.info(" upload practicum Report url:{},teacherId:{},resultReport:{},score:{}", upload.getUrl(),
                user.getId(), resultReport, score);
        // 如果报告不存在，则新建
        if (resultReport == null) {
            resultReport = new AssessmentReport();
            BeanUtils.copyProperties(report, resultReport);
            resultReport.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
            resultReport.setUrl(upload.getUrl());
            resultReport.setReaded(UaReportStatus.NEWADD);
            resultReport.setOnlineClassId(onlineClass.getId());
            resultReport.setUploadDateTime(new Timestamp(System.currentTimeMillis())); // 记录上传时间
            if (!StringUtils.isEmpty(score))
                resultReport.setScore(Integer.valueOf(score));
            assessmentReportDao.save(resultReport);

            // 日志记录
            String content = FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.PRACTICUM_REPORT_CREATE,
                    parmMap);
            auditDao.saveAudit(ApplicationConstant.AuditCategory.PRACTICUM_REPORT_CREATE, "INFO", content,
                    user.getName(), resultReport, IpUtils.getRemoteIP());

            resultMap.put("result", true);
            resultMap.put("msg", "Upload Successful");
        } else {
            // 存在则检查是否审核
            // 未审核,或者被驳回, 则更新
            if (UaReportStatus.NEWADD == resultReport.getReaded()
                    || UaReportStatus.RESUBMIT == resultReport.getReaded()) {
                // 日志记录
                String content = FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.PRACTICUM_REPORT_UPDATE,
                        parmMap);
                content += "【" + resultReport.getUrl() + " Update to " + upload.getUrl() + "】";
                auditDao.saveAudit(ApplicationConstant.AuditCategory.PRACTICUM_REPORT_UPDATE, "INFO", content,
                        user.getName(), resultReport, IpUtils.getRemoteIP());
                resultReport.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
                resultReport.setUrl(upload.getUrl());
                resultReport.setReaded(UaReportStatus.NEWADD);
                resultReport.setOnlineClassId(onlineClass.getId());
                resultReport.setUploadDateTime(new Timestamp(System.currentTimeMillis())); // 记录上传时间
                if (!StringUtils.isEmpty(score))
                    resultReport.setScore(Integer.valueOf(score));
                assessmentReportDao.update(resultReport);

                resultMap.put("result", true);
                resultMap.put("msg", "Upload Successful");
            } else {
                // 已审核则提示信息 //TODO
                resultMap.put("msg",
                        "This practicum report has been sent to the parent already so you cannot make changes to it now.");
            }
        }

        // 上传报告发送消息
        if (resultReport != null && resultReport.getReaded() == UaReportStatus.REVIEWED
                && resultReport.getOnlineClassId() > 0) {
            logger.info("上传报告发送消息  onlineClassId = {} ", resultReport.getOnlineClassId());
        }

        return resultMap;
    }

    /**
     * DemoReport<br/>
     *
     * 根据studentId,onlineclassId获取DemoReport对象<br/>
     *
     * @Author:ALong
     * @Title: getDemoReport
     * @param studentId
     * @param onlineClassId
     * @return DemoReport
     * @date 2015年12月12日
     */
    public DemoReport getDemoReport(long studentId, long onlineClassId) {
        return demoReportDao.findByStudentIdAndOnlineClassId(studentId, onlineClassId);
    }

    /**
     * DemoReport<br/>
     *
     * 从文件中读取JSON数据<br/>
     *
     * @Author:ALong
     * @Title: getDemoReports
     * @return DemoReports
     * @date 2015年12月12日
     */
    public DemoReports getDemoReports() {
        if (demoReports == null) {
            String contentJson = FilesUtils.readContent(this.getClass().getResourceAsStream("data/demoReports.json"),
                    StandardCharsets.UTF_8);
            demoReports = JsonTools.readValue(contentJson, DemoReports.class);
        }

        return demoReports;
    }

    /**
     * DemoReport<br/>
     *
     * 从文件中读取JSON数据<br/>
     *
     * @Author:ALong
     * @Title: getReportLevels
     * @return ReportLevels
     * @date 2015年12月12日
     */
    public ReportLevels getReportLevels() {
        if (reportLevels == null) {
            String contentJson = FilesUtils.readContent(this.getClass().getResourceAsStream("data/levels.json"),
                    StandardCharsets.UTF_8);
            reportLevels = JsonTools.readValue(contentJson, ReportLevels.class);
        }

        return reportLevels;
    }

    /**
     * DemoReport<br/>
     *
     * 保存或提交<br/>
     *
     * 1.如果管理端创建失败，这里将不能进行任何操作<br/>
     * 2.仅状态为null或者UNFINISHED可进行保存/提交操作<br/>
     * 3.保存后状态变为UNFINISHED <br/>
     * 4.提交后状态直接变为SUBMITTED,并且不能再进行保存/提交操作<br/>
     *
     * @Author:ALong
     * @Title: submitOrSaveDemoReport
     * @param demoReport
     * @param isSubmited true:提交，false:保存
     * @return Map<String,Object>
     * @date 2015年12月14日
     */
    /*
     * public Map<String, Object> saveOrSubmitDemoReport(DemoReport demoReport, boolean isSubmited,User user) {
     * Map<String, Object> resultMap = Maps.newHashMap(); resultMap.put("result", false); if(demoReport.getId() == 0){
     * resultMap.put("msg", "This report does not exist.Please contact management!"); return resultMap; } DemoReport
     * dbEntity = demoReportDao.findById(demoReport.getId()); // 如果管理端创建失败，这里将不能进行任何操作 if (dbEntity == null) {
     * resultMap.put("msg", "This report does not exist.Please contact management!"); return resultMap; } //
     * 仅状态为null或者UNFINISHED可进行保存/提交操作 if (!(StringUtils.isEmpty(dbEntity.getLifeCycle()) || ReportLifeCycle.UNFINISHED
     * .equals(dbEntity.getLifeCycle()))) { resultMap.put("msg", "This report has been submitted, can not be modified."
     * ); return resultMap; } //日志模板参数准备 Map<String, Object> parmMap = Maps.newHashMap(); parmMap.put("teacherId",
     * dbEntity.getTeacherId()); parmMap.put("studentId", dbEntity.getStudentId()); String content = ""; //
     * 提交后状态直接变为SUBMITTED,并且不能再进行保存/提交操作 if (isSubmited) { demoReport.setSubmitDateTime(new
     * Timestamp(System.currentTimeMillis())); demoReport.setLifeCycle(ReportLifeCycle.SUBMITTED); content =
     * FilesUtils.readLogTemplate (ApplicationConstant.AuditCategory.REPORT_DEMO_SUBMIT, parmMap); } else { //
     * 保存后状态变为UNFINISHED demoReport.setLifeCycle(ReportLifeCycle.UNFINISHED); content =
     * FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.REPORT_DEMO_SAVE , parmMap); } String INFO = "INFO";
     * if (0 != demoReportDao.updateDemoReport(demoReport)) { resultMap.put("result", true); } else { INFO = "ERROR";
     * resultMap.put("msg", "Operation failed.Please contact management!"); } //日志记录 if (isSubmited) {
     * auditDao.saveAudit(ApplicationConstant.AuditCategory .REPORT_DEMO_SUBMIT,INFO,content, user.getName()); }else{
     * auditDao.saveAudit (ApplicationConstant.AuditCategory.REPORT_DEMO_SAVE,INFO,content, user.getName()); } return
     * resultMap; }
     */

    /**
     *
     * DemoReport<br/>
     *
     * 保存或提交DemoReport<br/>
     *
     * 1.如果管理端创建失败，这里将不能进行任何操作<br/>
     * 2.仅状态为null或者UNFINISHED和SUBMITED可进行保存或者提交操作<br/>
     * 3.保存后 未提交的将其修改状态为UNFINISHED，已经提交的不修改状态 <br/>
     * 4.提交后 未提交的将其修改状态为SUBMITTED， 已经提交的不修改状态<br/>
     *
     * @Author:ALong
     * @Title: submitOrSaveDemoReport
     * @param demoReport
     * @param isSubmited true:提交，false:保存
     * @return Map<String,Object>
     * @date 2015年12月14日
     */
    public Map<String, Object> saveOrSubmitDemoReport(DemoReport demoReport, boolean isSubmited, User user) {
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("result", false);

        if (demoReport.getId() == 0) {
            logger.info("This report does not exist. Please contact management!,studentId:{},teacherId:{}",
                    demoReport.getStudentId(), demoReport.getTeacherId());
            resultMap.put("msg", "This report does not exist. Please contact management!");
            return resultMap;
        }

        DemoReport dbEntity = demoReportDao.findById(demoReport.getId());
        // 如果管理端创建失败，这里将不能进行任何操作
        if (dbEntity == null) {
            logger.info(
                    "This report does not exist. Please contact management!,studentId:{},teacherId:{},demoReportId:{}",
                    demoReport.getStudentId(), demoReport.getTeacherId(), demoReport.getId());
            resultMap.put("msg", "This report does not exist. Please contact management!");
            return resultMap;
        }

        // 仅状态为null或者UNFINISHED或者SUBMITTED可进行保存/提交操作
        if (!(StringUtils.isEmpty(dbEntity.getLifeCycle()) || ReportLifeCycle.UNFINISHED.equals(dbEntity.getLifeCycle())
                || ReportLifeCycle.SUBMITTED.equals(dbEntity.getLifeCycle()))) {
            logger.info(
                    "This report has been submitted, can not be modified.,studentId:{},teacherId:{},demoReportId:{}",
                    demoReport.getStudentId(), demoReport.getTeacherId(), demoReport.getId());
            // resultMap.put("msg", "This report has been submitted, can not be modified.");
            resultMap.put("msg",
                    "This report has been sent to the parent already so you cannot make changes to it now.");
            return resultMap;
        }

        // 日志模板参数准备
        Map<String, Object> parmMap = Maps.newHashMap();
        parmMap.put("teacherId", dbEntity.getTeacherId());
        parmMap.put("studentId", dbEntity.getStudentId());
        parmMap.put("onlineClassId", dbEntity.getOnlineClassId());
        String content = "";

        logger.info("Operation this demoReoprt: studentId:{},teacherId:{},demoReportId:{},demoReport Status:{}",
                demoReport.getStudentId(), demoReport.getTeacherId(), demoReport.getId(), demoReport.getLifeCycle());

        // 提交后状态直接变为SUBMITTED,并且不能再进行保存/提交操作
        if (isSubmited) {
            // 设置提交时间
            demoReport.setSubmitDateTime(new Timestamp(System.currentTimeMillis()));
            // 仅仅为UNFINISHED的时候 或者为空的时候才修改其状态为提交，其他状态不修改其状态
            if (StringUtils.isEmpty(dbEntity.getLifeCycle())
                    || ReportLifeCycle.UNFINISHED.equals(dbEntity.getLifeCycle())) {
                demoReport.setLifeCycle(ReportLifeCycle.SUBMITTED);
            }
            content = FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.REPORT_DEMO_SUBMIT, parmMap);
        } else {
            // 仅仅为空的时候才将其变为UNFINISHED，其他状态不修其状态
            if (StringUtils.isEmpty(dbEntity.getLifeCycle())) {
                // 保存后状态变为UNFINISHED
                demoReport.setLifeCycle(ReportLifeCycle.UNFINISHED);
            }
            content = FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.REPORT_DEMO_SAVE, parmMap);
        }

        String INFO = "INFO";
        if (0 != demoReportDao.updateDemoReport(demoReport)) {
            resultMap.put("result", true);
        } else {
            INFO = "ERROR";
            resultMap.put("msg", "Operation failed. Please contact management!");
        }

        // 日志记录
        if (isSubmited) {
            auditDao.saveAudit(ApplicationConstant.AuditCategory.REPORT_DEMO_SUBMIT, INFO, content, user.getName(),
                    demoReport, IpUtils.getRemoteIP());
        } else {
            auditDao.saveAudit(ApplicationConstant.AuditCategory.REPORT_DEMO_SAVE, INFO, content, user.getName(),
                    demoReport, IpUtils.getRemoteIP());
        }

        return resultMap;
    }

    /**
     *
     * feedback<br/>
     *
     * 根据onlineClassId 查找OnlineClass<br/>
     * 存在则返回本身，不存在则返回null<br/>
     *
     * @Author:ALong
     * @Title: findOnlineClassById
     * @param onlineClassId
     * @return OnlineClass
     * @date 2015年12月16日
     */
    public OnlineClass findOnlineClassById(long onlineClassId) {
        if (0 == onlineClassId) {
            return null;
        }
        return onlineClassDao.findById(onlineClassId);
    }

    /**
     *
     * feedback<br/>
     * 根据lessonId 查找 Lesson<br/>
     * 存在则返回本身，不存在则返回null<br/>
     *
     * @Author:ALong
     * @Title: findLessonById
     * @param lessonId
     * @return Lesson
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
     *
     * 根据onlineclassid 和studentid 得到feebback<br/>
     *
     * 存在则返回本身，错误则返回null，不存在则新建<br/>
     *
     * @Author:ALong
     * @param onlineClassId
     * @param studentId
     * @date 2015年12月16日
     * @return
     */
    public TeacherComment findCFByOnlineClassIdAndStudentIdAndTeacherId(long onlineClassId, long studentId,
        OnlineClass onlineClass,Lesson lesson) {
        logger.info("onlineClassId：" + onlineClassId + ";studentId:" + studentId);
        if (0 == onlineClassId || 0 == studentId || onlineClass==null || lesson==null) {
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

        tcuDto.setStars(0);
        tcuDto.setEmpty(true);
        //先按teacher_id和student_id和onlineClassId检索,如果有则返回已存在的,如果没有则插入一条新的
        TeacherCommentResult tcResult = teacherService.checkExistOrInsertOne(tcuDto);
        if(tcResult==null){
            logger.error("进入教室后,老师点击feedback按钮无法获取teacherComment信息!");
            return null;
        }else{
            TeacherComment comment = new TeacherComment(tcResult);
            return comment;
        }
    }


    /**
     * FeedBack SAVE<br/>
     *
     * 更新TeacherComment，并更新empty为0（代表已提交）<br/>
     *
     * @Author:ALong
     * @Title: submitTeacherComment
     * @param teacherComment
     * @date 2015年12月16日
     */
    public Map<String, Object> submitTeacherComment(SubmitTeacherCommentDto teacherComment, User user,String serialNumber,
            String scheduledDateTime,boolean isFromH5) {
        // 如果ID为0 则抛出异常并回滚
        checkArgument(0 != teacherComment.getId(), "Argument teacherComment id equals 0");

        teacherComment.setEmpty(0);

        // 日志记录参数准备
        TeacherCommentResult oldtcFromAPI = teacherService
                .findByTeacherCommentId(String.valueOf(teacherComment.getId()));
        if(oldtcFromAPI==null){
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
        TeacherCommentUpdateDto tcuDto = new TeacherCommentUpdateDto(teacherComment);
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

            executor.execute(() -> payrollMessageService.sendFinishOnlineClassMessage(teacherComment, onlineClassId,
                    OperatorType.ADD_TEACHER_COMMENTS));
        }

        boolean isPreVipkid = false;
        if(StringUtils.isNotBlank(serialNumber)
            && (serialNumber.toLowerCase().startsWith("mc-l1")
            ||serialNumber.equalsIgnoreCase("T1-U1-LC1-L0"))){
            isPreVipkid = true;
        }

        if(isPreVipkid){
            if (teacherComment.getPerformance() == 1 || teacherComment.getPerformance() == 5
                || teacherComment.isNeedParentSupport()) {
                logger.info(
                    "previp检查Performance和needParentSupport判断是否给CLT发邮件: studentId = {}, serialNumber = {} ",
                    oldtc.getStudentId(), serialNumber);
                sendEmailExecutor.execute(() -> {
                    emailService.sendEmail4PreVip2CLT(oldtc.getStudentId(), serialNumber,
                        teacherComment.isNeedParentSupport());
                });
            }

        }else{
            if (teacherComment.getPerformanceAdjust()==1 && teacherComment.getPerformance()!=0){
                logger.info("判断PerformanceAdjust给CLT发邮件: studentId = {}, serialNumber = {}, scheduledDateTime = {} ",
                    oldtc.getStudentId(), serialNumber, scheduledDateTime);
                final String finalScheduledDateTime = scheduledDateTime;
                sendEmailExecutor.execute(() -> {
                    emailService.sendEmail4PerformanceAdjust2CLT(oldtc.getStudentId(), serialNumber,
                        finalScheduledDateTime, teacherComment.getPerformance());
                });
            }

            if (teacherComment.getPerformance()==1 || teacherComment.getPerformance()==5){
                logger.info("检查Performance判断是否给CLT发邮件: studentId = {}, serialNumber = {} ", oldtc.getStudentId(), serialNumber);
                sendEmailExecutor.execute(() -> {
                    emailService.sendEmail4Performance2CLT(oldtc.getStudentId(), serialNumber);
                });
            }
        }
        return paramMap;
    }

    /**
     * uaReport<br/>
     *
     * 根据学生ID 和 report名称查找UAReport<br/>
     * 存在则返回本身，不存在则返回null<br/>
     *
     * @Author:ALong
     * @Title: findReportByStudentIdAndName
     * @param name
     * @param studentId
     * @return AssessmentReport
     * @date 2015年12月16日
     */
    public AssessmentReport findReportByStudentIdAndName(String name, long studentId) {
        return assessmentReportDao.findReportByStudentIdAndName(name, studentId);
    }

    /**
     * Report<br/>
     *
     * 根据onlineClassId 查找 Report<br/>
     * 存在则返回本身，不存在则返回null<br/>
     *
     * @Author:ALong
     * @Title: findByClassId
     * @param onlineClassId
     * @return AssessmentReport
     * @date 2015年12月16日
     */
    public AssessmentReport findReportByClassId(long onlineClassId) {
        return assessmentReportDao.findReportByClassId(onlineClassId);
    }

    /**
     * INFO<br/>
     *
     * 通过studentId查询StudentExam(最近考试信息)<br/>
     *
     * @Author:ALong
     * @Title: findStudentExamByStudentId
     * @param studentId
     * @return StudentExam
     * @date 2015年12月18日
     */
    public StudentExam findStudentExamByStudentId(long studentId) {
        return studentExamDao.findStudentExamByStudentId(studentId);
    }

    /**
     * INFO<br/>
     *
     * 更加studentId 查询学生信息<br/>
     * 当id为0的时候直接返回NULL
     *
     * @Author:ALong
     * @Title: findStudentById
     * @param studentId
     * @return Student
     * @date 2015年12月18日
     */
    public Student findStudentById(long studentId) {
        if (0 == studentId) {
            return null;
        }
        return studentDao.findById(studentId);
    }


    /**
     * 根据serialNum处理 考试的Level名称显示<br/>
     * studentExam 为NULL 则返回一个空对象
     *
     * @Author:ALong
     * @Title: handleExamLevel
     * @param studentExam
     * @param serialNum
     * @return StudentExam
     * @date 2016年1月12日
     */
    public StudentExam handleExamLevel(StudentExam studentExam, String serialNum) {
        logger.info("ReportController: handleExamLevel() 参数为：serialNum={}, studentExam={}", serialNum, JSON.toJSONString(studentExam));

        // studentExam 不为空则进行替换逻辑
        if (studentExam != null) {
            // ExamLevel 不为空则进行替换逻辑
            if (studentExam.getExamLevel() != null) {
                String lowerCase = studentExam.getExamLevel().toLowerCase();
                if ("l1u0".equals(lowerCase)) {
                    studentExam.setExamLevel("Level Test result is Level 0 Unit 0");
                } else if (lowerCase.startsWith("l")) {
                    studentExam.setExamLevel("Level Test result is " + lowerCase.replaceAll("l", "Level ").replaceAll("u", " Unit "));
                }
            }
        } else {
            // studentExam 为空则返回空对象
            studentExam = new StudentExam();
            // ExamLevel 为空则根据Lession的SerialNum进行处理
            if (serialNum != null) {
                switch (serialNum) {
                    case "T1-U1-LC1-L1":
                        studentExam.setExamLevel("No Computer Test result, use Level 2 Unit 01");
                        break;
                    case "T1-U1-LC1-L2":
                        studentExam.setExamLevel("No Computer Test result, use Level 2 Unit 04");
                        break;
                    case "T1-U1-LC1-L3":
                        studentExam.setExamLevel("No Computer Test result, use Level 3 Unit 01");
                        break;
                    case "T1-U1-LC1-L4":
                        studentExam.setExamLevel("No Computer Test result, use Level 4 Unit 01");
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
}
