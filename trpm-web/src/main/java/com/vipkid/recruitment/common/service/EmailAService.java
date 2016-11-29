package com.vipkid.recruitment.common.service;

import com.google.api.client.util.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhangzhaojun on 2016/11/29.
*/
@Service
public class EmailAService{
    private static Logger logger = LoggerFactory.getLogger(EmailAService.class);

    @Autowired
    private TeacherDao teacherDao;
    public Map<String,Object> sendCancelPrac1(long teacherId){
        try{
        Teacher teacher  =  teacherDao.findById(teacherId);
        Map<String, String> paramsMap = Maps.newHashMap();

        if (teacher.getRealName() != null)
            paramsMap.put("teacherName", teacher.getRealName());
        logger.info("【EMAIL.sendCancelPrac1】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            Map<String, String> emailMap = TempleteUtils.readTemplete("InterviewBook.html", paramsMap, "InterviewBookTitle.html");
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
        logger.info("【EMAIL.sendCancelPrac1】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
         return ResponseUtils.responseSuccess();
    } catch (Exception e) {
        logger.error("【EMAIL.sendCancelPrac1】ERROR: {}", e);
    }
        return ResponseUtils.responseFail("eamil send fail",this);
    }

    public Map<String,Object> sendReschedulePrac1(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
            logger.info("【EMAIL.sendReschedulePrac1】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            Map<String, String> emailMap = TempleteUtils.readTemplete("InterviewBook.html", paramsMap, "InterviewBookTitle.html");
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendReschedulePrac1】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            return ResponseUtils.responseSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendReschedulePrac1】ERROR: {}", e);
        }
        return ResponseUtils.responseFail("eamil send fail",this);
    }


    public Map<String,Object> sendPrac1Pass(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
            logger.info("【EMAIL.sendPrac1Pass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            Map<String, String> emailMap = TempleteUtils.readTemplete("InterviewBook.html", paramsMap, "InterviewBookTitle.html");
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendPrac1Pass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            return ResponseUtils.responseSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPrac1Pass】ERROR: {}", e);
        }
        return ResponseUtils.responseFail("eamil send fail",this);
    }


    public Map<String,Object> sendPrac2Start(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
            logger.info("【EMAIL.sendPrac2Start】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            Map<String, String> emailMap = TempleteUtils.readTemplete("InterviewBook.html", paramsMap, "InterviewBookTitle.html");
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendPrac2Start】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            return ResponseUtils.responseSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPrac2Start】ERROR: {}", e);
        }
        return ResponseUtils.responseFail("eamil send fail",this);
    }


    public Map<String,Object> sendCancelPrac2(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
            logger.info("【EMAIL.sendCancelPrac2】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            Map<String, String> emailMap = TempleteUtils.readTemplete("InterviewBook.html", paramsMap, "InterviewBookTitle.html");
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendCancelPrac2】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            return ResponseUtils.responseSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendCancelPrac2】ERROR: {}", e);
        }
        return ResponseUtils.responseFail("eamil send fail",this);
    }


    public Map<String,Object> sendReschedulePrac2(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
            logger.info("【EMAIL.sendReschedulePrac2】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            Map<String, String> emailMap = TempleteUtils.readTemplete("InterviewBook.html", paramsMap, "InterviewBookTitle.html");
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendReschedulePrac2】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            return ResponseUtils.responseSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendReschedulePrac2】ERROR: {}", e);
        }
        return ResponseUtils.responseFail("eamil send fail",this);
    }


    public Map<String,Object> sendPrac2Pass(long teacherId){
        try{
            Teacher teacher  =  teacherDao.findById(teacherId);
            Map<String, String> paramsMap = Maps.newHashMap();

            if (teacher.getRealName() != null)
                paramsMap.put("teacherName", teacher.getRealName());
            logger.info("【EMAIL.sendPrac2Pass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            Map<String, String> emailMap = TempleteUtils.readTemplete("InterviewBook.html", paramsMap, "InterviewBookTitle.html");
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendPrac2Pass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
            return ResponseUtils.responseSuccess();
        } catch (Exception e) {
            logger.error("【EMAIL.sendPrac2Pass】ERROR: {}", e);
        }
        return ResponseUtils.responseFail("eamil send fail",this);
    }
}