/**
 *
 */
package com.vipkid.http.utils;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.util.Lists;
import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zouqinghua
 * @date 2016年4月9日 下午6:01:54
 *
 */
@Deprecated
public class JsonUtils {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static ObjectMapper mapper = new ObjectMapper();



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


	@Deprecated
	public static <T> List<T> toBeanList(Object objects, Class<T> clazz){
		List<T> list = Lists.newArrayList();

		if (objects ==null || clazz == null) {
			return list;
		}

		if(objects instanceof  String){
			list = readJson((String) objects, new TypeReference<List<T>>() {});
		}else{
			list = readJson(toJSONString(objects),new TypeReference<List<T>>() {});
		}
		return list;
	}






}
