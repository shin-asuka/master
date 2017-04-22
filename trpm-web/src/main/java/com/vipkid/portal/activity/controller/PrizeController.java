package com.vipkid.portal.activity.controller;


import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.http.service.ManageGatewayService;
import com.vipkid.http.service.PrizeService;
import com.vipkid.portal.activity.dto.AddTicketDto;
import com.vipkid.portal.activity.dto.DrawUserDto;
import com.vipkid.portal.activity.dto.ShareDrawResultDto;
import com.vipkid.portal.activity.service.PrizeHandleService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.portal.vo.StudentCommentVo;
import com.vipkid.rest.utils.ApiResponseUtils;

@RestController
@RequestMapping("/portal/activity/prize")
public class PrizeController extends RestfulController {
		
	private final static Logger logger = LoggerFactory.getLogger(PrizeController.class);
	
	@Autowired
	private PrizeService prizeService;
	
	@Autowired
	private ManageGatewayService manageGatewayService;
	
	@Autowired
	private PrizeHandleService prizeHandleService;
		
	/**
	 * 转盘模块接受插入抽奖卷逻辑
	 * 1.如果奖卷已经存在则返回你已经获得过本次抽奖卷
	 * 2.否则插入后告知 成功获得一张抽奖卷，并返回用户拥有的抽奖卷数量
	 * @param bean
	 * @return
	 */
	@RestInterface(lifeCycle = LifeCycle.REGULAR)
	@RequestMapping(value="/addTicket",method = RequestMethod.POST)
	public Map<String, Object> addTicket(HttpServletRequest request, HttpServletResponse response, @RequestBody AddTicketDto bean){
    	try{
			Map<String, Object> resultMap = Maps.newHashMap();
	    	//1.参数校验
	    	resultMap = checkParmar(bean, response);
	    	if(MapUtils.isNotEmpty(resultMap)){
	    		return resultMap;
	    	}
	    	Long teacherId = getUser(request).getId();
	    	Long onlineClassId = bean.getOnlineClassId();
	    	
	    	//活动时间判断
	    	Date date = new Date();
	    	if(!date.after(PrizeHandleService.START_DATE) && date.before(PrizeHandleService.END_DATE)){
	    		return ApiResponseUtils.buildErrorResp(-1, "不在活动范围内,不能插入抽奖卷:" + onlineClassId);
	    	}
	    	
	    	//课程时间判断
	    	boolean checkResult = this.prizeHandleService.checkOnlineClass(onlineClassId);
	    	if(!checkResult){
	    		return ApiResponseUtils.buildErrorResp(-1, "分享的课程不在活动要求范围内:" + onlineClassId);
	    	}
	    	
	    	
	    	String onlineClassIdStr = Long.toString(onlineClassId);
	    	//Check 五星好评
	    	List<StudentCommentVo> list = manageGatewayService.getStudentCommentListByBatch(onlineClassIdStr);
	    	if(CollectionUtils.isEmpty(list)){
	    		response.setStatus(HttpStatus.FORBIDDEN.value());
	    		return ApiResponseUtils.buildErrorResp(-2, "没有找到评论onlineClassId:"+onlineClassIdStr);
	    	}
	    	StudentCommentVo checkBean = list.get(0);
	    	if(checkBean.getRating() < 4.6){
	    		return ApiResponseUtils.buildErrorResp(-2, "获取抽奖卷失败，原因不是五星好评的Comment:"+onlineClassIdStr);	    		
	    	}
	    	
	    	if(checkBean.getTeacher_id() != teacherId.intValue()){
	    		response.setStatus(HttpStatus.FORBIDDEN.value());
	    		return ApiResponseUtils.buildErrorResp(-2, "获取抽奖卷失败，没有权限获取此抽奖卷:"+onlineClassIdStr);	    		
	    	}
	    	
	    	//2.获取抽奖卷逻辑
	    	JSONObject resultJson = this.prizeService.addTicket(request.getHeader(AUTOKEN), bean.getOnlineClassId());
			if(!resultJson.getBooleanValue("status")){
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return ApiResponseUtils.buildErrorResp(resultJson.getIntValue("code"), resultJson.getString("info"));
			}
			return ApiResponseUtils.buildSuccessDataResp(resultJson.get("data"));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误:"+e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常:"+e.getMessage());
        }
	}
	
	
	/**
	 * 转盘模块接受插入抽奖卷逻辑
	 * 1.如果奖卷已经存在则返回你已经获得过本次抽奖卷
	 * 2.否则插入后告知 成功获得一张抽奖卷，并返回用户拥有的抽奖卷数量
	 * @param bean
	 * @return
	 */
	@RestInterface(lifeCycle = LifeCycle.REGULAR)
	@RequestMapping(value="/luckDraw",method = RequestMethod.POST)
	public Map<String, Object> getLuckDraw(HttpServletRequest request, HttpServletResponse response){
    	try{
	    	//活动时间判断
	    	Date date = new Date();
	    	if(!date.after(PrizeHandleService.START_DATE) && date.before(PrizeHandleService.END_DATE)){
	    		return ApiResponseUtils.buildErrorResp(-1, "不在活动范围内,不能抽奖");
	    	}
			JSONObject resultJson = this.prizeService.luckDraw(request.getHeader(AUTOKEN));
			if(!resultJson.getBooleanValue("status")){
				return ApiResponseUtils.buildErrorResp(resultJson.getIntValue("code"), resultJson.getString("info"));
			}
			return ApiResponseUtils.buildSuccessDataResp(resultJson.get("data"));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误:"+e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常:"+e.getMessage());
        }
	}
	
	
	/**
	 * 获取全站最新20条中奖信息
	 * @param bean
	 * @return
	 */
	@RestInterface(lifeCycle = LifeCycle.REGULAR)
	@RequestMapping(value="/drawlist",method = RequestMethod.GET)
	public Object drawListByAll(HttpServletRequest request, HttpServletResponse response){
    	try{
    		JSONObject resultJson = this.prizeService.findDrawListByAll(request.getHeader(AUTOKEN));
			if(!resultJson.getBooleanValue("status")){
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return ApiResponseUtils.buildErrorResp(resultJson.getIntValue("code"), resultJson.getString("info"));
			}
			return ApiResponseUtils.buildSuccessDataResp(prizeHandleService.findTeacherName(resultJson));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误:"+e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常:"+e.getMessage());
        }
	}
	
	
	/**
	 * 获取用户自己的抽奖记录
	 * @param bean
	 * @return
	 */
	@RestInterface(lifeCycle = LifeCycle.REGULAR)
	@RequestMapping(value="/ticketRecord",method = RequestMethod.POST)
	public Object ticketRecordByUser(HttpServletRequest request, HttpServletResponse response,@RequestBody DrawUserDto bean){
    	try{
			Map<String, Object> resultMap = Maps.newHashMap();
	    	//1.参数校验
	    	resultMap = checkParmar(bean, response);
	    	if(MapUtils.isNotEmpty(resultMap)){
	    		return resultMap;
	    	}
	    	JSONObject resultJson = this.prizeService.findTicketRecordUser(request.getHeader(AUTOKEN), bean.getPage(), bean.getPageSize());
			if(!resultJson.getBooleanValue("status")){
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return ApiResponseUtils.buildErrorResp(resultJson.getIntValue("code"), resultJson.getString("info"));
			}
			return ApiResponseUtils.buildSuccessDataResp(resultJson.get("data"));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误:"+e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常:"+e.getMessage());
        }
	}
	
	/**
	 * 获取用户自己的中奖记录
	 * @param bean
	 * @return
	 */
	@RestInterface(lifeCycle = LifeCycle.REGULAR)
	@RequestMapping(value="/drawRecord",method = RequestMethod.POST)
	public Object drawRecordByUser(HttpServletRequest request, HttpServletResponse response,@RequestBody DrawUserDto bean){
    	try{
			Map<String, Object> resultMap = Maps.newHashMap();
	    	//1.参数校验
	    	resultMap = checkParmar(bean, response);
	    	if(MapUtils.isNotEmpty(resultMap)){
	    		return resultMap;
	    	}
	    	//2.获取用户自己的抽奖记录
	    	JSONObject resultJson = this.prizeService.findDrawRecordListByUser(request.getHeader(AUTOKEN), bean.getPage(), bean.getPageSize());
			if(!resultJson.getBooleanValue("status")){
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return ApiResponseUtils.buildErrorResp(resultJson.getIntValue("code"), resultJson.getString("info"));
			}
			return ApiResponseUtils.buildSuccessDataResp(resultJson.get("data"));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误:"+e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常:"+e.getMessage());
        }
	}
	
	
	/**
	 * 获取用户自己的中奖记录
	 * @param bean
	 * @return
	 */
	@RestInterface(lifeCycle = LifeCycle.REGULAR)
	@RequestMapping(value="/shareDrawResult",method = RequestMethod.POST)
	public Object shareDrawResult(HttpServletRequest request, HttpServletResponse response, @RequestBody ShareDrawResultDto bean){
    	try{
			Map<String, Object> resultMap = Maps.newHashMap();
	    	//1.参数校验
	    	resultMap = checkParmar(bean, response);
	    	if(MapUtils.isNotEmpty(resultMap)){
	    		return resultMap;
	    	}
	    	//2.获取用户自己的抽奖记录
	    	JSONObject resultJson = this.prizeService.shareDrawResult(request.getHeader(AUTOKEN), bean.getDrawRecordId());
			if(!resultJson.getBooleanValue("status")){
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return ApiResponseUtils.buildErrorResp(resultJson.getIntValue("code"), resultJson.getString("info"));
			}
			return ApiResponseUtils.buildSuccessDataResp(resultJson.get("data"));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误:"+e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常:"+e.getMessage());
        }
	}
	
	
	@RequestMapping(value="/shareClick",method = RequestMethod.POST)
	public Object shareClick(HttpServletRequest request, HttpServletResponse response, @RequestBody ShareDrawResultDto bean){
    	try{
			Map<String, Object> resultMap = Maps.newHashMap();
	    	//1.参数校验
	    	resultMap = checkParmar(bean, response);
	    	if(MapUtils.isNotEmpty(resultMap)){
	    		return resultMap;
	    	}
	    	//2.获取用户自己的抽奖记录
	    	JSONObject resultJson = this.prizeService.shareClick(bean.getDrawRecordId());
			if(!resultJson.getBooleanValue("status")){
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return ApiResponseUtils.buildErrorResp(resultJson.getIntValue("code"), resultJson.getString("info"));
			}
			return ApiResponseUtils.buildSuccessDataResp(resultJson.get("data"));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误:"+e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常:"+e.getMessage());
        }
	}
	
	/**
	 * 获取用户剩余票量
	 * @param bean
	 * @return
	 */
	@RestInterface(lifeCycle = LifeCycle.REGULAR)
	@RequestMapping(value="/countTicket",method = RequestMethod.GET)
	public Object countTicket(HttpServletRequest request, HttpServletResponse response){
    	try{
    		JSONObject resultJson = this.prizeService.countTicket(request.getHeader(AUTOKEN));
			if(!resultJson.getBooleanValue("status")){
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return ApiResponseUtils.buildErrorResp(resultJson.getIntValue("code"), resultJson.getString("info"));
			}
			return ApiResponseUtils.buildSuccessDataResp(resultJson.get("data"));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误:"+e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常:"+e.getMessage());
        }
	}
	
}
