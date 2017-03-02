package com.vipkid.portal.classroom.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vipkid.dataSource.annotation.Slave;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.portal.classroom.model.PeCommentsVo;
import com.vipkid.portal.classroom.model.PeSupervisorCommentsVo;
import com.vipkid.portal.classroom.service.PracticumFeedbackService;
import com.vipkid.recruitment.event.AuditEvent;
import com.vipkid.recruitment.event.AuditEventHandler;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.EvaluationService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.trpm.entity.DemoReport;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;

@RestController
@RestInterface(lifeCycle=LifeCycle.REGULAR)
@RequestMapping("/portal/comments/")
public class PracticumFeedBackController extends RestfulController {

    @Autowired
    private AuditEventHandler auditEventHandler;
    
    @Autowired
    private PracticumFeedbackService practicumFeedbackService;
    
    @Autowired
    private EvaluationService evaluationService;
	
	private static Logger logger = LoggerFactory.getLogger(PracticumFeedBackController.class);
	
	@Slave
    @RequestMapping(value = "/getTags", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getTags(HttpServletRequest request, HttpServletResponse response){
        try{
            User user = getUser(request);
            logger.info("userId:" + user.getId());
			return ApiResponseUtils.buildSuccessDataResp(evaluationService.findTags());
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常");
        }
    }
	
	/**
	 * PE 保存
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pe/save", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> peSave(HttpServletRequest request, HttpServletResponse response, @RequestBody PeCommentsVo bean){
		try{
			//通用参数校验
			if(StringUtils.isBlank(bean.getSubmitType())){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				return ApiResponseUtils.buildErrorResp(-1,"reslult:submitType,The field is required !");
			}
			
			// 如果不是保存 则进行全局check验证
			List<Result> list = new ArrayList<Result>();
			if (!"SAVE".endsWith(bean.getSubmitType()) && !StringUtils.equalsIgnoreCase(bean.getResult(),com.vipkid.enums.TeacherApplicationEnum.Result.REAPPLY.toString())){
	            list = ValidateUtils.checkBean(bean,false);
	            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
	                response.setStatus(HttpStatus.BAD_REQUEST.value());
	                return ApiResponseUtils.buildErrorResp(-1,"reslult:"+list.get(0).getName() + "," + list.get(0).getMessages());
	            }
	            /** 1:pe other pes */
				if(bean.getFormType() == 1){
					list = ValidateUtils.checkForField(bean,com.google.common.collect.Lists.newArrayList("timeManagementScore", "accent", "positive", "engaged", "appearance", "phonics"), false);
		            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
		                response.setStatus(HttpStatus.BAD_REQUEST.value());
		                return ApiResponseUtils.buildErrorResp(-1,"reslult:"+list.get(0).getName() + "," + list.get(0).getMessages());
		            }
				}
			}
			
			Map<String, Object> resultMap = practicumFeedbackService.saveDoPeAudit(getTeacher(request), bean);
			//发邮件
			if(resultMap.get("applicationResult") != null){
				Teacher recruitTeacher = (Teacher) resultMap.get("recruitTeacher");
				auditEventHandler.onAuditEvent(new AuditEvent(recruitTeacher.getId(), TeacherEnum.LifeCycle.PRACTICUM.toString(), (String)resultMap.get("applicationResult")));
			}
			return ApiResponseUtils.buildSuccessDataResp(resultMap);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常");
        }
	}
	
	/**
	 * PE 查看
	 * @param request
	 * @param response
	 * @return
	 */
	@Slave
	@RequestMapping(value = "/pe/view", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> peView(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Integer id){
		try{
			if(id == null){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				return ApiResponseUtils.buildErrorResp(-5, "参数错误");
			}
			PeCommentsVo bean = this.practicumFeedbackService.findPeFromByAppId(id);
			return ApiResponseUtils.buildSuccessDataResp(bean);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常");
        }
	}
	
	
	/**
	 * PES 保存
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pes/save", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> peSupervisorSave(HttpServletRequest request, HttpServletResponse response, @RequestBody PeSupervisorCommentsVo bean){
		try{
			//通用参数校验
			if(StringUtils.isBlank(bean.getSubmitType())){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				return ApiResponseUtils.buildErrorResp(-1,"reslult:submitType,The field is required !");
			}
			
			// 如果不是保存 则进行全局check验证
			if (!"SAVE".endsWith(bean.getSubmitType()) && !StringUtils.equalsIgnoreCase(bean.getResult(),com.vipkid.enums.TeacherApplicationEnum.Result.REAPPLY.toString())){ 
	            List<Result> list = ValidateUtils.checkBean(bean,false);
	            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
	                response.setStatus(HttpStatus.BAD_REQUEST.value());
	                return ApiResponseUtils.buildErrorResp(-1,"reslult:"+list.get(0).getName() + "," + list.get(0).getMessages());
	            }
			}
			
			Map<String, Object> resultMap = practicumFeedbackService.saveDoPeSupervisorAudit(getTeacher(request), bean);
			//发邮件
			if(resultMap.get("applicationResult") != null){
				Teacher recruitTeacher = (Teacher) resultMap.get("recruitTeacher");
				auditEventHandler.onAuditEvent(new AuditEvent(recruitTeacher.getId(), TeacherEnum.LifeCycle.PRACTICUM.toString(), (String)resultMap.get("applicationResult")));
			}
			return ApiResponseUtils.buildSuccessDataResp(resultMap);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常");
        }
	}
	
	
	/**
	 * PES 查看
	 * @param request
	 * @param response
	 * @return
	 */
	@Slave
	@RequestMapping(value = "/pes/view", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> peSupervisorView(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Integer id){
		try{
			if(id == null){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				return ApiResponseUtils.buildErrorResp(-5, "参数错误");
			}
			PeSupervisorCommentsVo bean = this.practicumFeedbackService.findPeSupervisorFromByAppId(id);
			return ApiResponseUtils.buildSuccessDataResp(bean);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常");
        }
	}
	
	
	/**
	 * demoReport 获取
	 * @param request
	 * @param response
	 * @return
	 */
	@Slave
	@RequestMapping(value = "/demoReport/view", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> demoReportView(HttpServletRequest request, HttpServletResponse response,@RequestParam("onlineClassId") long onlineClassId, @RequestParam("studentId") long studentId){
		try{
			Map<String,Object> resultMap = Maps.newHashMap();
            DemoReport currentReport =  practicumFeedbackService.getDemoReport(studentId, onlineClassId);
            if (currentReport == null)
                currentReport = new DemoReport();
            resultMap.put("onlineClassId", onlineClassId);
            resultMap.put("currentReport", currentReport);
            resultMap.put("demoReports", practicumFeedbackService.getDemoReports());
            resultMap.put("reportLevels", practicumFeedbackService.getReportLevels());
			return ApiResponseUtils.buildSuccessDataResp(resultMap);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(),e);
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常");
        }
	}
}
