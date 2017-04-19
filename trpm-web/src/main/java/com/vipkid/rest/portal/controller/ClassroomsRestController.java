package com.vipkid.rest.portal.controller;

import com.google.common.base.Stopwatch;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.portal.model.ClassroomDetail;
import com.vipkid.rest.portal.model.ClassroomsData;
import com.vipkid.rest.portal.service.ClassroomsRestService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.utils.ClassroomUtils;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.entity.teachercomment.TeacherComment;
import com.vipkid.trpm.service.portal.TeacherService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RestInterface(lifeCycle = {LifeCycle.REGULAR,LifeCycle.QUIT})
public class ClassroomsRestController extends RestfulController{
	private static final Logger logger = LoggerFactory.getLogger(ClassroomsRestController.class);
	public static final long MILLS_24_HOURS = 24*60*60*1000;

	@Autowired
	private ClassroomsRestService classroomsRestService;
	@Autowired
	private TeacherService teacherService;

	/**
	 * classrooms页面的数据接口
	 * @param request
	 * @param response
	 * @param teacherId
	 * @param offsetOfMonth
	 * @param courseType
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "/restClassrooms", method = RequestMethod.GET)
	public Map<String, Object> classrooms(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value="teacherId", required=true) long teacherId,
			@RequestParam(value="month",required=true) int offsetOfMonth,
			@RequestParam(value="tag", required=true) String courseType,
			@RequestParam(value="page", required=true) int page) {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restClassrooms接口，传入参数：teacherId={}, month={}, tag={}, page={}", teacherId, offsetOfMonth, courseType, page);
			
			Map<String, Object> result = classroomsRestService.getClassroomsData(teacherId, offsetOfMonth, courseType, page);

			try {
				if ((Boolean)result.get("ret")){

					ClassroomsData classroomsData = (ClassroomsData)result.get("data");
					List<ClassroomDetail> dataList = classroomsData.getDataList();

					for (ClassroomDetail classroomDetail : dataList) {
						//异步刷新课标（如果当前时间是周一的11:50~12:10,则今天约的课的课标要临时调整)
						ClassroomUtils.buildAsyncLessonSN(classroomDetail);

						if(classroomDetail.getIsPaidTrail() == null || classroomDetail.getIsPaidTrail().intValue() == 0) {
							continue;
						}

						if (!(
								OnlineClassEnum.ClassStatus.isFinished(classroomDetail.getStatus()) && (
										ApplicationConstant.FinishType.AS_SCHEDULED.equals(classroomDetail.getFinishType())
										|| ApplicationConstant.FinishType.STUDENT_IT_PROBLEM.equals(classroomDetail.getFinishType())
										|| ApplicationConstant.FinishType.TEACHER_IT_PROBLEM.equals(classroomDetail.getFinishType())
										|| ApplicationConstant.FinishType.SYSTEM_PROBLEM.equals(classroomDetail.getFinishType())
								)
						)) {
							classroomDetail.setIsPaidTrail(0);
							continue;
						}					

						TeacherComment teacherComment = teacherService.findByStudentIdAndOnlineClassId(classroomDetail.getStudentId(), classroomDetail.getOnlineClassId());
						if (teacherComment == null || teacherComment.getFirstDateTime() == null || teacherComment.getScheduledDateTime() == null
								|| StringUtils.isBlank(teacherComment.getTeacherFeedback()) || teacherComment.getFirstDateTime().getTime() - teacherComment.getScheduledDateTime().getTime() > MILLS_24_HOURS) {
							classroomDetail.setIsPaidTrail(0);
							continue;
						}
						

						int[] yearMonth = classroomsRestService.getPaidTrailPaymentYearMonth(classroomDetail.getStudentId(), classroomDetail.getOnlineClassId(),teacherComment.getScheduledDateTime().getTime());
						if (yearMonth == null) {
							classroomDetail.setIsPaidTrail(0);
							continue;
						}

						classroomDetail.setIsPaidTrail(1);
						classroomDetail.setPaidTrailPaymentYearMonth(yearMonth);
					}
				}
			} catch (Exception e) {
				logger.error("setPaidTrailPaymentMonth抛异常，传入参数：teacherId={}, month={}, tag={}, page={}。抛异常: {}", teacherId, offsetOfMonth, courseType, page, e);
			}

			long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restClassrooms接口，传入参数：teacherId={}, month={}, tag={}, page={}。返回Json={}。共耗时{}ms", teacherId, offsetOfMonth, courseType, page, JsonUtils.toJSONString(result), millis);
	        return result;
		} catch (Exception e) {
			logger.error("调用restClassrooms接口抛异常，传入参数：teacherId={}, month={}, tag={}, page={}。抛异常: {}", teacherId, offsetOfMonth, courseType, page, e);//由于维龙的代码没有合上去，暂时这么处理
		}
		return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
	}
	
	/**
	 * 根据lessonId获取此节课的material接口，要求登陆的regular老师才能请求
	 * @param request
	 * @param response
	 * @param lessonId
	 * @return
	 */
	@RequestMapping(value = "/restClassroomsMaterial", method  = RequestMethod.GET)
	public Map<String, Object> material(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "lessonId", required = true) long lessonId){
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restClassroomsMaterials接口， 传入参数：lessonId = {}", lessonId);
//			Teacher teacher = loginService.getTeacher();
//			if(null == teacher){
//				return ApiResponseUtils.buildErrorResp(1002, "没有权限请求此接口");
//			}
			Map<String, Object> result  = classroomsRestService.getClassroomsMaterialByLessonId(lessonId);
			long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restClassroomsMaterials接口，传入参数：lessonId = {}。返回Json={}。耗时{}ms", lessonId, JsonUtils.toJSONString(result), millis);
			return result;
		} catch (Exception e) {
			logger.error("调用restClassroomsMaterial接口， 传入参数：lessonId = {}。抛异常: {}", lessonId, e);//由于维龙的代码没有合上去，暂时这么处理
		}
		return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
	}
}
