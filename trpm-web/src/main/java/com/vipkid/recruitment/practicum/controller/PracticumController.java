package com.vipkid.recruitment.practicum.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Maps;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.recruitment.practicum.service.PracticumService;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.entity.Teacher;

@RestController
@RestInterface(lifeCycle={LifeCycle.PRACTICUM})
@RequestMapping("/recruitment/practicum")
public class PracticumController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(PracticumController.class);

    @Autowired
    private PracticumService practicumService;
    @Autowired
    private RecruitmentService recruitmentService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> list(HttpServletRequest request, HttpServletResponse response) {
        try{
            Map<String,Object> result = Maps.newHashMap();
            result.put("list", this.practicumService.findTimeList(getTeacher(request)));
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this,e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this, e);
        }
    }

    @RequestMapping(value = "/bookClass", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> bookClass(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String,Object> pramMap){
        try{
            Object onlineClassId = pramMap.get("onlineClassId");
            if(onlineClassId == null || !StringUtils.isNumeric(onlineClassId+"")){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail("onlineClassId is error !", this);
            }
            Map<String,Object> result = this.practicumService.bookClass(Long.valueOf(onlineClassId+""), getTeacher(request));
            if(ResponseUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this,e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this, e);
        }
    }

    @RequestMapping(value = "/reschedule", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> reschedule(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String,Object> pramMap){
        try{
            Object onlineClassId = pramMap.get("onlineClassId");
            if(onlineClassId == null || !StringUtils.isNumeric(onlineClassId+"")){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail("onlineClassId is error !", this);
            }
            Map<String,Object> result = this.practicumService.cancelClass(Long.valueOf(onlineClassId+""), getTeacher(request));
            if(ResponseUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this,e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this, e);
        }
    }

    @RequestMapping(value = "/getClassRoomUrl", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getClassRoomUrl(HttpServletRequest request, HttpServletResponse response,long onlineClassId){
        try{
            Map<String,Object> result = this.practicumService.getClassRoomUrl(onlineClassId, getTeacher(request));
            if(ResponseUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this,e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this, e);
        }
    }

    @RequestMapping(value = "/toContract", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> toContract(HttpServletRequest request, HttpServletResponse response){
        try{
            Teacher teacher = getTeacher(request);
            logger.info("user:{},getReschedule",teacher.getId());
            Map<String,Object> result = practicumService.toContract(teacher);
            if(ResponseUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this,e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this, e);
        }
    }

    @RequestMapping(value = "/getRemainRescheduleTimes", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getRemainRescheduleTimes(HttpServletRequest request, HttpServletResponse response){
        try{
            int count = this.recruitmentService.getRemainRescheduleTimes(getTeacher(request), Status.PRACTICUM.toString(), Result.CANCEL.toString());
            Map<String,Object> result = Maps.newHashMap();
            result.put("count",count);
            if(ResponseUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this,e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this, e);
        }
    }
}
