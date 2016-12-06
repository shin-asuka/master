package com.vipkid.recruitment.common.service;

import com.google.api.client.util.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.templete.TempleteUtils;
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

    private static String PracticumPassTitle="";
    private static String PracticumPassContent="";

    private static String Practicum2StartTitle="";
    private static String Practicum2StartContent="";

    private static String PracticumReapplyTitle="";
    private static String PracticumReapplyContent="";

    private static String InterviewPassTitle="";
    private static String InterviewPassContent="";

    private static String InterviewReapplyTitle="";
    private static String InterviewReapplyContent="";

    private static String ContractInfoPassTitle="";
    private static String ContractInfoPassContent="";

    private static String ContractInfoReapplyTitle="";
    private static String ContractInfoReapplyContent="";



    public Map<String,Object> sendPracticumPass(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
            logger.info("【EMAIL.sendPracticumPass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),PracticumPassTitle,PracticumPassContent);
            Map<String, String> emailMap = TempleteUtils.readTemplete(PracticumPassContent, paramsMap, PracticumPassTitle);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendPracticumPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),PracticumPassTitle,PracticumPassContent);
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPracticumPass】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail  ",this);
    }

    public Map<String,Object> sendPracticum2Start(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null) {
                paramsMap.put("teacherName", teacher.getRealName());
            }
            logger.info("【EMAIL.sendPracticum2Start】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),Practicum2StartTitle,Practicum2StartContent);

            Map<String, String> emailMap = TempleteUtils.readTemplete(Practicum2StartContent, paramsMap, Practicum2StartTitle);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);

            logger.info("【EMAIL.sendPracticum2Start】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),Practicum2StartTitle,Practicum2StartContent);
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPracticum2Start】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail",this);
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
                    teacher.getRealName(),teacher.getEmail(),PracticumReapplyTitle,PracticumReapplyContent);
            Map<String, String> emailMap = TempleteUtils.readTemplete(PracticumReapplyContent, paramsMap, PracticumReapplyTitle);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendPracticumReapply】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),PracticumReapplyTitle,PracticumReapplyContent);
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPracticumReapply】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail",this);
    }


    public Map<String,Object> sendInterviewPass(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
            logger.info("【EMAIL.sendInterviewPass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),InterviewPassTitle,InterviewPassContent);
            Map<String, String> emailMap = TempleteUtils.readTemplete(InterviewPassContent, paramsMap,InterviewPassTitle);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendInterviewPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),InterviewPassTitle,InterviewPassContent);
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendInterviewPass】ERROR: {}", e);
        }

        return ReturnMapUtils.returnFail("email send fail ",this);
    }

    public Map<String,Object> sendInterviewReapply(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
            logger.info("【EMAIL.sendInterviewReapply】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),InterviewReapplyTitle,InterviewReapplyContent);
            Map<String, String> emailMap = TempleteUtils.readTemplete(InterviewReapplyContent, paramsMap, InterviewReapplyTitle);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendInterviewReapply】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                    teacher.getRealName(),teacher.getEmail(),InterviewReapplyTitle,InterviewReapplyContent);
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendInterviewReapply】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail ", this);
    }

    public Map<String,Object> sendContractInfoPass(Long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
            logger.info("【EMAIL.sendContractInfoPass】toAddMailPool: teacher name = {}, email = {}", teacher.getRealName(),teacher.getEmail(),ContractInfoPassTitle,ContractInfoPassContent);
            Map<String, String> emailMap = TempleteUtils.readTemplete(ContractInfoPassTitle,paramsMap, ContractInfoPassContent);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendContractInfoPass】addedMailPool: teacher name = {}, email = {}", teacher.getRealName(),teacher.getEmail());
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendContractInfoReapply】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail ", this);
    }

    public Map<String,Object> sendContractInfoReapply(Long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
            logger.info("【EMAIL.sendContractInfoReapply】toAddMailPool: teacher name = {}, email = {}", teacher.getRealName(),teacher.getEmail(),"ContractInfoReapply.html","ContractInfoReapply.html");
            Map<String, String> emailMap = TempleteUtils.readTemplete(ContractInfoReapplyTitle, paramsMap, ContractInfoReapplyContent);
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendContractInfoReapply】addedMailPool: teacher name = {}, email = {}", teacher.getRealName(),teacher.getEmail());
            return ReturnMapUtils.returnSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendContractInfoReapply】ERROR: {}", e);
        }
        return ReturnMapUtils.returnFail("email send fail ", this);
    }

}