package com.vipkid.rest.portal.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.common.base.Stopwatch;
import java.util.List;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.http.service.GatewayAppService;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.rest.portal.vo.StudentCommentApi;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.portal.service.ClassroomsRestService;
import com.vipkid.rest.portal.vo.StudentCommentPageApi;
import com.vipkid.rest.portal.vo.StudentCommentTotalApi;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.utils.ApiResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
//@RestInterface(lifeCycle = LifeCycle.REGULAR)
public class StudentCommentRestController extends RestfulController{
	private static final Logger logger = LoggerFactory.getLogger(StudentCommentRestController.class);
	
	@Autowired
	private GatewayAppService gatewayAppService;

	/**
	 * 获取一个可双向翻页的StudentComment分页
	 * @param request
	 * @param response
	 * @param teacherId
	 * @return
	 */

	@RequestMapping(value = "/getStudentCommentByDoublePage", method = RequestMethod.GET)
	public Map<String, Object> getStudentCommentByDoublePage(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value="onlineClassId", required=true) long onlineClassId,
			@RequestParam(value="teacherId",required=true) int teacherId) {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("【StudentCommentRestController.getStudentCommentByDoublePage】input：onlineClassId={},teacherId={}",onlineClassId, teacherId);

			StudentCommentPageApi studentCommentPageApi = gatewayAppService.getStudentCommentListByTeacherId(teacherId, null, null, null);
			Integer[] offsetAndLimit = gatewayAppService.calculateOffsetAndLimit(studentCommentPageApi, onlineClassId);
			studentCommentPageApi = gatewayAppService.getStudentCommentListByTeacherId(teacherId,offsetAndLimit[0],offsetAndLimit[1],null);
			//计算当前评价在当前分页中的位置
			Integer currentPosition = 0 ;
			Integer prevOnlineClassId = -1;
			Integer nextOnlineClassId = -1;
			List<StudentCommentApi> stuCommentList = studentCommentPageApi.getData();
			for(int i=0;i<stuCommentList.size();i++){
				if(stuCommentList.get(i).getClass_id()==onlineClassId){
					currentPosition = i;
					if(i>0) {
						prevOnlineClassId = stuCommentList.get(i-1).getClass_id();
					}
					if(i<stuCommentList.size()-1) {
						nextOnlineClassId = stuCommentList.get(i+1).getClass_id();
					}
					break;
				}
			}

			Map<String,Object> ret = Maps.newHashMap();
			ret.put("data",studentCommentPageApi.getData());
			ret.put("currentOnlineClassId",onlineClassId);
			ret.put("prevOnlineClassId",prevOnlineClassId);
			ret.put("nextOnlineClassId",nextOnlineClassId);
			ret.put("currentPosition",currentPosition);
			long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("【StudentCommentRestController.getStudentCommentByDoublePage】output：result={},运行时间={}ms",ret,millis);
	        return ApiResponseUtils.buildSuccessDataResp(ret);
		} catch (Exception e) {
	        logger.error("调用restClassrooms接口抛异常，传入参数：teacherId={}。抛异常: {}", teacherId, e);//由于维龙的代码没有合上去，暂时这么处理
		}
		return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
	}
	
	/**
	 * 根据total获取老师的评价统计数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getStudentCommentTotal", method = RequestMethod.GET)
	public Map<String, Object> getStudentCommentTotal(HttpServletRequest request, HttpServletResponse response,
													  @RequestParam(value="teacherId",required=true) int teacherId) {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("【StudentCommentRestController.getStudentCommentTotal】input：teacherId={}",teacherId);
			Map ret = Maps.newHashMap();
			StudentCommentTotalApi data = gatewayAppService.getStudentCommentTotalByTeacherId(teacherId);
			Integer allComments = data.getRating_1_count() +
								  data.getRating_2_count() +
					   			  data.getRating_3_count() +
							      data.getRating_4_count() +
					              data.getRating_5_count();

			Integer satisfied = data.getRating_4_count() + data.getRating_5_count();
			Integer average = data.getRating_3_count();
			Integer dislike = data.getRating_1_count() + data.getRating_2_count();

			Integer	totalGrade = (1 * data.getRating_1_count() +
					              2 * data.getRating_2_count() +
					              3 * data.getRating_3_count() +
					              4 * data.getRating_4_count() +
					              5 * data.getRating_5_count())/allComments;

			ret.put("allComments",allComments);
			ret.put("satisfied",satisfied);
			ret.put("average",average);
			ret.put("dislike",dislike);
			ret.put("totalGrade",totalGrade);

			long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("【StudentCommentRestController.getStudentCommentTotal】output：result={},运行时间={}ms", JSONObject.toJSONString(data),millis);
			return ApiResponseUtils.buildSuccessDataResp(ret);
		} catch (Exception e) {
			logger.error("【StudentCommentRestController.getStudentCommentTotal】input：teacherId={}。Exception: {}", teacherId, e);
		}
		return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
	}


	/**
	 * 根据total获取老师的评价分页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getStudentCommentByPage",method = RequestMethod.GET)
	public Map<String, Object> getStudentCommentByPage(HttpServletRequest request, HttpServletResponse response,
													   @RequestParam(value="start",required=false,defaultValue = "0") Integer start ,
													   @RequestParam(value="limit",required=false,defaultValue = "10") Integer limit,
													   @RequestParam(value="ratingLevel",required=false,defaultValue = "") String ratingLevel,
													   @RequestParam(value="teacherId",required=true) Integer teacherId) {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("【StudentCommentRestController.getStudentCommentByPage】input：teacherId={},start={},limit={},ratingLevel={}",teacherId,start,limit,ratingLevel);
			StudentCommentPageApi data = gatewayAppService.getStudentCommentListByTeacherId(teacherId,start,limit,ratingLevel);
			long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("【StudentCommentRestController.getStudentCommentByPage】output：result={},运行时间={}ms ", JSONObject.toJSONString(data),millis);
			return ApiResponseUtils.buildSuccessDataResp(data);
		} catch (Exception e) {
			logger.error("【StudentCommentRestController.getStudentCommentByPage】传入参数：teacherId={}。抛异常: {}", teacherId, e);
		}
		return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
	}

}
