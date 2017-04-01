package com.vipkid.recruitment.common.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vipkid.http.utils.JacksonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.dto.TimezoneDto;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.rest.web.LoginController;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.entity.TeacherRecruitPeakTime;

/**
 * 该类,仅提供给招募端各个状态下需要调用的通用接口
 * Created by zengweilong
 *
 */
@RestController
@RestInterface
@RequestMapping("/recruitment")
public class RecruitmentController extends RestfulController{
    
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @Autowired
    private RecruitmentService recruitmentService;
        
    @RequestMapping(value = "/getStatus", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getStatus(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String,Object> result =  this.recruitmentService.getStatus(getTeacher(request));
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    } 
    
    @RequestMapping(value = "/getTeacherInfo", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getTeacherInfo(HttpServletRequest request, HttpServletResponse response){
        try{
            Teacher teacher = getTeacher(request);
            Map<String,Object> result = Maps.newHashMap();
            result.put("timezone", teacher.getTimezone());
            result.put("countryId","");
            result.put("countryName","");
            result.put("stateId","");
            result.put("stateName","");
            result.put("city","");
            result.put("cityName","");
            TeacherAddress ta = this.recruitmentService.getTeacherAddress(teacher.getCurrentAddressId());
            if(ta != null){
                result.put("countryId",ta.getCountryId());
                TeacherLocation country = this.recruitmentService.getTeacherLocation(ta.getCountryId());
                if(country != null){
                    result.put("countryName",country.getName());
                }
                result.put("stateId", ta.getStateId());
                TeacherLocation state = this.recruitmentService.getTeacherLocation(ta.getStateId());
                if(state != null){
                    result.put("stateName",state.getName());
                }
                result.put("city",ta.getCity());
                TeacherLocation city = this.recruitmentService.getTeacherLocation(ta.getCity());
                if(city != null){
                    result.put("cityName",city.getName());
                }
            }
            result.putAll(this.recruitmentService.getOnlineClassInfo(teacher));
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    } 
    
    @RequestMapping(value = "/timezone", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> timezone(HttpServletRequest request, HttpServletResponse response,@RequestBody TimezoneDto timezone){
        try{
            Teacher teacher = getTeacher(request);
            List<Result> list = ValidateUtils.checkBean(timezone,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(list.get(0).getName() + "," + list.get(0).getMessages());
            }
            logger.info("user:{},timezone",teacher.getId());
            Map<String,Object> result = this.recruitmentService.updateTimezone(timezone, teacher);
            if(ReturnMapUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }


    /**
     * 保存老师约面试时间
     * @param map 老师约面试时间和面试状态
     * */
    @RequestMapping(value = "/saveRecruitPeakTime",method = RequestMethod.POST)
    public Map<String,Object> teacherRecruitPeakTime(HttpServletRequest request,@RequestBody Map<String,Object> map){
        try {
            if (map == null || map.get("status") == null || map.get("scheduledDateTime") == null){
                logger.error("保存老师面试时间为空{}", JacksonUtils.toJSONString(map));
                return ApiResponseUtils.buildErrorResp(org.apache.http.HttpStatus.SC_BAD_REQUEST, "请求参数有误");
            }

            Teacher teacher = getTeacher(request);
            String status = String.valueOf(map.get("status"));
            List<Long> scheduledDateTimeList = (List<Long>) map.get("scheduledDateTime");

            return recruitmentService.saveTeacherRecruitPeak(teacher, status, scheduledDateTimeList);
        }catch (Exception e){
            logger.error("保存老师约面试时间异常{}",e);
            return ApiResponseUtils.buildErrorResp(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,"保存老师约课面试信息异常", JacksonUtils.toJSONString(map));
        }
    }
}
