package com.vipkid.recruitment.interview.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vipkid.http.utils.JacksonUtils;
import com.vipkid.trpm.entity.User;
import org.apache.commons.collections.MapUtils;
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
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.recruitment.common.service.AuditEmailService;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.interview.InterviewConstant;
import com.vipkid.recruitment.interview.service.InterviewService;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.trpm.entity.Teacher;

@RestController
@RestInterface(lifeCycle={LifeCycle.INTERVIEW})
@RequestMapping("/recruitment/interview")
public class InterviewController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(InterviewController.class);

    @Autowired
    private InterviewService interviewService;
    
    @Autowired
    private RecruitmentService recruitmentService;
    
    @Autowired
    private AuditEmailService auditEmailService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> list(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String,Object> result = Maps.newHashMap();
            result.put("list", this.interviewService.findListByInterview());
            result.put("days", InterviewConstant.SHOW_DAYS_EXCLUDE_TODAY);
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }
    
    
    @RequestMapping(value = "/bookClass", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> bookClass(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String,Object> pramMap){
        try{
            //Map<String,Object> result = this.interviewService.bookInterviewClass(Long.valueOf(onlineClassId+""), getTeacher(request));
            Object onlineClassScheduleTS   = pramMap.get("onlineClassScheduleTS");
            if(onlineClassScheduleTS == null || !StringUtils.isNumeric(onlineClassScheduleTS+"")){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail("This online class does not exist.");
            }

            //Optimise the interviewer randomise process
            String onlineClassIdStr = this.interviewService.getOnlineClassIdRandomised(Long.valueOf(onlineClassScheduleTS + ""));
            if (onlineClassIdStr == ""){
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return  ReturnMapUtils.returnFail("Oops, someone else just booked this time slot. Please select another.");
            }

            Map<String, Object> result = this.interviewService.bookInterviewClass(Long.valueOf(onlineClassIdStr + ""), getTeacher(request), InterviewConstant.BOOK_TIME);

            if(ReturnMapUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;   
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), e);
        }
    } 
    
    
    @RequestMapping(value = "/reschedule", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> reschedule(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String,Object> pramMap){
        try{
            Object onlineClassId = pramMap.get("onlineClassId");
            if(onlineClassId == null || !StringUtils.isNumeric(onlineClassId+"")){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail("This online class does not exist.");
            }
            Map<String,Object> result = this.interviewService.cancelInterviewClass(Long.valueOf(onlineClassId+""), getTeacher(request));
            if(ReturnMapUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }else{
            	//add cancel 邮件
            	auditEmailService.sendInterviewReapply(getTeacher(request).getId());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), e);
        }
    } 
    
    
    @RequestMapping(value = "/getClassRoomUrl", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getClassRoomUrl(HttpServletRequest request, HttpServletResponse response,long onlineClassId){
        try{
            Map<String,Object> result = this.interviewService.getClassRoomUrl(onlineClassId, getTeacher(request),getUser(request));
            if(ReturnMapUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), e);
        }
    }


    @RequestMapping(value = "/createBook", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> start(HttpServletRequest request, HttpServletResponse response){
        try{
            Teacher teacher = getTeacher(request);
            Map<String, Object> ret;

            ret = interviewService.createInterviewClass(teacher);
            logger.info("interview/start 1: teacherId = {}, ret = {}", teacher.getId(), JacksonUtils.toJSONString(ret));


            if (ReturnMapUtils.isFail(ret)) {
                return ret;
            }

            long onlineClassId = Long.parseLong(ret.get("id").toString());


            if (MapUtils.getBooleanValue(ret, "isCreated")) {
                ret = ReturnMapUtils.returnSuccess();
                ret.put("onlineClassId", onlineClassId);
                return ret;
            }

            ret = interviewService.bookInterviewClass(onlineClassId, teacher, InterviewConstant.BOOK_TIME_0);
            logger.info("interview/start 2: teacherId = {}, ret = {}", teacher.getId(), JacksonUtils.toJSONString(ret));


            if (ReturnMapUtils.isFail(ret)) {
                return ret;
            }

            ret = ReturnMapUtils.returnSuccess();
            ret.put("onlineClassId", onlineClassId);
            return ret;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), e);
        }
    }


    @RequestMapping(value = "/toTraining", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> toTraining(HttpServletRequest request, HttpServletResponse response){
        try{
            Teacher teacher = getTeacher(request);
            logger.info("user:{},getReschedule",teacher.getId());
            Map<String,Object> result = this.interviewService.updateToTraining(teacher);
            if(ReturnMapUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), e);
        }
    }


    @RequestMapping(value = "/getRemainRescheduleTimes", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getRemainRescheduleTimes(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String,Object> result = Maps.newHashMap();
            //result.put("count",this.recruitmentService.getRemainRescheduleTimes(getTeacher(request), Status.INTERVIEW.toString(), Result.CANCEL.toString(), false));
            result.put("count", 5);
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), e);
        }
    }
}
