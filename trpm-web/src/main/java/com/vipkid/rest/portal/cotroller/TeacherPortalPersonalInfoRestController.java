package com.vipkid.rest.portal.cotroller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Maps;
import com.google.common.base.Stopwatch;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.rest.portal.service.PersonalInfoRestService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.service.passport.RemberService;
import com.vipkid.trpm.util.CookieUtils;

@RestController
//@RestInterface( lifeCycle = "REGULAR")
public class TeacherPortalPersonalInfoRestController {
	private final Logger logger = LoggerFactory.getLogger(TeacherPortalPersonalInfoRestController.class);
	
	@Autowired
	private PersonalInfoRestService personalInfoRestService;
	
	@RequestMapping(value = "restTeachingInfo", method = RequestMethod.GET)
	public Map<String, Object> restTeachingInfo(
			@RequestParam(value = "teacherId", required = true) long teacherId){
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restTeachingInfo接口。传入参数：teacherId = {}", teacherId);
			
			Map<String, Object> result = personalInfoRestService.getTeachingInfoData(teacherId);
			
			long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restTeachingInfo接口。传入参数：teacherId = {}，返回json = {}。用时{}ms", teacherId, JsonUtils.toJSONString(result), millis);
			return result;
		} catch (Exception e) {
			logger.error("调用restTeachingInfo接口抛异常。传入参数：teacherId = {}，异常 = {}。", teacherId, e);
		}
		return ApiResponseUtils.buildErrorResp(1002, "抛异常");
	}
	
	@RequestMapping(value = "restChangePassword", method = RequestMethod.POST)
	public Map<String, Object> restChangPassword( HttpServletRequest request,HttpServletResponse response,
			@RequestBody Map<String, Object> param){
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			
			Map<String,Object> result = Maps.newHashMap();
			
			List<Result> list = ValidateUtils.checkBean(param,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                result.put("status", false);
                result.put("resultCheck",list);
                logger.warn("resultCheck:"+JsonTools.getJson(list));
                return ApiResponseUtils.buildErrorResp(1001, "参数错误");
            }
            Integer teacherIdInt = (Integer) param.get("teacherId");
            Long teacherId = Long.valueOf(teacherIdInt);
            String currentPassword = (String) param.get("currentPassword");
            String newPassword = (String) param.get("newPassword");
			
            logger.info("开始调用restChangPassword接口。传入参数：teacherId = {}", teacherId);
            
			result = personalInfoRestService.changePassword(teacherId, currentPassword, newPassword, request, response);
			
			long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restChangPassword接口。传入参数：teacherId = {}，currentPassword = 缺省， newPassword = 缺省。返回json = {}。用时{}ms", teacherId, JsonUtils.toJSONString(result), millis);
			return result;
		} catch (Exception e) {
			logger.error("调用restChangPassword接口抛异常。传入参数：post json = {}。异常 = {}。", JsonUtils.toJSONString(param), e);
		}
		return ApiResponseUtils.buildErrorResp(1002, "抛异常");
	}
	
	@RequestMapping(value = "restTaxpayer", method = RequestMethod.GET)
	public Map<String, Object> restChangPassword( HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value = "teacherId", required = true) long teacherId){
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restTaxpayer接口。传入参数：teacherId = {}", teacherId);
			
			Map<String, Object> result = personalInfoRestService.getTaxpayerData(teacherId);
			
			long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restTaxpayer接口。传入参数：teacherId = {}。返回json = {}。用时{}ms", teacherId, JsonUtils.toJSONString(result), millis);
			return result;
		} catch (Exception e) {
			logger.error("调用restTaxpayer接口抛异常。传入参数：teacherId = {}。异常 = {}。", teacherId, e);
		}
		return ApiResponseUtils.buildErrorResp(1002, "抛异常");
	}
}
