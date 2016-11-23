package com.vipkid.trpm.util;

import java.util.UUID;

import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.file.utils.StringUtils;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.entity.User;

/**
 * 缓存工具类
 */
public class CacheUtils {

	private final static Logger logger = LoggerFactory.getLogger(CacheUtils.class);
	
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
	
	/**
	 * 检查用户IP是否变化
	 * @param user
	 * @return
	 */
	public static Boolean checkUserIpChange(User user){
		if(!PropertyConfigurer.booleanValue("signup.checkIP")){
			return false; //跳过ip检查
		}
		Boolean isChange = true;
		if(user != null && StringUtils.isNotBlank(user.getIp())){
			String ip = IpUtils.getRequestRemoteIP();
	        String redisIp = user.getIp();
	        logger.info("检测用户IP地址  getUserIP user = {}, redisIp = {}, currentIp = {}",user.getId()+"|"+user.getUsername(),redisIp,ip);
	        if(StringUtils.isNotBlank(ip) && ip.equals(redisIp)){
	        	isChange = false;
	        }else{
	        	logger.info("用户IP地址发生变化, userIPChange user = {}, redisIp = {}, currentIp = {}",user.getId()+"|"+user.getUsername(),redisIp,ip);
	        }
		}
		return isChange;
	}
}
