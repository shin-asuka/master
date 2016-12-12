package com.vipkid.recruitment.common.controller;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.recruitment.event.AuditEvent;
import com.vipkid.recruitment.event.AuditEventHandler;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.Authentication;
import com.vipkid.rest.interceptor.annotation.RemoteInterface;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;


/**
 * Created by zhangzhaojun on 2016/11/29.
 */
@RemoteInterface(portal ={Authentication.Portal.MANAGEMENT})
@RestController
@RequestMapping("/recruitment/auditEvent")
public class AuditEventController extends RestfulController {
    
    private static Logger logger = LoggerFactory.getLogger(AuditEventController.class);


    @Autowired
    private AuditEventHandler auditEventHandler;

    /**
     * 从管理端发送邮件
     * @param pramMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/sendEvent",method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> process(@RequestBody AuditEvent auditEvent,HttpServletRequest request, HttpServletResponse response) {

        logger.info("接收到管理端邮件调用参数:"+JsonTools.getJson(auditEvent));

        List<Result> list = ValidateUtils.checkBean(auditEvent,false);
        if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
            return ReturnMapUtils.returnFail("参数不合法:"+list.get(0).getName() + "," + list.get(0).getMessages());
        }
        
        auditEvent.setDateTime(System.currentTimeMillis());

        Map<String, Object> ret = auditEventHandler.onAuditEvent(auditEvent);

        return ret;
    }

}
