package com.vipkid.trpm.controller.h5;

import com.vipkid.file.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liyang on 2017/2/27.
 */
@Controller
@RequestMapping("/h5")
public class TestController {
    private static Logger logger = LoggerFactory.getLogger(TestController.class);

    @Deprecated
    @RequestMapping("/test/test1")
    public String test(HttpServletRequest request, HttpServletResponse response, Model model){
        logger.info("this is test");
        return StringUtils.EMPTY;
    }

}
