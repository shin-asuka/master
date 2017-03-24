package com.vipkid.rest.activity.share;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Maps;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.activity.dto.ShareHandleDto;
import com.vipkid.rest.activity.dto.StartHandleDto;
import com.vipkid.rest.activity.dto.SubmitHandleDto;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;

@Controller
@RequestMapping("/portal/referral/activity")
public class ReferralActivityController extends RestfulController{

	
	private final static Logger logger = LoggerFactory.getLogger(ReferralActivityController.class);
	
	/**
	 * 点击分享时间处理函数
	 * 1.linkSourceId 从父链接中携带的参数 link源 1.APP  2.PC  3.广告入口
	 * 2.candidateKey 如果老师点击share 则为老师ID 否则为link中携带的参数
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/share", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> ShareHandle(HttpServletRequest request, HttpServletResponse response, @RequestBody ShareHandleDto bean){		
		try{
			Map<String,Object> result = Maps.newHashMap();
			
			
			return ApiResponseUtils.buildSuccessDataResp(result);
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
	 * 点击参与事件记录接口
	 * 1 linkSourceId 该参数为link来源ID 默认值为 2
	 * 2.shareRecordId 分享记录ID (可选)
	 * @param request
	 * @param response
	 * @param shareRecordId
	 * @return
	 */
	@RequestMapping(value = "/click", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> clickHandle(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value="linkSourceId",required=true,defaultValue="2")Integer linkSourceId, 
			@RequestParam(value="shareRecordId",required=false)Integer shareRecordId){		
		try{
			Map<String,Object> result = Maps.newHashMap();
			
			
			return ApiResponseUtils.buildSuccessDataResp(result);
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
	 * 开始答题事件记录及数据获取接口
	 * 1.shareRecordId
	 * 2.candidateKey (从考必须有，非从靠可以不需要有)
	 * 获取考试JSON串，新的考试ID
	 * @param request
	 * @param response
	 * @param shareRecordId
	 * @return
	 */
	@RequestMapping(value = "/start", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> startHandle(HttpServletRequest request, HttpServletResponse response,@RequestBody StartHandleDto bean){		
		try{
			Map<String,Object> result = Maps.newHashMap();
			
			
			return ApiResponseUtils.buildSuccessDataResp(result);
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
	 * 完成答题结果提交接口
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> submitHandle(HttpServletRequest request, HttpServletResponse response,@RequestBody SubmitHandleDto bean){		
		try{
			Map<String,Object> result = Maps.newHashMap();
			
			
			return ApiResponseUtils.buildSuccessDataResp(result);
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
	 * 完成答题结果提交接口(需要登陆后才能访问该接口)
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RestInterface
	@RequestMapping(value = "/checkResult", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> checkResult(HttpServletRequest request, HttpServletResponse response){		
		try{
			Map<String,Object> result = Maps.newHashMap();
			
			
			return ApiResponseUtils.buildSuccessDataResp(result);
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
