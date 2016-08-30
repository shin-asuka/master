package com.vipkid.rest.web;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.task.service.UnitAssesssmentService;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.service.portal.PersonalInfoService;
import com.vipkid.trpm.service.portal.TeacherService;

//import com.google.common.base.Preconditions;

@RestController
public class PersonalInfoRestController {

	private Logger logger = LoggerFactory.getLogger(PersonalInfoRestController.class);
	
	@Resource
	private TeacherService teacherService;
	
	@Resource
	private PersonalInfoService personalInfoService;
	
	@Autowired
	private UnitAssesssmentService unitAsusesssmentService;
	
	@RequestMapping(value = "/personal/getBankInfoByTeacherId")
	public Map<String, Object> getBankInfoByTeacherId(Long teacherId,HttpServletRequest request, HttpServletResponse response) {
		logger.info("获取教师银行信息 teacherId = {}",teacherId);
		Map<String, Object> model = Maps.newHashMap();
		Teacher teacher = null;
		//Preconditions.checkArgument(null != teacherId, "教师Id不能为空！");
		if(teacherId!=null){
			teacher = teacherService.get(teacherId);
		}
		if(teacher!=null){
			TeacherAddress beneficiaryAddress = personalInfoService.getTeacherAddress(teacher.getBeneficiaryAddressId());
			if (null != beneficiaryAddress) {
				TeacherLocation beneficiaryCountry = personalInfoService.getLocationById(beneficiaryAddress.getCountryId());
				TeacherLocation beneficiaryState = personalInfoService.getLocationById(beneficiaryAddress.getStateId());
				TeacherLocation beneficiaryCity = personalInfoService.getLocationById(beneficiaryAddress.getCity());
				model.put("beneficiaryAddress", beneficiaryAddress);
				model.put("beneficiaryCountry", beneficiaryCountry);
				model.put("beneficiaryState", beneficiaryState);
				model.put("beneficiaryCity", beneficiaryCity);
			}
			TeacherAddress beneficiaryBankAddress = personalInfoService.getTeacherAddress(teacher.getBeneficiaryBankAddressId());
			if (beneficiaryBankAddress != null) {
				TeacherLocation beneficiaryBankCountry = personalInfoService.getLocationById(beneficiaryBankAddress.getCountryId());
				TeacherLocation beneficiaryBankState = personalInfoService.getLocationById(beneficiaryBankAddress.getStateId());
				TeacherLocation beneficiaryBankCity = personalInfoService.getLocationById(beneficiaryBankAddress.getCity());
				model.put("beneficiaryBankAddress", beneficiaryBankAddress);
				model.put("beneficiaryBankCountry", beneficiaryBankCountry);
				model.put("beneficiaryBankState", beneficiaryBankState);
				model.put("beneficiaryBankCity", beneficiaryBankCity);
			}
			TeacherLocation issuanceCountry = personalInfoService.getLocationById(teacher.getIssuanceCountry());
			model.put("issuanceCountry", issuanceCountry);
		}
		logger.info("教师银行信息 teacherId = {} , bankInfo = {}",teacherId,JsonUtils.toJSONString(model));
		
		return model;
	}

	@RequestMapping(value = "/assess/test")
	public void test(){
		unitAsusesssmentService.remindTeacherUnitAssessmentFor6Hour();
		unitAsusesssmentService.remindTeacherUnitAssessmentFor12Hour();
	}
}
