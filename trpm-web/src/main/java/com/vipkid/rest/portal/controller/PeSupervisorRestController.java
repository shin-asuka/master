package com.vipkid.rest.portal.controller;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Stopwatch;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.rest.portal.model.PeSupervisorData;
import com.vipkid.rest.portal.service.PeSupervisorRestService;
import com.vipkid.trpm.controller.AbstractController;

@RestController
public class PeSupervisorRestController extends AbstractController{
	
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
	public PeSupervisorData peSupervisor(
			@RequestParam(value = "teacherId", required = true) long teacherId ,
			@RequestParam(value = "page", required = true) int page ){
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restPeSupervisor接口，传入参数：teacherId={}, page={}", teacherId, page);
			
			PeSupervisorData result = peSupervisorRestService.getPeSupervisorData(teacherId, page);
			
			long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restPeSupervisor接口，传入参数：teacherId={}, page={}。返回Json={}。共耗时{}ms", teacherId,  page, JsonUtils.toJSONString(result), millis);
	        return result;
	        } catch (Exception e) {
	        	logger.error("调用restPeSupervisor接口抛异常，传入参数：teacherId={}, page={}。抛异常: {}", teacherId,  page, e);//由于维龙的代码没有合上去，暂时这么处理
	        }
		return null;
	}
}
