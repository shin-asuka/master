package com.vipkid.portal.personal.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.trpm.entity.Teacher;

@Service
public class PortalBasicInfoService {

	
	public Map<String,Object> getBasicInfo(Teacher teacher){
		
		return Maps.newHashMap();
	}
	
	public Map<String,Object> updateBasicInfo(Teacher teacher){
		
		return ReturnMapUtils.returnSuccess();
	}
}
