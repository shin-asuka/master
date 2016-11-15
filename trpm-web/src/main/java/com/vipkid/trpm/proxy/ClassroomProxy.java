package com.vipkid.trpm.proxy;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
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

    private static String getHttpUrl(){     
        return PropertyConfigurer.stringValue("http.appServer");
    }
    
    /**
     * 用于从管理端获取教室URL
     * @param userId  从teacher中获取id
     * @param realName 从Teacher中获取
     * @param roomId 从onlineClass对象中获取
     * @param userRole Teacher,Student 招聘端
     * @param supplierCode 1 多贝 , 2 学点 从onlineClass对象中获取
     * @return   Map<String,Object>  教室URL
     */
    public static Map<String,Object> generateRoomEnterUrl(String userId, String realName, String roomId,RoomRole userRole,String supplierCode) {
        Map<String,Object> resultMap = Maps.newHashMap();
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
            resultMap.put("status", false);
            resultMap.put("info", "fail to get url: classroom ,the classroom is empty");
            logger.warn("fail to get url: classroom ,the classroom is empty");
            return resultMap;
        }
        String requestUrl = getHttpUrl() + "/api/service/private/supplier/getOnlineClassRoomURL";
        logger.info("Request before: URL:{}; params:{},roomId:{}",requestUrl,JsonTools.getJson(params),roomId);
        String responseBody = HttpClientProxy.get(requestUrl, params, header);
        logger.info("Request after: responseBody=" + responseBody);
        if (StringUtils.isBlank(responseBody)) {
            resultMap.put("status", false);
            resultMap.put("info", "Failed to get classroom "+roomId+"'s url");
            logger.error("Failed to get classroom {}'s url", roomId);
            return resultMap;
        }
        JsonNode result = null;
        try{
            result = JsonTools.readValue(responseBody);
        }catch(Exception ex){
            resultMap.put("status", false); 
            resultMap.put("info", responseBody);
            logger.error(ex.getMessage());
            return resultMap;
        }
        logger.info("Request after =" + JsonTools.getJson(result));
        JsonNode jsonURL = result.get("url");
        if (jsonURL == null) {
            resultMap.put("status", false);
            resultMap.put("info", "Not have class room url,you should checke request param ok!");
            logger.error("Not have class room url,you should checke request param ok!");
            return resultMap;
        }
        resultMap.put("status", true);
        resultMap.put("url", jsonURL.asText());
        return resultMap;
    }
    
    /**
     * BOOK ClASS
     * @param userId
     * @param onlineClassId
     * @param type
     * @param scheduledDateTime
     * @return    
     * Map<String,Object>
     */
    public static Map<String,Object> doBookRecruitment(long userId,long onlineClassId,String type,String scheduledDateTime){
        Map<String,Object> result = Maps.newHashMap();
        Map<String, String> requestParams = Maps.newHashMap();             
        requestParams.put("onlineClassId", String.valueOf(onlineClassId));
        requestParams.put("courseType", type);
        requestParams.put("teacherId", String.valueOf(userId));
        requestParams.put("scheduleDateTime", scheduledDateTime);
        
        Map<String, String>requestHeader = new HashMap<String, String>();
        String author = "TEACHER " + userId;
        requestHeader.put("Authorization", author + " " + DigestUtils.md5Hex(author));
        
        String requestUrl=getHttpUrl()+"/recruitment/bookOnlineClassforRecruitment";        
        String responseBody = HttpClientProxy.post(requestUrl, requestParams, requestHeader);
        logger.info("BOOK CLASS MESSAGE : " + responseBody);
        if (StringUtils.isBlank(responseBody)) {
            result.put("status", false);
            result.put("info", "Request failed, please try again later !");
        } else if (responseBody.indexOf("Success") > 0) {
            result.put("status", true);
        } else {
            result.put("status", false);
            result.put("info", "Request failed, Please try again later!");
        }
        return result;
    }

}
