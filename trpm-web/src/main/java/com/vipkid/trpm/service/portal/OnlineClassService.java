package com.vipkid.trpm.service.portal;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.community.config.PropertyConfigurer;
import org.community.http.client.HttpClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.vipkid.http.service.AssessmentHttpService;
import com.vipkid.http.vo.StudentUnitAssessment;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.*;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.*;
import com.vipkid.trpm.proxy.ClassroomProxy;
import com.vipkid.trpm.util.DateUtils;
import com.vipkid.trpm.util.FilesUtils;
import com.vipkid.trpm.util.IPUtils;

@Service
public class OnlineClassService {

    private static final Logger logger = LoggerFactory.getLogger(OnlineClassService.class);

    @Autowired
    private OnlineClassDao onlineClassDao;

    @Autowired
    private LessonDao lessonDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TeacherCommentDao teacherCommentDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private DemoReportDao demoReportDao;

    @Autowired
    private AuditDao auditDao;

    @Autowired
    private TeacherPeDao teacherPeDao;

    @Autowired
    private TeacherModuleDao teacherModuleDao;
    
    @Autowired
    private TeacherQuizDao teacherQuizDao;

    @Autowired
    private TeacherQuizDao teacherQuizDao;

    @Autowired
	private AssessmentHttpService assessmentHttpService;
    
    @Autowired
    private AssessmentReportDao assessmentReportDao;
    
    /**
     * 根据id找online class
     *
     * @param onlineClassId
     * @return
     */
    public OnlineClass getOnlineClassById(long onlineClassId) {
        return onlineClassDao.findById(onlineClassId);
    }

    /**
     * 根据id得到lesson
     *
     * @param lessonId
     * @return
     */
    public Lesson getLesson(long lessonId) {
        return lessonDao.findById(lessonId);
    }

    /**
     * 进入Open课程 逻辑
     *
     * @Title: enterOpen
     * @param onlineClassId
     * @param studentId
     * @param teacher
     * @param lesson
     * @return Map<String,Object>
     * @date 2016年1月15日
     */
    public Map<String, Object> enterOpen(OnlineClass onlineClass, long studentId, Teacher teacher,
            Lesson lesson) {
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.putAll(this.enterBefore(onlineClass, studentId, "OPEN"));
        modelMap.put("url",
                ClassroomProxy.generateRoomEnterUrl(String.valueOf(teacher.getId()),
                        teacher.getRealName(), onlineClass.getClassroom(),
                        ClassroomProxy.ROLE_TEACHER, onlineClass.getSupplierCode()));

        this.enterAfter(teacher, onlineClass);
        return modelMap;
    }

    /**
     * 进入Practicum 逻辑
     *
     * @Title: enterPracticum
     * @param onlineClassId
     * @param studentId
     * @param teacher
     * @param lesson
     * @return Map<String,Object>
     * @date 2016年1月15日
     */
    public Map<String, Object> enterPracticum(OnlineClass onlineClass, long studentId,
            Teacher student, Lesson lesson) {
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.putAll(this.enterBefore(onlineClass, studentId, "BOOKED"));

        TeacherApplication teacherApplication = teacherApplicationDao
                .findApplictionByOlineclassId(onlineClass.getId(), student.getId());
        modelMap.put("teacherApplication", teacherApplication);
        modelMap.put("url",
                ClassroomProxy.generateRoomEnterUrl(String.valueOf(student.getId()),
                        student.getRealName(), onlineClass.getClassroom(),
                        ClassroomProxy.ROLE_STUDENT, onlineClass.getSupplierCode()));
        modelMap.put("teacherPe", teacherPeDao.findByOnlineClassId(onlineClass.getId()));
        List<TeacherApplication> list = teacherApplicationDao
                .findApplictionForPracticum2(teacherApplication.getTeacherId());
        if (list != null && list.size() > 0) {
            modelMap.put("practicum2", true);
        } else {
            modelMap.put("practicum2", false);
        }
        this.enterAfter(student, onlineClass);

        List<TeacherModule> teacherModules = teacherModuleDao.findByTeacherPe(student.getId());
        // 判断当前用户是否拥有PE Supervisor权限
        if (CollectionUtils.isNotEmpty(teacherModules)) {
            modelMap.put("PESupervisor", true);
        } else {
            modelMap.put("PESupervisor", false);
        }

        return modelMap;
    }

    /**
     * 进入Major课程 逻辑<br/>
     * 主修课分为:Aessement课和非Aessement课程
     *
     * @Title: enterMajor
     * @param onlineClassId
     * @param studentId
     * @param teacher
     * @param lesson
     * @return Map<String,Object>
     * @date 2016年1月15日
     */
    public Map<String, Object> enterMajor(OnlineClass onlineClass, long studentId, Teacher teacher,
            Lesson lesson) {
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.putAll(this.enterBefore(onlineClass, studentId, "BOOKED"));

        /** 获取teacherComments中的stars字段的值，并存入model */
        int stars = this.findTeacherCommentByOnlineClassId(onlineClass, studentId);
        logger.info("query stars : {},onlineClassId:{},studentId:{}", stars, onlineClass.getId(),
                studentId);
        modelMap.put("stars", stars);
        if (lesson.getSerialNumber().startsWith("A")) {
            DemoReport currentReport =
                    demoReportDao.findByStudentIdAndOnlineClassId(studentId, onlineClass.getId());
            modelMap.put("currentReport", currentReport);
        }
        modelMap.put("url",
                ClassroomProxy.generateRoomEnterUrl(String.valueOf(teacher.getId()),
                        teacher.getRealName(), onlineClass.getClassroom(),
                        ClassroomProxy.ROLE_TEACHER, onlineClass.getSupplierCode()));

        this.enterAfter(teacher, onlineClass);
        
        //查询是否旧版UA报告
        AssessmentReport assessmentReport = null;
        String serialNumber = lesson.getSerialNumber();
        Long onlineClassId = onlineClass.getId();
        long schedDateTime = onlineClass.getScheduledDateTime().getTime();
        
        if (DateUtils.isSearchById(schedDateTime)) {
            assessmentReport = assessmentReportDao.findReportByClassId(onlineClassId);
        } else {
            assessmentReport = assessmentReportDao.findReportByStudentIdAndName(serialNumber, studentId);
        }
        modelMap.put("isNewUa", assessmentReport == null ? 1: 0);
        return modelMap;
    }

    /**
     * 进入教室前基本数据 1.查询学生信息 2.更新教师进入教室时间 3.判断是否进入回放页面
     **/
    private Map<String, Object> enterBefore(OnlineClass onlineClass, long studentId,
            String status) {
        Map<String, Object> modelMap = Maps.newHashMap();
        this.modifyTeacherEnterTime(onlineClass.getId());
        modelMap.put("onlineClass", onlineClass);
        modelMap.put("student", studentDao.findById(studentId));

        Instant instant = onlineClass.getScheduledDateTime().toInstant();
        String scheduleTime = DateUtils.formatTo(instant, DateUtils.FMT_YMD_HMS);
        /* 用于帮助时需要发送的时间 */
        modelMap.put("helpTimes", scheduleTime);
        /* 不等FINISHED状态的onlineClass需要获取其用户和倒计时参数* */
        if (onlineClass.getStatus().equals(status)) {
            modelMap.put("studentUser", userDao.findById(studentId));
            /* 获取当前服务器时间和scheduleTime，用于教室倒计时计算,这里的时间 */
            modelMap.put("serverTime", DateUtils.getThisYearMonth(TimeZone.getDefault().getID()));
            modelMap.put("scheduleTime", scheduleTime);
        } else {
            modelMap.put("isReplay", "isReplay");
        }
        modelMap.put("classtime", scheduleTime);
        modelMap.put("msURL", PropertyConfigurer.stringValue("microservice.url"));
        modelMap.put("sysInfoURL", PropertyConfigurer.stringValue("sys.info.url"));
        return modelMap;
    }

    /** 进入教室后日志记录 **/
    private void enterAfter(Teacher teacher, OnlineClass onlineClass) {
        /* 进入教室记录日志 */
        Map<String, Object> parmMap = Maps.newHashMap();
        parmMap.put("teacherId", teacher.getId());
        parmMap.put("teacherName", teacher.getRealName());
        parmMap.put("onlineClassId", onlineClass.getId());
        parmMap.put("roomId", onlineClass.getClassroom());
        String content = FilesUtils
                .readLogTemplete(ApplicationConstant.AuditCategory.CLASSROOM_ENTER, parmMap);
        auditDao.saveAudit(ApplicationConstant.AuditCategory.CLASSROOM_ENTER, "INFO", content,
                teacher.getRealName(), teacher, IPUtils.getRemoteIP());
    }

    /**
     * 退出教室，记录日志
     *
     * @Author:ALong
     * @Title: exitclassroom
     * @param onlineClassId
     * @param teacher
     * @return void
     * @date 2016年1月11日
     */
    public void exitclassroom(long onlineClassId, Teacher teacher) {
        /* 退出教室记录日志 */
        Map<String, Object> parmMap = Maps.newHashMap();
        parmMap.put("teacherId", teacher.getId());
        parmMap.put("teacherName", teacher.getRealName());
        parmMap.put("onlineClassId", onlineClassId);
        OnlineClass onlineClass = getOnlineClassById(onlineClassId);
        parmMap.put("roomId", onlineClass.getClassroom());
        String content = FilesUtils
                .readLogTemplete(ApplicationConstant.AuditCategory.CLASSROOM_EXIT, parmMap);
        auditDao.saveAudit(ApplicationConstant.AuditCategory.CLASSROOM_EXIT, "INFO", content,
                teacher.getRealName(), teacher, IPUtils.getRemoteIP());
    }

    /**
     * 修改onlineClass的状态和完成类型
     *
     * @param onlineClassId
     */
    public void exitOpenclass(long onlineClassId) {
        onlineClassDao.updateEntity(new OnlineClass().setId(onlineClassId).setStatus("FINISHED")
                .setFinishType("AS_SCHEDULED"));
    }

    /**
     * 更新教师进入教室时间
     *
     * @Title: modifyTeacherEnterTime
     * @param onlineClassId
     * @return void
     * @date 2016年1月11日
     */
    private void modifyTeacherEnterTime(long onlineClassId) {
        OnlineClass onlineClass = onlineClassDao.findById((onlineClassId));
        if (null != onlineClass && null == onlineClass.getTeacherEnterClassroomDateTime()) {
            onlineClassDao.updateEnterClassTime(onlineClassId,
                    new Timestamp(System.currentTimeMillis()));
        }
    }

    /**
     * 根据onlineclassid 和studentid 得到teacher comment
     *
     * @param onlineClassId
     * @param studentId
     * @return
     */
    public TeacherComment getTeacherComment(long onlineClassId, long studentId) {
        return teacherCommentDao.findByStudentIdAndOnlineClassId(studentId, onlineClassId);
    }

    /**
     * 结束Practicum课操作
     *
     * @Author:ALong
     * @param teacher 操作老师(学生角色)
     * @param onlineClassId 上课Id
     * @param result 结果
     * @param studentId 学生Id
     * @param comments 备注
     * @param finishType 完成类型
     * @return Map<String,Object>
     * @date 2016年1月11日
     */
    public Map<String, Object> updateAudit(Teacher pe, TeacherApplication currTeacherApplication,
            String result, String finishType) {
        // 默认操作状态
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.put("result", false);

        // 1.finishType 如果为null，则抛出错误信息
        if (StringUtils.isEmpty(finishType)) {
            logger.info("Finish Type is null ");
            modelMap.put("msg", "Please select an finish type first！");
            return modelMap;
        }

        // 2.验证 teacherApplications 是否为空
        long onlineClassId = currTeacherApplication.getOnlineClassId();
        TeacherApplication teacherApplication = teacherApplicationDao
                .findApplictionByOlineclassId(onlineClassId, currTeacherApplication.getTeacherId());
        if (teacherApplication == null) {
            modelMap.put("msg", "Not exis the online-class recruitment info ！");
            logger.info(" TeacherApplication is null onlineClassId:{} , status is PRACTICUM ",
                    onlineClassId);
            return modelMap;
        }

        // 3.验证 recruitTeacher 是否存在
        Teacher recruitTeacher = teacherDao.findById(teacherApplication.getTeacherId());
        if (recruitTeacher == null) {
            modelMap.put("msg", "System error！");
            logger.info(" Recruitment Teacher is null , teacher id is {}",
                    teacherApplication.getTeacherId());
            return modelMap;
        }

        OnlineClass onlineClass = onlineClassDao.findById(onlineClassId);
        // 4.如果result 不等于null 则返回错误
        if (!StringUtils.isEmpty(teacherApplication.getResult())) {
            logger.info(
                    "Teacher application already end or recruitment process step already end, class id is : {},status is {}",
                    onlineClass.getId(), onlineClass.getStatus());
            modelMap.put("msg", " The recruitment process already end.");
            return modelMap;
        }

        // 检查课程是否开始15分钟
        if (!DateUtils.count15Mine(onlineClass.getScheduledDateTime().getTime())) {
            modelMap.put("msg", "Sorry, you can't submit the form within 15min!");
            return modelMap;
        }

        // 5.practicum2 判断是否存在
        if (ApplicationConstant.RecruitmentResult.PRACTICUM2.equals(result)) {
            List<TeacherApplication> list = teacherApplicationDao
                    .findApplictionForPracticum2(teacherApplication.getTeacherId());
            if (list != null && list.size() > 0) {
                logger.info(
                        "The teacher is already in practicum 2., class id is : {},status is {},recruitTeacher:{}",
                        onlineClass.getId(), onlineClass.getStatus(), recruitTeacher.getId());
                modelMap.put("msg", "The teacher is already in practicum 2.");
                return modelMap;
            }
        }

        Map<String, Object> parmMap = Maps.newHashMap();
        parmMap.put("teacherId", pe.getId());
        parmMap.put("teacherName", pe.getRealName());
        parmMap.put("recruitId", recruitTeacher.getId());
        parmMap.put("recruitName", recruitTeacher.getRealName());
        parmMap.put("onlineClassId", onlineClass.getId());
        parmMap.put("roomId", onlineClass.getClassroom());
        parmMap.put("result", result);
        parmMap.put("finishType", finishType);

        // 6.然后操作TeacherApllication
        if (ClassStatus.isBooked(onlineClass.getStatus())
                || ClassStatus.isFinished(onlineClass.getStatus())) {
            List<TeacherModule> teacherModules = teacherModuleDao.findByTeacherPe(pe.getId());
            // 判断当前用户是否拥有PE Supervisor权限
            if (CollectionUtils.isNotEmpty(teacherModules)) {
                currTeacherApplication.setContractUrl("PE-Supervisor");
            } else {
                currTeacherApplication.setContractUrl("PE");
            }
            // 如果课程已经结束
            modelMap = this.updateTeacherApplication(recruitTeacher, pe, result, "",
                    currTeacherApplication);
            // 日志 2
            String content = FilesUtils
                    .readLogTemplete(ApplicationConstant.AuditCategory.PRACTICUM_AUDIT, parmMap);
            auditDao.saveAudit(ApplicationConstant.AuditCategory.PRACTICUM_AUDIT, "INFO", content,
                    pe.getRealName(), recruitTeacher, IPUtils.getRemoteIP());
            logger.info(
                    "Practicum Online Class[finish] updateAudit,studentId:{},onlineClassId:{},recruitTeacher:{},teacherId:{}",
                    pe.getId(), onlineClass.getId(), recruitTeacher.getId(), pe.getId());

            // 设置调用接口的参数
            modelMap.put("teacherApplicationId", currTeacherApplication.getId());
            modelMap.put("recruitTeacher",
                    teacherDao.findById(currTeacherApplication.getTeacherId()));

            return modelMap;
        } else {
            logger.error("online class status not is booked or finish status,online class Id:{}",
                    onlineClass.getId());
            modelMap.put("msg", "System error！");
        }
        return modelMap;
    }

    /**
     * Update TeacherApplication 操作逻辑<br/>
     * 1.判断认证课程是否存在，不存在则创建一个<br/>
     * 2.操作TeacherApplicaton状态为FINISHED<br/>
     * 3.修改教师状态为REGULAR
     *
     * @Author:ALong
     * @Title: updateTeacherApplication
     * @param recruitTeacher
     * @param result
     * @param comments
     * @param teacherApplication
     * @return Map<String,Object>
     * @date 2016年1月14日
     */
    private Map<String, Object> updateTeacherApplication(Teacher recruitTeacher, Teacher pe,
            String result, String comments, TeacherApplication teacherApplication) {
        Map<String, Object> modelMap = Maps.newHashMap();

        // 设置审核结果
        teacherApplication.setResult(result);
        // 设置审核备注
        teacherApplication.setComments(comments);
        // 设置面试官Id
        teacherApplication.setAuditorId(pe.getId());
        // 设置应聘老师Id
        teacherApplication.setTeacherId(recruitTeacher.getId());
        // 如果是PASS操作，则ta状态修改为FINISH，教师状态修改为REGULAR
        if (RecruitmentResult.PASS.equals(result)) {
            teacherApplication.setStatus(RecruitmentStatus.FINISHED);
            // 2.教师状态更新
            recruitTeacher.setLifeCycle(TeacherLifeCycle.REGULAR);
            // 3.新增教师入职时间
            recruitTeacher.setEntryDate(new Date());
            recruitTeacher.setType(TeacherType.PART_TIME);
            this.teacherDao.update(recruitTeacher);
            // 增加quiz的考试记录
            teacherQuizDao.insertQuiz(recruitTeacher.getId(), pe.getId());
        }
        // 3.更新teacherApplication
        this.teacherApplicationDao.update(teacherApplication);

        // 4.更新最后一次编辑人,编辑时间
        User ruser = this.userDao.findById(recruitTeacher.getId());
        if (ruser != null) {
            ruser.setLastEditorId(pe.getId());
            ruser.setLastEditDateTime(new Timestamp(System.currentTimeMillis()));
            this.userDao.update(ruser);
        }

        modelMap.put("result", true);
        return modelMap;
    }

    /**
     * 向Appserver发送帮助请求<br/>
     * 上课期间可以发送帮助请求，非上课期间不能发送
     *
     * @Title: sendHelp
     * @param scheduleTime
     * @param onlineClassId
     * @param teacher
     * @return Map<String,Object>
     * @date 2016年1月11日
     */
    public Map<String, Object> sendHelp(String scheduleTime, long onlineClassId, Teacher teacher) {
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.put("status", false);
        /* 获取服务器时间毫秒 */
        long serverMillis = System.currentTimeMillis();

        /* 计算schedule时间毫秒 */
        Timestamp ldtSchedule = DateUtils.parseFrom(scheduleTime, DateUtils.FMT_YMD_HMS);
        long scheduleMillis = ldtSchedule.getTime();

        /* 判断时间间隔是否在上课时间段之内，如果是则发送帮助请求 */
        long interval = serverMillis - scheduleMillis;

        /* 在30分钟之内可以发送帮助请求 */
        if (0 <= interval && interval <= 30 * 60 * 1000) {
            /* 请求参数 */
            Map<String, String> requestParams = new HashMap<String, String>();
            requestParams.put("onlineClassId", String.valueOf(onlineClassId));
            /* 请求头设置 */
            String t = "TEACHER " + teacher.getId();
            Map<String, String> requestHeader = new HashMap<String, String>();
            requestHeader.put("Authorization", t + " " + DigestUtils.md5Hex(t));

            String content = HttpClientProxy.post(ApplicationConstant.HELP_URL, requestParams,
                    requestHeader);
            logger.info("### Request help return content: {}", content);
            if (!StringUtils.isEmpty(content)) {
                modelMap.put("status", true);
            } else {
                modelMap.put("msg", "Request failed, please contact manager!");
            }
        } else {
            modelMap.put("msg", "Sorry, you can only use this function during class time.");
        }
        return modelMap;
    }

    /**
     * 老师进入教室后 ，通过该方法通知appserver
     *
     * @Title: sendTeacherInClassroom
     * @param requestParams
     * @param teacher
     * @return Map<String,Object>
     * @date 2016年1月11日
     */
    public Map<String, Object> sendTeacherInClassroom(Map<String, String> requestParams,
            Teacher teacher) {
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.put("status", false);

        String t = "TEACHER " + teacher.getId();
        Map<String, String> requestHeader = new HashMap<String, String>();
        requestHeader.put("Authorization", t + " " + DigestUtils.md5Hex(t));
        String content = HttpClientProxy.get(ApplicationConstant.TEACHER_IN_CLASSROOM_URL,
                requestParams, requestHeader);

        logger.info("### Mark that teacher enter classroom: {}", content);
        logger.info("### Sent get request to {} with params {}",
                ApplicationConstant.TEACHER_IN_CLASSROOM_URL, requestParams.get("onlineClassId"));
        if (!StringUtils.isNotEmpty(content)) {
            modelMap.put("status", true);
        } else {
            modelMap.put("msg", "failed to tell the fireman teacher in the classroom!");
        }
        return modelMap;
    }

    /**
     * 判断当前课程是否为公开课，如果是则设置不显示退出教室提示
     *
     * @Title: isEmpty
     * @param onlineClassId
     * @param studentId
     * @return Map<String,Object>
     * @date 2016年1月11日
     */
    public Map<String, Object> isEmpty(long onlineClassId, long studentId) {
        Map<String, Object> modelMap = Maps.newHashMap();
        OnlineClass onlineClass = onlineClassDao.findById(onlineClassId);
        Lesson lesson = lessonDao.findById(onlineClass.getLessonId());

        String serialNumber = lesson.getSerialNumber();
        if (serialNumber.startsWith("OPEN")) {
            modelMap.put("empty", false);
            return modelMap;
        }

        /* 判断当前课程是否显示退出教室提示 */
        TeacherComment teacherComment =
                teacherCommentDao.findByStudentIdAndOnlineClassId(studentId, onlineClassId);
        if (null == teacherComment || StringUtils.isBlank(teacherComment.getTeacherFeedback())) {
            modelMap.put("empty", true);
            
            //查询UA是否已经填写
            StudentUnitAssessment studentUnitAssessment = assessmentHttpService.findStudentUnitAssessmentByOnlineClassId(onlineClassId);
            if(studentUnitAssessment !=null){
            	modelMap.put("empty", false);
            }
        } else {
            modelMap.put("empty", false);
        }
        return modelMap;
    }

    /**
     * 查询DemoReport对象
     *
     * @param onlineClassId
     * @return
     */
    public DemoReport getDemoReport(long onlineClassId, long studentId) {
        return demoReportDao.findByStudentIdAndOnlineClassId(studentId, onlineClassId);
    }

    /**
     *
     * @Title: sendStarlogs
     * @param send
     * @param studentId
     * @param onlineClassId
     * @param teacher
     * @date 2016年1月11日
     */
    public void sendStarlogs(boolean send, long studentId, long onlineClassId, Teacher teacher) {
        Student student = studentDao.findById(studentId);
        OnlineClass onlineClass = onlineClassDao.findById(onlineClassId);

        /* 记录操作日志 */
        Map<String, Object> parmMap = Maps.newHashMap();

        parmMap.put("teacherId", teacher.getId());
        parmMap.put("teacherName", teacher.getRealName());

        parmMap.put("studentId", student.getId());
        parmMap.put("studentName", student.getEnglishName());

        parmMap.put("onlineClassId", onlineClass.getId());
        parmMap.put("roomId", onlineClass.getClassroom());

        if (send) {
            String content = FilesUtils.readLogTemplete(ApplicationConstant.AuditCategory.STAR_SEND,
                    parmMap);
            auditDao.saveAudit(ApplicationConstant.AuditCategory.STAR_SEND, "INFO", content,
                    teacher.getRealName(), teacher, IPUtils.getRemoteIP());
            logger.info(
                    "Teacher: id={},name={} send star, Student: id={},name={}, onlineClassId: id={},room={}",
                    teacher.getId(), teacher.getRealName(), studentId, student.getEnglishName(),
                    onlineClassId, onlineClass.getClassroom());
        } else {
            String content = FilesUtils
                    .readLogTemplete(ApplicationConstant.AuditCategory.STAR_REMOVE, parmMap);
            auditDao.saveAudit(ApplicationConstant.AuditCategory.STAR_REMOVE, "INFO", content,
                    teacher.getRealName(), teacher, IPUtils.getRemoteIP());
            logger.info(
                    "Teacher: id={},name={} remove star, Student: id={},name={}, onlineClassId: id={},room={}",
                    teacher.getId(), teacher.getRealName(), studentId, student.getEnglishName(),
                    onlineClassId, onlineClass.getClassroom());
        }
    }

    /**
     * 根据onlineClass对象的Id获取TeacherComment对象，无则返回0，有则返回第一条
     *
     *
     * @Title: findTeacherCommentByOnlineClassId
     * @param onlineclass
     * @return TeacherComment
     * @date 2016年1月9日
     */
    private int findTeacherCommentByOnlineClassId(OnlineClass onlineclass, long studentId) {
        TeacherComment bean =
                teacherCommentDao.findByStudentIdAndOnlineClassId(studentId, onlineclass.getId());
        if (bean != null) {
            return bean.getStars();
        }
        return 0;
    }

    /**
     * 检查onlineClassId studentId 是否匹配
     *
     * @Author:ALong (ZengWeiLong)
     * @param onlineClassId
     * @param studentId
     * @return boolean
     * @date 2016年5月14日
     */
    public boolean checkStudentIdClassId(long onlineClassId, long studentId) {
        List<Map<String, Object>> list =
                onlineClassDao.findOnlineClassIdAndStudentId(onlineClassId, studentId);
        if (list != null && !list.isEmpty()) {
            return true;
        }
        return false;
    }

    public void finishPracticum(long onlineClassId, String finishType) {
        Preconditions.checkArgument(0 != onlineClassId);

        OnlineClass onlineClass = onlineClassDao.findById(onlineClassId);
        if (null != onlineClass) {
            if (StringUtils.isNotEmpty(finishType)) {
                onlineClass.setFinishType(finishType);
            } else {
                onlineClass.setFinishType(FinishType.AS_SCHEDULED);
            }
            onlineClass.setStatus(ClassStatus.FINISHED);
            onlineClassDao.updateEntity(onlineClass);
        }
    }

}
