/**
 * 
 */
package com.vipkid.http.utils;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/**
 * @author zouqinghua
 * @date 2016年4月9日 下午6:01:54
 *
 */
public class JsonUtils {

    public static String toJSONString(Object object) {
        String str = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            str = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
        	e.printStackTrace();
        }
        return str;
    }
    
    public static <T> T toBean(String json, Class<T> clazz) {
    	ObjectMapper mapper = new ObjectMapper();
    	T t = null;
		try {
			t = mapper.readValue(json, clazz);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return t;
    }
    
    public static <T> List<T> toBeanList(Object objects,Class<T> clazz){
    	List<T> list = null;
    	if(objects!=null){
    		String jsons = JSONArray.toJSONString(objects);
    		list = toBeanList(jsons, clazz);
    	}
    	return list;
    }
    
    public static <T> List<T> toBeanList(String jsons,Class<T> clazz){
    	List<T> list = Lists.newArrayList();
    	if(StringUtils.isNotBlank(jsons)){
    		JSONArray jsonArray = JSONArray.parseArray(jsons);
    		for (Object object : jsonArray) {
    			//String json = JSON.toJSONString(object);
    			//T t = toBean(json, clazz);
    			JSONObject json = (JSONObject) object;
				T t = JSON.toJavaObject(json, clazz);
    			list.add(t);
			}
    	}
    	return list;
    }

    public static JSONObject toJSONObject(Object object){
    	JSONObject jsonObject = null;
    	if(object!=null){
    		jsonObject = (JSONObject) JSON.toJSON(object);
    	}
    	return jsonObject;
    }
}
