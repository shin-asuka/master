package com.vipkid.recruitment.utils;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.Maps;

/**
 * 定义返回Map status 必须
 *
 * @author Along
 */
public class ResponseUtils {

    public static Logger logger = LoggerFactory.getLogger(ResponseUtils.class);

    /** 1. Successful response */
    public static Map<String, Object> responseSuccess() {
        return responseSuccess(null, null);
    }

    public static Map<String, Object> responseSuccess(Map<String, Object> data) {
        return responseSuccess(null, data);
    }

    public static Map<String, Object> responseSuccess(String info, Map<String, Object> data) {
        //compose the result
        Map<String, Object> result = Maps.newHashMap();
        result.put("status", true);
        if (StringUtils.isNotBlank(info)) {
            result.put("info", info);
        }
        if (MapUtils.isNotEmpty(data)) {
            result.putAll(data);
        }

        logger.info("result value:{}", JsonTools.getJson(result));

        return result;
    }

    /**  2. Fail response */

    public static Map<String, Object> responseFail(String info, Object object) {
        return responseFail(info, null, object.getClass());
    }

    public static Map<String, Object> responseFail(Map<String, Object> data, Object object) {
        return responseFail(null, data, object.getClass());
    }

    public static Map<String, Object> responseFail(String info, Class<?> clazz) {
        return responseFail(info, null, clazz);
    }
    
    public static Map<String, Object> responseFail(String info, Map<String, Object> data, Object object) {
        return responseFail(info, data, object.getClass());
    }

    public static Map<String, Object> responseFail(String info, Map<String, Object> data, Class<?> clazz) {
        return responseFail(info, data, clazz, null);
    }

    public static Map<String, Object> responseFail(String info, Object object,Throwable t) {
        return responseFail(info,object.getClass(),t);
    }
    
    public static Map<String, Object> responseFail(String info, Class<?> clazz,Throwable t) {
        return responseFail(info, null, clazz,t);
    }
    
    public static Map<String, Object> responseFail(String info, Map<String, Object> data, Class<?> clazz, Throwable t) {
        //1. print the error logger and the exception stack
        Logger clazzLogger = LoggerFactory.getLogger(clazz);
        if (clazzLogger == null) {
            clazzLogger = logger;
        }

        if (null != t) {
            clazzLogger.error(info, t);
        } else {
            clazzLogger.error(info);
        }

        //2. compose the result
        Map<String, Object> result = Maps.newHashMap();
        result.put("status", false);
        if (StringUtils.isNotBlank(info)) {
            result.put("info", info);
        }
        if (MapUtils.isNotEmpty(data)) {
            result.putAll(data);
        }

        return result;
    }

    /**  3. util functions */
    public static boolean isSuccess(Map<String, Object> data) {
        return MapUtils.getBooleanValue(data, "status");
    }

    public static boolean isFail(Map<String, Object> data) {
        return !isSuccess(data);
    }

}
