package com.vipkid.rest.portal.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PersonalInfoRestService {
	public Map<String, Object> getTeachingInfoData(long teacherId);
	public Map<String, Object> changePassword(long teacherId, String currentPassword, String newPassword, HttpServletRequest request,HttpServletResponse response);
	public Map<String, Object> getTaxpayerData(long teacherId);
}
