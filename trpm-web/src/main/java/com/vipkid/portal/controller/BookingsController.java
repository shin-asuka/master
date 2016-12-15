package com.vipkid.portal.controller;

import com.vipkid.rest.config.RestfulConfig;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuguowen on 2016/12/15.
 */
@RestController
@RequestMapping("/api/portal")
public class BookingsController {

    @RequestMapping(value = "/scheduled", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public void scheduled(@RequestParam String type,
                    @RequestParam(required = false, defaultValue = "0") int weekOffset) {

    }

}
