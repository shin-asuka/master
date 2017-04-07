package com.vipkid.rest.portal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * 实现描述:前端调用此接口记录日志用
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2017/4/7 下午5:17
 */
@RestController
@RequestMapping("/trace")
public class FePrintLogController {

    private static final Logger logger = LoggerFactory.getLogger(FePrintLogController.class);

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> printLogFromFE(String traceLog){
        //traceLog 是encode的值
        logger.info("print log from fe! content:{}",traceLog);
        return null;
    }
}
