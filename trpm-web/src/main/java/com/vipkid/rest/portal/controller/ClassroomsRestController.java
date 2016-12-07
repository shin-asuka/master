package com.vipkid.rest.portal.controller;

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

import com.google.api.client.util.Maps;
import com.google.common.base.Stopwatch;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.rest.portal.service.ClassroomsRestService;

@RestController
public class ClassroomsRestController {
	private static Logger logger = LoggerFactory.getLogger(ClassroomsRestController.class);
	
	@Autowired
	private ClassroomsRestService classroomsRestService;

	@RequestMapping(value = "/restClassrooms", method = RequestMethod.GET)
	public Map<String, Object> classrooms(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value="teacherId", required=true) long teacherId,
			@RequestParam(value="month",required=true) int offsetOfMonth,
			@RequestParam(value="tag", required=true) String courseType,
			@RequestParam(value="page", required=true) int page) {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restClassrooms接口，传入参数：teacherId={}, month={}, tag={}, page={}", teacherId, offsetOfMonth, courseType, page);
			
			Map<String, Object> result = null;
			result = classroomsRestService.getClassroomsData(teacherId, offsetOfMonth, courseType, page);
			
			long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restClassrooms接口，传入参数：teacherId={}, month={}, tag={}, page={}。返回Json={}。共耗时{}ms", teacherId, offsetOfMonth, courseType, page, JsonUtils.toJSONString(result), millis);
	        return result;
	        } catch (Exception e) {
	        	logger.error("调用restClassrooms接口抛异常: {}", e);//由于维龙的代码没有合上去，暂时这么处理
	        }
		return null;
	}
	
	@RequestMapping(value = "/restClassroomsMaterial", method  = RequestMethod.GET)
	public Map<String, Object> material(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "lessonId", required = true) long lessonId){
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restClassroomsMaterials接口， 传入参数：lessonId = {}", lessonId);
			Map<String, Object> result = Maps.newHashMap();
			result = classroomsRestService.getClassroomsMaterialByLessonId(lessonId);
			long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restClassroomsMaterials接口，传入参数：lessonId = {}。返回Json={}。耗时{}ms", lessonId, JsonUtils.toJSONString(result), millis);
			return result;
		} catch (Exception e) {
			logger.error("调用restClassroomsMaterial接口抛异常: {}", e);//由于维龙的代码没有合上去，暂时这么处理
		}
		return null;
	}
	
	@Deprecated
	@RequestMapping(value = "/restClassroomsMaterials", method = RequestMethod.GET)
	public Map<String, Object> materials(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "lessonId", required = true) long lessonId){
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restClassroomsMaterials接口， 传入参数：lessonId = {}", lessonId);
			
			Map<String, Object> result = null;
			result = classroomsRestService.getClassroomsMaterials(lessonId);

			long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restClassroomsMaterials接口，传入参数：lessonId = {}。返回Json={}。耗时{}ms", lessonId, JsonUtils.toJSONString(result), millis);
			return result;
		} catch (Exception e) {
        	logger.error("调用restClassroomsMaterials接口抛异常: {}", e);//由于维龙的代码没有合上去，暂时这么处理
		}
		return null;
	}
	
}
