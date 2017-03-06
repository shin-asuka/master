package com.vipkid.portal.bookings.controller;

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
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.rest.RestfulController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
                logger.error("This online class ：{} does not exist.", onlineClassId);
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "This online class  does not exist.", onlineClassId);
            }

            if (  cancelReason==null|| String.valueOf(cancelReason).length() > 1000 ) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.error("This cancelReason ：{} is too long or is Empty.", cancelReason);
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "This cancelReason ：{} is too long.", onlineClassId);
            }
            Preconditions.checkArgument(request.getAttribute(TEACHER) != null);
            Teacher teacher = (Teacher) request.getAttribute(TEACHER);

            if (0 == teacher.getId()) {
                logger.error("This teacher ：{} have no jurisdiction .", teacher.getId());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "The teacher have no jurisdiction.", teacher.getId());
            }

            boolean isSuccess = bookingsService.cancelClassSuccess(Long.valueOf(onlineClassId + ""), teacher.getId(), String.valueOf(cancelReason));
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
                logger.error("This online class ：{} does not exist.", onlineClassId);
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "This online class  does not exist.", onlineClassId);
            }
            Preconditions.checkArgument(request.getAttribute(TEACHER) != null);
            Teacher teacher = (Teacher) request.getAttribute(TEACHER);

            if (0 == teacher.getId()) {
                logger.error("This teacher ：{} have no jurisdiction .", teacher.getId());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "The teacher have no jurisdiction.", teacher.getId());
            }

            String finishType = bookingsService.getFinishType(Long.valueOf(onlineClassId + ""), teacher.getId());
            if (StringUtils.isBlank(finishType)) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.error("This online class ：{} does not exist.", onlineClassId);
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "This online class  does not exist.", onlineClassId);
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

    /**
     * 判断课程是否能被取消
     * @param paramMap
     * @param request
     * @param response
     * @return
     */

    @RequestMapping(value = "/isCancelCourse", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> isCancelCourse(@RequestBody Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> dataMap = Maps.newHashMap();
        Object onlineClassId = paramMap.get("onlineClassId");
        try {

            if (onlineClassId == null || !StringUtils.isNumeric(onlineClassId + "")) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.error("This online class ：{} does not exist.", onlineClassId);
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "This online class  does not exist.", onlineClassId);
            }

            Preconditions.checkArgument(request.getAttribute(TEACHER) != null);
            Teacher teacher = (Teacher) request.getAttribute(TEACHER);

            if (0 == teacher.getId()) {
                logger.error("This teacher ：{} have no jurisdiction .", teacher.getId());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "The teacher have no jurisdiction.", teacher.getId());
            }
            boolean isCancel = bookingsService.isCancelCourse(Long.valueOf(onlineClassId + ""), teacher.getId());
            dataMap.put("isCancel", isCancel);
            if (!isCancel) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.warn("This online class ：{} cannot be cancelled.", onlineClassId);
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "This online class cannot be cancelled.", dataMap);
            }
            return ApiResponseUtils.buildSuccessDataResp(dataMap);
        } catch (IllegalArgumentException e) {
            logger.error("This online class :{} cannot be cancelled {}", onlineClassId, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExceptionUtils.getFullStackTrace(e));
        }
    }

}
