package com.vipkid.portal.controller;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.Maps;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.portal.entity.*;
import com.vipkid.portal.service.BookingsService;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.service.LoginService;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.Map;

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

    /* 获取 Scheduled 详细数据接口 */
    @RequestMapping(value = "/scheduled", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> scheduled(@RequestBody String body, HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            logger.info("Invocation scheduled() arguments: {}", body);
            ScheduledRequest scheduledRequest = JsonUtils.toBean(body, ScheduledRequest.class);

            Preconditions.checkArgument(StringUtils.isNotBlank(scheduledRequest.getType()));

            return bookingsService.doSchedule(scheduledRequest, loginService.getTeacher());
        } catch (IllegalArgumentException e) {
            logger.error("Illegal arguments error", e);

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            resultMap.put("error", "Illegal arguments format");
        } catch (Exception e) {
            logger.error("Internal server error", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            resultMap.put("error", "Server error");
        }
        return resultMap;
    }

    /* 创建 TimeSlot 接口 */
    @RequestMapping(value = "/createTimeSlot", method = RequestMethod.PUT, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> createTimeSlot(@RequestBody String body, HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            logger.info("Invocation createTimeSlot() arguments: {}", body);
            TimeSlotCreateRequest timeSlotCreateRequest = JsonUtils.toBean(body, TimeSlotCreateRequest.class);

            Preconditions.checkArgument(StringUtils.isNotBlank(timeSlotCreateRequest.getType()));
            Preconditions.checkArgument(StringUtils.isNotBlank(timeSlotCreateRequest.getScheduledDateTime()));

            return bookingsService.doCreateTimeSlotWithLock(timeSlotCreateRequest, loginService.getTeacher());
        } catch (IllegalArgumentException e) {
            logger.error("Illegal arguments error", e);

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            resultMap.put("error", "Illegal arguments format");
        } catch (Exception e) {
            logger.error("Internal server error", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            resultMap.put("error", "Server error");
        }
        return resultMap;
    }

    /* 取消 TimeSlot 接口 */
    @RequestMapping(value = "/cancelTimeSlot", method = RequestMethod.DELETE, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> cancelTimeSlot(@RequestBody String body, HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            logger.info("Invocation cancelTimeSlot() arguments: {}", body);
            TimeSlotCancelRequest timeSlotCancelRequest = JsonUtils.toBean(body, TimeSlotCancelRequest.class);

            Preconditions.checkArgument(0 != timeSlotCancelRequest.getOnlineClassId());

            return bookingsService.doCancelTimeSlot(timeSlotCancelRequest, loginService.getTeacher());
        } catch (IllegalArgumentException e) {
            logger.error("Illegal arguments error", e);

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            resultMap.put("error", "Illegal arguments format");
        } catch (Exception e) {
            logger.error("Internal server error", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            resultMap.put("error", "Server error");
        }
        return resultMap;
    }

    @RequestMapping(value = "/set24Hours", method = RequestMethod.PUT, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> set24Hours(@RequestBody String body, HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            logger.info("Invocation set24Hours() arguments: {}", body);
            Set24HourRequest set24HourRequest = JsonUtils.toBean(body, Set24HourRequest.class);

            Preconditions.checkArgument(CollectionUtils.isNotEmpty(set24HourRequest.getOnlineClassIds()));
            Preconditions.checkArgument(!set24HourRequest.getOnlineClassIds().stream().anyMatch(id -> 0 == id));

            return bookingsService.doSet24Hours(set24HourRequest, loginService.getTeacher());
        } catch (IllegalArgumentException e) {
            logger.error("Illegal arguments error", e);

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            resultMap.put("error", "Illegal arguments format");
        } catch (Exception e) {
            logger.error("Internal server error", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            resultMap.put("error", "Server error");
        }
        return resultMap;
    }

    @RequestMapping(value = "/delete24Hours", method = RequestMethod.DELETE, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> delete24Hours(@RequestBody String body, HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            logger.info("Invocation delete24Hours() arguments: {}", body);
            Delete24HourRequest delete24HourRequest = JsonUtils.toBean(body, Delete24HourRequest.class);

            Preconditions.checkArgument(CollectionUtils.isNotEmpty(delete24HourRequest.getOnlineClassIds()));
            Preconditions.checkArgument(!delete24HourRequest.getOnlineClassIds().stream().anyMatch(id -> 0 == id));

            return bookingsService.doDelete24Hours(delete24HourRequest, loginService.getTeacher());
        } catch (IllegalArgumentException e) {
            logger.error("Illegal arguments error", e);

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            resultMap.put("error", "Illegal arguments format");
        } catch (Exception e) {
            logger.error("Internal server error", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            resultMap.put("error", "Server error");
        }
        return resultMap;
    }

    @RequestMapping(value = "/getTips", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> getTips(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            return bookingsService.getTips(request, response, loginService.getTeacher());
        } catch (Exception e) {
            logger.error("Internal server error", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            resultMap.put("error", "Server error");
        }
        return resultMap;
    }

}
