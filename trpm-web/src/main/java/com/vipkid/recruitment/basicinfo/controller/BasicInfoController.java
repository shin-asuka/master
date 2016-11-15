package com.vipkid.recruitment.basicinfo.controller;

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

import com.google.api.client.util.Maps;
import com.vipkid.recruitment.basicinfo.service.BasicInfoService;
import com.vipkid.recruitment.basicinfo.service.TeachingExperienceService;
import com.vipkid.recruitment.entity.TeachingExperience;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.dto.TeacherDto;
import com.vipkid.rest.dto.TeachingExperienceDto;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.trpm.constant.ApplicationConstant.TeacherLifeCycle;
import com.vipkid.trpm.entity.TeacherNationalityCode;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.entity.app.AppEnum;
import com.vipkid.trpm.util.AppUtils;

@RestController
@RestInterface(lifeCycle={TeacherLifeCycle.SIGNUP,TeacherLifeCycle.BASIC_INFO})
@RequestMapping("/recruitment/basicinfo")
public class BasicInfoController extends RestfulController{
 
    private static Logger logger = LoggerFactory.getLogger(BasicInfoController.class);
    
    @Autowired    
    private BasicInfoService basicInfoService;
    
    @Autowired
    private TeachingExperienceService teachingExperienceService;
    
    
    @RequestMapping(value = "/getRecruitmentChannelList", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getRecruitmentChannelList(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            logger.info("user:{},getRecruitmentChannelList",user.getId());
            result.put("list", this.basicInfoService.getRecruitmentChannelList());
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
    
    
    @RequestMapping(value = "/getTeachingList", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getTeachingList(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            List<TeachingExperience> list = this.teachingExperienceService.getTeachingList(user.getId());
            result.put("list", list);
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
    
    @RequestMapping(value = "/saveOrUpdateTeaching", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> saveOrUpdateTeaching(HttpServletRequest request, HttpServletResponse response,
            @RequestBody TeachingExperienceDto teachingExperience){
        Map<String,Object> result = Maps.newHashMap();
        try{
            long resultRow = 0;
            User user = getUser(request);
            List<Result> list = ValidateUtils.checkBean(teachingExperience,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                result.put("status", false);
                result.put("resultCheck",list);
                logger.warn("resultCheck:"+JsonTools.getJson(list));
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
            return result;
        } catch (IllegalArgumentException e) {
            result.clear();
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.put("status", false);
        } catch (Exception e) {
            result.clear();
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
            result.clear();
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.put("status", false);
        } catch (Exception e) {
            result.clear();
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
            List<Result> list = ValidateUtils.checkBean(bean,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                result.put("status", false);
                result.put("resultCheck",list);
                logger.warn("resultCheck:"+JsonTools.getJson(list));
                return result;
            }
            if(!AppUtils.containsName(AppEnum.Gender.class, bean.getGender())){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                result.put("status", false);
                result.put("info", "Gender data is error:"+bean.getGender());
                logger.warn("warn:{}",result.get("info"));
                return result;
            }
            if(!AppUtils.containsName(AppEnum.DegreeType.class, bean.getHighestLevelOfEdu())){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                result.put("status", false);
                result.put("info", "Gender data is error:"+bean.getHighestLevelOfEdu());
                logger.warn("warn:{}",result.get("info"));
                return result;
            }
            if(!AppUtils.containsName(AppEnum.RecruitmentChannel.class, bean.getRecruitmentChannel())){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                result.put("status", false);
                result.put("info", "RecruitmentChannel data is error:"+bean.getRecruitmentChannel());
                logger.warn("warn:{}",result.get("info"));
                return result;
            }
            User user = getUser(request);
            String token = request.getHeader(RestfulController.AUTOKEN);
            result = this.basicInfoService.submitInfo(bean, user,token);
            return result;
        } catch (IllegalArgumentException e) {
            result.clear();
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.put("status", false);
        } catch (Exception e) {
            result.clear();
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.put("status", false);
        }
        return result;
    } 
    
        
    @RequestMapping(value = "/findTeacher", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> findTeacher(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            logger.info("userId:{}",user.getId());
            List<Map<String,Object>> list = this.basicInfoService.findTeacher();
            result.put("list", list);
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
    
    @RequestMapping(value = "/findPhoneCode", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> findPhoneCode(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            logger.info("userId:{}",user.getId());
            List<TeacherNationalityCode> list = this.basicInfoService.getTeacherNationalityCodes();
            result.put("list", list);
            return result;
        } catch (IllegalArgumentException e) {
            result.clear();
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            result.clear();
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }
}
