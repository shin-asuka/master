package com.vipkid.portal.personal.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.portal.personal.model.TeachingInfoData;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.entity.personal.TaxpayerView;
import com.vipkid.trpm.security.SHA256PasswordEncoder;
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
public class PersonalInfoRestService {
	private static final Logger logger = LoggerFactory.getLogger(PersonalInfoRestService.class);

	@Autowired
	private PersonalInfoService personalInfoService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private SHA256PasswordEncoder mSHA256PasswordEncoder;

	@Autowired
	private RemberService remberService;

	@Autowired
	private TeacherTaxpayerFormService teacherTaxpayerFormService;

	public TeachingInfoData getTeachingInfoData(Teacher teacher, User user) {
		Long teacherId = teacher.getId();
		TeachingInfoData data = new TeachingInfoData();
		Map<String, Object> map = personalInfoService.personalTeaching(teacherId);// 复用以前代码获取teacherCertificatedCourseName数据

		data.setTeacherSerialNum(teacher.getSerialNumber());
		data.setTeacherType(teacher.getType());
		data.setLifeCycle(teacher.getLifeCycle());
		data.setStatus(user.getStatus());
		data.setTeachingExperience(teacher.getTeachingExperience());
		data.setCertificates(teacher.getCertificateFiles());
		data.setTrainingProfile((String) map.get("teacherCertificatedCourseName"));

		Date contractStartDate = teacher.getContractStartDate();
		Date contractEndDate = teacher.getContractEndDate();
		if (null != contractStartDate && null != contractEndDate) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			data.setContractStartDate(df.format(contractStartDate));
			data.setContractEndDate(df.format(contractEndDate));
		}

		data.setCurrency(teacher.getCurrency());

		return data;
	}

	public Map<String, Object> changePassword(Teacher teacher, User user, String currentPassword, String newPassword,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> data = Maps.newHashMap();
		// 执行密码修改操作
		String encodedNewPassword = mSHA256PasswordEncoder.encode(newPassword);
		Map<String, Object> map = personalInfoService.doChangePassword(teacher.getId(), encodedNewPassword);
		if ((Boolean) map.get("action")) {
			data.put("isSuccess", true);
			data.put("errCode", 0);
			remberService.delkeys(request, response);
			CookieUtils.removeCookie(response, CookieKey.TRPM_CHANGE_WINDOW, null, null);
			return data;// 修改成功
		} else {
			logger.error("教师修改密码失败。teacherId = {}, newPassword = {}", teacher.getId(), newPassword);
			data.put("isSuccess", false);
			data.put("errCode", 1);
			return data;
		}
	}

	public Map<String, Object> getTaxpayerData(long teacherId) {
		Map<String, Object> data = null;
		TaxpayerView taxpayerView = teacherTaxpayerFormService.getTeacherTaxpayerView(teacherId);

		if (null != taxpayerView) {
			TeacherTaxpayerForm teacherTaxpayerFormW9 = taxpayerView.getFormW9();// 只返回W9的Taxpayer
			if (null != teacherTaxpayerFormW9) {
				data = Maps.newHashMap();
				data.put("id", teacherTaxpayerFormW9.getId());
				data.put("fileName", teacherTaxpayerFormW9.getFileName());
				data.put("url", teacherTaxpayerFormW9.getUrl());
			}
		}
		return data;
	}
}
