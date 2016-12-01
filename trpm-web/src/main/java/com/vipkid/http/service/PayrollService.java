package com.vipkid.http.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Maps;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.http.vo.HttpResult;
import com.vipkid.neo.model.Teacher;
import com.vipkid.payroll.model.Page;
import com.vipkid.service.neo.grpc.FindPayrollItemByTypeWithPageResponse;

public class PayrollService extends HttpBaseService {

	private static final Logger logger = LoggerFactory.getLogger(PayrollService.class);

	public JSONObject getPayrollItemByTeacherAndMonth(Integer teacherId, Integer month) {
		 Map<String, String> params = Maps.newHashMap();
	     params.put("teacherId", new Integer(teacherId).toString());
	     params.put("month", new Integer(month).toString());

		String url = new StringBuilder(super.serverAddress).append("/api/app/teacher/getPayrollItemByTeacherAndMonth").toString();
		JSONObject jsonObject = null ;
		jsonObject = getResult(params, url, jsonObject);
		return jsonObject;
	}



	public JSONObject findRuleByTeacherMonth(Integer teacherId, int month) {
		String url = new StringBuilder(super.serverAddress).append("/api/app/teacher/findRuleByTeacherMonth").toString();
		JSONObject jsonObject = null ;
		 Map<String, String> params = Maps.newHashMap();
	     params.put("teacherId", new Integer(teacherId).toString());
	     params.put("month", new Integer(month).toString());

		jsonObject = getResult(params, url, jsonObject);
		return jsonObject;
	}

	public FindPayrollItemByTypeWithPageResponse findPayrollItemByTypeWithPage(
			 int slalaryTypeCourseAdditionRule, int teacherId, int month) {
		
		Page<Teacher> page = new Page<Teacher>(request, response);
		
		
		String url = new StringBuilder(super.serverAddress).append("/api/app/teacher/findRuleByTeacherMonth").toString();
		JSONObject jsonObject = null ;
		 Map<String, String> params = Maps.newHashMap();
	     params.put("teacherId", new Integer(teacherId).toString());
	     params.put("month", new Integer(month).toString());
		// TODO Auto-generated method stub
		return null;
	}
	
	private JSONObject getResult(Map<String, String> params, String url, JSONObject jsonObject) {
		try {
			HttpResult result = WebUtils.post(url, params);
			if (HttpResult.STATUS_SUCCESS.equals(result.getStatus())) {
				jsonObject = JSONObject.parseObject(result.getResponse().toString());
			}
		} catch (Exception e) {
			logger.error("url", e);
		}
		return jsonObject;
	}

}
