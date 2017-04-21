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

    public static Map<String, Object> returnFail(String info, String jsonParam) {
        return returnFail(info, jsonParam , null);
    }

    public static Map<String, Object> returnFail(String info,Throwable t) {
        return returnFail(info, null, t);
    }
    
    public static Map<String, Object> returnFail(String info, String jsonParam,Throwable t) {        

        String message = info + "-logger["+jsonParam+"]";

        /*业务的异常，给屏蔽*/
        if (null != t && t instanceof  IllegalArgumentException) {
            logger.warn(message, t);
        }else if (null != t){
            logger.error(message,t);
        } else {
            //错误消息栈
            logger.warn(message);
            int j = 0;
            StackTraceElement[] elements = new Throwable().getStackTrace();
            for(int i = 0 ; i < elements.length; i++){
                if(!ReturnMapUtils.class.getCanonicalName().equals(elements[i].getClassName())){
                    if(j < 3)
                        logger.warn(elements[i]+"");
                    j++;
                }
            }
        }

        //2. compose the result
        Map<String, Object> result = Maps.newHashMap();
        result.put("status", false);
        if (StringUtils.isNotBlank(info)) {
            if(info.indexOf("Exception:") > -1){
                info = info.substring(info.indexOf("Exception:")+("Exception:".length()),info.length());
            }
            result.put("info", info);
            result.put("data", jsonParam);
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
