package com.vipkid.portal.controller;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.Maps;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.portal.entity.ScheduledRequest;
import com.vipkid.portal.service.BookingsService;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.service.rest.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by liuguowen on 2016/12/15.
 */
@RestController
@RequestMapping("/portal")
public class BookingsController {

    @Autowired
    private BookingsService bookingsService;

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/scheduled", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> scheduled(@RequestBody String body, HttpServletResponse response) {
        try {
            ScheduledRequest scheduledRequest = JsonUtils.toBean(body, ScheduledRequest.class);
            Preconditions.checkArgument(StringUtils.isNotBlank(scheduledRequest.getType()));

            Teacher teacher = loginService.getTeacher();
            return bookingsService.doSchedule(scheduledRequest.getWeekOffset(), teacher.getId(), teacher.getTimezone(),
                            scheduledRequest.getType());
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("error", "Wrong params format");
            return resultMap;
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("error", "Server error");
            return resultMap;
        }
    }

}
