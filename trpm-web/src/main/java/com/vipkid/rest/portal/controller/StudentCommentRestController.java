package com.vipkid.rest.portal.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.common.base.Stopwatch;
import java.util.List;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.http.service.GatewayAppService;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.payroll.service.StudentService;
import com.vipkid.rest.portal.vo.StudentCommentApi;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.portal.service.ClassroomsRestService;
import com.vipkid.rest.portal.vo.StudentCommentPageApi;
import com.vipkid.rest.portal.vo.StudentCommentTotalApi;
import com.vipkid.rest.security.AppContext;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.utils.UserUtils;
import com.vipkid.rest.utils.ext.baidu.BaiduTranslateAPI;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Student;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.portal.OnlineClassService;
import com.vipkid.trpm.util.LessonSerialNumber;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
	@Autowired
	private OnlineClassService onlineClassService;
	@Autowired
	private StudentService studentService;
	@Autowired
	private LessonDao lessonDao;
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
//			User getUser = UserUtils.getUser(request);
//			if(getUser.getId()!=teacherId){
//				return ApiResponseUtils.buildErrorResp(1002, "没有数据访问权限");
//			}
			//取全量评论
			StudentCommentPageApi studentCommentPageApi = gatewayAppService.getStudentCommentListByTeacherId(teacherId, 0, 3000, null);
			logger.info("获取全量评论成功：teacherId:{},size:{}",teacherId,studentCommentPageApi.getTotal());
			Integer[] offsetAndLimit = gatewayAppService.calculateOffsetAndLimit(studentCommentPageApi, onlineClassId);
			//截取当前窗口数据和前后指针
			List<StudentCommentApi> stuCommentList = studentCommentPageApi.getData().subList(offsetAndLimit[0],offsetAndLimit[0] + offsetAndLimit[1]);		//计算当前评价在当前分页中的位置
			Integer currentPosition = 0 ;
			Integer prevOnlineClassId = -1;
			Integer nextOnlineClassId = -1;
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
//			User getUser = UserUtils.getUser(request);
//			if(getUser.getId()!=teacherId){
//				return ApiResponseUtils.buildErrorResp(1002, "没有数据访问权限");
//			}
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
//			User getUser = UserUtils.getUser(request);
//			if(getUser.getId()!=teacherId){
//				return ApiResponseUtils.buildErrorResp(1002, "没有数据访问权限");
//			}
			StudentCommentPageApi data = gatewayAppService.getStudentCommentListByTeacherId(teacherId, start, limit, ratingLevel);
			for(StudentCommentApi stuCommentApi : data.getData()){
				OnlineClass onlineClass = onlineClassService.getOnlineClassById(stuCommentApi.getClass_id());
				if(onlineClass!= null) {
					stuCommentApi.setScheduleDateTime(DateFormatUtils.format(onlineClass.getScheduledDateTime(), "yyyy-MM-dd hh:mm"));
					//构造OnlineClassName
					Lesson lesson = lessonDao.findById(onlineClass.getLessonId());
					if(lesson!=null) {
						String lessonSn = lesson.getSerialNumber();
						String onlineClassName = LessonSerialNumber.formatToStudentCommentPattern(lessonSn);
						onlineClassName = onlineClassName + lesson.getName();
						stuCommentApi.setOnlineClassName(onlineClassName);
					}
					Student student = studentService.getById(stuCommentApi.getStudent_id().longValue());
					if(student!=null) {
						stuCommentApi.setStudentAvatar(student.getAvatar());
						stuCommentApi.setStudentName(student.getEnglishName());
					}
				}
			}

			long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("【StudentCommentRestController.getStudentCommentByPage】output：result={},运行时间={}ms ", JSONObject.toJSONString(data),millis);
			return ApiResponseUtils.buildSuccessDataResp(data);
		} catch (Exception e) {
			logger.error("【StudentCommentRestController.getStudentCommentByPage】传入参数：teacherId={}。抛异常: {}", teacherId, e);
		}
		return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
	}

	@RequestMapping(value = "/translateZhToEn",method = RequestMethod.GET)
	public Map<String, Object> translateZhToEn(HttpServletRequest request, HttpServletResponse response,
													   @RequestParam(value="text",required=false,defaultValue = "") String text) {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			Map map = Maps.newHashMap();
			String retText = "";
			logger.info("【StudentCommentRestController.translateZhToEn】input：text={}",text);
			if (StringUtils.isNotBlank(text)) {
				retText = BaiduTranslateAPI.translate(text);
				map.put("relText",retText);
			}
			long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("【StudentCommentRestController.translateZhToEn】output：result={},运行时间={}ms ", JSONObject.toJSONString(retText),millis);
			return ApiResponseUtils.buildSuccessDataResp(map);
		} catch (Exception e) {
			logger.error("【StudentCommentRestController.getStudentCommentByPage】传入参数：text={}。抛异常: {}",text,e);
		}
		return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
	}

	@RequestMapping(value = "/getStudentCommentTranslation", method  = RequestMethod.GET)
	public Map<String, Object> getStudentCommentTranslation(HttpServletRequest request, HttpServletResponse response,
															@RequestParam(value = "id", required = true) Long id){
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restClassroomsMaterials接口， 传入参数：id = {}", id);
			String result  = gatewayAppService.getTranslation(id);
			Map map = Maps.newHashMap();
			map.put("translation",result);
			long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("【getStudentCommentTranslation】，传入参数：id = {}。返回Json={}。耗时{}ms", id, JsonUtils.toJSONString(result), millis);
			return ApiResponseUtils.buildSuccessDataResp(map);
		} catch (Exception e) {
			logger.error("调用restClassroomsMaterial接口， 传入参数：lessonId = {}。抛异常: {}", id, e);
		}
		return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
	}

	@RequestMapping(value = "/saveStudentCommentTranslation", method  = RequestMethod.POST)
	public Map<String, Object> saveStudentCommentTranslation(HttpServletRequest request, HttpServletResponse response,
															 @RequestParam(value = "id", required = true) Long id,
															 @RequestParam(value = "text",required = true) String text){
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restClassroomsMaterials接口， 传入参数：id = {},text = {}", id,text);
			Boolean result  = gatewayAppService.saveTranslation(id, text);
			Map map = Maps.newHashMap();
			map.put("status",result);
			long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("saveStudentCommentTranslation，传入参数：id = {},text = {}。返回Json={}。耗时{}ms", id,text, JsonUtils.toJSONString(result), millis);
			return ApiResponseUtils.buildSuccessDataResp(map);
		} catch (Exception e) {
			logger.error("调用restClassroomsMaterial接口， 传入参数：id = {},text = {}。抛异常: {}", id,text,e);//由于维龙的代码没有合上去，暂时这么处理
		}
		return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
	}
}
