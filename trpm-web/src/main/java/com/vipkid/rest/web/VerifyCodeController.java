package com.vipkid.rest.web;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Maps;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.service.passport.VerifyCodeService;

@RestController
public class VerifyCodeController {
    private Logger logger = LoggerFactory.getLogger(VerifyCodeController.class.getSimpleName());
    @Resource
    private VerifyCodeService verifyCodeService;

    /**
     * 发送图片验证码
     *
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/user/verifycode", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    @ResponseBody
    public Object getVerifyCode() throws IOException {
        logger.info("getVerifyCode start");
        Map<String,Object> result = Maps.newHashMap();
        result.put("status", HttpStatus.OK.value());
        result.put("info",verifyCodeService.getVerifyCode());
        return JsonTools.getJson(result);
    }
}
