package com.vipkid.recruitment.common.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.template.TemplateUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeacherContractFileDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.recruitment.event.analysis.EmailTemplateTools;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherPeCommentsDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherPeComments;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import java.util.List;
import java.util.Map;

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
    private TeacherApplicationDao teacherApplicationDao;


    @Autowired
    private TeacherPeCommentsDao teacherPeCommentsDao;

    @Autowired
    private TeacherContractFileDao teacherContractFileDao;

    private static String BASICINFO_PASS_TITLE = "BasicInfoPassTitle.html";
    private static String BASICINFO_PASS_CONTENT = "BasicInfoPass.html";


    private static String PRACTICUM_PASS_TITLE = "PracticumPassTitle.html";
    private static String PRACTICUM_PASS_CONTENT = "PracticumPass.html";

    private static String PRACTICUM_PASS_4_OLD_PROCESS_TITLE = "PracticumPass4OldProcessTitle.html";
    private static String PRACTICUM_PASS_CONTENT_4_OLD_PROCESS = "PracticumPass4OldProcess.html";

    private static String PRACTICUM2_START_TITLE = "Practicum2StartTitle.html";
    private static String PRACTICUM2_START_CONTENT = "Practicum2Start.html";

    private static String PRACTICUM_REAPPLY_TITLE = "PracticumReapplyTitle.html";
    private static String PRACTICUM_REAPPLY_CONTENT = "PracticumReapply.html";

    private static String INTERVIEW_PASS_TITLE = "InterviewPassTitle.html";
    private static String INTERVIEW_PASS_CONTENT ="InterviewPass.html";

    private static String INTERVIEW_REAPPLY_TITLE = "InterviewNoRescheduleTitle.html";
    private static String INTERVIEW_REAPPLY_CONTENT = "InterviewNoReschedule.html";

    private static String CONTRACTINFO_PASS_TITLE = "ContractInfoPassTitle.html";
    private static String CONTRACTINFO_PASS_CONTENT = "ContractInfoPass.html";

    private static String CONTRACTINFO_REAPPLY_TITLE = "ContractInfoReapplyTitle.html";
    private static String CONTRACTINFO_REAPPLY_CONTENT = "ContractInfoReapply.html";

    public Map<String,Object> sendBasicInfoPass(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
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





    public Map<String,Object> sendPracticumPass(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (StringUtils.isNotBlank(teacher.getFirstName())){
                paramsMap.put("teacherName", teacher.getFirstName());
            }else if (StringUtils.isNotBlank(teacher.getRealName())){
                paramsMap.put("teacherName", teacher.getRealName());
            }
            
            TeacherApplication  application =  teacherApplicationDao.findCurrentApplication(teacherId).stream().findFirst().get();
            logger.info(" teacherId:{},application Id{}",teacherId,application.getId());
            TeacherPeComments teacherPeComments =  teacherPeCommentsDao.getTeacherPeComments(Long.valueOf(application.getId()).intValue());

            logger.info("teacherPeComments:{}",JSON.toJSONString(teacherPeComments));
            if(teacherPeComments!=null) {
                paramsMap.put("thingsDidWell", HtmlUtils.htmlUnescape(teacherPeComments.getThingsDidWell()));
                logger.info("thingsDidWell:{}",HtmlUtils.htmlUnescape(teacherPeComments.getThingsDidWell()));
                paramsMap.put("areasImprovement", HtmlUtils.htmlUnescape(teacherPeComments.getAreasImprovement()));
            }
            List<TeacherApplication> list = teacherApplicationDao.findApplictionForStatusResult(teacher.getId(), TeacherApplicationEnum.Status.SIGN_CONTRACT.toString(), TeacherApplicationEnum.Result.PASS.toString());
            if(CollectionUtils.isNotEmpty(list)){
                logger.info("【EMAIL.sendPracticumPass4OldProcess】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                        teacher.getRealName(),teacher.getEmail(), PRACTICUM_PASS_4_OLD_PROCESS_TITLE, PRACTICUM_PASS_CONTENT_4_OLD_PROCESS);
                Map<String, String> emailMap = TemplateUtils.readTemplate(PRACTICUM_PASS_CONTENT_4_OLD_PROCESS, paramsMap, PRACTICUM_PASS_4_OLD_PROCESS_TITLE);
                EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
                logger.info("【EMAIL.sendPracticumPass4OldProcess】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                        teacher.getRealName(),teacher.getEmail(), PRACTICUM_PASS_4_OLD_PROCESS_TITLE, PRACTICUM_PASS_CONTENT_4_OLD_PROCESS);
            } else {
                logger.info("【EMAIL.sendPracticumPass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                        teacher.getRealName(),teacher.getEmail(), PRACTICUM_PASS_TITLE, PRACTICUM_PASS_CONTENT);
                Map<String, String> emailMap = TemplateUtils.readTemplate(PRACTICUM_PASS_CONTENT, paramsMap, PRACTICUM_PASS_TITLE);
                EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
                logger.info("【EMAIL.sendPracticumPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                        teacher.getRealName(),teacher.getEmail(), PRACTICUM_PASS_TITLE, PRACTICUM_PASS_CONTENT);
            }

            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPracticumPass】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail  ");
    }

    public Map<String,Object> sendPracticum2Start(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (StringUtils.isNotBlank(teacher.getFirstName())){
                paramsMap.put("teacherName", teacher.getFirstName());
            }else if (StringUtils.isNotBlank(teacher.getRealName())){
                paramsMap.put("teacherName", teacher.getRealName());
            }

            TeacherApplication application = teacherApplicationDao.findCurrentApplication(teacherId).stream().findFirst().get();
            logger.info("【EMAIL.sendPracticum2Start】teacherId:{}, applicationId:{}", teacherId, application.getId());
            TeacherPeComments teacherPeComments =  teacherPeCommentsDao.getTeacherPeComments(Long.valueOf(application.getId()).intValue());

            if(TeacherApplicationEnum.Result.PRACTICUM2.toString().equals(application.getResult()) && teacherPeComments!=null && "SUBMIT".equalsIgnoreCase(teacherPeComments.getStatus())
                    && StringUtils.isNotBlank(teacherPeComments.getThingsDidWell()) && StringUtils.isNotBlank(teacherPeComments.getAreasImprovement()) && teacherPeComments.getTotalScore()>0
            ) {
                paramsMap.put("thingsDidWell", HtmlUtils.htmlUnescape(teacherPeComments.getThingsDidWell()));
                paramsMap.put("areasImprovement", HtmlUtils.htmlUnescape(teacherPeComments.getAreasImprovement()));
                paramsMap.put("totalScore", teacherPeComments.getTotalScore() + "");


                logger.info("【EMAIL.sendPracticum2Start】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                        teacher.getRealName(),teacher.getEmail(),PRACTICUM2_START_TITLE,PRACTICUM2_START_CONTENT);

                Map<String, String> emailMap = TemplateUtils.readTemplate(PRACTICUM2_START_CONTENT, paramsMap, PRACTICUM2_START_TITLE);
                EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);

                logger.info("【EMAIL.sendPracticum2Start】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                        teacher.getRealName(),teacher.getEmail(),PRACTICUM2_START_TITLE, PRACTICUM2_START_CONTENT);
            }
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPracticum2Start】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail");
    }


    public Map<String,Object> sendPracticumReapply(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);

            //TODO   FinishType
            // if(onlineClass.getFinishType().equals(""))

            Map<String, String> paramsMap = Maps.newHashMap();
            if (StringUtils.isNotBlank(teacher.getFirstName())){
                paramsMap.put("teacherName", teacher.getFirstName());
            }else if (StringUtils.isNotBlank(teacher.getRealName())){
                paramsMap.put("teacherName", teacher.getRealName());
            }            
            logger.info("【EMAIL.sendPracticumReapply】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),PRACTICUM_REAPPLY_TITLE, PRACTICUM_REAPPLY_CONTENT);
            Map<String, String> emailMap = TemplateUtils.readTemplate(PRACTICUM_REAPPLY_CONTENT, paramsMap, PRACTICUM_REAPPLY_TITLE);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendPracticumReapply】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),PRACTICUM_REAPPLY_TITLE, PRACTICUM_REAPPLY_CONTENT);
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPracticumReapply】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail");
    }


    public Map<String,Object> sendInterviewPass(long teacherId){
        try{

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