package com.vipkid.http.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Maps;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.http.vo.HttpResult;
import com.vipkid.payroll.model.Page;

public class PayrollService extends HttpBaseService {

	private static final Logger logger = LoggerFactory.getLogger(PayrollService.class);

	public JSONObject getPayrollItemByTeacherAndMonth(Integer teacherId, Integer month) {
		 Map<String, String> params = Maps.newHashMap();
	     params.put("teacherId", new Integer(teacherId).toString());
	     params.put("month", new Integer(month).toString());

		String url = new StringBuilder(super.serverAddress).append("/public/payroll/getPayrollItemByTeacherAndMonth").toString();
		JSONObject jsonObject = null ;
		jsonObject = getResult(params, url, jsonObject);
		return jsonObject;
	}



	public JSONObject findRuleByTeacherMonth(Integer teacherId, int month) {
		String url = new StringBuilder(super.serverAddress).append("/public/payroll/findRuleByTeacherMonth").toString();
		JSONObject jsonObject = null ;
		 Map<String, String> params = Maps.newHashMap();
	     params.put("teacherId", new Integer(teacherId).toString());
	     params.put("month", new Integer(month).toString());
		jsonObject = getResult(params, url, jsonObject);
		return jsonObject;
	}

	public String findPayrollItemByTypeWithPage(int slalaryTypeCourseAdditionRule, int teacherId, int month, Page page) {
		Map<String, String> params = Maps.newHashMap();

		params.put("teacherId", new Integer(teacherId).toString());
		params.put("pageNo",  new Integer(page.getPageNo()).toString());
		params.put("itemType", new Integer(slalaryTypeCourseAdditionRule).toString());
		params.put("month", new Integer(month).toString());
		if (page.getPageSize() != 0) {
			params.put("pageSize",  new Integer(page.getPageSize()).toString());
		} else {
			params.put("pageSize", "15");
		}
		String url = new StringBuilder(super.serverAddress).append("/public/payroll/findPayrollItemByTypeWithPage")
				.toString();
		HttpResult result = WebUtils.post(url, params);
		String resultString = null;
		if (HttpResult.STATUS_SUCCESS.equals(result.getStatus())) {
			resultString = result.getResponse().toString();
		}
		return resultString;
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
