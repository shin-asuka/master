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
public class ReturnMapUtils {

    public static Logger logger = LoggerFactory.getLogger(ReturnMapUtils.class);

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

    public static Map<String, Object> returnFail(String info) {
        return returnFail(info, null,null);
    }

    public static Map<String, Object> returnFail(Map<String, Object> data) {
        return returnFail("error", data ,null);
    }
    
    public static Map<String, Object> returnFail(String info, Map<String, Object> data) {
        return returnFail(info, data , null);
    }

    public static Map<String, Object> returnFail(String info,Throwable t) {
        return returnFail(info, null, t);
    }
    
    public static Map<String, Object> returnFail(String info, Map<String, Object> data,Throwable t) {        

        if (null != t) {
            logger.error(info, t);
        } else {
            //错误消息栈
            logger.warn(info);
            StackTraceElement[] elements = new Throwable().getStackTrace();
            for(int i = 0 ; i < elements.length; i++){
                if(!elements[i].getClassName().equals(ReturnMapUtils.class.getCanonicalName())){
                    logger.warn(elements[i]+"");
                }
            }
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
