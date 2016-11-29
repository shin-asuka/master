package com.vipkid.recruitment.common.controller;
import com.google.api.client.util.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.handle.EmailServer;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.recruitment.common.service.EmailAService;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.trpm.entity.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by zhangzhaojun on 2016/11/29.
 */
@RestController
@RequestMapping("/recruitment/auditEven")
public class EmailAController extends RestfulController {
    private static Logger logger = LoggerFactory.getLogger(EmailAController.class);

    @Autowired
    private EmailAService emailAService;

    @RequestMapping(value = "/sendCancelPrac1")
    public Map<String,Object> sendCancelPrac1(@RequestBody Map<String,Object> pramMap,HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(String.valueOf(pramMap.get("teacherId")));
        String  email = String.valueOf(pramMap.get("eamil"));
        try{
            logger.info("向用户{}取消Prac1的邮件{}",id,email);
            Map<String,Object> result = emailAService.sendCancelPrac1(id,email);
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }
}
