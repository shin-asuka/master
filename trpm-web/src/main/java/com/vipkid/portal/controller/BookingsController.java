package com.vipkid.portal.controller;

import com.vipkid.portal.service.BookingsService;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.service.rest.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Map<String, Object> scheduled(@RequestParam String type,
                    @RequestParam(required = false, defaultValue = "0") int weekOffset) {
        Teacher teacher = loginService.getTeacher();
        return bookingsService.doSchedule(weekOffset, teacher.getId(), teacher.getTimezone(), type);
    }

}
