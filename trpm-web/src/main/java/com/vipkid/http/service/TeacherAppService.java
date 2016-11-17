/**
 * 
 */
package com.vipkid.http.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.http.vo.Announcement;
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
            	list = JsonUtils.toBeanList(data, TeacherNationalityCode.class);
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
            	list = JsonUtils.toBeanList(data, TeacherLocation.class);
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
		url +="?countryId="+countryId;
		
        logger.info("httpGet getStateList ,  url = {}", url);
        List<TeacherLocation> list = null;
        try {
        	String data = WebUtils.simpleGet(url);
            if (data!=null) {
            	list = JsonUtils.toBeanList(data, TeacherLocation.class);
            }
		} catch (Exception e) {
			logger.error("getStateList error",e);
		}
        
        if(list == null){
        	list = Lists.newArrayList();
        }
        return list;
	}

	public List<TeacherLocation> getCityList(Integer stateId) {
		String url = new StringBuilder(super.serverAddress)
                .append("/api/app/info/cities").toString();
		url +="?stateId="+stateId;
		
        logger.info("httpGet getCityList ,  url = {}", url);
        List<TeacherLocation> list = null;
        try {
        	String data = WebUtils.simpleGet(url);
            if (data!=null) {
            	list = JsonUtils.toBeanList(data, TeacherLocation.class);
            }
		} catch (Exception e) {
			logger.error("getCityList error",e);
		}
        
        if(list == null){
        	list = Lists.newArrayList();
        }
        return list;
	}
   
}
