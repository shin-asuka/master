package com.vipkid.recruitment.common.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.web.LoginController;
import com.vipkid.trpm.constant.ApplicationConstant.TeacherLifeCycle;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;

@RestController
@RestInterface(lifeCycle={TeacherLifeCycle.SIGNUP,TeacherLifeCycle.BASIC_INFO,TeacherLifeCycle.INTERVIEW,TeacherLifeCycle.SIGN_CONTRACT,TeacherLifeCycle.TRAINING,TeacherLifeCycle.PRACTICUM,TeacherLifeCycle.REGULAR})
@RequestMapping("/recruitment")
public class RecruitmentController extends RestfulController{
    
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @Autowired
    private RecruitmentService recruitmentService;
    
    @RequestMapping(value = "/getStatus", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getStatus(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            result =  this.recruitmentService.getStatus(user.getId());
            return result;
        } catch (IllegalArgumentException e) {
            result.put("status", false);
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            result.put("status", false);
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    } 
    
    @RequestMapping(value = "/getTeacherInfo", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getTeacherInfo(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        try{
            Teacher teacher = getTeacher(request);
            result.put("teacher", teacher);
            result.put("teacherAddress", this.recruitmentService.getTeacherAddress(teacher.getCurrentAddressId()));
            result.put("status", true);
            return result;
        } catch (IllegalArgumentException e) {
            result.put("status", false);
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            result.put("status", false);
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    } 
    
    @RequestMapping(value = "/timezone", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> timezone(HttpServletRequest request, HttpServletResponse response,String timezone){
        Map<String,Object> result = Maps.newHashMap();
        try{
            Teacher teacher = getTeacher(request);
            logger.info("user:{},timezone",teacher.getId());
            boolean resultBol = this.recruitmentService.updateTimezone(timezone, teacher);
            result.put("status", resultBol);
            return result;
        } catch (IllegalArgumentException e) {
            result.clear();
            result.put("status", false);
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            result.clear();
            result.put("status", false);
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }
}
