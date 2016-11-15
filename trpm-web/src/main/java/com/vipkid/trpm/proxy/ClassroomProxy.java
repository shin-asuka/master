package com.vipkid.trpm.proxy;

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
    
    public enum RoomRole{
        TEACHER,
        STUDENT
    }

    private static final String REQUEST_URL = PropertyConfigurer.stringValue("classroom.url.request.api");
    
    /**
     * 用于从管理端获取教室URL
     * @param userId  从teacher中获取id
     * @param realName 从Teacher中获取
     * @param roomId 从onlineClass对象中获取
     * @param userRole Teacher,Student 招聘端
     * @param supplierCode 1 多贝 , 2 学点 从onlineClass对象中获取
     * @return   String
     *              教室URL
     */
    public static String generateRoomEnterUrl(String userId, String realName, String roomId,RoomRole userRole,String supplierCode) {
        Map<String, String> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("name", realName);
        params.put("roomId", roomId);
        params.put("role", userRole.toString());
        params.put("supplierCode", supplierCode);
        
        Map<String, String> header = new HashMap<String, String>();
        String author = "TEACHER " + userId;
        header.put("Authorization", author + " " + DigestUtils.md5Hex(author));
        if (roomId == null) {
            logger.error("fail to get url: classroom ,the classroom is empty");
            return "";
        }
        logger.info("Request before: URL:{}; params:{},roomId:{}",REQUEST_URL,JsonTools.getJson(params),roomId);
        String responseBody = HttpClientProxy.get(REQUEST_URL, params, header);
        logger.info("Request after: responseBody=" + responseBody);
        if (null == responseBody || "".equals(responseBody)) {
            logger.error("Failed to get classroom {}'s url", roomId);
            return "";
        }
        JsonNode result = JsonTools.readValue(responseBody);
        logger.info("Request after =" + JsonTools.getJson(result));
        JsonNode jsonURL = result.get("url");
        if (jsonURL == null) {
            logger.error("没有获取到教室url,检查请求地址及参数是否正常");
            return "";
        }
        return jsonURL.asText();
    }

}
