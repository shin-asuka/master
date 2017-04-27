package com.vipkid.http.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.teacher.tools.utils.NumericUtils;
import com.vipkid.teacher.tools.utils.ReturnMapUtils;
import com.vipkid.teacher.tools.utils.conversion.JsonUtils;
import com.vipkid.trpm.constant.ApplicationConstant.RedisConstants;
import com.vipkid.trpm.proxy.RedisProxy;


/**
 * 抽奖转盘相关API 调用
 * @author zengweilong
 *
 */
public class PrizeService extends HttpBaseService {
	
	private final static Logger logger = LoggerFactory.getLogger(PrizeService.class);
	
	@Autowired
	private RedisProxy redisProxy;
	
	private final static String  URL_PREFIX = "api/prize/";

	/**
	 * 新增抽奖卷，每次五星好评中奖后会调用此接口，如果五星好评已经插入过则记录次数
	 * @param onlineClassId
	 * @param token
	 * @return
	 */
	public JSONObject addTicket(String token, Long onlineClassId){
		String urlString = new StringBuilder(super.serverAddress).append(URL_PREFIX).append("addTicket").toString();
		Map<String, Object> pramMap = Maps.newHashMap();
		pramMap.put("onlineClassId", onlineClassId);
		JSONObject reslultJson = this.getJsonResult(pramMap,urlString,token);
		return reslultJson;
	}
	
	/**
	 * 抽奖
	 * @param token
	 * @return
	 */
	public JSONObject luckDraw(String token){
		String urlString = new StringBuilder(super.serverAddress).append(URL_PREFIX).append("luckDraw").toString();
		Map<String, Object> pramMap = Maps.newHashMap();
		JSONObject reslultJson = this.getJsonResult(pramMap,urlString,token);
		return reslultJson;
	}
	
	/**
	 * 获取前面20条最新中奖信息数据
	 * @return
	 */
	public JSONObject findDrawListByAll(String token){
		String urlString = redisProxy.get(RedisConstants.TRPM_PRIZER_KEY);
		if(StringUtils.isBlank(urlString)){
			urlString = new StringBuilder(super.serverAddress).append(URL_PREFIX).append("drawlist").toString();
			Map<String, Object> pramMap = Maps.newHashMap();
			JSONObject reslultJson = this.getJsonResult(pramMap,urlString,token);
			if(NumericUtils.isNotNull(reslultJson)){
				urlString = reslultJson.toJSONString();
				redisProxy.set(RedisConstants.TRPM_PRIZER_KEY, urlString, RedisConstants.TRPM_PRIZER_TIME);
				return reslultJson;
			}
			logger.error("无法访问link:{}",urlString);
			return null;
		}else{
			JSONObject reslultJson = JSONObject.parseObject(urlString);		
			return reslultJson;
		}
	}
	
	/**
	 * 获取用户的抽奖记录
	 * @param token
	 * @param page 请求页
	 * @param pageSize 每页显示条数
	 * @return
	 */
	public JSONObject findTicketRecordUser(String token, int page, int pageSize){
		String urlString = new StringBuilder(super.serverAddress).append(URL_PREFIX).append("ticketRecord").toString();
		Map<String, Object> pramMap = Maps.newHashMap();
		pramMap.put("page", page);
		pramMap.put("pageSize", pageSize);
		JSONObject reslultJson = this.getJsonResult(pramMap,urlString,token);
		return reslultJson;
	}
	
	
	/**
	 * 获取用户的中奖记录
	 * @param token
	 * @return
	 */
	public JSONObject findDrawRecordListByUser(String token, int page, int pageSize){
		String urlString = new StringBuilder(super.serverAddress).append(URL_PREFIX).append("drawRecord").toString();
		Map<String, Object> pramMap = Maps.newHashMap();
		pramMap.put("page", page);
		pramMap.put("pageSize", pageSize);
		JSONObject reslultJson = this.getJsonResult(pramMap,urlString,token);
		return reslultJson;
	}
	
	
	/**
	 * 获取剩余抽奖卷
	 * @param token
	 * @return
	 */
	public JSONObject countTicket(String token){
		String urlString = new StringBuilder(super.serverAddress).append(URL_PREFIX).append("countTicket").toString();
		Map<String, Object> pramMap = Maps.newHashMap();
		JSONObject reslultJson = this.getJsonResult(pramMap,urlString,token);
		return reslultJson;
	}
	
	
	/**
	 * 分享中奖结果的 次数记录表
	 * @param token
	 * @param onlineClassId
	 * @return
	 */
	public JSONObject shareDrawResult(String token, Long drawRecordId){
		String urlString = new StringBuilder(super.serverAddress).append(URL_PREFIX).append("shareDrawResult").toString();
		Map<String, Object> pramMap = Maps.newHashMap();
		pramMap.put("drawRecordId", drawRecordId);
		JSONObject reslultJson = this.getJsonResult(pramMap,urlString,token);
		return reslultJson;
	}
	
	/**
	 * 对分享点击进行打点记录 通过ID
	 * @param drawRecordId
	 * @return
	 */
	public JSONObject shareClick(Long drawRecordId){
		String urlString = new StringBuilder(super.serverAddress).append(URL_PREFIX).append("shareClick").toString();
		Map<String, Object> pramMap = Maps.newHashMap();
		pramMap.put("drawRecordId", drawRecordId);
		JSONObject reslultJson = this.getJsonResult(pramMap,urlString,null);
		return reslultJson;
	}
	
	/**
	 * Json 带head Json 请求封装
	 * @param pramMap
	 * @param url
	 * @param token
	 * @return
	 */
	private JSONObject getJsonResult(Map<String, Object> pramMap, String url,String token) {
		String pramJson = JsonUtils.toJson(pramMap);
		JSONObject jsonObject = JSONObject.parseObject(pramJson);
		Map<String, String> heardMaps = Maps.newHashMap();
		if(StringUtils.isNotBlank(token)){
			heardMaps.put(RestfulController.AUTOKEN, token);
		}
		logger.info("开始发送请求:[" + url +"] 参数:"+ pramJson);
		String jsonString = WebUtils.postJSON(url,jsonObject,heardMaps);
		logger.info("完成请求:[" + url +"] 参数:"+ pramJson + "; 结果：" + jsonString);
		if(StringUtils.isBlank(jsonString)){
			jsonString = JsonUtils.toJson(ReturnMapUtils.returnFail(0, "teacher-activity 服务无法访问：URL=["+url+"]。"));
		}
 		JSONObject resultJson = JSONObject.parseObject(jsonString);		
		return resultJson;
	}

}
