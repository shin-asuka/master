package com.vipkid.portal.classroom.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Maps;
import com.vipkid.dataSource.annotation.Slave;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.portal.classroom.model.ClassRoomVo;
import com.vipkid.portal.classroom.service.ClassroomService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;

@RestController
@RestInterface(lifeCycle=LifeCycle.REGULAR)
@RequestMapping("/portal/classroom/")
public class ClassroomController extends RestfulController{

	@Autowired
	private ClassroomService classroomService;
	
	private static Logger logger = LoggerFactory.getLogger(ClassroomController.class);
	
	@RequestMapping(value = "stars/send", method = RequestMethod.POST)
	public Map<String, Object> starsSend(HttpServletRequest request, HttpServletResponse response, @RequestBody ClassRoomVo bean){		
		try{
			//参数校验
            List<Result> list = ValidateUtils.checkBean(bean,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(-1,"reslult:"+list.get(0).getName() + "," + list.get(0).getMessages());
            }
            
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
	
	@RequestMapping(value = "stars/remove", method = RequestMethod.POST)
	public Map<String, Object> starsRemove(HttpServletRequest request, HttpServletResponse response, @RequestBody ClassRoomVo bean){
		try{
			//参数校验
            List<Result> list = ValidateUtils.checkBean(bean,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(-1,"reslult:"+list.get(0).getName() + "," + list.get(0).getMessages());
            }
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
	
	@Slave
	@RequestMapping(value = "info/room", method = RequestMethod.GET)
	public Map<String, Object> infoRoom(HttpServletRequest request, HttpServletResponse response,@RequestParam("onlineClassId") long onlineClassId, @RequestParam("studentId") long studentId){
		try{
			ClassRoomVo bean = new ClassRoomVo();
			bean.setOnlineClassId(onlineClassId);
            bean.setStudentId(studentId);
			return ApiResponseUtils.buildSuccessDataResp(this.classroomService.getInfoRoom(bean, getTeacher(request)));
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
	
	@Slave
	@RequestMapping(value = "info/student", method = RequestMethod.GET)
	public Map<String, Object> infoStudent(HttpServletRequest request, HttpServletResponse response,@RequestParam("serialNum") String serialNum, @RequestParam("studentId") long studentId){
		try{
			return ApiResponseUtils.buildSuccessDataResp(this.classroomService.getInfoStudent(studentId, serialNum));
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

	
    @RequestMapping(value = "/getClassRoomUrl", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getClassRoomUrl(HttpServletRequest request, HttpServletResponse response,@RequestParam("onlineClassId") long onlineClassId){
        try{
        	Map<String,Object> resultMap = this.classroomService.getClassRoomUrl(onlineClassId,getTeacher(request));
        	if(resultMap.get("info") == null){
        		return ApiResponseUtils.buildSuccessDataResp(resultMap);
        	}else{
                response.setStatus(HttpStatus.FORBIDDEN.value());
    			return ApiResponseUtils.buildErrorResp(-5, "错误信息:"+resultMap.get("info"));
        	}
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
    
    
	@RequestMapping(value = "room/time", method = RequestMethod.GET)
	public Map<String, Object> roomTime(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String,Object> maps = Maps.newHashMap();
			maps.put("serverTime", System.currentTimeMillis());
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
	
	@RequestMapping(value = "room/change", method = RequestMethod.GET)
	public Map<String, Object> roomChange(HttpServletRequest request, HttpServletResponse response, @RequestParam("onlineClassId") long onlineClassId, @RequestParam("classroom") String classroom){
		try{
			Map<String,Object> maps = Maps.newHashMap();
			maps.put("supplierCode", 1);
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
