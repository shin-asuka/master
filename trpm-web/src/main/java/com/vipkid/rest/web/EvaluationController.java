package com.vipkid.rest.web;

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

import com.google.api.client.util.Maps;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherPageLoginEnum.LoginType;
import com.vipkid.recruitment.utils.MapReturnUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.EvaluationService;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.service.TeacherPageLoginService;
import com.vipkid.trpm.entity.User;

@RestController
@RestInterface(lifeCycle=LifeCycle.REGULAR)
@RequestMapping("/evaluation")
public class EvaluationController extends RestfulController{
    
    private Logger logger = LoggerFactory.getLogger(PersonalInfoRestController.class);
    
    @Autowired
    private LoginService loginService;
    
    @Autowired
    private EvaluationService evaluationService;
    
    @Autowired
    private TeacherPageLoginService teacherPageLoginService;
    
    @RequestMapping(value = "/getTags", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getTags(HttpServletRequest request, HttpServletResponse response){
        try{
            User user = getUser(request);
            logger.info("userId:" + user.getId());
            return MapReturnUtils.responseSuccess(evaluationService.findTags());
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return MapReturnUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return MapReturnUtils.responseFail(e.getMessage(), this);
        }
    }
    
    
    @RequestMapping(value = "/getTeacherBio", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getTeacherBio(HttpServletRequest request, HttpServletResponse response, long teacherId){
        try{
            User user = getUser(request);
            logger.info("userId:" + user.getId());
            return MapReturnUtils.responseSuccess(evaluationService.findTeacherBio(teacherId));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return MapReturnUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return MapReturnUtils.responseFail(e.getMessage(), this);
        }
    }
    
    @RequestMapping(value = "/saveClick", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> saveClick(HttpServletRequest request, HttpServletResponse response){
        try{
            User user = getUser(request);
            logger.info("userId:" + user.getId());
            Map<String,Object> result = Maps.newHashMap();
            result.put("result",this.teacherPageLoginService.saveTeacherPageLogin(user.getId(),LoginType.EVALUATION_CLICK));
            return MapReturnUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return MapReturnUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return MapReturnUtils.responseFail(e.getMessage(), this);
        }
    }
}
