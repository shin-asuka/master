package com.vipkid.rest.portal.service;

import java.util.Map;

public interface PersonalInfoRestService {
	public Map<String, Object> getTeachingInfoData(long teacherId);
	public Map<String, Object> changePassword(long teacherId, String currentPassword, String newPassword);
}
