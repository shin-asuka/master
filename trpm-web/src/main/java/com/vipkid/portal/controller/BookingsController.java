package com.vipkid.portal.controller;

import com.google.api.client.util.Maps;
import com.vipkid.enums.OnlineClassEnum.CourseType;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.http.service.AnnouncementHttpService;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.vo.Announcement;
import com.vipkid.portal.entity.*;
import com.vipkid.portal.service.BookingsService;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.utils.ApiResponseUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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

/**
 * Created by liuguowen on 2016/12/15.
 */
@RestController
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
    public Map<String, Object> scheduled(@RequestBody String body, HttpServletResponse response) {
        try {
            logger.info("Invocation scheduled() arguments: {}", body);
            ScheduledRequest scheduledRequest = JsonUtils.toBean(body, ScheduledRequest.class);

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
    public Map<String, Object> cancelTimeSlot(@RequestBody String body, HttpServletResponse response) {
        try {
            logger.info("Invocation cancelTimeSlot() arguments: {}", body);
            TimeSlotCancelRequest timeSlotCancelRequest = JsonUtils.toBean(body, TimeSlotCancelRequest.class);

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
    public Map<String, Object> delete24Hours(@RequestBody String body, HttpServletResponse response) {
        try {
            logger.info("Invocation delete24Hours() arguments: {}", body);
            Delete24HourRequest delete24HourRequest = JsonUtils.toBean(body, Delete24HourRequest.class);

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
            return ApiResponseUtils.buildSuccessDataResp(announcements);
        } catch (Exception e) {
            logger.error("Internal server error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            ExceptionUtils.getFullStackTrace(e));
        }
    }

}
