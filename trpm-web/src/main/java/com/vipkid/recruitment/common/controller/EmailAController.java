package com.vipkid.recruitment.common.controller;
import com.vipkid.recruitment.common.service.EmailAService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.Authentication;
import com.vipkid.rest.interceptor.annotation.RemoteInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;




/**
 * Created by zhangzhaojun on 2016/11/29.
 */
@RemoteInterface(portal ={Authentication.Portal.MANAGEMENT})
@RestController
@RequestMapping("/recruitment/auditEven")
public class EmailAController extends RestfulController {
    private static Logger logger = LoggerFactory.getLogger(EmailAController.class);

    @Autowired
    private EmailAService emailAService;

    /**
     * 从管理端发送邮件
     * @param pramMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/mail")
    public Map<String,Object> sendmail(@RequestBody Map<String,Object> pramMap,HttpServletRequest request, HttpServletResponse response) {

        int teacherId = (Integer) pramMap.get("teacherId");
        String status = (String) pramMap.get("lifeCycle");
        String result = (String) pramMap.get("result");
        Map<String, Object> ret = new HashMap<>();
        switch (status) {
            //TODO   caoxinzhou  Interview
            case "INTERVIEW":
                if (result != null) {

                    switch (result) {
                        case "PASS":
                            logger.info("向用户{}sendInterviewPass的邮件", teacherId);
                            ret = emailAService.sendInterViewPass(teacherId);
                            break;
                        case "REAPPLY":
                            logger.info("向用户{}sendInterviewReapply的邮件", teacherId);
                            ret = emailAService.sendInterViewReapply(teacherId);
                            break;
                    }
                }
            case "PRACTICUM":

                if (result != null) {

                    switch (result) {
                        case "PASS":
                            logger.info("向用户{}sendPrac1Pass的邮件", teacherId);
                            ret = emailAService.sendPracPass(teacherId);
                            break;
                        case "REAPPLY":
                            logger.info("向用户{}sendPracReapply", teacherId);
                            ret = emailAService.sendPracReapply(teacherId);
                            break;
                        case "PRACTICUM2":
                            logger.info("向用户{}sendPrac2Start", teacherId);
                            ret = emailAService.sendPrac2Start(teacherId);
                            break;
                        case "TBD":
                            logger.info("向用户{}sendPrac2Start", teacherId);
                            ret = emailAService.sendPracTbd(teacherId);
                            break;
                        default:
                            break;
                    }
                }
                //TODO   caoxinzhou  ContractInfo
                break;
            default:
                break;
        }
        return ret;
    }

}
