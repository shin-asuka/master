package com.vipkid.portal.bookings.controller;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.common.base.Preconditions;
import com.vipkid.enums.OnlineClassEnum.CourseType;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.file.utils.Encodes;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.http.service.AnnouncementHttpService;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.vo.Announcement;
import com.vipkid.portal.bookings.entity.*;
import com.vipkid.portal.bookings.service.BookingsService;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.INCENTIVE;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.service.portal.TeacherService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.vipkid.enums.OnlineClassEnum.ClassType;
import static com.vipkid.rest.RestfulController.TEACHER;

/**
 * Created by liuguowen on 2016/12/15.
 */
@RestController
@RestInterface(lifeCycle = {TeacherEnum.LifeCycle.REGULAR})
@RequestMapping("/portal")
public class BookingsController {

    private static final Logger logger = LoggerFactory.getLogger(BookingsController.class);

    @Autowired
    private BookingsService bookingsService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private AnnouncementHttpService announcementHttpService;

    @Autowired
    private RedisProxy redisProxy;

    @Autowired
    private TeacherService teacherService;

    private final static String EMERGENCY_CHINESE  ="紧急情况（个人或者家庭成员出现疾病发作、意外事故、突发不测等）";
    private final static String PERSONAL_REASON_CHINESE ="个人原因（日程冲突、安排不当等）";
    private final static String UNRELIABLE_INTERNET_ACCESS_CHINESE  ="网络故障（住址搬迁、旅行在途等）";


    private final static String EMERGENCY_ENGLISH  ="Emergency (personal or family member sickness, accident, mishap, etc.)";
    private final static String PERSONAL_REASON_ENGLISH ="Personal reason (schedule conflict, prior oversight, etc.)";
    private final static String UNRELIABLE_INTERNET_ACCESS_ENGLISH  ="Unreliable internet access (relocation, travel, etc.)";
    /* 获取 Scheduled 详细数据接口 */
    @RequestMapping(value = "/scheduled", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> scheduled(ScheduledRequest scheduledRequest, HttpServletResponse response) {
        try {
            logger.info("Invocation scheduled() arguments: {}", JsonUtils.toJSONString(scheduledRequest));

            Map<String, Object> checkMap = checkArgumentForType(scheduledRequest.getType());
            if (Objects.nonNull(checkMap.get("errMsg"))) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return checkMap;
            }

            Map<String, Object> dataMap = bookingsService.doSchedule(scheduledRequest, loginService.getTeacher());
            return ApiResponseUtils.buildSuccessDataResp(dataMap);
        } catch (Exception e) {
            logger.error("Internal server error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExceptionUtils.getFullStackTrace(e));
        }
    }

    /* 创建 TimeSlot 接口 */
    @RequestMapping(value = "/createTimeSlot", method = RequestMethod.PUT, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> createTimeSlot(@RequestBody String body, HttpServletResponse response) {
        try {
            logger.info("Invocation createTimeSlot() arguments: {}", body);
            TimeSlotCreateRequest timeSlotCreateRequest = JsonUtils.toBean(body, TimeSlotCreateRequest.class);

            Map<String, Object> checkMap = checkArgumentForType(timeSlotCreateRequest.getType());
            if (Objects.nonNull(checkMap.get("errMsg"))) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return checkMap;
            }

            if (StringUtils.isBlank(timeSlotCreateRequest.getScheduledDateTime())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
                        "Argument 'scheduledDateTime' is required");
            }

            Map<String, Object> dataMap =
                    bookingsService.doCreateTimeSlotWithLock(timeSlotCreateRequest, loginService.getTeacher());
            if (Objects.nonNull(dataMap.get("error"))) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.FORBIDDEN.value(), (String) dataMap.get("error"));
            } else {
                return ApiResponseUtils.buildSuccessDataResp(dataMap);
            }
        } catch (Exception e) {
            logger.error("Internal server error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExceptionUtils.getFullStackTrace(e));
        }
    }

    private Map<String, Object> checkArgumentForType(String type) {
        if (StringUtils.isBlank(type)) {
            return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "Argument 'type' is required");
        } else if (!(CourseType.isMajor(type) || CourseType.isPracticum(type))) {
            return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "Argument 'type' is illegal");
        }
        return Maps.newHashMap();
    }

    /* 取消 TimeSlot 接口 */
    @RequestMapping(value = "/cancelTimeSlot", method = RequestMethod.DELETE, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> cancelTimeSlot(TimeSlotCancelRequest timeSlotCancelRequest,
                                              HttpServletResponse response) {
        try {
            logger.info("Invocation cancelTimeSlot() arguments: {}", JsonUtils.toJSONString(timeSlotCancelRequest));

            if (0 == timeSlotCancelRequest.getOnlineClassId()) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());

                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
                        "Argument 'onlineClassId' is illegal");
            }

            Map<String, Object> dataMap =
                    bookingsService.doCancelTimeSlot(timeSlotCancelRequest, loginService.getTeacher());
            if (Objects.nonNull(dataMap.get("error"))) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.FORBIDDEN.value(), (String) dataMap.get("error"));
            } else {
                return ApiResponseUtils.buildSuccessDataResp(dataMap);
            }
        } catch (Exception e) {
            logger.error("Internal server error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExceptionUtils.getFullStackTrace(e));
        }
    }

    @RequestMapping(value = "/set24Hours", method = RequestMethod.PUT, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> set24Hours(@RequestBody String body, HttpServletResponse response) {
        try {
            logger.info("Invocation set24Hours() arguments: {}", body);
            Set24HourRequest set24HourRequest = JsonUtils.toBean(body, Set24HourRequest.class);

            Map<String, Object> checkMap = checkArgumentForOnlineClassIds(set24HourRequest.getOnlineClassIds());
            if (Objects.nonNull(checkMap.get("errMsg"))) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return checkMap;
            }

            if (!(set24HourRequest.getClassType() == ClassType.MAJOR.val()
                    || set24HourRequest.getClassType() == ClassType.PRACTICUM.val())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());

                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
                        "Argument 'classType' is illegal");
            }

            Map<String, Object> dataMap = bookingsService.doSet24Hours(set24HourRequest, loginService.getTeacher());
            if (Objects.nonNull(dataMap.get("error"))) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.FORBIDDEN.value(), (String) dataMap.get("error"));
            } else {
                return ApiResponseUtils.buildSuccessDataResp(dataMap);
            }
        } catch (Exception e) {
            logger.error("Internal server error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExceptionUtils.getFullStackTrace(e));
        }
    }

    @RequestMapping(value = "/delete24Hours", method = RequestMethod.DELETE, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> delete24Hours(Delete24HourRequest delete24HourRequest, HttpServletResponse response) {
        try {
            logger.info("Invocation delete24Hours() arguments: {}", JsonUtils.toJSONString(delete24HourRequest));

            Map<String, Object> checkMap = checkArgumentForOnlineClassIds(delete24HourRequest.getOnlineClassIds());
            if (Objects.nonNull(checkMap.get("errMsg"))) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return checkMap;
            }

            Map<String, Object> dataMap =
                    bookingsService.doDelete24Hours(delete24HourRequest, loginService.getTeacher());
            return ApiResponseUtils.buildSuccessDataResp(dataMap);
        } catch (Exception e) {
            logger.error("Internal server error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExceptionUtils.getFullStackTrace(e));
        }
    }

    private Map<String, Object> checkArgumentForOnlineClassIds(List<Long> onlineClassIds) {
        if (CollectionUtils.isEmpty(onlineClassIds)) {
            return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
                    "Argument 'onlineClassIds' is required");
        } else if (onlineClassIds.stream().anyMatch(id -> 0 == id)) {
            return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
                    "Argument 'onlineClassIds' is illegal");
        }
        return Maps.newHashMap();
    }

    @RequestMapping(value = "/getTips", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> getTips(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> dataMap = bookingsService.getTips(request, response, loginService.getTeacher());
            return ApiResponseUtils.buildSuccessDataResp(dataMap);
        } catch (Exception e) {
            logger.error("Internal server error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExceptionUtils.getFullStackTrace(e));
        }
    }

    @RequestMapping(value = "/getAnnouncements", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> getAnnouncements(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Announcement> announcements = announcementHttpService.findAnnouncementList();
            if (CollectionUtils.isNotEmpty(announcements)) {
                for (Announcement announcement : announcements) {
                    String content = announcement.getContent();
                    if (StringUtils.isNotBlank(content)) {
                        String unHtml = Encodes.unescapeHtml(content);
                        announcement.setContent(unHtml);
                    }
                }
            }
            return ApiResponseUtils.buildSuccessDataResp(announcements);
        } catch (Exception e) {
            logger.error("Internal server error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExceptionUtils.getFullStackTrace(e));
        }
    }

    /**
     * 老师自动取消已经booked的课程
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/cancelClass", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> cancelClass(@RequestBody Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response) {
        Object onlineClassId = paramMap.get("onlineClassId");
        Object cancelReason = paramMap.get("cancelReason");


        try {


            if (onlineClassId == null || !StringUtils.isNumeric(onlineClassId + "")) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.warn("This online class ：{} does not exist.", onlineClassId);
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "This online class  does not exist.", onlineClassId);
            }

            if (  cancelReason==null|| String.valueOf(cancelReason).length() > 1000 ) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.error("This cancelReason ：{} is too long or is Empty.", cancelReason);
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "This cancelReason ：{} is too long or is Empty.", onlineClassId);
            }
            String finishReason = String.valueOf(cancelReason);
            if(StringUtils.equalsIgnoreCase(finishReason,EMERGENCY_ENGLISH)){
                finishReason = EMERGENCY_CHINESE;
            }

            if(StringUtils.equalsIgnoreCase(finishReason,PERSONAL_REASON_ENGLISH)){
                finishReason = PERSONAL_REASON_CHINESE;
            }

            if(StringUtils.equalsIgnoreCase(finishReason,UNRELIABLE_INTERNET_ACCESS_ENGLISH)){
                finishReason = UNRELIABLE_INTERNET_ACCESS_CHINESE;
            }

            Preconditions.checkArgument(request.getAttribute(TEACHER) != null);
            Teacher teacher = (Teacher) request.getAttribute(TEACHER);

            if (0 == teacher.getId()) {
                logger.warn("This teacher ：{} have no jurisdiction .", teacher.getId());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "The teacher have no jurisdiction.", teacher.getId());
            }

            boolean isSuccess = bookingsService.cancelClassSuccess(Long.valueOf(onlineClassId + ""), teacher.getId(), finishReason);
            if (!isSuccess) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.error("cancel online class:{} was Failed.", onlineClassId);
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "cancel online class was Failed.", onlineClassId);
            }
            return ApiResponseUtils.buildSuccessDataResp("This online class was canceled");
        } catch (IllegalArgumentException e) {
            logger.error("cancel online class:{} is Exception {}", onlineClassId, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExceptionUtils.getFullStackTrace(e));
        }


    }


    /**
     * 查询finishType
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryFinishType", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> queryFinishType(@RequestBody Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> dataMap = Maps.newHashMap();
        Object onlineClassId = paramMap.get("onlineClassId");
        try {
            if (onlineClassId == null || !StringUtils.isNumeric(onlineClassId + "")) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.warn("This online class ：{} does not exist.", onlineClassId);
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "This online class  does not exist.", onlineClassId);
            }
            Preconditions.checkArgument(request.getAttribute(TEACHER) != null);
            Teacher teacher = (Teacher) request.getAttribute(TEACHER);

            if (0 == teacher.getId()) {
                logger.warn("This teacher ：{} have no jurisdiction .", teacher.getId());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "The teacher have no jurisdiction.", teacher.getId());
            }

            String finishType = bookingsService.getFinishType(Long.valueOf(onlineClassId + ""), teacher.getId());
            if (StringUtils.isBlank(finishType)) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.info("This online class ：{} cannot cancel.", onlineClassId);
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "This online class  cannot cancel.", onlineClassId);
            }
            dataMap.put("finishType", finishType);

            return ApiResponseUtils.buildSuccessDataResp(dataMap);
        } catch (IllegalArgumentException e) {
            logger.error("Get online class:{} finishType is Exception {}", onlineClassId, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExceptionUtils.getFullStackTrace(e));
        }

    }



    @ResponseBody
    @RequestMapping(value = "/getIncentivesInitCount", method = RequestMethod.GET)
    public Map<String, Object> getIncentivesInitCount(String teacherId) {
        logger.info("getIncentivesInitCount ：teacherId={} .", teacherId);
        if(StringUtils.isBlank(teacherId)){
            return ApiResponseUtils.buildErrorResp(HttpStatus.NOT_IMPLEMENTED.value(),
                    "参数为空");
        }
        String count=null;
        Integer countInt=0;
        try{
             count=redisProxy.get(ApplicationConstant.RedisConstants.INCENTIVE_FOR_APRIL+teacherId);
            if(StringUtils.isBlank(count)){
                countInt=teacherService.incentivesTeacherInit(teacherId);
                if(countInt ==0){
                    return ApiResponseUtils.buildErrorResp(HttpStatus.OK.value(),
                            "没有查到具体数据");
                }
                count=countInt+"";
            }
            Map<String, Object> dataMap = Maps.newHashMap();
            dataMap.put("errCode",HttpStatus.OK.value());
            dataMap.put("errMsg","成功");
            dataMap.put("data","count");
            return dataMap;

        }catch (Exception e){
            logger.warn("getIncentivesInitCount fail：teacherId={},error={} ", teacherId,ExceptionUtils.getFullStackTrace(e));
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExceptionUtils.getFullStackTrace(e));
        }


    }

    /**
     * 查询finishType
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getIncentives", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> getIncentives(@RequestBody Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> dataMap = Maps.newHashMap();
        Object from = paramMap.get("from");
        Object to = paramMap.get("to");

        try {

            Preconditions.checkArgument(request.getAttribute(TEACHER) != null);
            Teacher teacher = (Teacher) request.getAttribute(TEACHER);
			if (from == null || to == null ||!(from instanceof Timestamp)||!(to instanceof Timestamp)) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				logger.warn("wrong parameters{} where get incentives ", teacher.getId());
				return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
						"wrong parameters where get incentives ,{}.", teacher.getId());
			}
			if (0 == teacher.getId()) {
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
						"The teacher have no jurisdiction.", teacher.getId());
			}

			List<Map<String, Object>> classes = bookingsService.findIncentiveClasses((Timestamp)from,(Timestamp)to,teacher.getId());
			List<Map<String, Object>> resultClasses = Lists.newArrayList();
			Long incentiveCount = bookingsService.getIncentiveCount(teacher.getId());
			if (classes != null && classes.size() > incentiveCount) {
				for (int i = incentiveCount.intValue(); i < classes.size(); i++) {
					resultClasses.add(classes.get(i));
				}
			}
			dataMap.put("incentiveClassList", resultClasses);
            return ApiResponseUtils.buildSuccessDataResp(dataMap);
        } catch (IllegalArgumentException e) {
            logger.error("Get getIncentives  Exception {}", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExceptionUtils.getFullStackTrace(e));
        }

    }

    @RequestMapping(value = "/getIncentiveCount", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> getIncentiveCount(@RequestBody Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> dataMap = Maps.newHashMap();
		Object from = paramMap.get("from");
		Object to = paramMap.get("to");

		try {

			Preconditions.checkArgument(request.getAttribute(TEACHER) != null);
			Teacher teacher = (Teacher) request.getAttribute(TEACHER);
			if (from == null || to == null || !(from instanceof Timestamp) || !(to instanceof Timestamp)) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				logger.warn("wrong parameters{} where get incentives ", teacher.getId());
				return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
						"wrong parameters where get incentives ,{}.", teacher.getId());
			}
			if (0 == teacher.getId()) {
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
						"The teacher have no jurisdiction.", teacher.getId());
			}
			Long incentiveCount = bookingsService.getIncentiveCount(teacher.getId());
			Map<String, Long> resultmap = Maps.newHashMap();

			bookingsService.countOnlineClassesByStartTimeAndEndTime(INCENTIVE.INCENTIVE_APRIL_01,
					INCENTIVE.INCENTIVE_APRIL_10, teacher.getId(), incentiveCount, resultmap);
			bookingsService.countOnlineClassesByStartTimeAndEndTime(INCENTIVE.INCENTIVE_APRIL_10,
					INCENTIVE.INCENTIVE_APRIL_17, teacher.getId(), incentiveCount, resultmap);
			bookingsService.countOnlineClassesByStartTimeAndEndTime(INCENTIVE.INCENTIVE_APRIL_17,
					INCENTIVE.INCENTIVE_APRIL_23, teacher.getId(), incentiveCount, resultmap);
			bookingsService.countOnlineClassesByStartTimeAndEndTime((Date) INCENTIVE.INCENTIVE_APRIL_23,
					INCENTIVE.INCENTIVE_APRIL_30, teacher.getId(), incentiveCount, resultmap);
			dataMap.put("incentiveCountMap", resultmap);

			return ApiResponseUtils.buildSuccessDataResp(dataMap);
		} catch (IllegalArgumentException e) {
			logger.error("Get getIncentives  count"
					+ "Exception {}", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					ExceptionUtils.getFullStackTrace(e));
		}
	}




}
