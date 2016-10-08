package com.vipkid.rest.web;

import static com.vipkid.trpm.constant.ApplicationConstant.NEW_TEACHER_NAME;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.proxy.RedisProxy;

@RestController
@RequestMapping("/user")
public class SendEmailController {

    private static Logger logger = LoggerFactory.getLogger(SendEmailController.class);

    private static final String LOGIN_ACTIVATION_EMAIL_KEY = "TRPM_REST_ACTIVATION_EMAIL_KEY:%s";

    private static final int EXPIRED_SECONDS = 30;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private RedisProxy redisProxy;

    @RequestMapping(value = "/applyActivationEmail", method = RequestMethod.POST,
            produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> applyActivationEmail(HttpServletRequest request,
            HttpServletResponse response, @RequestParam(required = true) String email) {
        Map<String, Object> resultMap = Maps.newHashMap();
        if (StringUtils.isBlank(email)) {
            resultMap.put("status", RestfulConfig.HttpStatus.STATUS_403);
            return resultMap;
        }

        String key = String.format(LOGIN_ACTIVATION_EMAIL_KEY, email);
        if (null != redisProxy.get(key)) {
            logger.info("The activation email [{}] time is not expire", email);
            resultMap.put("status", RestfulConfig.HttpStatus.STATUS_403);
            resultMap.put("expire", redisProxy.ttl(key));
            return resultMap;
        }

        Teacher teacher = teacherDao.findByEmail(email);
        if (0 == teacher.getId()) {
            resultMap.put("status", RestfulConfig.HttpStatus.STATUS_403);
            return resultMap;
        } else {
            Map<String, String> paramsMap = Maps.newHashMap();
            paramsMap.put("teacherName", NEW_TEACHER_NAME);
            paramsMap.put("link", PropertyConfigurer.stringValue("teacher.www")
                    + "activation.shtml?uuid=" + teacher.getRecruitmentId());
            TempleteUtils templete = new TempleteUtils();
            Map<String, String> sendMap = templete.readTemplete("VIPKIDAccountActivationLink.html",
                    paramsMap, "VIPKIDAccountActivationLink-Title.html");
            new EmailEngine().addMailPool(email, sendMap, EmailFormEnum.TEACHVIP);

            redisProxy.set(key, "true", EXPIRED_SECONDS);
            logger.info("Apply again activation email [{}] ok", email);

            resultMap.put("status", RestfulConfig.HttpStatus.STATUS_200);
            return resultMap;
        }
    }

}
