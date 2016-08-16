package com.vipkid.trpm.proxy;

/**
 * Created by killpanda on 26/10/15.
 */

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.community.config.PropertyConfigurer;
import org.community.http.client.HttpClientProxy;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

public class ClassroomProxy {

	private static final Logger logger = LoggerFactory.getLogger(ClassroomProxy.class);

	public static final String ROLE_TEACHER = "TEACHER";
	public static final String ROLE_STUDENT = "STUDENT";

	private static final String REQUEST_URL = PropertyConfigurer
			.stringValue("classroom.url.request.api");

	public static String generateRoomEnterUrl(String uid, String nickname, String roomId,
			String userRole, String supplierCode) {
		Map<String, String> params = Maps.newHashMap();
		params.put("userId", uid);
		params.put("name", nickname);
		params.put("roomId", roomId);
		params.put("role", userRole);
		params.put("supplierCode", supplierCode);

		Map<String, String> requestHeader = new HashMap<String, String>();
		String t = "TEACHER " + uid;
		requestHeader.put("Authorization", t + " " + DigestUtils.md5Hex(t));
		if (roomId == null) {
			logger.error("fail to get url: classroom ,the classroom is empty");
			return "";
		}
		logger.info("Request before: URL=" + REQUEST_URL + "; params:"+JsonTools.getJson(params)+";roomId="+roomId);
		String responseBody = HttpClientProxy.get(REQUEST_URL, params, requestHeader);
		logger.info("Request after: responseBody=" + responseBody);
		if (null == responseBody || "".equals(responseBody)) {
			logger.error("Failed to get classroom {}'s url", roomId);
			return null;
		}
		JsonNode result = JsonTools.readValue(responseBody);
		logger.info("Request after =" + JsonTools.getJson(result));
		JsonNode jsonURL = result.get("url");
		if(jsonURL != null){
		    return jsonURL.asText();
		}
		logger.info("没有获取到教室url,检查请求地址及参数是否正常");
		return "";
	}

}
