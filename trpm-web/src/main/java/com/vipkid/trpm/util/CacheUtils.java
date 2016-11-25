package com.vipkid.trpm.util;

import java.util.UUID;

import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxmind.geoip2.record.Country;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.http.service.GeoIPService;
import com.vipkid.rest.utils.SpringContextHolder;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.entity.User;

/**
 * 缓存工具类
 */
public class CacheUtils {

	
	public static String getCoursesKey(long userId) {
		StringBuffer key = new StringBuffer();
		key.append("TRPM-COURSES-");
		key.append(userId);
		return key.toString();
	}
	
	
	public static String getTokenId(){
	    return UUID.randomUUID().toString();
	}

	public static String getUserTokenKey(String token){
		String key = null;
		if(StringUtils.isNotBlank(token)){
			key = ApplicationConstant.RedisConstants.TEACHER_TOKEN+"_"+token;
		}
		return key;
	}
	
}
