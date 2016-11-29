package com.vipkid.trpm.proxy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.community.http.client.HttpClientProxy;
import org.community.tools.JsonTools;
import org.community.tools.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.vipkid.recruitment.utils.ResponseUtils;

public class OnlineClassProxy {

    private static final Logger logger = LoggerFactory.getLogger(OnlineClassProxy.class);
    
    /**
     * TEACHER:以老师的角色进入教室<br/>
     * STUDENT:以学生的角色进入教室
     * @author Along 
     *
     */
    public enum RoomRole{
        TEACHER,
        STUDENT
    }
    
    /**
     * TEACHER_RECRUITMENT:面试课程<br/>
     * PRACTICUM:实习课程
     * @author Along 
     *
     */
    public enum ClassType{
        TEACHER_RECRUITMENT,
        PRACTICUM
    }

    private static String getHttpUrl(){     
        return PropertyConfigurer.stringValue("http.appServer");
    }
    
    /**
     * 用于从管理端获取教室URL
     * @param userId  从teacher中获取id
     * @param realName 从Teacher中获取
     * @param roomId 从onlineClass对象中获取
     * @param userRole {@link RoomRole} Teacher,Student 招聘端
     * @param supplierCode 1 多贝 , 2 学点 从onlineClass对象中获取
     * @return   Map&lt;String,Object&gt;  教室URL
     */
    public static Map<String,Object> generateRoomEnterUrl(String userId, String realName, String roomId,RoomRole userRole,String supplierCode) {
        Map<String, String> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("name", realName);
        params.put("roomId", roomId);
        params.put("role", userRole.toString());
        params.put("supplierCode", supplierCode);
        
        Map<String, String> header = new HashMap<String, String>();
        String author = "TEACHER " + userId;
        header.put("Authorization", author + " " + DigestUtils.md5Hex(author));
        if (StringUtils.isBlank(roomId)) {
            return ResponseUtils.responseFail("fail to get url: classroom ,the classroom is empty", OnlineClassProxy.class);
        }
        String requestUrl = getHttpUrl() + "/api/service/private/supplier/getOnlineClassRoomURL";
        logger.info("Request before: URL:{}; params:{},roomId:{}",requestUrl,JsonTools.getJson(params),roomId);
        String responseBody = HttpClientProxy.get(requestUrl, params, header);
        logger.info("Request after: responseBody=" + responseBody);
        if (StringUtils.isBlank(responseBody)) {
            return ResponseUtils.responseFail("Failed to get classroom "+roomId+"'s url", OnlineClassProxy.class);
        }
        JsonNode resultJson = null;
        try{
            resultJson = JsonTools.readValue(responseBody);
        }catch(Exception ex){
            logger.error(ex.getMessage());
            return ResponseUtils.responseFail(responseBody, OnlineClassProxy.class);
        }
        logger.info("Request after =" + JsonTools.getJson(resultJson));
        JsonNode jsonURL = resultJson.get("url");
        if (jsonURL == null) {
            return ResponseUtils.responseFail("Not have class room url,you should checke request param ok!", OnlineClassProxy.class);
        }
        Map<String,Object> resultMap = Maps.newHashMap();
        resultMap.put("url", jsonURL.asText());
        return ResponseUtils.responseSuccess(resultMap);
    }
    
    /**
     * BOOK ClASS
     * @param userId
     * @param onlineClassId
     * @param {@link ClassType} type TEACHER_RECRUITMENT/PRACTICUM
     * @param scheduledDateTime
     * @return    
     * Map&lt;String,Object&gt;
     */
    public static Map<String,Object> doBookRecruitment(long userId,long onlineClassId,ClassType type,String scheduledDateTime){
        Map<String, String> requestParams = Maps.newHashMap();             
        requestParams.put("onlineClassId", String.valueOf(onlineClassId));
        requestParams.put("courseType", type.toString());
        requestParams.put("teacherId", String.valueOf(userId));
        requestParams.put("scheduleDateTime", scheduledDateTime);
        
        Map<String, String>requestHeader = new HashMap<String, String>();
        String author = "TEACHER " + userId;
        requestHeader.put("Authorization", author + " " + DigestUtils.md5Hex(author));
        
        String requestUrl=getHttpUrl()+"/recruitment/bookOnlineClassforRecruitment";        
        String responseBody = HttpClientProxy.post(requestUrl, requestParams, requestHeader);
        logger.info("BOOK CLASS MESSAGE : " + responseBody);
        if (StringUtils.isBlank(responseBody)) {
            return ResponseUtils.responseFail("Request failed, please try again later !", OnlineClassProxy.class);
        } else if (responseBody.indexOf("Success") > 0) {
            return ResponseUtils.responseSuccess();
        } else {
            return ResponseUtils.responseFail("Request failed, Please try again later!", OnlineClassProxy.class);
        }
    }
    
    /**
     * CANCEL ClASS
     * @param userId
     * @param onlineClassId
     * @param {@link ClassType} type TEACHER_RECRUITMENT/PRACTICUM
     * @return    
     * Map&lt;String,Object&gt;
     */
    public static Map<String,Object> doCancelRecruitement(long userId,long onlineClassId,ClassType type){
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("onlineClassId", String.valueOf(onlineClassId));
        requestParams.put("courseType", type.toString());
        requestParams.put("teacherId", String.valueOf(userId));
        Map<String, String> requestHeader = new HashMap<String, String>();
        
        String author = "TEACHER " + userId;
        requestHeader.put("Authorization", author + " " + DigestUtils.md5Hex(author));
        
        String requestUrl = getHttpUrl() + "/recruitment/cancelOnlineClassforRecruitment";
        String responseBody = HttpClientProxy.post(requestUrl, requestParams, requestHeader);
        
        logger.info("CANCEL CLASS MESSAGE : " + responseBody);
        if (StringUtils.isBlank(responseBody)) {
            return ResponseUtils.responseFail("Request failed, Please try again later !",OnlineClassProxy.class);
        } else if (responseBody.indexOf("Success") > 0) {
            return ResponseUtils.responseSuccess();
        } else if (responseBody.indexOf("628") > 0) {
            return ResponseUtils.responseFail("Sorry, you can't cancel again within 5 minutes. Try again later!",OnlineClassProxy.class);
        } else {
            return ResponseUtils.responseFail("Request failed, Please try again later!",OnlineClassProxy.class);
        }
    }

    public static List<String> get24HourClass (long teacherId, List<String> onlineClassIds) {
        Map<String, String> requestHeader = get24HourClassRequestHeader(teacherId);
        logger.info("Get 24Hour Request Header: {}",requestHeader.get("Authorization"));
        try {
            Map<String, String> requestParams = Maps.newHashMap();
            String value = onlineClassIds.stream().collect(Collectors.joining(","));
            requestParams.put("classIds", value);
            String requestUrl = getHttpUrl() + "/api/service/public/24HourClass/filterByClass";
            logger.info("Get 24Hour Request Url: {}", requestUrl);
            String responseBody = HttpClientProxy.get(requestUrl, requestParams, requestHeader);
            if (StringUtils.isBlank(responseBody)) {
                return Lists.newArrayList();
            }
            responseBody = StringTools.matchString(responseBody, "\\[(.*?)\\]", Pattern.CASE_INSENSITIVE, 1);
            return Arrays.asList(StringUtils.split(responseBody, ","));
        } catch (Exception e) {
            logger.error("HttpClientProxy.get24HourClass err: {}", e);
            return Lists.newArrayList();
        }
    }

    private static Map<String, String> get24HourClassRequestHeader(long teacherId) {
        String t = "TEACHER " + teacherId;
        Map<String, String> requestHeader = new HashMap<String, String>();
        requestHeader.put("Authorization", t + " " + org.apache.commons.codec.binary.Base64.encodeBase64String(DigestUtils.md5(t)));
        return requestHeader;
    }
}
