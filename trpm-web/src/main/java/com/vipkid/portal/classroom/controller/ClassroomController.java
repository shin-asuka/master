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

import com.google.api.client.util.Maps;
import com.vipkid.dataSource.annotation.Slave;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.portal.classroom.model.ClassRoomVo;
import com.vipkid.portal.classroom.model.SendHelpVo;
import com.vipkid.portal.classroom.service.ClassroomService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;

@RestController
@RestInterface(lifeCycle=LifeCycle.REGULAR)
@RequestMapping("/portal/classroom")
public class ClassroomController extends RestfulController{

	@Autowired
	private ClassroomService classroomService;
	
	private static Logger logger = LoggerFactory.getLogger(ClassroomController.class);
	
	/**
	 * 移除星星
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/stars/send", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
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
	
	/**
	 * 移除星星
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/stars/remove", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
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
	
	/**
	 * 教室信息获取 Practicum 课程studentId 为被面试者ID
	 * @param request
	 * @param response
	 * @param onlineClassId
	 * @param studentId
	 * @return
	 */
	@Slave
	@RequestMapping(value = "/info/room", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
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
	
	/**
	 * 获取Open 学生信息
	 * @param request
	 * @param response
	 * @param serialNum
	 * @param studentId
	 * @return
	 */
	@Slave
	@RequestMapping(value = "/info/student", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
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

	/**
	 * 获取教室URL
	 * @param request
	 * @param response
	 * @param onlineClassId
	 * @return
	 */
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
    
    /**
     * 服务器时间
     * @param request
     * @param response
     * @return
     */
	@RequestMapping(value = "/room/time", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
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
	
	/**
	 * 定时获取教室是否需要切换
	 * @param request
	 * @param response
	 * @param onlineClassId
	 * @param classroom
	 * @return
	 */
	@RequestMapping(value = "/room/change", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> roomChange(HttpServletRequest request, HttpServletResponse response, @RequestParam("onlineClassId") long onlineClassId, @RequestParam("classroom") String classroom){
		try{
			Map<String,Object> resultMap = this.classroomService.roomChange(onlineClassId+"");
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
	
	/**
	 * 进教室后定时声明老师在教室里面
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/send/enter", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> sendEnter(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String,Object> paramMap){
		try{
			if(paramMap.get("onlineClassId") == null){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(-1,"reslult:onlineClassId,The field is required !");
			}
			Map<String,String>  requestParams = Maps.newHashMap();
			requestParams.put("onlineClassId", paramMap.get("onlineClassId")+"");
			Map<String,Object> resultMap = this.classroomService.sendTeacherInClassroom(requestParams, getTeacher(request));
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
	
	/**
	 * 向Firemen请求帮助
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/send/help", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> sendHelp(HttpServletRequest request, HttpServletResponse response,@RequestBody SendHelpVo bean){
		try{
			List<Result> list = ValidateUtils.checkBean(bean, false);
			if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(-1,"reslult:"+list.get(0).getName() + "," + list.get(0).getMessages());
            }
			Map<String,Object> resultMap = this.classroomService.sendHelp(bean.getScheduleTime(), bean.getOnlineClassId(), getTeacher(request));
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
	
}