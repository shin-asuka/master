package com.vipkid.rest.utils;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.file.utils.StringUtils;
import com.vipkid.rest.security.AppContext;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.User;

public class UserUtils {

	private static Logger logger = LoggerFactory.getLogger(UserUtils.class);
			
	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	
	public static User getUser(HttpServletRequest request){
		User user = null;
		String token = AppContext.getToken(request);
		
		logger.info("Auth url = {} , token = {}",request.getRequestURI(),token);
		if(StringUtils.isNotBlank(token)){
			user = userDao.findByToken(token);
		}
		if(user == null){
			user = new User();
			user.setId(0);
			user.setName("");
			user.setUsername("");
		}
		return user;
	}

}
