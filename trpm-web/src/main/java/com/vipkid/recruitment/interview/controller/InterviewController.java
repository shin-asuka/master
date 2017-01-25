package com.vipkid.recruitment.interview.controller;

import com.google.api.client.util.Maps;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.interview.InterviewConstant;
import com.vipkid.recruitment.interview.service.InterviewService;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.trpm.entity.Teacher;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RestInterface(lifeCycle={LifeCycle.INTERVIEW})
@RequestMapping("/recruitment/interview")
public class InterviewController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(InterviewController.class);

    @Autowired
    private InterviewService interviewService;
    @Autowired
    private RecruitmentService recruitmentService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> list(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String,Object> result = Maps.newHashMap();
            result.put("list", this.interviewService.findlistByInterview());
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
//            Object onlineClassId = pramMap.get("onlineClassId");
//            if(onlineClassId == null || !StringUtils.isNumeric(onlineClassId+"")){
//                response.setStatus(HttpStatus.BAD_REQUEST.value());
//                return ReturnMapUtils.returnFail("This online class does not exist.");
//            }

            //Map<String,Object> result = this.interviewService.bookInterviewClass(Long.valueOf(onlineClassId+""), getTeacher(request));
            Object onlineClassScheduleTS   = pramMap.get("onlineClassScheduleTS");
            if(onlineClassScheduleTS == null || !StringUtils.isNumeric(onlineClassScheduleTS+"")){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail("This online class does not exist.");
            }

            String onlineClassIdStr = this.interviewService.randomiseInterviewer(Long.valueOf(onlineClassScheduleTS+""), getTeacher(request));

            Map<String,Object> result = this.interviewService.bookInterviewClass(Long.valueOf(onlineClassIdStr+""), getTeacher(request));

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
            Map<String,Object> result = this.interviewService.getClassRoomUrl(onlineClassId, getTeacher(request));
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
            result.put("count",this.recruitmentService.getRemainRescheduleTimes(getTeacher(request), Status.INTERVIEW.toString(), Result.CANCEL.toString(), false));
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
