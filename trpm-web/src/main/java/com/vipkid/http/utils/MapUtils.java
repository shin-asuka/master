/**
 * 
 */
package com.vipkid.http.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vipkid.neo.utils.DateTimeUtils;



/**
 * @author zouqinghua
 * @date 2016年5月4日  上午11:55:45
 *
 */
public class MapUtils {

	private static Logger logger = LoggerFactory.getLogger(MapUtils.class);

	public static Map<String, Object> parseJsonToMap(JSONObject jsonObject){
		return parseJsonToMap(jsonObject, null);
	}
	
	public static Map<String, Object> parseJsonToMap(JSONObject jsonObject,String keyPre){
		Map<String, Object> map = Maps.newHashMap();
		if(StringUtils.isNoneBlank(keyPre)){
			keyPre += ".";
		}else{
			keyPre ="";
		}
		if(jsonObject!=null && !jsonObject.isEmpty()){
			Set<String> keys = jsonObject.keySet();
			for (String key : keys) {
				Object object = jsonObject.get(key);
				key = keyPre+key;
				if(object instanceof String || object instanceof Integer){
					map.put(key,object);
				}else if(object instanceof JSONArray){
					map.putAll(parseJsonToMap((JSONArray)object,key));
				}else if(object instanceof JSONObject){
					map.putAll(parseJsonToMap((JSONObject)object,key));
				}else{
					map.put(key,object);
				}
				
			}
		}
		
		return map;
	}
	
	public static Map<String, Object> parseJsonToMap(JSONArray jsonArray,String keyPre){
		Map<String, Object> map = Maps.newHashMap();
		if(keyPre == null){
			keyPre ="";
		}
		if(jsonArray!=null && !jsonArray.isEmpty()){
			Integer index = 0;
			for (Object object : jsonArray) {
				String key = keyPre+"["+index+"]";
				if(object instanceof String || object instanceof Integer){
					map.put(key,object);
				}if(object instanceof JSONArray){
					map.putAll(parseJsonToMap((JSONArray)object,key));
				}else if(object instanceof JSONObject){
					map.putAll(parseJsonToMap((JSONObject)object,key));
				}else{
					map.put(key,object);
				}
				index++;
			}
		}
		return map;
	}
}
