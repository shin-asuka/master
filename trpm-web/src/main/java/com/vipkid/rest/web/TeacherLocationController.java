package com.vipkid.rest.web;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;
import com.vipkid.http.service.TeacherAppService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.entity.TeacherNationalityCode;

/**
 * @author zouqinghua
 * @date 2016年11月17日  下午5:31:24
 *
 */
@RestController
@RequestMapping("/app")
public class TeacherLocationController {

	private Logger logger = LoggerFactory.getLogger(TeacherLocationController.class);
	
	@Resource
	private TeacherAppService teacherAppService;

	/**
	 * 获取nationalCodes 列表
	 * @return
	 */
	@RequestMapping(value = "/teacher/nationalCodes", method = RequestMethod.GET)
	public Object getAllNationCodes() {
		logger.info("getAllNationCodes");
		List<TeacherNationalityCode> list = teacherAppService.getAllNationCodes();
		logger.info("nationalCodes list = {}",list.size());
		return ApiResponseUtils.buildSuccessDataResp(list);
	}
	
	/**
	 * 获取Country 列表
	 * @return
	 */
	@RequestMapping(value = "/info/countries", method = RequestMethod.GET)
	public Object getCountryList() {
		logger.info("getCountryList");
		List<TeacherLocation> list = teacherAppService.getCountryList();
		logger.info("countryList list = {}",list.size());
		return ApiResponseUtils.buildSuccessDataResp(list);
	}
	
	/**
	 * 根据country id 获取states 列表
	 * 
	 * @param countryId
	 * @return
	 */
	@RequestMapping(value = "/info/states", method = RequestMethod.GET)
	public Object getStateList(Integer countryId) {
		logger.info("getStateList countryId = {}", countryId);
		Preconditions.checkArgument(countryId != null , "countryId 不能为空");
		
		List<TeacherLocation> list = teacherAppService.getStateList(countryId);
		logger.info("stateList countryId = {}, list = {}",countryId,list.size());
		
		return ApiResponseUtils.buildSuccessDataResp(list);
	}
	
	/**
	 * 根据stateId 获取cities 列表
	 * 
	 * @param stateId
	 * @return
	 */
	@RequestMapping(value = "/info/cities", method = RequestMethod.GET)
	public Object getCityList(Integer stateId) {
		logger.info("getCityList stateId = {}", stateId);
		Preconditions.checkArgument(stateId != null , "stateId 不能为空");
		
		List<TeacherLocation> list = teacherAppService.getCityList(stateId);
		logger.info("cityList parentId = {}, list = {}",stateId,list.size());
		
		return ApiResponseUtils.buildSuccessDataResp(list);
	}
	
}
