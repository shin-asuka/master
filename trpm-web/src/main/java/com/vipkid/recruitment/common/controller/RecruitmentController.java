package com.vipkid.recruitment.common.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.dto.TimezoneDto;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.rest.web.LoginController;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;

@RestController
@RestInterface(lifeCycle={LifeCycle.ALL})
@RequestMapping("/recruitment")
public class RecruitmentController extends RestfulController{
    
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @Autowired
    private RecruitmentService recruitmentService;
    
    @RequestMapping(value = "/getStatus", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getStatus(HttpServletRequest request, HttpServletResponse response){
        try{
            User user = getUser(request);
            Map<String,Object> result =  this.recruitmentService.getStatus(user.getId());
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    } 
    
    @RequestMapping(value = "/getTeacherInfo", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getTeacherInfo(HttpServletRequest request, HttpServletResponse response){
        try{
            Teacher teacher = getTeacher(request);
            Map<String,Object> result = Maps.newHashMap();
            result.put("teacher", teacher);
            result.put("teacherAddress", this.recruitmentService.getTeacherAddress(teacher.getCurrentAddressId()));
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    } 
    
    @RequestMapping(value = "/timezone", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> timezone(HttpServletRequest request, HttpServletResponse response,@RequestBody TimezoneDto timezone){
        try{
            Teacher teacher = getTeacher(request);
            List<Result> list = ValidateUtils.checkBean(timezone,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail(list.get(0).getName() + "," + list.get(0).getMessages(), this);
            }
            logger.info("user:{},timezone",teacher.getId());
            this.recruitmentService.updateTimezone(timezone, teacher);
            return ResponseUtils.responseSuccess();
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }
}
