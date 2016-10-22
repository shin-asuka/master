package com.vipkid.rest.recruitment;

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
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.dto.TeacherDto;
import com.vipkid.rest.dto.TeachingExperienceDto;
import com.vipkid.rest.interceptor.RestInterface;
import com.vipkid.rest.validation.ValidationUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.trpm.constant.ApplicationConstant.TeacherLifeCycle;
import com.vipkid.trpm.entity.TeachingExperience;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.entity.app.AppEnum;
import com.vipkid.trpm.service.recruitment.BasicInfoService;
import com.vipkid.trpm.service.recruitment.TeachingExperienceService;
import com.vipkid.trpm.util.AppUtils;

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
            @RequestBody TeachingExperienceDto teachingExperience){
        Map<String,Object> result = Maps.newHashMap();
        try{
            long resultRow = 0;
            User user = getUser(request);
            Result resultCheck = ValidationUtils.checkForField(teachingExperience);
            if(resultCheck != null && resultCheck.isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                result.put("status", false);
                result.put("resultCheck",resultCheck);
                return result;
            }
            //时间判断
            if(teachingExperience.getTimePeriodStart() >= teachingExperience.getTimePeriodEnd()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                result.put("status", false);
                result.put("info", "The time interval is illegal!");
                logger.warn("时间区间不正确");
                return result;
            }
            if(teachingExperience.getId() > 0){
                resultRow = this.teachingExperienceService.updateTeaching(teachingExperience, user);
            }else{
                resultRow = this.teachingExperienceService.saveTeaching(teachingExperience, user);
            }
            result.put("id", resultRow);
            result.put("status", resultRow > 0 ? true : false);
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.put("status", false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.put("status", false);
        }
        return result;
    } 
    
    
    @RequestMapping(value = "/delTeaching", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> delTeaching(HttpServletRequest request, HttpServletResponse response,long id){
        Map<String,Object> result = Maps.newHashMap();
        try{
            long resultRow = 0;
            User user = getUser(request);
            resultRow = this.teachingExperienceService.delTeaching(id, user);
            result.put("id", resultRow);
            result.put("status", resultRow > 0 ? true : false);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.put("status", false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.put("status", false);
        }
        return result;
    } 
    /*
    @Deprecated
    @RequestMapping(value = "/saveInfo", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> saveInfo(HttpServletRequest request, HttpServletResponse response,@RequestBody BasicInfoBean bean){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            return this.basicInfoService.saveInfo(bean, user);
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.put("status", false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.put("status", false);
        }
        return result;
    } */
    
    
    @RequestMapping(value = "/submitInfo", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> submitInfo(HttpServletRequest request, HttpServletResponse response,@RequestBody TeacherDto bean){
        Map<String,Object> result = Maps.newHashMap();
        try{
            Result resultCheck = ValidationUtils.checkForField(bean);
            if(resultCheck != null && resultCheck.isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                result.put("status", false);
                result.put("resultCheck",resultCheck);
                return result;
            }
            if(!AppUtils.containsName(AppEnum.Gender.class, bean.getGender())){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                result.put("status", false);
                result.put("info", "Gender data is error!");
                return result;
            }
            if(!AppUtils.containsName(AppEnum.DegreeType.class, bean.getHighestLevelOfEdu())){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                result.put("status", false);
                result.put("info", "Gender data is error!");
                return result;
            }
            User user = getUser(request);
            return this.basicInfoService.submitInfo(bean, user);
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.put("status", false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.put("status", false);
        }
        return result;
    } 
    
    
    @RequestMapping(value = "/getStatus", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getStatus(HttpServletRequest request, HttpServletResponse response){
        try{
            User user = getUser(request);
            return this.basicInfoService.getStatus(user.getId());
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return Maps.newHashMap();
    } 
    
    @RequestMapping(value = "/findTeacher", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> findTeacher(HttpServletRequest request, HttpServletResponse response){
        try{
            User user = getUser(request);
            logger.info("userId:{}",user.getId());
            List<Map<String,Object>> list = this.basicInfoService.findTeacher();
            Map<String,Object> result = Maps.newHashMap();
            result.put("list", list);
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
