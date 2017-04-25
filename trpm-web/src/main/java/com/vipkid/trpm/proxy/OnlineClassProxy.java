package com.vipkid.trpm.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vipkid.http.utils.HttpClientUtils;
import com.vipkid.http.utils.JacksonUtils;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.security.AppContext;
import com.vipkid.trpm.constant.ApplicationConstant;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.community.http.client.HttpClientProxy;
import org.community.tools.JsonTools;
import org.community.tools.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        PRACTICUM,
        MAJOR,
        OPEN
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
    public static Map<String,Object> generateRoomEnterUrl(String userId, String realName, String roomId,RoomRole userRole,String supplierCode,long onlineClassId,ClassType type) {
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
            return ReturnMapUtils.returnFail("fail to get url: classroom ,the classroom is empty");
        }

        String refererKey = String.format(ApplicationConstant.HEADER_REFERER,Thread.currentThread().getId());
        String referer = (String)AppContext.get(refererKey);
        header.put("Referer",referer);

        String requestUrl = getHttpUrl() + "/api/service/private/supplier/getOnlineClassRoomURL";
        logger.info("用户Id:【{}】以【{}】角色,尝试进入onlineClassId:【{}】的教室，classType:【{}】,supplierCode:【{}】", userId,userRole.toString(),onlineClassId,type.toString(),supplierCode);
        String responseBody = HttpClientProxy.get(requestUrl, params, header);
        logger.info("Request after: responseBody=" + responseBody);
        if (StringUtils.isBlank(responseBody)) {
            logger.warn("用户Id:【{}】以【{}】角色,进入onlineClassId:【{}】的教室失败1，classType:【{}】,supplierCode:【{}】,原因：接口 "+requestUrl + " 返回为空。", userId,userRole.toString(),onlineClassId,type.toString(),supplierCode);
            return ReturnMapUtils.returnFail("Failed to get classroom "+roomId+"'s url");
        }
        JsonNode resultJson = null;
        try{
            resultJson = JsonTools.readValue(responseBody);
        }catch(Exception ex){
            logger.warn("用户Id:【{}】以【{}】角色,进入onlineClassId:【{}】的教室失败2，classType:【{}】,supplierCode:【{}】,原因：接口 "+requestUrl + " 返回Json字符串格式不对。", userId,userRole.toString(),onlineClassId,type.toString(),supplierCode);
            return ReturnMapUtils.returnFail(responseBody,ex);
        }
        logger.info("Request after =" + JsonTools.getJson(resultJson));
        JsonNode jsonURL = resultJson.get("url");
        if (jsonURL == null) {
            logger.warn("用户Id:【{}】以【{}】角色,进入onlineClassId:【{}】的教室失败3，classType:【{}】,supplierCode:【{}】,原因：接口 "+requestUrl + " 返回Json字符串没有url字段。", userId,userRole.toString(),onlineClassId,type.toString(),supplierCode);
            return ReturnMapUtils.returnFail("Not have class room url,you should checke request param ok!","url is:"+jsonURL);
        }
        logger.info("用户Id:【{}】以【{}】角色,进入onlineClassId:【{}】的教室成功，classType:【{}】,supplierCode:【{}】,教室Url:"+jsonURL.asText(), userId,userRole.toString(),onlineClassId,type.toString(),supplierCode);
        Map<String,Object> resultMap = Maps.newHashMap();
        resultMap.put("url", jsonURL.asText());
        return ReturnMapUtils.returnSuccess(resultMap);
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
            logger.warn("用户Id:【{}】,Booked onlineClassId:【{}】失败1,classType:【{}】,classTime:【{}】,原因：接口 "+requestUrl + " 返回为空。", userId,onlineClassId,type,scheduledDateTime);
            return ReturnMapUtils.returnFail("Booking failed! Please try again.");
        } else if (responseBody.indexOf("Success") > 0) {
            logger.info("用户Id:【{}】,Booked onlineClassId:【{}】成功,classType:【{}】,classTime:【{}】", userId,onlineClassId,type,scheduledDateTime);
            return ReturnMapUtils.returnSuccess();
        } else {
            logger.warn("用户Id:【{}】,Booked onlineClassId:【{}】失败2,classType:【{}】,classTime:【{}】,原因：接口 "+requestUrl + " 未知【"+responseBody+"】", userId,onlineClassId,type,scheduledDateTime);
            return ReturnMapUtils.returnFail("Booking failed! Please check if you have already scheduled.");
        }
    }

    public static Map<String,Object> doCreateInterview(long userId, long scheduledDateTime){

        String requestJson = "{\"scheduledDateTime\":" + scheduledDateTime + ",\"class_type\":3,\"status\":\"OPEN\",\"maxStudentNumber\":1,\"minStudentNumber\":1,\"teacher\":{\"id\":4600103},\"course\":{\"id\":211702},\"lesson\":{\"id\":211705,\"name\":\"Recruitment Lesson\"}}";

        Map<String, String>requestHeader = Maps.newHashMap();
        String author = "TEACHER " + userId;
        requestHeader.put("Authorization", author + " " + DigestUtils.md5Hex(author));

        String requestUrl=getHttpUrl()+"/api/service/private/onlineClasses";
        String responseBody = HttpClientUtils.post(requestUrl, requestJson, null, requestHeader);
        logger.info("doCreateInterview MESSAGE : " + responseBody);
        if (StringUtils.isBlank(responseBody)) {
            logger.warn("用户Id:【{}】,doCreateInterview 失败1,classTime:【{}】,原因：接口 "+requestUrl + " 返回为空。", userId,scheduledDateTime);
            return ReturnMapUtils.returnFail("Booking failed! Please try again.");
        } else if (responseBody.startsWith("{")) {

            Map ret = JacksonUtils.toBean(responseBody, Map.class);

            if (ret.get("id") == null || Long.parseLong(ret.get("id").toString()) <= 0) {
                logger.warn("用户Id:【{}】,doCreateInterview 失败2,classTime:【{}】,原因：接口 "+requestUrl + " 未知【"+responseBody+"】", userId,scheduledDateTime);
                return ReturnMapUtils.returnFail("Booking failed! Please check if you have already scheduled.");
            }

            logger.info("用户Id:【{}】,doCreateInterview onlineClassId:【{}】成功,classTime:【{}】", userId, ret.get("id"), scheduledDateTime);
            return ReturnMapUtils.returnSuccess(ret);
        } else {
            logger.warn("用户Id:【{}】,doCreateInterview 失败3,classTime:【{}】,原因：接口 "+requestUrl + " 未知【"+responseBody+"】", userId,scheduledDateTime);
            return ReturnMapUtils.returnFail("Booking failed! Please check if you have already scheduled.");
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
            logger.warn("用户Id:【{}】,Cancel onlineClassId:【{}】失败1,classType:【{}】,原因：接口 "+requestUrl + " 返回空白。", userId,onlineClassId,type);
            return ReturnMapUtils.returnFail("Cancel failed, Please try again later !");
        } else if (responseBody.indexOf("Success") > 0) {
            logger.info("用户Id:【{}】,Cancel onlineClassId:【{}】成功,classType:【{}】", userId,onlineClassId,type);
            return ReturnMapUtils.returnSuccess();
        } else if (responseBody.indexOf("628") > 0) {
            logger.warn("用户Id:【{}】,Cancel onlineClassId:【{}】失败2,classType:【{}】,原因：接口 "+requestUrl + " 628错误。", userId,onlineClassId,type);
            return ReturnMapUtils.returnFail("Sorry, you can't cancel twice in less than 5 minutes. Please try again later.");
        } else {
            logger.warn("用户Id:【{}】,Cancel onlineClassId:【{}】失败3,classType:【{}】,原因：接口 "+requestUrl + " 未知【"+responseBody+"】", userId,onlineClassId,type);
            return ReturnMapUtils.returnFail("Cancel failed, Please try again later!");
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
