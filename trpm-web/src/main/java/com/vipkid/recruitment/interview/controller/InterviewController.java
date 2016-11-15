package com.vipkid.recruitment.interview.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Maps;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.recruitment.interview.service.InterviewService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.constant.ApplicationConstant.TeacherLifeCycle;
import com.vipkid.trpm.entity.User;

@RestController
@RestInterface(lifeCycle={TeacherLifeCycle.INTERVIEW,TeacherLifeCycle.REGULAR})
@RequestMapping("/recruitment/interview")
public class InterviewController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(InterviewController.class);

    @Autowired
    private InterviewService interviewService;
    
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> list(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            logger.info("user:{},list",user.getId());
            result.put("list", this.interviewService.findlistByInterview());
            result.put("status", true);
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
    
    
    @RequestMapping(value = "/bookClass", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> bookClass(HttpServletRequest request, HttpServletResponse response,long onlineClassId){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            logger.info("user:{},bookClass",user.getId());

            result.put("status", true);
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
    
    
    @RequestMapping(value = "/reschedule", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> reschedule(HttpServletRequest request, HttpServletResponse response,long onlineClassId){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            logger.info("user:{},reschedule",user.getId());

            result.put("status", true);
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
    
    
    @RequestMapping(value = "/getClassRoomUrl", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getClassRoomUrl(HttpServletRequest request, HttpServletResponse response,long onlineClassId){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            logger.info("user:{},getClassRoomUrl",user.getId());
            result = this.interviewService.getClassRoomUrl(onlineClassId, getTeacher(request));
            if(!MapUtils.getBooleanValue(result, "status")){
                result.put("info", "The class room url not exis.");
                response.setStatus(HttpStatus.BAD_REQUEST.value());  
            }
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
    
    
    @RequestMapping(value = "/getReschedule", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getReschedule(HttpServletRequest request, HttpServletResponse response,long onlineClassId){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            logger.info("user:{},getReschedule",user.getId());
            result.put("count", 1);
            result.put("status", true);
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
