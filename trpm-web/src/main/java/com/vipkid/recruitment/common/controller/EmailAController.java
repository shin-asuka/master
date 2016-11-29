package com.vipkid.recruitment.common.controller;
import com.vipkid.recruitment.common.service.EmailAService;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.rest.RestfulController;
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

    /**
     * 从管理端取消Prac1
     * @param pramMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/sendCancelPrac1")
    public Map<String,Object> sendCancelPrac1(@RequestBody Map<String,Object> pramMap,HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(String.valueOf(pramMap.get("teacherId")));
        try{
            logger.info("向用户{}取消Prac1的邮件",id);
            Map<String,Object> result = emailAService.sendCancelPrac1(id);
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }

    /**
     *从管理端RESCHEDULE-Prac1
     * @param pramMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/sendReschedulePrac1")
    public Map<String,Object> sendReschedulePrac1(@RequestBody Map<String,Object> pramMap,HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(String.valueOf(pramMap.get("teacherId")));
        try{
            logger.info("向用户{}sendReschedulePrac1",id);
            Map<String,Object> result = emailAService.sendReschedulePrac1(id);
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }


    /**
     * Prac1从管理端和教师端选择PASS
     * @param pramMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/sendPrac1Pass")
    public Map<String,Object> sendPrac1Pass(@RequestBody Map<String,Object> pramMap,HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(String.valueOf(pramMap.get("teacherId")));
        try{
            logger.info("向用户{}sendPrac1Pass的邮件",id);
            Map<String,Object> result = emailAService.sendPrac1Pass(id);
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }

    /**
     * 在管理端或者教师端选择PRACTICUM2
     * @param pramMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/sendPrac2Start")
    public Map<String,Object> sendPrac2Start(@RequestBody Map<String,Object> pramMap,HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(String.valueOf(pramMap.get("teacherId")));
        try{
            logger.info("向用户{}sendPrac2Start的邮件",id);
            Map<String,Object> result = emailAService.sendPrac2Start(id);
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }

    /**
     * 从管理端取消Prac2
     * @param pramMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/sendCancelPrac2")
    public Map<String,Object> sendCancelPrac2(@RequestBody Map<String,Object> pramMap,HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(String.valueOf(pramMap.get("teacherId")));
        try{
            logger.info("向用户{}sendCancelPrac2的邮件",id);
            Map<String,Object> result = emailAService.sendCancelPrac2(id);
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }

    /**
     * 从管理端RESCHEDULE-Prac2
     * @param pramMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/sendReschedulePrac2")
    public Map<String,Object> sendReschedulePrac2(@RequestBody Map<String,Object> pramMap,HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(String.valueOf(pramMap.get("teacherId")));
        try{
            logger.info("向用户{}sendReschedulePrac2的邮件",id);
            Map<String,Object> result = emailAService.sendReschedulePrac2(id);
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }

    /**
     * Prac2从管理端和教师端选择PASS
     * @param pramMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/sendPrac2Pass")
    public Map<String,Object> sendPrac2Pass(@RequestBody Map<String,Object> pramMap,HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(String.valueOf(pramMap.get("teacherId")));
        try{
            logger.info("向用户{}sendPrac2Pass的邮件",id);
            Map<String,Object> result = emailAService.sendPrac2Pass(id);
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
