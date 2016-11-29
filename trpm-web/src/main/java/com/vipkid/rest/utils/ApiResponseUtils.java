package com.vipkid.rest.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 对 Response body 做一个包装, 便于返回相应消息
 *
 * @author Austin.Cao  Date: 9/6/16
 */
public class ApiResponseUtils {


    /**
     * 成功返回
     *
     * @param msg
     * @return
     */
    public static Map<String, Object> buildSuccessDataResp(String msg) {
        Map<String, Object> retMap = buildResponse(true, 0, msg, null);
        return retMap;
    }

    /**
     * 成功返回
     *
     * @param data
     * @return
     */
    public static Map<String, Object> buildSuccessDataResp(Object data) {
        Map<String, Object> retMap = buildResponse(true, 0, null, data);
        return retMap;
    }




    /**
     * 错误返回, 只有错误码和错误消息
     *
     * @param errCode
     * @param errMsg
     * @return
     */
    public static Map<String, Object> buildErrorResp(int errCode, String errMsg) {
        Map<String, Object> retMap = buildErrorResp(errCode, errMsg, null);
        return retMap;
    }

    /**
     * 错误返回, 带有 data 信息
     *
     * @param data
     * @return
     */
    public static Map<String, Object> buildErrorResp(int errCode, String errMsg, Object data) {
        Map<String, Object> retMap = buildResponse(false, errCode, errMsg, data);
        return retMap;
    }

    /**
     * 返回消息体
     *
     * @param data
     * @return
     */
    public static Map<String, Object> buildResponse(boolean ret, int errorCode, String errMsg, Object data) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("ret", ret);
        retMap.put("errCode", errorCode);
        retMap.put("errMsg", errMsg);
        retMap.put("data", data);
        return retMap;
    }

}
