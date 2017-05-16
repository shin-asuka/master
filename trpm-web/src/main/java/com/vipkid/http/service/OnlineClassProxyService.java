package com.vipkid.http.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.util.Maps;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.security.AppContext;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.proxy.OnlineClassProxy;
import com.vipkid.trpm.proxy.OnlineClassProxy.ClassType;
import com.vipkid.trpm.proxy.OnlineClassProxy.RoomRole;
import com.vipkid.trpm.service.portal.OnlineClassService;


public class OnlineClassProxyService extends HttpBaseService{

	private static final Logger logger = LoggerFactory.getLogger(OnlineClassProxyService.class);
	
	@Autowired
	private OnlineClassService onlineClassService;
	
	@Autowired
	private OnlineClassProxyService onlineClassProxyService;
	
	public Map<String,Object> generateRoomEnterUrl(String userId, String realName, String roomId,RoomRole userRole,String supplierCode,long onlineClassId,ClassType type) {
		String requestUrl = new StringBuilder(super.serverAddress).append("/api/invoker/service/classroom/").append(roomId).toString();
        if (StringUtils.isBlank(roomId)) {
            return ReturnMapUtils.returnFail("fail to get url: classroom ,the classroom is empty");
        }
		Map<String, Object> params = Maps.newHashMap();
		params.put("userId", userId);
		params.put("name", realName);
		params.put("role", userRole.name());
		params.put("supplierCode", supplierCode);
		if(RoomRole.STUDENT == userRole){
			OnlineClass onlineClass = onlineClassService.getOnlineClassById(onlineClassId);
			
			params.put("scheduledDateTime", onlineClass.getScheduledDateTime().getTime());
		}
		
        Map<String, String> header = new HashMap<String, String>();
        String author = "TEACHER " + userId;
        header.put("Authorization", author + " " + DigestUtils.md5Hex(author));
        
        String refererKey = String.format(ApplicationConstant.HEADER_REFERER,Thread.currentThread().getId());
        String referer = (String)AppContext.get(refererKey);
        header.put("Referer",referer);
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("【getClassroomUrl】Invocation 用户Id:【{}】以【{}】角色,尝试进入onlineClassId:【{}】的教室，classType:【{}】,supplierCode:【{}】", userId,userRole.toString(),onlineClassId,type.toString(),supplierCode);
        
		String responseBody = WebUtils.simpleGet(requestUrl,params,header);
        JsonNode resultJson = null;
        
        stopWatch.stop();
        logger.info("【getClassroomUrl】Invocation Use time:"+(stopWatch.getNanoTime()/(1000*1000))+"毫秒,用户Id:【{}】以【{}】角色,进入onlineClassId:【{}】的教室",userId,userRole.toString(),onlineClassId);
        try{
            resultJson = JsonTools.readValue(responseBody);
        }catch(Exception ex){
            logger.error("【getClassroomUrl】Invocation 用户Id:【{}】以【{}】角色,进入onlineClassId:【{}】的教室失败2，正在尝试通过Appserver 获取教室。 classType:【{}】,supplierCode:【{}】,原因：接口 "+requestUrl + " 返回Json字符串格式不对。json:"+responseBody, userId,userRole.toString(),onlineClassId,type.toString(),supplierCode);
            return OnlineClassProxy.generateRoomEnterUrl(userId, realName, roomId, userRole, supplierCode, onlineClassId, type);
        }
        logger.info("【getClassroomUrl】Invocation Request after =" + JsonTools.getJson(resultJson));
        JsonNode jsonURL = resultJson.get("url");
        if (jsonURL == null) {
        	logger.error("【getClassroomUrl】Invocation 未获取到教室,正在尝试通过Appserver 获取教室:"+requestUrl + ",参数:" + JsonTools.getJson(params)+ ",Header:" + JsonTools.getJson(header));
            return OnlineClassProxy.generateRoomEnterUrl(userId, realName, roomId, userRole, supplierCode, onlineClassId, type);
        }
        logger.info("【getClassroomUrl】Invocation 用户Id:【{}】以【{}】角色,进入onlineClassId:【{}】的教室成功，classType:【{}】,supplierCode:【{}】,教室Url:"+jsonURL.asText(), userId,userRole.toString(),onlineClassId,type.toString(),supplierCode);
        Map<String,Object> resultMap = Maps.newHashMap();
        resultMap.put("url", jsonURL.asText());
        return ReturnMapUtils.returnSuccess(resultMap);
	}
}
