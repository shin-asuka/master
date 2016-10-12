package com.vipkid.rest.web;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	public Map<String, String> getBankInfoByTeacherId(Long userId,HttpServletRequest request, HttpServletResponse response) {
		logger.info(" 重置教师密码  teacherId = {}",userId);
		Map<String, String> resultMap = null;
		if(userId!=null){
			User user = new User();
			user.setId(userId);
			resultMap = passportService.senEmailForPassword(user);
		}
		logger.info("重置教师密码信息 userId = {} , resultMap = {}",userId,JsonUtils.toJSONString(resultMap));
		
		return resultMap;
	}

}
