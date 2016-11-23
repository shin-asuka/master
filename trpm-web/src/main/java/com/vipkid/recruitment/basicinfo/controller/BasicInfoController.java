package com.vipkid.recruitment.basicinfo.controller;

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

import com.google.api.client.util.Maps;
import com.vipkid.enums.AppEnum;
import com.vipkid.enums.TeacherEnum.DegreeType;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherEnum.RecruitmentChannel;
import com.vipkid.enums.UserEnum.Gender;
import com.vipkid.recruitment.basicinfo.service.BasicInfoService;
import com.vipkid.recruitment.basicinfo.service.TeachingExperienceService;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.dto.TeacherDto;
import com.vipkid.rest.dto.TeachingExperienceDto;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;

@RestController
@RestInterface(lifeCycle={LifeCycle.SIGNUP,LifeCycle.BASIC_INFO})
@RequestMapping("/recruitment/basicinfo")
public class BasicInfoController extends RestfulController{
 
    private static Logger logger = LoggerFactory.getLogger(BasicInfoController.class);
    
    @Autowired    
    private BasicInfoService basicInfoService;
    
    @Autowired
    private TeachingExperienceService teachingExperienceService;
    
    
    @RequestMapping(value = "/getRecruitmentChannelList", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getRecruitmentChannelList(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String,Object> result = Maps.newHashMap();
            result.put("list", this.basicInfoService.getRecruitmentChannelList());
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }
    
    
    @RequestMapping(value = "/getTeachingList", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getTeachingList(HttpServletRequest request, HttpServletResponse response){
        try{
            User user = getUser(request);
            Map<String,Object> result = Maps.newHashMap();
            result.put("list", this.teachingExperienceService.getTeachingList(user.getId()));
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }
    
    @RequestMapping(value = "/saveOrUpdateTeaching", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> saveOrUpdateTeaching(HttpServletRequest request, HttpServletResponse response,
            @RequestBody TeachingExperienceDto teachingExperience){
        try{
            long resultRow = 0;
            User user = getUser(request);
            List<Result> list = ValidateUtils.checkBean(teachingExperience,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail(list.get(0).getName() + "," + list.get(0).getMessages(), this);
            }
            //时间判断
            if(teachingExperience.getTimePeriodStart() >= teachingExperience.getTimePeriodEnd()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail("The time interval is illegal!", this);
            }
            if(teachingExperience.getId() > 0){
                resultRow = this.teachingExperienceService.updateTeaching(teachingExperience, user);
            }else{
                resultRow = this.teachingExperienceService.saveTeaching(teachingExperience, user);
            }
            Map<String,Object> result = Maps.newHashMap();
            result.put("status", resultRow > 0 ? true : false);
            if(ResponseUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return ResponseUtils.responseFail("Save or Update fail",result,this);
            }
            result.put("id", resultRow);
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    } 
    
    
    @RequestMapping(value = "/delTeaching", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> delTeaching(HttpServletRequest request, HttpServletResponse response,long id){
        Map<String,Object> result = Maps.newHashMap();
        try{
            long resultRow = 0;
            User user = getUser(request);
            resultRow = this.teachingExperienceService.delTeaching(id, user);
            result.put("status", resultRow > 0 ? true : false);
            if(ResponseUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return ResponseUtils.responseFail("Save or Update fail",result,this);
            }
            result.put("id", resultRow);
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    } 
    
    /*
    @Deprecated
    @RequestMapping(value = "/saveInfo", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> saveInfo(HttpServletRequest request, HttpServletResponse response,@RequestBody BasicInfoBean bean){
        try{
            User user = getUser(request);
            Map<String,Object> result = this.basicInfoService.saveInfo(bean, user);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    } */
    
    
    @RequestMapping(value = "/submitInfo", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> submitInfo(HttpServletRequest request, HttpServletResponse response,@RequestBody TeacherDto bean){
        try{
            Map<String,Object> result = Maps.newHashMap();
            List<Result> list = ValidateUtils.checkBean(bean,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail(list.get(0).getName() + "," + list.get(0).getMessages(), this);
            }
            if(!AppEnum.containsName(Gender.class, bean.getGender())){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail("Gender data is error:"+bean.getGender(), this);
            }
            if(!AppEnum.containsName(DegreeType.class, bean.getHighestLevelOfEdu())){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail("HighestLevelOfEdu data is error:"+bean.getHighestLevelOfEdu(), this);
            }
            if(!AppEnum.containsName(RecruitmentChannel.class, bean.getRecruitmentChannel())){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail("RecruitmentChannel data is error:"+bean.getRecruitmentChannel(), this);
            }
            User user = getUser(request);
            String token = request.getHeader(RestfulController.AUTOKEN);
            result = this.basicInfoService.submitInfo(bean,user,token);
            if(ResponseUtils.isFail(result)){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    } 
    
    
    @RequestMapping(value = "/toInterview", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> toInterview(HttpServletRequest request, HttpServletResponse response){
        try{
            Teacher teacher = getTeacher(request);
            logger.info("user:{},getReschedule",teacher.getId());
            Map<String,Object> result = this.basicInfoService.toInterview(teacher);
            if(ResponseUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    } 
        
    @RequestMapping(value = "/findTeacher", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> findTeacher(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String,Object> result = Maps.newHashMap();
            User user = getUser(request);
            logger.info("userId:{}",user.getId());
            result.put("list", this.basicInfoService.findTeacher());
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
