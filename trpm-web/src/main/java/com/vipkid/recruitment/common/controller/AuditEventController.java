package com.vipkid.recruitment.common.controller;

import com.vipkid.recruitment.common.service.AuditEmailService;
import com.vipkid.recruitment.event.AuditEvent;
import com.vipkid.recruitment.event.AuditEventHandler;
import com.vipkid.recruitment.utils.MapReturnUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.Authentication;
import com.vipkid.rest.interceptor.annotation.RemoteInterface;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by zhangzhaojun on 2016/11/29.
 */
@RemoteInterface(portal ={Authentication.Portal.MANAGEMENT})
@RestController
@RequestMapping("/recruitment/auditEvent")
public class AuditEventController extends RestfulController {
    private static Logger logger = LoggerFactory.getLogger(AuditEventController.class);

    @Autowired
    private AuditEmailService auditEmailService;

    @Autowired
    private AuditEventHandler auditEventHandler;

    /**
     * 从管理端发送邮件
     * @param pramMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/sendEvent")
    public Map<String,Object> process(@RequestBody Map<String,Object> pramMap,HttpServletRequest request, HttpServletResponse response) {

        Long teacherId = (Long) pramMap.get("teacherId");
        String status = (String) pramMap.get("lifeCycle");
        String result = (String) pramMap.get("result");

        if(teacherId == null || StringUtils.isBlank(status) || StringUtils.isBlank(result)) {
            return MapReturnUtils.returnFail("Parameter invalid!", this);
        }

        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setSourceId(teacherId);
        auditEvent.setStatus(status);
        auditEvent.setAuditResult(result);
        auditEvent.setDateTime(System.currentTimeMillis());

        Map<String, Object> ret = auditEventHandler.onAuditEvent(auditEvent);

        return ret;
    }

}
