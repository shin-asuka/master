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
public class MapReturnUtils {

    public static Logger logger = LoggerFactory.getLogger(MapReturnUtils.class);

    /** 1. Successful return */
    public static Map<String, Object> returnSuccess() {
        return returnSuccess(null, null);
    }

    public static Map<String, Object> returnSuccess(Map<String, Object> data) {
        return returnSuccess(null, data);
    }

    public static Map<String, Object> returnSuccess(String info, Map<String, Object> data) {
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

    /**  2. Fail return */

    public static Map<String, Object> returnFail(String info, Object object) {
        return returnFail(info, null, object.getClass());
    }

    public static Map<String, Object> returnFail(Map<String, Object> data, Object object) {
        return returnFail(null, data, object.getClass());
    }

    public static Map<String, Object> returnFail(String info, Class<?> clazz) {
        return returnFail(info, null, clazz);
    }
    
    public static Map<String, Object> returnFail(String info, Map<String, Object> data, Object object) {
        return returnFail(info, data, object.getClass());
    }

    public static Map<String, Object> returnFail(String info, Map<String, Object> data, Class<?> clazz) {
        return returnFail(info, data, clazz, null);
    }

    public static Map<String, Object> returnFail(String info, Object object,Throwable t) {
        return returnFail(info,object.getClass(),t);
    }
    
    public static Map<String, Object> returnFail(String info, Class<?> clazz,Throwable t) {
        return returnFail(info, null, clazz,t);
    }
    
    public static Map<String, Object> returnFail(String info, Map<String, Object> data, Class<?> clazz, Throwable t) {
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
