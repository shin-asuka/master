package com.vipkid.recruitment.common.controller;
import com.vipkid.recruitment.common.service.EmailAService;
import com.vipkid.rest.RestfulController;
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

        int id = (Integer) pramMap.get("teacherId");
        String status = (String) pramMap.get("lifeCycle");
        String result = (String) pramMap.get("result");
        Map<String, Object> ret = new HashMap<>();
        switch (status) {
            case "PRACTICUM":

                if (result != null) {

                    switch (result) {
                        case "PASS":
                            logger.info("向用户{}sendPrac1Pass的邮件", id);
                            ret = emailAService.sendPracPass(id);
                            break;
                        case "REAPPLY":
                            ret = emailAService.sendPracReapply(id);
                            break;
                        case "PRACTICUM2":
                            ret = emailAService.sendPrac2Start(id);
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
        return ret;
    }

}
