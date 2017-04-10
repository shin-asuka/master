package com.vipkid.portal.personal.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.portal.personal.model.TeachingInfoData;
import com.vipkid.rest.service.LoginService;
import com.vipkid.teacher.tools.security.SHA256PasswordEncoder;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.entity.personal.TaxpayerView;
import com.vipkid.trpm.service.passport.RemberService;
import com.vipkid.trpm.service.portal.PersonalInfoService;
import com.vipkid.trpm.service.portal.TeacherTaxpayerFormService;
import com.vipkid.trpm.util.CookieUtils;

/**
 * 
 * @author zhangbole
 *
 */
@Service
public class PortalPersonalInfoService {
	private static final Logger logger = LoggerFactory.getLogger(PortalPersonalInfoService.class);

	@Autowired
	private PersonalInfoService personalInfoService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private RemberService remberService;

	@Autowired
	private TeacherTaxpayerFormService teacherTaxpayerFormService;

	public TeachingInfoData getTeachingInfoData(Teacher teacher, User user) {
		Long teacherId = teacher.getId();
		TeachingInfoData teachingInfoData = new TeachingInfoData();
		

		teachingInfoData.setTeacherSerialNum(teacher.getSerialNumber());
		teachingInfoData.setTeacherType(teacher.getType());
		teachingInfoData.setLifeCycle(teacher.getLifeCycle());
		teachingInfoData.setStatus(user.getStatus());
		teachingInfoData.setTeachingExperience(teacher.getTeachingExperience());
		teachingInfoData.setCertificates(teacher.getCertificates());
		teachingInfoData.setCurrency(teacher.getCurrency());
		
		Map<String, Object> map = personalInfoService.personalTeaching(teacherId);// 复用以前代码获取teacherCertificatedCourseName数据
		if(MapUtils.isNotEmpty(map)){
			teachingInfoData.setTrainingProfile((String) map.get("teacherCertificatedCourseName"));
		}

		Date contractStartDate = teacher.getContractStartDate();
		Date contractEndDate = teacher.getContractEndDate();
		if (null != contractStartDate && null != contractEndDate) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			teachingInfoData.setContractStartDate(df.format(contractStartDate));
			teachingInfoData.setContractEndDate(df.format(contractEndDate));
		}
		
		return teachingInfoData;
	}

	public Map<String, Object> updatePassword(Teacher teacher, User user, String currentPassword, String newPassword,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> teachingInfoData = Maps.newHashMap();
		// 执行密码修改操作
		SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
		String encodedNewPassword = encoder.encode(newPassword);
		Map<String, Object> map = personalInfoService.doChangePassword(teacher.getId(), encodedNewPassword);
		if ((Boolean) map.get("action")) {
			teachingInfoData.put("isSuccess", true);
			teachingInfoData.put("errCode", 0);
			remberService.delkeys(request, response);
			CookieUtils.removeCookie(response, CookieKey.TRPM_CHANGE_WINDOW, null, null);
			return teachingInfoData;// 修改成功
		} else {
			logger.error("教师修改密码失败。teacherId = {}, newPassword = {}", teacher.getId(), newPassword);
			teachingInfoData.put("isSuccess", false);
			teachingInfoData.put("errCode", 1);
			return teachingInfoData;
		}
	}

	public Map<String, Object> getTaxpayerData(long teacherId) {
		Map<String, Object> resutMap = Maps.newHashMap();
		TaxpayerView taxpayerView = teacherTaxpayerFormService.getTeacherTaxpayerView(teacherId);

		if (null != taxpayerView) {
			TeacherTaxpayerForm teacherTaxpayerFormW9 = taxpayerView.getFormW9();// 只返回W9的Taxpayer
			if (null != teacherTaxpayerFormW9) {
				resutMap.put("id", teacherTaxpayerFormW9.getId());
				resutMap.put("fileName", teacherTaxpayerFormW9.getFileName());
				resutMap.put("url", teacherTaxpayerFormW9.getUrl());
			}
		}
		return resutMap;
	}
}
