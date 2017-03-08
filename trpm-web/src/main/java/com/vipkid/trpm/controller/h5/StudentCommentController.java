package com.vipkid.trpm.controller.h5;

import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.common.base.Stopwatch;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.http.service.ManageGatewayService;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.portal.vo.StudentCommentPageVo;
import com.vipkid.rest.portal.vo.StudentCommentTotalVo;
import com.vipkid.rest.portal.vo.StudentCommentVo;
import com.vipkid.rest.portal.vo.TranslationVo;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.utils.ext.baidu.BaiduTranslateAPI;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.service.portal.OnlineClassService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/h5")
public class StudentCommentController extends RestfulController{
    private static final Logger logger = LoggerFactory.getLogger(StudentCommentController.class);
    public static String TEACHER_RATINGS_HIGH = "5";
    public static String TEACHER_RATINGS_MEDIUM = "4";
    public static String TEACHER_RATINGS_LOW = "1,2,3";
    public static String TEACHER_RATINGS_ALL = "1,2,3,4,5";
    public static Integer PAGE_SIZE = 10;

    @Autowired
    private ManageGatewayService manageGatewayService;
    @Autowired
    private OnlineClassService onlineClassService;
    /**
     * 获取一个可双向翻页的StudentComment分页
     * @param request
     * @param response
     * @return
     */

    @RequestMapping(value = "/getStudentCommentByBatch", method = RequestMethod.GET)
    public Map<String, Object> getStudentCommentByBatch(HttpServletRequest request, HttpServletResponse response,
                                                        @RequestParam(value="onlineClassId", required=true) long onlineClassId) {
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            logger.info("【StudentCommentRestController.getStudentCommentListByBatch】input：onlineClassId={}",onlineClassId);
//			User getUser = UserUtils.getUser(request);
//			if(getUser.getId()!=teacherId){
//				return ApiResponseUtils.buildErrorResp(1002, "没有数据访问权限");
//			}
            String onlineClassIdStr = Long.toString(onlineClassId);
            //根据页号获取老师的评论列表分页
            List<StudentCommentVo> studentCommentVos = manageGatewayService.getStudentCommentListByBatch(onlineClassIdStr);

            if(CollectionUtils.isEmpty(studentCommentVos)){
                return ApiResponseUtils.buildErrorResp(1001,"没有获取到评价信息");
            };
            StudentCommentVo studentCommentVo = studentCommentVos.get(0);
            OnlineClass onlineClass = onlineClassService.getOnlineClassById(studentCommentVo.getClass_id());
            Timestamp scheduleDateTime = onlineClass.getScheduledDateTime();
            studentCommentVo.setScheduleDateTime(DateFormatUtils.format(scheduleDateTime, "MMM dd hh:mma", Locale.ENGLISH));
            Map<String,Object> ret = Maps.newHashMap();
            ret.put("data",studentCommentVo);
            long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
            logger.info("【StudentCommentRestController.getStudentCommentListByBatch】output：result={},运行时间={}ms",ret,millis);
            return ApiResponseUtils.buildSuccessDataResp(ret);
        } catch (Exception e) {
            String errorMessage = String.format("调用【StudentCommentRestController.getStudentCommentListByBatch】接口抛异常，传入参数：onlineClassId=%d",onlineClassId);
            logger.error(errorMessage, e);
        }
        return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
    }


    @RequestMapping(value = "/getStudentCommentByDoublePage", method = RequestMethod.GET)
    public Map<String, Object> getStudentCommentByDoublePage(HttpServletRequest request, HttpServletResponse response,
                                                             @RequestParam(value="onlineClassId", required=true) long onlineClassId,
                                                             @RequestParam(value="teacherId",required=true) int teacherId,
                                                             @RequestParam(value="PageNo",required=false,defaultValue = "-1") Integer pageNo) {
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            logger.info("【StudentCommentRestController.getStudentCommentByDoublePage】input：onlineClassId={},teacherId={}",onlineClassId, teacherId);
//			User getUser = UserUtils.getUser(request);
//			if(getUser.getId()!=teacherId){
//				return ApiResponseUtils.buildErrorResp(1002, "没有数据访问权限");
//			}

            Integer absolutePosition = 0;//在全部评论中的位置
            Integer position = 0;//在评论分页中的位置
            List<StudentCommentVo> stuCommentList = Lists.newArrayList();

            if(pageNo == -1) {// 页号未知需重新定位页号
                StudentCommentPageVo studentCommentPageVo = manageGatewayService.getStudentCommentListByTeacherId(teacherId, 0, 3000, null);
                logger.info("获取全量评论成功：teacherId:{},size:{}",teacherId,studentCommentPageVo.getTotal());
                absolutePosition = manageGatewayService.calculateAbsolutePosition(studentCommentPageVo, onlineClassId);
                if(absolutePosition == -1) {//未找到classId;
                    return ApiResponseUtils.buildErrorResp(1003,"未找到该课程的评论");
                }
                pageNo = absolutePosition / ApplicationConstant.PAGE_SIZE + 1;
            }

            //计算总页数
            StudentCommentTotalVo studentCommentTotalVo = manageGatewayService.getStudentCommentTotalByTeacherId(teacherId);
            Integer totalPageNo = (studentCommentTotalVo.getRating_1_count() +
                    studentCommentTotalVo.getRating_2_count() +
                    studentCommentTotalVo.getRating_3_count() +
                    studentCommentTotalVo.getRating_4_count() +
                    studentCommentTotalVo.getRating_5_count()) / ApplicationConstant.PAGE_SIZE + 1;
            if(totalPageNo < pageNo){
                return ApiResponseUtils.buildErrorResp(1004,"请求的页码超过了总页数");
            }

            //根据页号获取老师的评论列表分页
            StudentCommentPageVo studentCommentPageVo = manageGatewayService.getStudentCommentListByTeacherId(teacherId,(pageNo-1) * ApplicationConstant.PAGE_SIZE,ApplicationConstant.PAGE_SIZE,null);
            stuCommentList = studentCommentPageVo.getData();

            //定位当前页的相对位置
            Integer flag = 0;
            for(StudentCommentVo studentCommentVo :stuCommentList){
                if(studentCommentVo.getClass_id().longValue() == onlineClassId){
                    flag = 1;
                    break;
                }
                position++;
            }
            if(flag == 0){
                return ApiResponseUtils.buildErrorResp(1003, "未能定位当前页的相对位置");
            }

            Map<String,Object> ret = Maps.newHashMap();
            ret.put("data",stuCommentList);
            ret.put("position",position);
            ret.put("pageNo",pageNo);
            ret.put("pageSize",ApplicationConstant.PAGE_SIZE);
            ret.put("totalPageNo",totalPageNo);
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
            StudentCommentTotalVo data = manageGatewayService.getStudentCommentTotalByTeacherId(teacherId);
            Integer allComments = data.getRating_1_count() +
                    data.getRating_2_count() +
                    data.getRating_3_count() +
                    data.getRating_4_count() +
                    data.getRating_5_count();

            Integer satisfied =  data.getRating_5_count();
            Integer average = data.getRating_4_count();
            Integer dislike = data.getRating_1_count() + data.getRating_2_count() + data.getRating_3_count();

            String totalGradeStr = "0.0";
            if(allComments!=0) {
                Float totalGrade = (1f * data.getRating_1_count() +
                        2f * data.getRating_2_count() +
                        3f * data.getRating_3_count() +
                        4f * data.getRating_4_count() +
                        5f * data.getRating_5_count()) / allComments;
                totalGradeStr = new DecimalFormat("0.0").format(totalGrade);
            }


            ret.put("allComments",allComments);
            ret.put("satisfied",satisfied);
            ret.put("average",average);
            ret.put("dislike",dislike);
            ret.put("totalGrade",totalGradeStr);

            long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
            logger.info("【StudentCommentRestController.getStudentCommentTotal】output：result={},运行时间={}ms", JSONObject.toJSONString(data),millis);
            return ApiResponseUtils.buildSuccessDataResp(ret);
        } catch (Exception e) {
            logger.error("【StudentCommentRestController.getStudentCommentTotal】input：teacherId={}。抛异常", teacherId);
            logger.error("【StudentCommentRestController.getStudentCommentTotal】接口异常", e);
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
            String ratings = TEACHER_RATINGS_ALL;
            if(ratingLevel.toLowerCase().equals("high")){
                ratings = TEACHER_RATINGS_HIGH;
            }else if(ratingLevel.toLowerCase().equals("medium")){
                ratings = TEACHER_RATINGS_MEDIUM;
            }else if(ratingLevel.toLowerCase().equals("low")){
                ratings = TEACHER_RATINGS_LOW;
            }

            StudentCommentPageVo data = manageGatewayService.getStudentCommentListByTeacherId(teacherId, start, limit, ratings);

            Integer hasHalfPage = (data.getTotal() % PAGE_SIZE == 0)? 0 : 1;

            data.setCurPageNo(start/PAGE_SIZE + 1);
            data.setTotalPageNo(data.getTotal()/PAGE_SIZE + hasHalfPage);
            for(StudentCommentVo studentCommentVo : data.getData()) {
                Date date = DateUtils.parseDate(studentCommentVo.getScheduleDateTime(), "yyyy-MM-dd HH:mm");
                studentCommentVo.setScheduleDateTime(DateFormatUtils.format(date, "MMM dd hh:mma",Locale.ENGLISH));
            }
            long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
            logger.info("【StudentCommentRestController.getStudentCommentByPage】output：result={},运行时间={}ms ", JSONObject.toJSONString(data),millis);
            return ApiResponseUtils.buildSuccessDataResp(data);
        } catch (Exception e) {
            logger.error("【StudentCommentRestController.getStudentCommentByPage】传入参数：teacherId={}。抛异常: ", teacherId);
            logger.error("【StudentCommentRestController.getStudentCommentByPage】接口异常",e);
        }
        return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
    }

    @RequestMapping(value = "/translateZhToEn",method = RequestMethod.POST)
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
            logger.error("【StudentCommentRestController.getStudentCommentByPage】传入参数：text={}。抛异常",text);
            logger.error("【StudentCommentRestController.getStudentCommentByPage】接口异常",e);
        }
        return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
    }

    @RequestMapping(value = "/getStudentCommentTranslation", method  = RequestMethod.POST,produces="application/json")
    public Map<String, Object> getStudentCommentTranslation(HttpServletRequest request, HttpServletResponse response,
                                                            @RequestBody TranslationVo translationVo){

        try {
            Long id = translationVo.getId();
            String text = translationVo.getText();
            Stopwatch stopwatch = Stopwatch.createStarted();
            logger.info("开始调用restClassroomsMaterials接口， 传入参数：id = {}", id);
            String result  = manageGatewayService.getTranslation(id);
            Boolean isSaved = false;
            if(StringUtils.isEmpty(result)){
                result = BaiduTranslateAPI.translate(text);
                isSaved = manageGatewayService.saveTranslation(id, result);
            }
            Map map = Maps.newHashMap();
            map.put("translation",result);
            map.put("isSaved",isSaved);
            long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
            logger.info("【getStudentCommentTranslation】，传入参数：id = {}。返回Json={}。耗时{}ms", id, JsonUtils.toJSONString(result), millis);
            return ApiResponseUtils.buildSuccessDataResp(map);
        } catch (Exception e) {
            logger.error("调用restClassroomsMaterial接口， 传入参数：lessonId = {}。抛异常", translationVo.getId());
            logger.error("调用restClassroomsMaterial接口， 传入参数：lessonId = {}。接口异常", e);
        }
        return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
    }

    @RequestMapping(value = "/saveStudentCommentTranslation", method  = RequestMethod.POST)
    public Map<String, Object> saveStudentCommentTranslation(HttpServletRequest request, HttpServletResponse response,
                                                             @RequestBody TranslationVo translationVo){
        try {
            Long id = translationVo.getId();
            String text = translationVo.getText();
            Stopwatch stopwatch = Stopwatch.createStarted();
            logger.info("开始调用restClassroomsMaterials接口， 传入参数：id = {},text = {}", id,text);
            Boolean result  = manageGatewayService.saveTranslation(id, text);
            Map map = Maps.newHashMap();
            map.put("status",result);
            long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
            logger.info("saveStudentCommentTranslation，传入参数：id = {},text = {}。返回Json={}。耗时{}ms", id,text, JsonUtils.toJSONString(result), millis);
            return ApiResponseUtils.buildSuccessDataResp(map);
        } catch (Exception e) {
            logger.error("调用restClassroomsMaterial接口， 传入参数：id = {},text = {}。抛异常: {}", translationVo.getId(),translationVo.getText(),e);//由于维龙的代码没有合上去，暂时这么处理
        }
        return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
    }
}
