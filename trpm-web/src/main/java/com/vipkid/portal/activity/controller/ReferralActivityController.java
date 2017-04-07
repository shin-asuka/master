package com.vipkid.portal.activity.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vipkid.enums.ShareActivityExamEnum.StatusEnum;
import com.vipkid.portal.activity.dto.ClickHandleDto;
import com.vipkid.portal.activity.dto.ShareHandleDto;
import com.vipkid.portal.activity.dto.StartHandleDto;
import com.vipkid.portal.activity.dto.SubmitHandleDto;
import com.vipkid.portal.activity.service.ReferralActivityService;
import com.vipkid.portal.activity.vo.CheckResultVo;
import com.vipkid.portal.activity.vo.CheckUrlVo;
import com.vipkid.portal.activity.vo.StartHandleVo;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.teacher.tools.utils.IpUtils;
import com.vipkid.teacher.tools.utils.NumericUtils;
import com.vipkid.teacher.tools.utils.ReturnMapUtils;
import com.vipkid.trpm.entity.ShareActivityExam;
import com.vipkid.trpm.entity.ShareExamDetail;
import com.vipkid.trpm.entity.User;

@RestController
@RequestMapping("/portal/referral/activity")
public class ReferralActivityController extends RestfulController{

	@Autowired
	private ReferralActivityService referralActivityService;
	
    @Autowired
    private LoginService loginService;
	
	private final static Logger logger = LoggerFactory.getLogger(ReferralActivityController.class);
	
	/**
	 * 点击分享按钮时处理函数
	 * 1.linkSourceId(link来源) 从父链接中携带的参数 link源 1.APP  2.PC  3.广告入口
	 * 2.candidateKey(分享人) 如果老师点击share 则为老师ID 否则为开始测试接口中获得的candidateKey(32位的UUID)
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/share", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> feacShareHandle(HttpServletRequest request, HttpServletResponse response, @RequestBody ShareHandleDto bean){		
		try{
        	Map<String,Object> resultMap = Maps.newHashMap();
	    	//1.参数校验
	    	resultMap = checkParmar(bean, response);
	    	if(MapUtils.isNotEmpty(resultMap)){
	    		return resultMap;
	    	}
	    	//逻辑开始
			if(StringUtils.isNumeric(bean.getCandidateKey())){
				//老师分享
				logger.info("识别为Teacher分享");
				User user = this.loginService.getUser();
				if(NumericUtils.isNotNull(user) && StringUtils.equals(user.getId()+"", bean.getCandidateKey())){
					resultMap = this.referralActivityService.updateTeacherShare(bean.getCandidateKey(), IpUtils.getIpAddress(request), bean.getLinkSourceId(),bean.getActivityExamId());
				}else{
					response.setStatus(HttpStatus.FORBIDDEN.value());
					return ApiResponseUtils.buildErrorResp(-5, "不是当前登陆老师的ID，不能分享。");
				}
			}else{
				//candidate 分享
				logger.info("识别为非Teacher分享");
				resultMap = this.referralActivityService.updateCandidateShare(bean.getCandidateKey(), IpUtils.getIpAddress(request), bean.getLinkSourceId(),bean.getActivityExamId());
			}
			if(ReturnMapUtils.isSuccess(resultMap)){
				return ApiResponseUtils.buildSuccessDataResp(resultMap.get("data"));
			}else{
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return ApiResponseUtils.buildErrorResp(-3, resultMap.get("info")+"");
			}
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
	 * 参与者点击分享者分享链接时接口
	 * 1 linkSourceId 该参数为link来源ID 默认值为 2
	 * 2.shareRecordId 分享记录ID (可选)
	 * @param request
	 * @param response
	 * @param shareRecordId
	 * @return
	 */
	@RequestMapping(value = "/click", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> clickHandle(HttpServletRequest request, HttpServletResponse response,@RequestBody ClickHandleDto bean){		
		try{
        	Map<String,Object> resultMap = Maps.newHashMap();
	    	//1.参数校验
	    	resultMap = checkParmar(bean, response);
	    	if(MapUtils.isNotEmpty(resultMap)){
	    		return resultMap;
	    	}
			if(NumericUtils.isNullOrZeor(bean.getShareRecordId())){
				//老师点击link 入口
				User user = this.loginService.getUser();
				if(NumericUtils.isNull(user)){
					response.setStatus(HttpStatus.FORBIDDEN.value());
					logger.warn("未登陆不能访问该链接");
					return ApiResponseUtils.buildErrorResp(-3, "未登陆不能访问该链接");
				}
				// 老师点击次数
				resultMap = this.referralActivityService.updateLinkSourceClick(bean.getLinkSourceId());
			}else{
				//分享后link被单击次数的更新
				resultMap = this.referralActivityService.updateShareRecordClick(bean.getLinkSourceId(), bean.getShareRecordId());
			}
			if(ReturnMapUtils.isFail(resultMap)){
				response.setStatus(HttpStatus.FORBIDDEN.value());
				logger.warn("未找到与匹配的分享记录:linkSourceId:{},shareRecordId:{}",bean.getLinkSourceId(), bean.getShareRecordId());
				return ApiResponseUtils.buildErrorResp(-4, "未找到与匹配的分享记录");
			}
			return ApiResponseUtils.buildSuccessDataResp(resultMap);
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
	 * 2.candidateKey (从考必须有，非从考可以不需要有)
	 * 如果candidateKey存在则认为是从新考试，获取考试JSON串，生成新的考试ID 不需要生成candidateKey
	 * 如果没有candidateKey，则认为是首次考试，获取考试JSON串，生成新的考试ID 生成candidateKey
	 * @param request
	 * @param response
	 * @param shareRecordId
	 * @return
	 */
	@RequestMapping(value = "/start", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> startHandle(HttpServletRequest request, HttpServletResponse response,@RequestBody StartHandleDto bean){		
		try{
        	Map<String,Object> resultMap = Maps.newHashMap();
	    	//1.参数校验
	    	resultMap = checkParmar(bean, response);
	    	if(MapUtils.isNotEmpty(resultMap)){
	    		return resultMap;
	    	}
	    	// 判断是否为Null
	    	User user = this.loginService.getUser();
	    	StartHandleVo beanVo = new StartHandleVo();
			if(NumericUtils.isNull(user)){
				//一般用户参与
				beanVo = this.referralActivityService.updateStartEaxm(bean.getShareRecordId(), bean.getCandidateKey(), IpUtils.getIpAddress(request), 1);
			}else{
				//老师参与 
				beanVo = this.referralActivityService.updateStartEaxmForTeacher(user.getId(), bean.getLinkSourceId(), IpUtils.getIpAddress(request), 1);
				if(beanVo == null){
					return ApiResponseUtils.buildErrorResp(-4, "你有未完成的考试记录,请完成后再继续。");
				}
			}
			return ApiResponseUtils.buildSuccessDataResp(beanVo);
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
	 * 1. 更新本题结果,如果没有答完成,则 插入下一道题的开始时间,
	 * 已经答题完成计算结果保存更新测试结束时间期间 插入时候需要
	 * 验证本次开始下一题是否存在,已经不在则不需要插入.
	 * 
	 * 1.activityExamId 考试ID
	 * 2.questionOrder 题目顺序
	 * 3.questionId 题目ID
	 * 4.questionResult 题目结果
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> submitHandle(HttpServletRequest request, HttpServletResponse response,@RequestBody SubmitHandleDto bean){		
		try{
        	Map<String,Object> resultMap = Maps.newHashMap();
	    	//1.参数校验
	    	resultMap = checkParmar(bean, response);
	    	if(MapUtils.isNotEmpty(resultMap)){
	    		return resultMap;
	    	}
	    	resultMap = this.referralActivityService.updateExamDetailResult(bean);
			if(ReturnMapUtils.isFail(resultMap)){
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return ApiResponseUtils.buildErrorResp(-2, resultMap.get("info")+"");
			}
			return ApiResponseUtils.buildSuccessDataResp(resultMap);
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
	 * 检查答题步骤，到了那一步(需要登陆后才能访问该接口)
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RestInterface
	@RequestMapping(value = "/checkResult", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> checkResult(HttpServletRequest request, HttpServletResponse response){		
		try{
			CheckResultVo beanVo = new CheckResultVo();
			String tokenString = (String)request.getAttribute(AUTOKEN);
			beanVo.setRequestType(this.referralActivityService.getRequestType(tokenString));
			beanVo.setCandidateKey(getTeacher(request).getId()+"");
			ShareActivityExam shareActivityExam = this.referralActivityService.checkTeacherExamStratus(getTeacher(request));
			if (NumericUtils.isNull(shareActivityExam)) {
				//表示从未考试
				logger.info("从没有考试");
				beanVo.setStatus(2);
			}else{
				beanVo.setActivityExamId(shareActivityExam.getId());
				beanVo.setStatus(shareActivityExam.getStatus());
				beanVo.setExamResult(shareActivityExam.getExamResult());
				//已经考试已经有结果不用走下一步
				if(shareActivityExam.getStatus() == StatusEnum.PENDING.val()){
					//已经考试，但没有结果 进入该逻辑，获取未完成的测试试题 
					ShareExamDetail shareExamDetail = this.referralActivityService.findPendingByExamId(shareActivityExam.getId());
					if(NumericUtils.isNull(shareExamDetail)){
						response.setStatus(HttpStatus.FORBIDDEN.value());
						return ApiResponseUtils.buildErrorResp(-2, "未找到未完成的测试试题:"+shareActivityExam.getId());
					}
					beanVo.setQuestionId(shareExamDetail.getQuestionId());
					beanVo.setQuestionIndex(shareExamDetail.getQuestionIndex());
				}
			}
			return ApiResponseUtils.buildSuccessDataResp(beanVo);
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
	 * 参数检查
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/checkUrl", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> checkUrl(HttpServletRequest request, HttpServletResponse response, @RequestBody ClickHandleDto bean){		
		try{
        	Map<String,Object> resultMap = Maps.newHashMap();
	    	//1.参数校验
	    	resultMap = checkParmar(bean, response);
	    	if(MapUtils.isNotEmpty(resultMap)){
	    		return resultMap;
	    	}
	    	if(NumericUtils.isNullOrZeor(bean.getShareRecordId())){
	    		//第一次 老师必须登陆
				User user = this.loginService.getUser();
				if(NumericUtils.isNull(user)){
					response.setStatus(HttpStatus.FORBIDDEN.value());
					return ApiResponseUtils.buildErrorResp(-3, "未登陆不能访问该链接");
				}
	    	}
	    	//校验数据
	    	CheckUrlVo beanVo = this.referralActivityService.checkUrl(bean);
	    	boolean checkResult = beanVo == null ? false : true;
			if(!checkResult){
	    		response.setStatus(HttpStatus.FORBIDDEN.value());
	    		logger.warn("参数不合法");
	    		return ApiResponseUtils.buildErrorResp(-2, "参数不合法",resultMap);
			}
			return ApiResponseUtils.buildSuccessDataResp(beanVo);
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
