package com.vipkid.rest.portal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * 实现描述:前端调用此接口记录日志用,不走任何过滤器
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2017/4/7 下午5:17
 */
@RestController
public class TraceLogController {

    private static final Logger logger = LoggerFactory.getLogger(TraceLogController.class);

    @RequestMapping(value = "/trace",method = RequestMethod.GET)
    public Map<String, Object> traceLogFromFE(String traceLog){
        //traceLog 是encode的值
        logger.info("print log from fe! content:{}",traceLog);
        return null;
    }

    /**
     *
     * 探活接口
     *
     * @return
     */
    @RequestMapping(value = "/health",method = RequestMethod.GET)
    public String health(){
        return "success";
    }
}
