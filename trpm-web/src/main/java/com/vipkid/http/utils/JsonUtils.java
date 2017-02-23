/**
 *
 */
package com.vipkid.http.utils;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zouqinghua
 * @date 2016年4月9日 下午6:01:54
 *
 */
public class JsonUtils {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static ObjectMapper mapper = new ObjectMapper();
	static{
		// 设置输出时包含属性的风格
		mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 禁止把POJO中值为null的字段映射到json字符串中
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES,true);
	}


	public static JSONObject toJSONObject(Object object){
		JSONObject jsonObject = null;
		if(object!=null){
			jsonObject = (JSONObject) JSON.toJSON(object);
		}
		return jsonObject;
	}

	public static JSONObject parseToJSONObject(String object){
		JSONObject jsonObject = null;
		if(object!=null){
			jsonObject = JSONObject.parseObject(object);
		}
		return jsonObject;
	}




	public static String toJSONString(Object object) {
		if (object == null) {
			return "";
		}
		try {
			return object instanceof String ? (String) object : mapper.writeValueAsString(object);
		} catch (Exception e) {
			String message = String.format("Object to jsonString error;object=%s", object.getClass());
			logger.error(message, e);
			return null;
		}
	}

	public static <T> T toBean(String json, Class<T> clazz) {
		if (StringUtils.isBlank(json) || clazz == null) {
			return null;
		}
		try {
			return clazz.equals(String.class) ? (T) json : mapper.readValue(json, clazz);
		} catch (Exception e) {
			String message = String.format("jsonString to Object error;jsonString=%s", json);
			logger.error(message, e);
			return null;
		}

	}


	/**
	 * 将json通过类型转换成对象
	 *
	 * @param json          json字符串
	 * @param typeReference 引用类型
	 * @return 返回对象
	 * @throws IOException
	 */
	public static <T> T readJson(String json, TypeReference<T> typeReference) {

		if (StringUtils.isBlank(json) || typeReference == null) {
			return null;
		}
		try {
			return (T) (typeReference.getType().equals(String.class) ? json : mapper.readValue(json, typeReference));
		} catch (Exception e) {
			String message = String.format("jsonString to Object error;jsonString=%s", json);
			logger.error(message, e);
			return null;
		}
	}




	public static JsonNode parseObject(String jsonStr) {
		try {
			return mapper.readTree(jsonStr);
		} catch (Exception e) {
			String message = String.format("jsonString to JsonNode error;jsonString=%s", jsonStr);
			logger.error(message, e);
			return null;
		}
	}




}
