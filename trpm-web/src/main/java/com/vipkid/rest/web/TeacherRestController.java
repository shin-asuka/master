package com.vipkid.rest.web;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.passport.PassportService;
import com.vipkid.trpm.service.portal.PersonalInfoService;
import com.vipkid.trpm.service.portal.TeacherService;


@RestController
public class TeacherRestController {

	private Logger logger = LoggerFactory.getLogger(TeacherRestController.class);
	
	@Resource
	private PassportService passportService;
	
	@RequestMapping(value = "/teacherPassword/sendEmail")
	public Map<String, String> getBankInfoByTeacherId(Long userId,String username,HttpServletRequest request, HttpServletResponse response) {
		logger.info(" 重置教师密码  teacherId = {} username = {}",userId,username);
		Map<String, String> resultMap = null;
		if(userId!=null && StringUtils.isNotBlank(username)){
			User user = passportService.findUserByUsername(username);
			if(user!=null && userId.equals(user.getId())){
				//user.setId(userId);
				resultMap = passportService.senEmailForPassword(user);
			}
		}
		if(resultMap == null){
			resultMap = Maps.newHashMap();
			resultMap.put("status", "0");
			logger.info("重置教师密码信息失败！  username = {}",username);
		}else{
			resultMap.put("status", "1");
		}
		
		logger.info("重置教师密码信息 userId = {} , resultMap = {}",userId,resultMap);
		
		return resultMap;
	}

}
