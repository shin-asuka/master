package com.vipkid.http.service;

import com.vipkid.http.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * Created by zhangzhaojun on 2017/2/27.
 */
public class ScalperService extends HttpBaseService {
    private static final Logger logger = LoggerFactory.getLogger(ScalperService.class);

    /**
     * 创建TimeSlot
     *
     * @param requestMap
     * @return
     */
    public String createTimeSlot(Map<String, Object> requestMap) {
        try {
            String url = new StringBuilder(super.serverAddress)
                    .append("/createTimeSlot").toString();
            return WebUtils.postNameValuePair(url, requestMap);
        } catch (Exception e) {
            logger.error("Http Post create TimeSlot fail", e);
        }
        return null;
    }

    /**
     * 取消TimeSlot
     *
     * @param requestMap
     * @return
     */
    public String cancelTimeSlot(Map<String, Object> requestMap) {
        try {
            String url = new StringBuilder(super.serverAddress)
                    .append("/cancelTimeSlot").toString();
            return WebUtils.postNameValuePair(url, requestMap);
        } catch (Exception e) {
            logger.error("Http Post cancel TimeSlot fail", e);
        }
        return null;
    }

    /**
     * 自主取消课程
     *
     * @param requestMap
     * @return
     */
    public String cancelClass(Map<String, Object> requestMap) {
        try {
            String url = new StringBuilder(super.serverAddress)
                    .append("/management/finish").toString();
            return WebUtils.postNameValuePair(url, requestMap);
        } catch (Exception e) {
            logger.error("Http Post cancel course fail", e);
        }
        return null;
    }

}
