package com.vipkid.recruitment.common.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.template.TemplateUtils;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeacherContractFileDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.recruitment.event.analysis.EmailTemplateTools;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherPeCommentsDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.dao.TeacherPeResultDao;
import com.vipkid.trpm.dao.TeacherPeRubricDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherPeComments;
import com.vipkid.trpm.entity.TeacherPeRubric;
import com.vipkid.trpm.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 主要用于发送 TR 审核过程中立即发送的邮件
 *
 * Created by zhangzhaojun on 2016/11/29.
*/
@Service
public class AuditEmailService {
    private static Logger logger = LoggerFactory.getLogger(AuditEmailService.class);

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    @Autowired
    private TeacherPeCommentsDao teacherPeCommentsDao;

    @Autowired
    private TeacherContractFileDao teacherContractFileDao;

    @Autowired
    private OnlineClassDao onlineClassDao;

    @Autowired
    private TeacherPeRubricDao teacherPeRubricDao;

    @Autowired
    private TeacherPeResultDao teacherPeResultDao;

    private VelocityEngine velocityEngine;

    private static String BASICINFO_PASS_TITLE = "BasicInfoPassTitle.html";
    private static String BASICINFO_PASS_CONTENT = "BasicInfoPass.html";


    private static String PRACTICUM_PASS_TITLE = "PracticumPassTitle.html";
    private static String PRACTICUM_PASS_CONTENT = "PracticumPass-20170323.html";

    private static String PRACTICUM_PASS_4_OLD_PROCESS_TITLE = "PracticumPass4OldProcessTitle.html";
    private static String PRACTICUM_PASS_CONTENT_4_OLD_PROCESS = "PracticumPass4OldProcess.html";

    private static String PRACTICUM2_START_TITLE = "Practicum2StartTitle.html";
    private static String PRACTICUM2_START_CONTENT = "Practicum2Start-20170323.html";

    private static String PRACTICUM_REAPPLY_TITLE = "PracticumReapplyTitle.html";
    private static String PRACTICUM_REAPPLY_CONTENT = "PracticumReapply.html";

    private static String INTERVIEW_PASS_TITLE = "InterviewPassTitle.html";
    private static String INTERVIEW_PASS_CONTENT ="InterviewPass.html";

    private static String INTERVIEW_REAPPLY_TITLE = "InterviewNoRescheduleTitle.html";
    private static String INTERVIEW_REAPPLY_CONTENT = "InterviewNoReschedule.html";
    private static String INTERVIEW_REAPPLY_QUICK_TITLE = "InterviewNoRescheduleQuickTitle.html";
    private static String INTERVIEW_REAPPLY_QUICK_CONTENT = "InterviewNoRescheduleQuick.html";

    private static String CONTRACTINFO_PASS_TITLE = "ContractInfoPassTitle.html";
    private static String CONTRACTINFO_PASS_CONTENT = "ContractInfoPass.html";

    private static String CONTRACTINFO_REAPPLY_TITLE = "ContractInfoReapplyTitle.html";
    private static String CONTRACTINFO_REAPPLY_CONTENT = "ContractInfoReapply.html";

    private static String MOCKCLASS_RUBRIC_TABLE_FOR_MONTER = "template/MockClassRubricTableForMonter.vm";
    private static String MOCKCLASS_RUBRIC_TABLE_FOR_RESULT = "template/MockClassRubricTableForResult.vm";

    private static String MOCKCLASS_TBD_TO_MONTER_TITLE = "MockClassTBDForMonterTitle.html";
    private static String MOCKCLASS_TBD_TO_MONTER_CONTENT = "MockClassTBDForMonterContent.html";

    public AuditEmailService() {
        Properties properties = new Properties();
        properties.setProperty("input.encoding", UTF_8.displayName());
        properties.setProperty("output.encoding", UTF_8.displayName());
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class",
                        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        velocityEngine = new VelocityEngine();
        velocityEngine.init(properties);
    }

    private String getMockClassRubricTable(String templateName, VelocityContext velocityContext) {
        try {
            Template template = velocityEngine.getTemplate(templateName);
            StringWriter stringWriter = new StringWriter();
            template.merge(velocityContext, stringWriter);

            return stringWriter.toString();
        } catch (Exception e) {
            logger.error("Get mockclass rubric table failed", e);
            return null;
        }
    }

    private void putMockClassRubricTable(Map<String, String> paramsMap, Integer templateId, long applicationId) {
        List<TeacherPeRubric> teacherPeRubrics = teacherPeRubricDao.listTeacherPeRubric(templateId);
        if (CollectionUtils.isNotEmpty(teacherPeRubrics)) {
            Map<Integer, Object> resultMap = Maps.newHashMap();
            for (TeacherPeRubric teacherPeRubric : teacherPeRubrics) {
                resultMap.put(teacherPeRubric.getId(),
                                teacherPeResultDao.getRubricResultTables(teacherPeRubric.getId(), applicationId));
            }

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("resultMap", resultMap);
            String rubricTableForResult = getMockClassRubricTable(MOCKCLASS_RUBRIC_TABLE_FOR_RESULT, velocityContext);
            paramsMap.put("rubricTableForResult", rubricTableForResult);
        }

    }

    private void putMockClassRubricTableForTBD(Map<String, String> paramsMap, Integer templateId, long applicationId) {
        List<TeacherPeRubric> teacherPeRubrics = teacherPeRubricDao.listTeacherPeRubric(templateId);
        if (CollectionUtils.isNotEmpty(teacherPeRubrics)) {
            Map<Integer, Object> resultMap = Maps.newHashMap();
            Map<Integer, Object> countMap = Maps.newHashMap();

            for (TeacherPeRubric teacherPeRubric : teacherPeRubrics) {
                List<Map<String, Object>> results =
                                teacherPeResultDao.getTBDRubricResultTables(teacherPeRubric.getId(), applicationId);

                Map<Integer, Long> sectionMap = results.stream()
                                .collect(Collectors.groupingBy(o -> (Integer) o.get("id"), Collectors.counting()));

                resultMap.put(teacherPeRubric.getId(), results);
                countMap.put(teacherPeRubric.getId(), sectionMap);
            }

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("resultMap", resultMap);
            velocityContext.put("countMap", countMap);
            String rubricTableForResult = getMockClassRubricTable(MOCKCLASS_RUBRIC_TABLE_FOR_MONTER, velocityContext);
            paramsMap.put("rubricTableForResult", rubricTableForResult);
        }
    }

    public Map<String, Object> sendTBDResultToMonter(Teacher peTeacher, String candidate, String mockClass,
                    TeacherApplication teacherApplication, OnlineClass onlineClass) {
        try {
            Map<String, String> paramsMap = Maps.newHashMap();
            paramsMap.put("teacherName", peTeacher.getRealName());
            paramsMap.put("candidateName", candidate);
            paramsMap.put("mockClass", mockClass);

            String scheduleDateTime = DateUtils.formatTo(onlineClass.getScheduledDateTime().toInstant(),
                            peTeacher.getTimezone(), DateUtils.FMT_YMD_HMA_US);
            paramsMap.put("scheduleDateTime", scheduleDateTime);
            paramsMap.put("result", teacherApplication.getResult());

            TeacherPeComments teacherPeComments = teacherPeCommentsDao
                            .getTeacherPeComments(Long.valueOf(teacherApplication.getId()).intValue());
            logger.info("teacherPeComments:{}", JSON.toJSONString(teacherPeComments));

            if (teacherPeComments != null) {
                // mock class
                paramsMap.put("totalScore", String.valueOf(teacherPeComments.getTotalScore()));
                putMockClassRubricTableForTBD(paramsMap, teacherPeComments.getTemplateId(), teacherApplication.getId());

                paramsMap.put("thingsDidWell", HtmlUtils.htmlUnescape(teacherPeComments.getThingsDidWell()));
                paramsMap.put("areasImprovement", HtmlUtils.htmlUnescape(teacherPeComments.getAreasImprovement()));
            }

            logger.info("【EMAIL.sendTBDResultToMonter】toAddMailPool: titleTemplate = {}, contentTemplate = {}",
                            MOCKCLASS_TBD_TO_MONTER_TITLE, MOCKCLASS_TBD_TO_MONTER_CONTENT);
            Map<String, String> emailMap = TemplateUtils.readTemplate(MOCKCLASS_TBD_TO_MONTER_CONTENT, paramsMap,
                            MOCKCLASS_TBD_TO_MONTER_TITLE);
            EmailEngine.addMailPool(peTeacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);

            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendTBDResultToMonter】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail  ");
    }

    public Map<String, Object> sendBasicInfoPass(long teacherId) {
        try {
            Teacher teacher = teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (StringUtils.isNotBlank(teacher.getFirstName())){
                paramsMap.put("teacherName", teacher.getFirstName());
            }else if (StringUtils.isNotBlank(teacher.getRealName())){
                paramsMap.put("teacherName", teacher.getRealName());
            }
            
            logger.info("【EMAIL.sendBasicInfoPass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(), BASICINFO_PASS_TITLE, BASICINFO_PASS_CONTENT);
            Map<String, String> emailMap = TemplateUtils.readTemplate(BASICINFO_PASS_CONTENT, paramsMap, BASICINFO_PASS_TITLE);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendBasicInfoPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(), BASICINFO_PASS_TITLE, BASICINFO_PASS_CONTENT);
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendBasicInfoPass】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail  ");
    }

    public Map<String, Object> sendPracticumPass(long teacherId) {
        try {
            Teacher teacher = teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (StringUtils.isNotBlank(teacher.getFirstName())) {
                paramsMap.put("teacherName", teacher.getFirstName());
            } else if (StringUtils.isNotBlank(teacher.getRealName())) {
                paramsMap.put("teacherName", teacher.getRealName());
            }

            TeacherApplication application =
                            teacherApplicationDao.findCurrentApplication(teacherId).stream().findFirst().get();
            logger.info(" teacherId:{},application Id{}", teacherId, application.getId());

            TeacherPeComments teacherPeComments =
                            teacherPeCommentsDao.getTeacherPeComments(Long.valueOf(application.getId()).intValue());
            logger.info("teacherPeComments:{}", JSON.toJSONString(teacherPeComments));

            if (teacherPeComments != null) {
                // mock class
                paramsMap.put("totalScore", String.valueOf(teacherPeComments.getTotalScore()));
                paramsMap.put("result", application.getResult());
                putMockClassRubricTable(paramsMap, teacherPeComments.getTemplateId(), application.getId());

                paramsMap.put("thingsDidWell", HtmlUtils.htmlUnescape(teacherPeComments.getThingsDidWell()));
                logger.info("thingsDidWell:{}", HtmlUtils.htmlUnescape(teacherPeComments.getThingsDidWell()));
                paramsMap.put("areasImprovement", HtmlUtils.htmlUnescape(teacherPeComments.getAreasImprovement()));
            }

            List<TeacherApplication> list = teacherApplicationDao.findApplicationForStatusResult(teacher.getId(),
                            TeacherApplicationEnum.Status.SIGN_CONTRACT.toString(),
                            TeacherApplicationEnum.Result.PASS.toString());
            if (CollectionUtils.isNotEmpty(list)) {
                logger.info("【EMAIL.sendPracticumPass4OldProcess】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                                teacher.getRealName(), teacher.getEmail(), PRACTICUM_PASS_4_OLD_PROCESS_TITLE,
                                PRACTICUM_PASS_CONTENT_4_OLD_PROCESS);
                Map<String, String> emailMap = TemplateUtils.readTemplate(PRACTICUM_PASS_CONTENT_4_OLD_PROCESS,
                                paramsMap, PRACTICUM_PASS_4_OLD_PROCESS_TITLE);
                EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
                logger.info("【EMAIL.sendPracticumPass4OldProcess】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                                teacher.getRealName(), teacher.getEmail(), PRACTICUM_PASS_4_OLD_PROCESS_TITLE,
                                PRACTICUM_PASS_CONTENT_4_OLD_PROCESS);
            } else {
                // 新版本
                logger.info("【EMAIL.sendPracticumPass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                                teacher.getRealName(), teacher.getEmail(), PRACTICUM_PASS_TITLE,
                                PRACTICUM_PASS_CONTENT);
                Map<String, String> emailMap =
                                TemplateUtils.readTemplate(PRACTICUM_PASS_CONTENT, paramsMap, PRACTICUM_PASS_TITLE);
                EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
                logger.info("【EMAIL.sendPracticumPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                                teacher.getRealName(), teacher.getEmail(), PRACTICUM_PASS_TITLE,
                                PRACTICUM_PASS_CONTENT);
            }

            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPracticumPass】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail  ");
    }

    public Map<String, Object> sendPracticum2Start(long teacherId) {
        try {
            Teacher teacher = teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (StringUtils.isNotBlank(teacher.getFirstName())) {
                paramsMap.put("teacherName", teacher.getFirstName());
            } else if (StringUtils.isNotBlank(teacher.getRealName())) {
                paramsMap.put("teacherName", teacher.getRealName());
            }

            TeacherApplication application =
                            teacherApplicationDao.findCurrentApplication(teacherId).stream().findFirst().get();
            logger.info("【EMAIL.sendPracticum2Start】teacherId:{}, applicationId:{}", teacherId, application.getId());
            TeacherPeComments teacherPeComments =
                            teacherPeCommentsDao.getTeacherPeComments(Long.valueOf(application.getId()).intValue());

            if (TeacherApplicationEnum.Result.PRACTICUM2.toString().equals(application.getResult())
                            && teacherPeComments != null && "SUBMIT".equalsIgnoreCase(teacherPeComments.getStatus())
                            && StringUtils.isNotBlank(teacherPeComments.getThingsDidWell())
                            && StringUtils.isNotBlank(teacherPeComments.getAreasImprovement())
                            && teacherPeComments.getTotalScore() > 0) {
                // mock class
                paramsMap.put("totalScore", String.valueOf(teacherPeComments.getTotalScore()));
                paramsMap.put("result", application.getResult());
                putMockClassRubricTable(paramsMap, teacherPeComments.getTemplateId(), application.getId());

                paramsMap.put("thingsDidWell", HtmlUtils.htmlUnescape(teacherPeComments.getThingsDidWell()));
                paramsMap.put("areasImprovement", HtmlUtils.htmlUnescape(teacherPeComments.getAreasImprovement()));
                paramsMap.put("totalScore", teacherPeComments.getTotalScore() + "");

                logger.info("【EMAIL.sendPracticum2Start】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                                teacher.getRealName(), teacher.getEmail(), PRACTICUM2_START_TITLE,
                                PRACTICUM2_START_CONTENT);

                Map<String, String> emailMap =
                                TemplateUtils.readTemplate(PRACTICUM2_START_CONTENT, paramsMap, PRACTICUM2_START_TITLE);
                EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);

                logger.info("【EMAIL.sendPracticum2Start】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                                teacher.getRealName(), teacher.getEmail(), PRACTICUM2_START_TITLE,
                                PRACTICUM2_START_CONTENT);
            }
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPracticum2Start】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail");
    }

    public Map<String, Object> sendPracticumReapply(long teacherId) {
        try {
            Teacher teacher = teacherDao.findById(teacherId);

            // TODO FinishType
            // if(onlineClass.getFinishType().equals(""))

            Map<String, String> paramsMap = Maps.newHashMap();
            if (StringUtils.isNotBlank(teacher.getFirstName())) {
                paramsMap.put("teacherName", teacher.getFirstName());
            } else if (StringUtils.isNotBlank(teacher.getRealName())) {
                paramsMap.put("teacherName", teacher.getRealName());
            }
            logger.info("【EMAIL.sendPracticumReapply】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                            teacher.getRealName(), teacher.getEmail(), PRACTICUM_REAPPLY_TITLE,
                            PRACTICUM_REAPPLY_CONTENT);
            Map<String, String> emailMap =
                            TemplateUtils.readTemplate(PRACTICUM_REAPPLY_CONTENT, paramsMap, PRACTICUM_REAPPLY_TITLE);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendPracticumReapply】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                            teacher.getRealName(), teacher.getEmail(), PRACTICUM_REAPPLY_TITLE,
                            PRACTICUM_REAPPLY_CONTENT);
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPracticumReapply】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail");
    }

    public Map<String, Object> sendInterviewPass(long teacherId) {
        try {

            List<TeacherApplication> list = teacherApplicationDao.findCurrentApplication(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();
                if (CollectionUtils.isNotEmpty(list)) {
                TeacherApplication teacherApplication = list.get(0);
                Teacher teacher  = teacherDao.findById(teacherApplication.getTeacherId());
                paramsMap =  EmailTemplateTools.interviewPassContentMap(teacherApplication,teacher);
            }
            Teacher teacher  =  teacherDao.findById(teacherId);
            if (StringUtils.isNotBlank(teacher.getFirstName())){
                paramsMap.put("teacherName", teacher.getFirstName());
            }else if (StringUtils.isNotBlank(teacher.getRealName())){
                paramsMap.put("teacherName", teacher.getRealName());
            }
            //推荐人信息
            Map<String, String> refereMap = this.recruitmentService.getReferralCompleteNumber(teacher);
            if(MapUtils.isNotEmpty(refereMap)){
            	paramsMap.putAll(refereMap);
            }

            logger.info("【EMAIL.sendInterviewPass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),INTERVIEW_PASS_TITLE, INTERVIEW_PASS_CONTENT);
            Map<String, String> emailMap = TemplateUtils.readTemplate(INTERVIEW_PASS_CONTENT, paramsMap, INTERVIEW_PASS_TITLE);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendInterviewPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),INTERVIEW_PASS_TITLE, INTERVIEW_PASS_CONTENT);
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendInterviewPass】ERROR: {}", e);
        }

        return ReturnMapUtils.returnFail("email send fail ");
    }

    public Map<String,Object> sendInterviewReapply(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (StringUtils.isNotBlank(teacher.getFirstName())){
                paramsMap.put("teacherName", teacher.getFirstName());
            }else if (StringUtils.isNotBlank(teacher.getRealName())){
                paramsMap.put("teacherName", teacher.getRealName());
            }

            /*如果 classType == 3  发此封邮件模板*/
            List<TeacherApplication> teacherApplicationList =  teacherApplicationDao.findCurrentApplication(teacher.getId());
            if (CollectionUtils.isNotEmpty(teacherApplicationList)) {
                TeacherApplication teacherApplication = teacherApplicationList.get(0);
                if (teacherApplication != null && teacherApplication.getOnlineClassId() != 0) {
                    OnlineClass onlineClass = onlineClassDao.findById(teacherApplication.getOnlineClassId());
                    if (onlineClass != null && onlineClass.getClassType() == OnlineClassEnum.ClassType.QUICK_INTERVIEW.val()) {

                        String comments = teacherApplication.getComments()==null ? "" : teacherApplication.getComments();
                        paramsMap.put("comments", comments);

                        logger.info("【EMAIL.sendQuickInterviewReapply】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                                teacher.getRealName(), teacher.getEmail(), INTERVIEW_REAPPLY_QUICK_TITLE, INTERVIEW_REAPPLY_QUICK_CONTENT);
                        Map<String, String> emailMap = TemplateUtils.readTemplate(INTERVIEW_REAPPLY_QUICK_CONTENT, paramsMap, INTERVIEW_REAPPLY_QUICK_TITLE);
                        EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
                        logger.info("【EMAIL.sendQuickInterviewReapply】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                                teacher.getRealName(),teacher.getEmail(), INTERVIEW_REAPPLY_QUICK_TITLE, INTERVIEW_REAPPLY_QUICK_CONTENT);
                        return ReturnMapUtils.returnSuccess();
                    }
                }
            }

            logger.info("【EMAIL.sendInterviewReapply】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),INTERVIEW_REAPPLY_TITLE, INTERVIEW_REAPPLY_CONTENT);
            Map<String, String> emailMap = TemplateUtils.readTemplate(INTERVIEW_REAPPLY_CONTENT, paramsMap, INTERVIEW_REAPPLY_TITLE);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendInterviewReapply】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),INTERVIEW_REAPPLY_TITLE, INTERVIEW_REAPPLY_CONTENT);
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendInterviewReapply】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail ");
    }

    public Map<String,Object> sendContractInfoPass(Long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (StringUtils.isNotBlank(teacher.getFirstName())){
                paramsMap.put("teacherName", teacher.getFirstName());
            }else if (StringUtils.isNotBlank(teacher.getRealName())){
                paramsMap.put("teacherName", teacher.getRealName());
            }
            
            logger.info("【EMAIL.sendContractInfoPass】toAddMailPool: teacher name = {}, email = {}",
                    teacher.getRealName(),teacher.getEmail(),CONTRACTINFO_PASS_TITLE, CONTRACTINFO_PASS_CONTENT);
            Map<String, String> emailMap = TemplateUtils.readTemplate(CONTRACTINFO_PASS_CONTENT, paramsMap, CONTRACTINFO_PASS_TITLE);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendContractInfoPass】addedMailPool: teacher name = {}, email = {}", teacher.getRealName(),teacher.getEmail());
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendContractInfoReapply】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail ");
    }

    public Map<String,Object> sendContractInfoReapply(Long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();
          StringBuilder stringBuilder = new StringBuilder();
            List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
            if(CollectionUtils.isNotEmpty(listEntity)){
                TeacherApplication teacherApplication = listEntity.get(0);
                JSONArray array =  JSON.parseArray(teacherApplication.getFailedReason());
               List<TeacherContractFile> teacherContractFiles = teacherContractFileDao.findByTeacherIdAndTeacherApplicationId(teacherId,0);
                teacherContractFiles.forEach(obj -> {
                   if(StringUtils.equals(obj.getResult(), TeacherApplicationEnum.Result.FAIL.toString())) {
                      if(StringUtils.isNotBlank(fileType(obj.getFileType()))){
                          stringBuilder.append(fileType(obj.getFileType()));
                          stringBuilder.append(",   ");
                      }
                   }
                });
                if(StringUtils.isNotBlank(teacherApplication.getFailedReason())){
                 for (int i=0;i<array.size();i++) {
                        JSONObject ob = (JSONObject) array.get(i);
                        if (StringUtils.equals(ob.getString("result"), TeacherApplicationEnum.Result.FAIL.toString())) {
                            if(StringUtils.isNotBlank(changeName(ob.getString("name")))) {
                                if (i == array.size() - 1) {
                                    stringBuilder.append(changeName(ob.getString("name")));
                                } else {
                                    stringBuilder.append(changeName(ob.getString("name")));
                                    stringBuilder.append(",   ");
                                }
                            }
                        }
                    }
                }
            }

            if (StringUtils.isNotBlank(teacher.getFirstName())){
                paramsMap.put("teacherName", teacher.getFirstName());
            }else if (StringUtils.isNotBlank(teacher.getRealName())){
                paramsMap.put("teacherName", teacher.getRealName());
            }
            
            paramsMap.put("failFiles", stringBuilder.toString());
            logger.info("【EMAIL.sendContractInfoReapply】toAddMailPool: teacher name = {}, email = {}", teacher.getRealName(),teacher.getEmail(),"ContractInfoReapply.html","ContractInfoReapply.html");
            Map<String, String> emailMap = TemplateUtils.readTemplate(CONTRACTINFO_REAPPLY_CONTENT, paramsMap, CONTRACTINFO_REAPPLY_TITLE);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendContractInfoReapply】addedMailPool: teacher name = {}, email = {}", teacher.getRealName(),teacher.getEmail());
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendContractInfoReapply】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail ");
    }

    private String fileType(int TypeNum){
        if (TypeNum == TeacherApplicationEnum.ContractFileType.OTHER_DEGREES.val()) {
           return "Other degrees";
        }
        if (TypeNum == TeacherApplicationEnum.ContractFileType.CERTIFICATIONFILES.val()) {
            return "Teaching certificates";
        }
        if (TypeNum == TeacherApplicationEnum.ContractFileType.IDENTIFICATION.val()) {
            return "Identity card";
        }
        if (TypeNum == TeacherApplicationEnum.ContractFileType.PASSPORT.val()) {
            return "Passport picture page";

        }
        if (TypeNum == TeacherApplicationEnum.ContractFileType.DRIVER.val()) {
            return "Driver's license";
        }
        if (TypeNum == TeacherApplicationEnum.ContractFileType.DIPLOMA.val()) {
            return "Diploma";
        }
        if (TypeNum == TeacherApplicationEnum.ContractFileType.CONTRACT.val()) {
            return "Contract";
        }
        if (TypeNum == TeacherApplicationEnum.ContractFileType.CONTRACT_W9.val()) {
            return "W-9 fillable form";
        }
        return null;
    }

    private String  changeName(String name){
        if(StringUtils.isNotBlank(name)) {
            if (StringUtils.equals(name, "avatar")) {
                 return "Professional Profile Picture";
            }
            if (StringUtils.equals(name, "lifePictures")) {
                return "Casual pictures of yourself";
            }
            if (StringUtils.equals(name, "shortVideo")) {
                return "15 Second video greeting";
            }
            if (StringUtils.equals(name, "selfIntroduction")) {
                return "Self introduction";
            }
        }
        return null;
    }

}