package com.vipkid.rest.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Maps;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.app.BasicInfoBean;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.constant.ApplicationConstant.TeacherLifeCycle;
import com.vipkid.trpm.entity.TeachingExperience;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.rest.BasicInfoService;
import com.vipkid.trpm.service.rest.TeachingExperienceService;

@RestController
@RestInterface(lifeCycle={TeacherLifeCycle.SIGNUP,TeacherLifeCycle.BASIC_INFO})
@RequestMapping("/basicinfo")
public class BasicInfoController extends RestfulController{
 
    private static Logger logger = LoggerFactory.getLogger(BasicInfoController.class);
    
    @Autowired    
    private BasicInfoService basicInfoService;
    
    @Autowired
    private TeachingExperienceService teachingExperienceService;
    
    
    @RequestMapping(value = "/getRecruitmentChannelList", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getRecruitmentChannelList(HttpServletRequest request, HttpServletResponse response){
        try{
            User user = getUser(request);
            logger.info("user:{},getRecruitmentChannelList",user.getId());
            Map<String,Object> result = Maps.newHashMap();
            result.put("list", this.basicInfoService.getRecruitmentChannelList());
            return Maps.newHashMap();
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return Maps.newHashMap();
    }
    
    
    @RequestMapping(value = "/getTeachingList", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getTeachingList(HttpServletRequest request, HttpServletResponse response){
        try{
            User user = getUser(request);
            List<TeachingExperience> list = this.teachingExperienceService.getTeachingList(user.getId());
            Map<String,Object> result = Maps.newHashMap();
            result.put("list", list);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return Maps.newHashMap();
    }
    
    @RequestMapping(value = "/saveOrUpdateTeaching", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> saveOrUpdateTeaching(HttpServletRequest request, HttpServletResponse response,
            @RequestBody TeachingExperience teachingExperience){
        try{
            long resultRow = 0;
            User user = getUser(request);
            if(teachingExperience.getId() > 0){
                resultRow = this.teachingExperienceService.updateTeaching(teachingExperience, user);
            }else{
                resultRow = this.teachingExperienceService.saveTeaching(teachingExperience, user);
            }
            Map<String,Object> resultMap = Maps.newHashMap();
            resultMap.put("id", resultRow);
            resultMap.put("status", resultRow > 0 ? true : false);
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return Maps.newHashMap();
    } 
    
    
    @RequestMapping(value = "/delTeaching", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> delTeaching(HttpServletRequest request, HttpServletResponse response,long id){
        try{
            long resultRow = 0;
            User user = getUser(request);
            this.teachingExperienceService.delTeaching(id, user);
            Map<String,Object> resultMap = Maps.newHashMap();
            resultMap.put("id", resultRow);
            resultMap.put("status", resultRow > 0 ? true : false);
            return Maps.newHashMap();
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return Maps.newHashMap();
    } 
    
    
    
    @RequestMapping(value = "/submitInfo", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> submitInfo(HttpServletRequest request, HttpServletResponse response,@RequestBody BasicInfoBean bean){
        try{
            User user = getUser(request);
            
            
            return Maps.newHashMap();
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return Maps.newHashMap();
    } 
    
    
    @RequestMapping(value = "/getStatus", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getStatus(HttpServletRequest request, HttpServletResponse response){
        try{
            User user = getUser(request);
            
            
            return Maps.newHashMap();
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return Maps.newHashMap();
    } 
    
    
    
}
