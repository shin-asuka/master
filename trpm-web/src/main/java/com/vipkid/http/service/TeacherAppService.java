/**
 *
 */
package com.vipkid.http.service;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.vipkid.trpm.entity.teachercomment.StudentAbilityLevelRule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.google.common.collect.Lists;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.entity.TeacherNationalityCode;

/**
 *
 * @author zouqinghua
 * @date 2016年7月15日  下午8:50:17
 *
 */
public class TeacherAppService extends HttpBaseService {

	private static final Logger logger = LoggerFactory.getLogger(TeacherAppService.class);

	public List<TeacherNationalityCode> getAllNationCodes() {

		String url = new StringBuilder(super.serverAddress)
				.append("/api/app/teacher/nationalCodes").toString();
		logger.info("httpGet getAllNationCodes , url = {}", url);
		List<TeacherNationalityCode> list = null;
		try {
			String data = WebUtils.simpleGet(url);
			if (data!=null) {
				String datas = getData(data);
				list = JsonUtils.toBeanList(datas, TeacherNationalityCode.class);
			}
		} catch (Exception e) {
			logger.error("getAllNationCodes error ",e);
			e.printStackTrace();
		}

		if(list == null){
			list = Lists.newArrayList();
		}
		return list;
	}

	public List<TeacherLocation> getCountryList() {
		String url = new StringBuilder(super.serverAddress)
				.append("/api/app/info/countries").toString();
		logger.info("httpGet getCountryList ,  url = {}", url);
		List<TeacherLocation> list = null;
		try {
			String data = WebUtils.simpleGet(url);
			if (data!=null) {
				String datas = getData(data);
				list = JsonUtils.toBeanList(datas, TeacherLocation.class);
			}
		} catch (Exception e) {
			logger.error("getCountryList error",e);
		}

		if(list == null){
			list = Lists.newArrayList();
		}
		return list;
	}

	public List<TeacherLocation> getStateList(Integer countryId) {
		String url = new StringBuilder(super.serverAddress)
				.append("/api/app/info/states").toString();
		//url = new StringBuilder("http://a6-teacher-rest.vipkid.com.cn").append("/api/app/info/states").toString();

		url +="?countryId="+countryId;

		logger.info("httpGet getStateList ,  url = {}", url);
		List<TeacherLocation> list = null;
		try {
			String data = WebUtils.simpleGet(url);
			if (data!=null) {
				String datas = getData(data);
				list = JsonUtils.toBeanList(datas, TeacherLocation.class);
			}
		} catch (Exception e) {
			logger.error("getStateList error",e);
		}

		if(list == null){
			list = Lists.newArrayList();
		}
		return list;
	}
	public List<StudentAbilityLevelRule> findlevelAndUnits() {

		String url = new StringBuilder(super.serverAddress)
				.append("/api/app/info/levelAndUnits").toString();
		logger.info("httpGet levelAndUnits ,  url = {}", url);
		List<StudentAbilityLevelRule> list = null;
		try {
			String data = WebUtils.simpleGet(url);
			if (data!=null) {
				String datas = getData(data);
				list = JsonUtils.toBeanList(datas, StudentAbilityLevelRule.class);
			}
		} catch (Exception e) {
			logger.error("levelAndUnits error",e);
		}

		if(list == null){
			list = Lists.newArrayList();
		}
		return list;
	}

	public List<TeacherLocation> getCityList(Integer stateId) {
		String url = new StringBuilder(super.serverAddress)
				.append("/api/app/info/cities").toString();
		//url = new StringBuilder("http://a6-teacher-rest.vipkid.com.cn").append("/api/app/info/cities").toString();

		url +="?stateId="+stateId;

		logger.info("httpGet getCityList ,  url = {}", url);
		List<TeacherLocation> list = null;
		try {
			String data = WebUtils.simpleGet(url);
			if (data!=null) {
				String datas = getData(data);
				list = JsonUtils.toBeanList(datas, TeacherLocation.class);
			}
		} catch (Exception e) {
			logger.error("getCityList error",e);
		}

		if(list == null){
			list = Lists.newArrayList();
		}
		return list;
	}

	public String getData(String respone){
		String data = null;
		if(StringUtils.isNotBlank(respone)){
			JsonNode json = JsonUtils.parseObject(respone);
			if(json!=null){
				JsonNode dataObj = json.get("data");
				if(dataObj != null ){
					data = dataObj.toString();
				}
			}
		}
		return data;
	}

}
