package com.vipkid.trpm.util;

import java.util.UUID;

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

}
