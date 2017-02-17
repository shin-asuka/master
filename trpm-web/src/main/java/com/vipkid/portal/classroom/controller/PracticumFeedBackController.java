package com.vipkid.portal.classroom.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.portal.classroom.model.ClassRoomVo;
import com.vipkid.portal.classroom.model.PeCommentsVo;
import com.vipkid.portal.classroom.model.PeSupervisorCommentsVo;
import com.vipkid.portal.classroom.service.PracticumFeedbackService;
import com.vipkid.recruitment.event.AuditEvent;
import com.vipkid.recruitment.event.AuditEventHandler;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.trpm.entity.Teacher;

@RestController
@RestInterface(lifeCycle=LifeCycle.REGULAR)
@RequestMapping("/portal/comments/")
public class PracticumFeedBackController extends RestfulController {

    @Autowired
    private AuditEventHandler auditEventHandler;
    
    @Autowired
    private PracticumFeedbackService practicumFeedbackService;
	
	private static Logger logger = LoggerFactory.getLogger(PracticumFeedBackController.class);

	/**
	 * PE 保存
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pe/save", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> peSave(HttpServletRequest request, HttpServletResponse response, @RequestBody PeCommentsVo bean){
		try{
			//参数校验
            List<Result> list = ValidateUtils.checkBean(bean,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(-1,"reslult:"+list.get(0).getName() + "," + list.get(0).getMessages());
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
			logger.error(e.getMessage());
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage());
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常");
        }
	}
	
	/**
	 * PE 查看
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pe/view", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> peView(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id){
		try{
			
			

			return ApiResponseUtils.buildSuccessDataResp(new Object());
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage());
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage());
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
			//参数校验
            List<Result> list = ValidateUtils.checkBean(bean,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(-1,"reslult:"+list.get(0).getName() + "," + list.get(0).getMessages());
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
			logger.error(e.getMessage());
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage());
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常");
        }
	}
	
	
	/**
	 * PES 查看
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pes/view", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> peSupervisorView(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id){
		try{
			
			return ApiResponseUtils.buildSuccessDataResp(new Object());
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage());
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage());
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常");
        }
	}
	
	
	/**
	 * demoReport 获取
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/demereport/view", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> demereportView(HttpServletRequest request, HttpServletResponse response,@RequestParam("onlineClassId") long onlineClassId, @RequestParam("studentId") long studentId){
		try{
			ClassRoomVo bean = new ClassRoomVo();
			bean.setOnlineClassId(onlineClassId);
            bean.setStudentId(studentId);
            //TODO
			return ApiResponseUtils.buildSuccessDataResp(new Object());
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
			logger.error(e.getMessage());
			return ApiResponseUtils.buildErrorResp(-6, "参数类型转化错误");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage());
			return ApiResponseUtils.buildErrorResp(-7, "服务器异常");
        }
	}
}
