package com.vipkid.portal.pesupervisor.controller;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Stopwatch;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.portal.pesupervisor.model.PeSupervisorData;
import com.vipkid.portal.pesupervisor.service.PeSupervisorRestService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.entity.Teacher;

/**
 * 
 * @author zhangbole
 *
 */
@RestController
@RestInterface(lifeCycle=LifeCycle.REGULAR)
public class PeSupervisorRestController extends RestfulController{
	
	private Logger logger = LoggerFactory.getLogger(PeSupervisorRestController.class);
	
	@Autowired
	private PeSupervisorRestService peSupervisorRestService;

	/**
	 * teacher-portal前后端分离PE Supervisor页面数据接口
	 * @param teacherId
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "restPeSupervisor", method = RequestMethod.GET)
	public Map<String, Object> peSupervisor(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "teacherId", required = true) long teacherId ,
			@RequestParam(value = "page", required = true) int page ){
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restPeSupervisor接口，传入参数：teacherId={}, page={}", teacherId, page);
			

			Teacher teacher = getTeacher(request);
			if (teacher == null) {
				return ApiResponseUtils.buildErrorResp(1001, "老师未登录");
			}
			if (teacher.getId() != teacherId) {
				return ApiResponseUtils.buildErrorResp(1001, "老师id非法");
			}

			PeSupervisorData data = peSupervisorRestService.getPeSupervisorData(teacher, page);
			Map<String, Object> result = ApiResponseUtils.buildSuccessDataResp(data);
			
			long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restPeSupervisor接口，传入参数：teacherId={}, page={}。返回Json={}。共耗时{}ms", teacherId,  page, JsonUtils.toJSONString(result), millis);
	        return result;
	        } catch (Exception e) {
	        	logger.error("调用restPeSupervisor接口抛异常，传入参数：teacherId={}, page={}。抛异常: {}", teacherId,  page, e);//由于维龙的代码没有合上去，暂时这么处理
	        }
		return ApiResponseUtils.buildErrorResp(1001, "服务器抛异常");
	}
}
