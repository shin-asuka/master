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
    public Map<String,Object> sendCancelPrac1(long teacherId,String email){
        try{
        Teacher teacher  =  teacherDao.findById(teacherId);
        Map<String, String> paramsMap = Maps.newHashMap();

        if (teacher.getRealName() != null)
            paramsMap.put("teacherName", teacher.getRealName());
        logger.info("【EMAIL.sendEmail4TrainingPass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
        Map<String, String> emailMap = new TempleteUtils().readTemplete("", paramsMap, "");
        new EmailEngine().addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
        logger.info("【EMAIL.sendEmail4TrainingPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
         return ResponseUtils.responseSuccess();
    } catch (Exception e) {
        logger.error("【EMAIL.sendEmail4TrainingPass】ERROR: {}", e);
    }
        return ResponseUtils.responseFail("eamil send fail",this);
    }
}