package com.vipkid.trpm.service.pe;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.vipkid.enums.OnlineClassEnum.ClassStatus;
import com.vipkid.enums.TbdResultEnum;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.enums.TeacherModuleEnum.RoleClass;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.dao.AuditDao;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherModuleDao;
import com.vipkid.trpm.dao.TeacherPeDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherModule;
import com.vipkid.trpm.entity.TeacherPe;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.OnlineClassProxy;
import com.vipkid.trpm.util.DateUtils;
import com.vipkid.trpm.util.FilesUtils;
import com.vipkid.trpm.util.IpUtils;

@Service
public class PeSupervisorService {

    public static Logger logger = LoggerFactory.getLogger(PeSupervisorService.class);

    @Autowired
    private TeacherPeDao teacherPeDao;

    @Autowired
    private OnlineClassDao onlineClassDao;

    @Autowired
    private LessonDao lessonDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private AuditDao auditDao;

    @Autowired
    private TeacherModuleDao teacherModuleDao;

    public int totalPe(long teacherId) {
        return teacherPeDao.countByTeacherId(teacherId);
    }

    public Map<String, Object> classList(long teacherId, int curPage, int linePerPage) {
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.put("dataList", teacherPeDao.pageByTeacherId(teacherId, curPage, linePerPage));
        return modelMap;
    }

    public TeacherPe getTeacherPe(int id) {
        return teacherPeDao.findById(id);
    }

    public int updatePeSupervisorStartTime(int id) {
        return teacherPeDao.updateTeacherPeStartTime(new TeacherPe().setId(id));
    }

    public TeacherApplication getTeacherApplication(long teacherId) {
        return teacherApplicationDao.findCurrentApplication(teacherId).stream().findFirst().get();
    }

    public String getClassRoomUrl(TeacherPe teacherPe) {
        OnlineClass onlineClass = onlineClassDao.findById(teacherPe.getOnlineClassId());
        Map<String,Object> result = OnlineClassProxy.generateRoomEnterUrl(String.valueOf(teacherPe.getStudentId()),
                teacherPe.getStudentName(), onlineClass.getClassroom(), OnlineClassProxy.RoomRole.STUDENT,
                onlineClass.getSupplierCode(),onlineClass.getId(),OnlineClassProxy.ClassType.PRACTICUM);
        return result.get("url")+"";
    }

    /**
     * 结束Practicum课操作
     *
     * @Author:ALong
     * @param peSupervisor 操作老师(学生角色)
     * @param currTeacherApplication 上课Id
     * @param result 结果
     * @param finishType 完成类型
     * @param peId peId
     * @return Map<String,Object>
     * @date 2016年1月11日
     */
    public Map<String, Object> updateAudit(Teacher peSupervisor,
                                           TeacherApplication currTeacherApplication, String result, String finishType, int peId) {
        // 默认操作状态
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.put("result", false);

        // PE是否已经处理
        TeacherPe teacherPe = this.teacherPeDao.findById(peId);
        if (teacherPe.getStatus() != 0) {
            logger.info(" PE:The recruitment process already end.");
            modelMap.put("msg", "Please select an finish type first！");
            return modelMap;
        }

        if (0 == teacherPe.getPeId()) {
            logger.info("The practicum pe has be recycled！");
            modelMap.put("msg", "The practicum task has be recycled！");
            return modelMap;
        }

        // 1.finishType 如果为null，则抛出错误信息
        if (StringUtils.isBlank(finishType)) {
            logger.info("Finish Type is null ");
            modelMap.put("msg", "Please select an finish type first！");
            return modelMap;
        }

        // 2.验证 teacherApplications 是否为空
        long onlineClassId = currTeacherApplication.getOnlineClassId();
        
        OnlineClass onlineClass = onlineClassDao.findById(onlineClassId);
        
        if(onlineClass == null){
        	modelMap.put("msg", "Not exist the online-class recruitment info ！");
            logger.info(" Online Class is null onlineClassId:{} , status is PRACTICUM ",
                    onlineClassId);
        }
        //查询ta信息
        /*TeacherApplication teacherApplication = teacherApplicationDao
                .findApplictionByOlineclassId(onlineClassId, peSupervisor.getId());*/
        TeacherApplication teacherApplication = teacherApplicationDao.findApplictionById(currTeacherApplication.getId());
        
        if (teacherApplication == null) {
            modelMap.put("msg", "Not exist the online-class recruitment info ！");
            logger.info(" TeacherApplication is null onlineClassId:{} , status is PRACTICUM ",
                    onlineClassId);
            return modelMap;
        }
        
        // 3.如果result 不等于null 则返回错误
        if (!StringUtils.isBlank(teacherApplication.getResult())) {
            logger.info(
                    "Teacher application already end or recruitment process step already end, class id is : {},status is {}",
                    onlineClass.getId(), onlineClass.getStatus());
            modelMap.put("msg", " The recruitment process already end.");
            return modelMap;
        }

        // 4.验证 recruitTeacher 是否存在
        Teacher recruitTeacher = teacherDao.findById(teacherApplication.getTeacherId());
        if (recruitTeacher == null) {
            modelMap.put("msg", "System error！");
            logger.info(" Recruitment Teacher is null , teacher id is {}",
                    teacherApplication.getTeacherId());
            return modelMap;
        }

        // 5.practicum2 判断是否存在
        if (TeacherApplicationEnum.Result.PRACTICUM2.toString().equals(result)) {
            List<TeacherApplication> list = teacherApplicationDao
                    .findApplictionForStatusResult(teacherApplication.getTeacherId(),Status.PRACTICUM.toString(),Result.PRACTICUM2.toString());
            if (list != null && list.size() > 0) {
                logger.info(
                        "The teacher is already in practicum 2., class id is : {},status is {},recruitTeacher:{}",
                        onlineClass.getId(), onlineClass.getStatus(), recruitTeacher.getId());
                modelMap.put("msg", "The teacher is already in practicum 2.");
                return modelMap;
            }
        }

        Map<String, Object> parmMap = Maps.newHashMap();
        parmMap.put("teacherId", peSupervisor.getId());
        parmMap.put("teacherName", peSupervisor.getRealName());
        parmMap.put("recruitId", recruitTeacher.getId());
        parmMap.put("recruitName", recruitTeacher.getRealName());
        parmMap.put("onlineClassId", onlineClass.getId());
        parmMap.put("roomId", onlineClass.getClassroom());
        parmMap.put("result", result);
        parmMap.put("finishType", finishType);

        // 6.先结束online Class，然后操作TeacherApllication
        if (ClassStatus.isBooked(onlineClass.getStatus())
                || ClassStatus.isFinished(onlineClass.getStatus())) {
            currTeacherApplication.setContractUrl("PE-Supervisor");
            modelMap = this.updateTeacherApplication(recruitTeacher, peSupervisor, result, "",
                    currTeacherApplication);
            // 日志 1
            String content = FilesUtils
                    .readLogTemplate(ApplicationConstant.AuditCategory.PRACTICUM_AUDIT, parmMap);
            auditDao.saveAudit(ApplicationConstant.AuditCategory.PRACTICUM_AUDIT, "INFO", content,
                    peSupervisor.getRealName(), recruitTeacher, IpUtils.getRemoteIP());

            if ("true".equals(String.valueOf(modelMap.get("result")))) {
                this.teacherPeDao.updateTeacherPeComments(teacherPe, result, "");

                logger.info(
                        "Practicum Online Class[booked] updateAudit,studentId:{},onlineClassId:{},recruitTeacher:{},teacherId:{}",
                        teacherApplication.getStduentId(), onlineClass.getId(),
                        recruitTeacher.getId(), peSupervisor.getId());
            }

            // 设置调用接口的参数
            modelMap.put("teacherApplicationId", currTeacherApplication.getId());
            modelMap.put("recruitTeacher", recruitTeacher);

            return modelMap;
        } else {
            logger.info("online class status not is booked or finish status,online class Id:{}",
                    onlineClass.getId());
            modelMap.put("msg", "Online class status error！");
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
    private Map<String, Object> updateTeacherApplication(Teacher recruitTeacher, Teacher pes,
                                                         String result, String comments, TeacherApplication teacherApplication) {

        Map<String, Object> modelMap = Maps.newHashMap();

        // 设置审核结果
        teacherApplication.setResult(result);
        // 设置审核备注
        teacherApplication.setComments(comments);
        // 设置面试官Id
        teacherApplication.setAuditorId(pes.getId());
        // 设置应聘老师Id
        teacherApplication.setTeacherId(recruitTeacher.getId());
        teacherApplication.setCurrent(1);
        teacherApplication.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
        // 如果是PASS操作，则ta状态修改为FINISH，教师状态修改为REGULAR
//        if (TeacherApplicationEnum.Result.PASS.toString().equals(result)) {
            //teacherApplication.setStatus(TeacherApplicationEnum.Status.FINISHED.toString());
            // 2.教师状态更新
            //recruitTeacher.setLifeCycle(LifeCycle.REGULAR.toString());
            // 3.新增教师入职时间
            //recruitTeacher.setEntryDate(new Date());
            //recruitTeacher.setType(TeacherEnum.Type.PART_TIME.toString());
            //this.teacherDao.update(recruitTeacher);
            // 增加quiz的考试记录
            //teacherQuizDao.insertQuiz(recruitTeacher.getId(),pes.getId());
//        }
        // 3.更新teacherApplication
        this.teacherApplicationDao.update(teacherApplication);

        // 4.更新最后一次编辑人,编辑时间
        User ruser = this.userDao.findById(recruitTeacher.getId());
        if (ruser != null) {
            ruser.setLastEditorId(pes.getId());
            ruser.setLastEditDateTime(new Timestamp(System.currentTimeMillis()));
            this.userDao.update(ruser);
        }

        modelMap.put("result", true);
        return modelMap;
    }

    /**
     * 处理PE的Practicum操作
     *
     * @param pe
     * @param teacherApplication
     * @return
     */
    public Map<String, Object> doPracticumForPE(Teacher pe, TeacherApplication teacherApplication,
                                                String result) {
        Preconditions.checkArgument(0 != teacherApplication.getId());
        Preconditions.checkArgument(StringUtils.isNotEmpty(result));

        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.put("result", false);

        OnlineClass onlineClass = onlineClassDao.findById(teacherApplication.getOnlineClassId());
        if (null == onlineClass) {
            modelMap.put("msg", "Online-class is empty");
            return modelMap;
        }

        // 检查课程是否开始15分钟
        if (!DateUtils.count15Mine(onlineClass.getScheduledDateTime().getTime())) {
            modelMap.put("msg", "Sorry, you can't submit the form within 15min!");
            return modelMap;
        }

        List<TeacherModule> teacherModules = teacherModuleDao.findByTeacherModuleName(pe.getId(),RoleClass.PE);

        // 判断当前用户是否拥有PE Supervisor权限
        if (CollectionUtils.isNotEmpty(teacherModules)
                && TeacherApplicationEnum.Result.TBD_FAIL.toString().equals(result)) {
            // 拥有PE Supervisor权限
            // 更新当前Application记录的结果为FAIL
            teacherApplication.setResult(TeacherApplicationEnum.Result.FAIL.toString());
            teacherApplication.setAuditorId(pe.getId());
            teacherApplication.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
            teacherApplication.setContractUrl("PE-Supervisor");
            teacherApplicationDao.update(teacherApplication);

            modelMap.put("result", true);

            // 设置调用接口的参数
            modelMap.put("teacherApplicationId", teacherApplication.getId());
            modelMap.put("recruitTeacher", teacherDao.findById(teacherApplication.getTeacherId()));

        } else {
            // 没有PE Supervisor权限
            // 插入当前Application记录的副本
            List<TeacherApplication> currentTeacherApplications =
                    teacherApplicationDao.findCurrentApplication(teacherApplication.getTeacherId());
            if (CollectionUtils.isEmpty(currentTeacherApplications)) {
                throw new RuntimeException("Illegal TeacherApplication data for teacher ["
                        + teacherApplication.getTeacherId() + "]");
            } else {
                Optional<TeacherApplication> optionalTeacherApplication =
                        currentTeacherApplications.stream().findFirst();
                TeacherApplication enabledTeacherApplication = optionalTeacherApplication.get();

                if (TeacherApplicationEnum.Status.PRACTICUM.toString().equals(enabledTeacherApplication.getStatus())) {
                    // 开始插入当前Application记录的副本
                    enabledTeacherApplication.setId(0);
                    enabledTeacherApplication.setContractUrl("PE-Supervisor");
                    enabledTeacherApplication.setVersion(2);
                    teacherApplicationDao.save(enabledTeacherApplication);

                    // 更新当前Application记录的结果为TBD
                    teacherApplication.setResult(result);
                    teacherApplication.setAuditorId(pe.getId());
                    teacherApplication.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
                    teacherApplication.setCurrent(0);
                    teacherApplication.setContractUrl("PE");
                    teacherApplicationDao.update(teacherApplication);

                    // 自动预分配任务
                    Lesson lesson = lessonDao.findById(onlineClass.getLessonId());
                    if (null == lesson) {
                        modelMap.put("msg", "Lesson is empty");
                        return modelMap;
                    }

                    TeacherPe teacherPe = teacherPeDao.findByOnlineClassId(onlineClass.getId());
                    if (null != teacherPe) {
                        modelMap.put("msg", "The Online-class already to TBD");
                        return modelMap;
                    }

                    User candidate = userDao.findById(teacherApplication.getTeacherId());
                    if (null == candidate) {
                        modelMap.put("msg", "Candidate is empty");
                        return modelMap;
                    }

                    String studentName = pe.getRealName();
                    // 如果名字里面含有空格，则取到空格后的第一个字符作为User的Name
                    try {
                        if (studentName.indexOf(" ") > -1) {
                            studentName = studentName.substring(0, studentName.indexOf(" ") + 2);
                        }
                    } catch (Exception e) {
                        logger.error("Format pe name error");
                    }

                    teacherPe = new TeacherPe();
                    teacherPe.setOnlineClassId(onlineClass.getId());
                    teacherPe.setTeacherId(candidate.getId());
                    teacherPe.setTeacherName(candidate.getName());

                    teacherPe.setSerialNumber(lesson.getSerialNumber());
                    teacherPe.setLessonName(lesson.getName());
                    teacherPe.setScheduleTime(onlineClass.getScheduledDateTime());
                    teacherPe.setStudentName(studentName);
                    teacherPe.setStudentId(pe.getId());

                    teacherPe.setCreationTime(new Timestamp(System.currentTimeMillis()));
                    teacherPe.setOperatorTime(new Timestamp(System.currentTimeMillis()));
                    teacherPe.setStatus(0);
                    teacherPe.setTeacherAction(TbdResultEnum.getResultEnum(result));
                    teacherPeDao.save(teacherPe);

                    modelMap.put("result", true);
                } else {
                    throw new RuntimeException(
                            "Illegal TeacherApplication status for teacher [" + pe.getId() + "]");
                }
            }
        }

        return modelMap;
    }

    public Map<String, Object> peExitClass(long teacherApplicationId) {
        Map<String, Object> resultMap = Maps.newHashMap();
        TeacherApplication teacherApplication =
                teacherApplicationDao.findApplictionById(teacherApplicationId);
        if (null == teacherApplication || StringUtils.isEmpty(teacherApplication.getResult())) {
            resultMap.put("result", false);
        } else {
            resultMap.put("result", true);
        }
        return resultMap;
    }

}
