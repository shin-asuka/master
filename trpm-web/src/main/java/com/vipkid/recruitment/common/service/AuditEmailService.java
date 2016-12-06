package com.vipkid.recruitment.common.service;

import com.google.api.client.util.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.template.TemplateUtils;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private OnlineClassDao onlineClassDao;

    private static String PRACTICUM_PASS_TITLE = "";
    private static String PRACTICUM_PASS_CONTENT = "";

    private static String PRACTICUM2_START_TITLE = "";
    private static String PRACTICUM2_START_CONTENT = "";

    private static String PRACTICUM_REAPPLY_TITLE = "";
    private static String PRACTICUM_REAPPLY_CONTENT = "";

    private static String INTERVIEW_PASS_TITLE = "";
    private static String INTERVIEW_PASS_CONTENT = "";

    private static String INTERVIEW_REAPPLY_TITLE = "";
    private static String INTERVIEW_REAPPLY_CONTENT = "";

    private static String CONTRACTINFO_PASS_TITLE = "";
    private static String CONTRACTINFO_PASS_CONTENT = "";

    private static String CONTRACTINFO_REAPPLY_TITLE = "";
    private static String CONTRACTINFO_REAPPLY_CONTENT = "";

    public Map<String,Object> sendPracticumPass(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
            logger.info("【EMAIL.sendPracticumPass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(), PRACTICUM_PASS_TITLE, PRACTICUM_PASS_CONTENT);
            Map<String, String> emailMap = TemplateUtils.readTemplate(PRACTICUM_PASS_CONTENT, paramsMap, PRACTICUM_PASS_TITLE);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendPracticumPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(), PRACTICUM_PASS_TITLE, PRACTICUM_PASS_CONTENT);
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

            if (teacher.getRealName() != null) {
                paramsMap.put("teacherName", teacher.getRealName());
            }
            logger.info("【EMAIL.sendPracticum2Start】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),PRACTICUM2_START_TITLE,PRACTICUM2_START_CONTENT);

            Map<String, String> emailMap = TemplateUtils.readTemplate(PRACTICUM2_START_CONTENT, paramsMap, PRACTICUM2_START_TITLE);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);

            logger.info("【EMAIL.sendPracticum2Start】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),PRACTICUM2_START_TITLE, PRACTICUM2_START_CONTENT);
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPracticum2Start】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail");
    }


    public Map<String,Object> sendPracticumReapply(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
            OnlineClass onlineClass = new OnlineClass();
            if(CollectionUtils.isNotEmpty(listEntity)){
                TeacherApplication teacherApplication = listEntity.get(0);
               onlineClass =  onlineClassDao.findById(teacherApplication.getOnlineClassId());
            }
            //TODO   FinishType
           // if(onlineClass.getFinishType().equals(""))


            Map<String, String> paramsMap = Maps.newHashMap();
            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
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
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
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

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
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

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
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

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
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

}