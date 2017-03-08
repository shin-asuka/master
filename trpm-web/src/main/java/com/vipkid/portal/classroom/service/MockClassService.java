package com.vipkid.portal.classroom.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vipkid.enums.MockClassEnum;
import com.vipkid.enums.OnlineClassEnum.ClassStatus;
import com.vipkid.enums.TbdResultEnum;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherModuleEnum.RoleClass;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.portal.classroom.model.mockclass.*;
import com.vipkid.portal.classroom.util.BeanUtils;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.event.AuditEvent;
import com.vipkid.recruitment.event.AuditEventHandler;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.*;
import com.vipkid.trpm.service.pe.AppserverPracticumService;
import com.vipkid.trpm.service.pe.TeacherPeLevelsService;
import com.vipkid.trpm.service.pe.TeacherPeResultService;
import com.vipkid.trpm.service.pe.TeacherPeTagsService;
import com.vipkid.trpm.service.portal.OnlineClassService;
import com.vipkid.trpm.util.DateUtils;
import com.vipkid.trpm.util.FilesUtils;
import com.vipkid.trpm.util.IpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.vipkid.enums.TbdResultEnum.ReScheduleEnum;

@Service
public class MockClassService {

    private static Logger logger = LoggerFactory.getLogger(MockClassService.class);

    private static final String MOCK_CLASS_1 = "P1-U1-LC1-L1";

    @Autowired
    private TeacherPeTemplateDao teacherPeTemplateDao;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    @Autowired
    private TeacherPeCommentsDao teacherPeCommentsDao;

    @Autowired
    private TeacherPeTagsDao teacherPeTagsDao;

    @Autowired
    private TeacherPeLevelsDao teacherPeLevelsDao;

    @Autowired
    private TeacherPeRubricDao teacherPeRubricDao;

    @Autowired
    private TeacherPeSectionDao teacherPeSectionDao;

    @Autowired
    private TeacherPeCriteriaDao teacherPeCriteriaDao;

    @Autowired
    private TeacherPeOptionDao teacherPeOptionDao;

    @Autowired
    private TeacherPeResultDao teacherPeResultDao;

    @Autowired
    private TeacherPeTagsService teacherPeTagsService;

    @Autowired
    private TeacherPeLevelsService teacherPeLevelsService;

    @Autowired
    private TeacherPeResultService teacherPeResultService;

    @Autowired
    private TeacherPeDao teacherPeDao;

    @Autowired
    private OnlineClassDao onlineClassDao;

    @Autowired
    private LessonDao lessonDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuditDao auditDao;

    @Autowired
    private LoginService loginService;

    @Autowired
    private OnlineClassService onlineclassService;

    @Autowired
    private AppserverPracticumService appserverPracticumService;

    @Autowired
    private AuditEventHandler auditEventHandler;

    public PeReviewOutputDto doPeReview(Integer applicationId) {
        TeacherApplication teacherApplication = teacherApplicationDao.findApplictionById(applicationId);
        Preconditions.checkNotNull(teacherApplication, "Teacher application [" + applicationId + "] not found");

        PeReviewOutputDto peReviewOutputDto = new PeReviewOutputDto();
        // 如果结果是 REAPPLY 则直接返回
        if (StringUtils.equals(teacherApplication.getResult(), Result.REAPPLY.name())) {
            peReviewOutputDto.setStatus(teacherApplication.getResult());
            return peReviewOutputDto;
        }

        TeacherPeComments teacherPeComments = teacherPeCommentsDao.getTeacherPeComments(applicationId);
        Preconditions.checkArgument(0 != teacherPeComments.getTemplateId(),
                        "This applicationId [" + applicationId + "] is illegal");

        // 首次进入初始化 Pe Comments
        if (null == teacherPeComments) {
            teacherPeComments = new TeacherPeComments();
            teacherPeComments.setApplicationId(applicationId);
            teacherPeComments.setTemplateId(getCurrentPeTemplate().getId());
            teacherPeCommentsDao.saveTeacherPeComments(teacherPeComments);
        }
        BeanUtils.copyPropertys(teacherPeComments, peReviewOutputDto);

        // 查询 result 列表
        List<TeacherPeResult> peResultList = teacherPeResultDao.listTeacherPeResult(applicationId);

        // 查询 rubric 列表
        List<PeRubricDto> rubricDtoList = listPeRubric(teacherPeComments.getTemplateId(), peResultList);
        peReviewOutputDto.setRubricList(rubricDtoList);

        // set tags
        List<TeacherPeTags> peTagsList = teacherPeTagsDao.getTeacherPeTagsByApplicationId(applicationId);
        if (CollectionUtils.isNotEmpty(peTagsList)) {
            List<Integer> tagIds =
                            peTagsList.stream().map(peTags -> new Integer(peTags.getId())).collect(Collectors.toList());
            peReviewOutputDto.setTagsList(tagIds);
        }

        // set levels
        List<TeacherPeLevels> peLevelsList = teacherPeLevelsDao.getTeacherPeLevelsByApplicationId(applicationId);
        if (CollectionUtils.isNotEmpty(peLevelsList)) {
            List<Integer> levelIds = peLevelsList.stream().map(peLevels -> new Integer(peLevels.getId()))
                            .collect(Collectors.toList());
            peReviewOutputDto.setLevelsList(levelIds);
        }

        return peReviewOutputDto;
    }

    public String peDoAudit(PeDoAuditInputDto peDoAuditInputDto) {
        Teacher peTeacher = loginService.getTeacher();
        TeacherPeComments teacherPeComments =
                        teacherPeCommentsDao.getTeacherPeComments(peDoAuditInputDto.getApplicationId());
        BeanUtils.copyPropertys(peDoAuditInputDto, teacherPeComments);

        // set totalScore
        int totalScore = 0;
        if (CollectionUtils.isNotEmpty(peDoAuditInputDto.getOptionList())) {
            totalScore = teacherPeOptionDao.calculateOptionsPoints(peDoAuditInputDto.getOptionList());
        }
        teacherPeComments.setTotalScore(totalScore);

        if (!StringUtils.equals(peDoAuditInputDto.getStatus(), ReScheduleEnum.ReSchedule.val())) {
            // set tags
            updatePeTags(peDoAuditInputDto.getApplicationId(), peDoAuditInputDto.getTagsList());
            // set levels
            updatePeLevels(peDoAuditInputDto.getApplicationId(), peDoAuditInputDto.getLevelsList());
            // set results
            updatePeResults(peDoAuditInputDto.getApplicationId(), peDoAuditInputDto.getOptionList());
        }

        teacherPeCommentsDao.updateTeacherPeComments(teacherPeComments);
        logger.info("Pe doAudit teacherPeComments: {}", JsonUtils.toJSONString(teacherPeComments));

        if (StringUtils.equals(peDoAuditInputDto.getStatus(), MockClassEnum.SAVE.name())) {
            // do save
            logger.info("Pe doAudit save succeed");
        } else if (StringUtils.equals(peDoAuditInputDto.getStatus(), MockClassEnum.SUBMIT.name())
                        || StringUtils.equals(peDoAuditInputDto.getStatus(), ReScheduleEnum.ReSchedule.val())) {
            // do submit or reschedule
            TeacherApplication teacherApplication =
                            teacherApplicationDao.findApplictionById(peDoAuditInputDto.getApplicationId());
            Preconditions.checkNotNull(teacherApplication,
                            "Teacher application [" + peDoAuditInputDto.getApplicationId() + "] not found");

            OnlineClass onlineClass = onlineClassDao.findById(teacherApplication.getOnlineClassId());
            Preconditions.checkNotNull(onlineClass,
                            "Online class [" + teacherApplication.getOnlineClassId() + "] not found");

            // set result
            String result = Result.REAPPLY.name();
            if (StringUtils.equals(peDoAuditInputDto.getStatus(), MockClassEnum.SUBMIT.name())) {
                int totalProfessionalisms =
                                teacherPeOptionDao.calculateOptionsProfessionalisms(peDoAuditInputDto.getOptionList());
                result = getResult(onlineClass.getSerialNumber(), totalScore, totalProfessionalisms);

                logger.info("Pe doAudit submit result: {}, totalScore: {}, totalProfessionalisms: {}", result,
                                totalScore, totalProfessionalisms);
            }

            // result 不为 null 则返回错误
            if (StringUtils.isNotBlank(teacherApplication.getResult())) {
                return "The recruitment process already end.";
            }

            // 课程是否已经开始 15 分钟
            if (!DateUtils.count15Mine(onlineClass.getScheduledDateTime().getTime())) {
                return "Sorry, you can't submit the form within 15min!";
            }

            // 判断 practicum2 是否已经存在
            if (StringUtils.equals(Result.PRACTICUM2.name(), result)) {
                List<TeacherApplication> practicum2List = teacherApplicationDao.findApplictionForStatusResult(
                                teacherApplication.getTeacherId(), Status.PRACTICUM.name(), Result.PRACTICUM2.name());
                if (CollectionUtils.isNotEmpty(practicum2List)) {
                    return "The teacher is already in practicum 2.";
                }
            }

            // TBD
            if (StringUtils.equals(Result.TBD.name(), result)) {
                return doTBD(teacherApplication, peTeacher, onlineClass);
            }

            // audit
            if (ClassStatus.isBooked(onlineClass.getStatus()) || ClassStatus.isFinished(onlineClass.getStatus())) {
                Teacher recruitTeacher = teacherDao.findById(teacherApplication.getTeacherId());
                Preconditions.checkNotNull(recruitTeacher,
                                "Recruitment teacher [" + teacherApplication.getTeacherId() + "] not found");

                teacherApplication.setContractUrl(RoleClass.PE);
                // 设置审核结果
                teacherApplication.setResult(result);
                // 设置面试官Id
                teacherApplication.setAuditorId(peTeacher.getId());
                teacherApplication.setAuditDateTime(new Timestamp(System.currentTimeMillis()));

                teacherApplicationDao.update(teacherApplication);
                logger.info("Pe doAudit mockClass, teacherApplication: {}", JsonUtils.toJSONString(teacherApplication));

                // TODO 合并 tags，levels

                // Finish 课程
                onlineclassService.finishPracticum(teacherApplication, peDoAuditInputDto.getFinishType(), peTeacher,
                                recruitTeacher);
                // 发送邮件
                auditEventHandler.onAuditEvent(
                                new AuditEvent(recruitTeacher.getId(), LifeCycle.PRACTICUM.name(), result));

                // 更新 last editor
                updateLastEditor(peTeacher, recruitTeacher);
                // audit logs
                auditLogs(peTeacher, onlineClass, recruitTeacher, result, peDoAuditInputDto.getFinishType());
            } else {
                throw new IllegalStateException("Online class status is not BOOKED or FINISHED");
            }

            logger.info("Pe doAudit submit succeed");
        } else {
            throw new IllegalArgumentException("Pe 'status' is not matched");
        }

        return HttpStatus.OK.getReasonPhrase();
    }

    public String getResult(String serialNumber, int totalScore, int totalProfessionalisms) {
        if (StringUtils.equals(MOCK_CLASS_1, serialNumber)) {
            if (totalProfessionalisms < 3 || totalScore < 35) {
                return Result.FAIL.name();
            } else if (totalProfessionalisms >= 3 && (totalScore >= 35 || totalScore <= 44)) {
                return Result.PRACTICUM2.name();
            } else {
                return Result.PASS.name();
            }
        } else {
            if (totalProfessionalisms < 3 || totalScore <= 39) {
                return Result.FAIL.name();
            } else if (totalProfessionalisms >= 3 && (totalScore >= 40 || totalScore <= 45)) {
                return Result.TBD.name();
            } else {
                return Result.PASS.name();
            }
        }
    }

    public String doTBD(TeacherApplication teacherApplication, Teacher peTeacher, OnlineClass onlineClass) {
        // 插入当前 Application 记录的副本
        List<TeacherApplication> currentApplications =
                        teacherApplicationDao.findCurrentApplication(teacherApplication.getTeacherId());

        if (CollectionUtils.isEmpty(currentApplications)) {
            throw new IllegalArgumentException(
                            "Teacher application not found for teacher [" + teacherApplication.getTeacherId() + "]");
        } else {
            Optional<TeacherApplication> optionalApplication = currentApplications.stream().findFirst();
            TeacherApplication enabledApplication = optionalApplication.get();

            if (Status.PRACTICUM.name().equals(enabledApplication.getStatus())) {
                // 开始插入当前 Application 记录的副本
                enabledApplication.setId(0);
                enabledApplication.setContractUrl(RoleClass.PES);
                enabledApplication.setVersion(2);
                teacherApplicationDao.save(enabledApplication);

                // 更新当前 Application 记录的结果为 TBD
                teacherApplication.setResult(Result.TBD.name());
                teacherApplication.setAuditorId(peTeacher.getId());
                teacherApplication.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
                teacherApplication.setCurrent(0);
                teacherApplication.setContractUrl(RoleClass.PE);
                teacherApplicationDao.update(teacherApplication);

                // 自动预分配任务
                Lesson lesson = lessonDao.findById(onlineClass.getLessonId());
                if (null == lesson) {
                    return "Lesson is empty";
                }

                TeacherPe teacherPe = teacherPeDao.findByOnlineClassId(onlineClass.getId());
                if (null != teacherPe) {
                    return "The Online-class already to TBD";
                }

                User candidate = userDao.findById(teacherApplication.getTeacherId());
                if (null == candidate) {
                    return "Candidate is empty";
                }

                teacherPe = new TeacherPe();
                teacherPe.setOnlineClassId(onlineClass.getId());
                teacherPe.setTeacherId(candidate.getId());
                teacherPe.setTeacherName(candidate.getName());

                teacherPe.setSerialNumber(lesson.getSerialNumber());
                teacherPe.setLessonName(lesson.getName());
                teacherPe.setScheduleTime(onlineClass.getScheduledDateTime());

                String studentName = getShowName(peTeacher.getRealName());
                teacherPe.setStudentName(studentName);
                teacherPe.setStudentId(peTeacher.getId());

                teacherPe.setCreationTime(new Timestamp(System.currentTimeMillis()));
                teacherPe.setOperatorTime(new Timestamp(System.currentTimeMillis()));
                teacherPe.setStatus(0);
                teacherPe.setTeacherAction(TbdResultEnum.getResultEnum(Result.TBD.name()));
                teacherPeDao.save(teacherPe);

                return HttpStatus.OK.getReasonPhrase();
            } else {
                throw new RuntimeException(
                                "Teacher application illegal status for teacher [" + peTeacher.getId() + "]");
            }
        }
    }

    public void updatePeTags(Integer applicationId, List<Integer> tagIds) {
        List<TeacherPeTags> peTagsList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(tagIds)) {
            for (Integer tagId : tagIds) {
                TeacherPeTags teacherPeTag = new TeacherPeTags();
                teacherPeTag.setApplicationId(applicationId);
                teacherPeTag.setTagId(tagId);
                peTagsList.add(teacherPeTag);
            }
        }
        teacherPeTagsService.updatePeTags(applicationId, peTagsList);
    }

    public void updatePeLevels(Integer applicationId, List<Integer> levelIds) {
        List<TeacherPeLevels> peLevelsList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(levelIds)) {
            for (Integer levelId : levelIds) {
                TeacherPeLevels teacherPeLevel = new TeacherPeLevels();
                teacherPeLevel.setApplicationId(applicationId);
                teacherPeLevel.setLevel(levelId);
                peLevelsList.add(teacherPeLevel);
            }
        }
        teacherPeLevelsService.updateTeacherPeLevels(applicationId, peLevelsList);
    }

    public void updatePeResults(Integer applicationId, List<Integer> optionIds) {
        List<TeacherPeResult> peResultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(optionIds)) {
            for (Integer optionId : optionIds) {
                TeacherPeResult teacherPeResult = new TeacherPeResult();
                teacherPeResult.setApplicationId(applicationId);
                teacherPeResult.setOptionId(optionId);
                peResultList.add(teacherPeResult);
            }
        }
        teacherPeResultService.updateTeacherPeResults(applicationId, peResultList);
    }

    public TeacherPeTemplate getCurrentPeTemplate() {
        TeacherPeTemplate teacherPeTemplate = teacherPeTemplateDao.getCurrentPeTemplate();
        return Preconditions.checkNotNull(teacherPeTemplate, "Pe current template not found");
    }

    public TeacherPeTemplate getPeTempate(Integer id) {
        TeacherPeTemplate teacherPeTemplate = teacherPeTemplateDao.getPeTemplate(id);
        return Preconditions.checkNotNull(teacherPeTemplate, "Pe template [" + id + "] not found");
    }

    public List<PeRubricDto> listPeRubric(Integer templateId, List<TeacherPeResult> peResultList) {
        List<PeRubricDto> rubricDtoList = Lists.newLinkedList();
        List<TeacherPeRubric> peRubricList = teacherPeRubricDao.listTeacherPeRubric(templateId);

        if (CollectionUtils.isNotEmpty(peRubricList)) {
            for (TeacherPeRubric peRubric : peRubricList) {
                List<PeSectionDto> peSectionDtoList = Lists.newLinkedList();
                List<TeacherPeSection> peSectionList = teacherPeSectionDao.listTeacherPeSection(peRubric.getId());

                if (CollectionUtils.isNotEmpty(peSectionList)) {
                    for (TeacherPeSection peSection : peSectionList) {
                        List<PeCriteriaDto> peCriteriaDtoList = Lists.newLinkedList();
                        List<TeacherPeCriteria> peCriteriaList =
                                        teacherPeCriteriaDao.listTeacherPeCriteria(peSection.getId());

                        if (CollectionUtils.isNotEmpty(peCriteriaList)) {
                            for (TeacherPeCriteria peCriteria : peCriteriaList) {
                                List<PeOptionDto> peOptionDtoList = Lists.newLinkedList();
                                List<TeacherPeOption> peOptionList =
                                                teacherPeOptionDao.listTeacherPeOption(peCriteria.getId());

                                if (CollectionUtils.isNotEmpty(peOptionList)) {
                                    for (TeacherPeOption peOption : peOptionList) {
                                        // set option
                                        PeOptionDto peOptionDto = new PeOptionDto();
                                        peOptionDto.setChecked(false);
                                        BeanUtils.copyPropertys(peOption, peOptionDto);

                                        if (CollectionUtils.isNotEmpty(peResultList)) {
                                            peResultList.stream().forEach(peResult -> {
                                                if (peResult.getId() == peOptionDto.getId()) {
                                                    peOptionDto.setChecked(true);
                                                }
                                            });
                                        }
                                        peOptionDtoList.add(peOptionDto);
                                    }
                                }

                                // set criteria
                                PeCriteriaDto peCriteriaDto = new PeCriteriaDto();
                                BeanUtils.copyPropertys(peCriteria, peCriteriaDto);
                                peCriteriaDto.setOptionList(peOptionDtoList);
                                peCriteriaDtoList.add(peCriteriaDto);
                            }
                        }

                        // set section
                        PeSectionDto peSectionDto = new PeSectionDto();
                        BeanUtils.copyPropertys(peSection, peSectionDto);
                        peSectionDto.setCriteriaList(peCriteriaDtoList);
                        peSectionDtoList.add(peSectionDto);
                    }
                }

                // set rubric
                PeRubricDto peRubricDto = new PeRubricDto();
                BeanUtils.copyPropertys(peRubric, peRubricDto);
                peRubricDto.setSectionList(peSectionDtoList);
                rubricDtoList.add(peRubricDto);
            }
        }

        return rubricDtoList;
    }

    public String getShowName(String studentName) {
        // 如果名字里面含有空格，则取到空格后的第一个字符作为 User 的 Name
        try {
            if (studentName.indexOf(" ") > -1) {
                return studentName.substring(0, studentName.indexOf(" ") + 2);
            }
        } catch (Exception e) {
            logger.warn("Format pe name failed");
        }
        return studentName;
    }

    public void auditLogs(Teacher peTeacher, OnlineClass onlineClass, Teacher recruitTeacher, String result,
                    String finishType) {
        // 写日志
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", peTeacher.getId());
        paramsMap.put("teacherName", peTeacher.getRealName());
        paramsMap.put("recruitId", recruitTeacher.getId());
        paramsMap.put("recruitName", recruitTeacher.getRealName());
        paramsMap.put("onlineClassId", onlineClass.getId());
        paramsMap.put("roomId", onlineClass.getClassroom());
        paramsMap.put("result", result);
        paramsMap.put("finishType", finishType);
        String content = FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.PRACTICUM_AUDIT, paramsMap);
        auditDao.saveAudit(ApplicationConstant.AuditCategory.PRACTICUM_AUDIT, "INFO", content, peTeacher.getRealName(),
                        recruitTeacher, IpUtils.getRemoteIP());
    }

    public void updateLastEditor(Teacher peTeacher, Teacher recruitTeacher) {
        User user = userDao.findById(recruitTeacher.getId());
        if (null != user) {
            user.setLastEditorId(peTeacher.getId());
            user.setLastEditDateTime(new Timestamp(System.currentTimeMillis()));
            userDao.update(user);
        }
    }

}
