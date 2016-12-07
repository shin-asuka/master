package com.vipkid.recruitment.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestUtils {
    
    private static Logger logger = LoggerFactory.getLogger(RequestUtils.class);
    
    public static final String readRequestBody(HttpServletRequest request) {
        //读取from
        Map<?,?> parame = request.getParameterMap();
        if(MapUtils.isNotEmpty(parame)){
            return JsonTools.getJson(parame);
        }
        //读取body
        /*
        HttpServletRequest _request = request;
        int contentLen = _request.getContentLength();
        if (contentLen > 0) {
            try {    
            InputStream is = _request.getInputStream();
            int readLen = 0;
            int readLengthThisTime = 0;
            byte[] message = new byte[contentLen];                
                while (readLen != contentLen) {
                        readLengthThisTime = is.read(message, readLen, contentLen - readLen);
                        if (readLengthThisTime == -1) {// Should not happen.
                                break;
                        }
                        readLen += readLengthThisTime;
                }
                return new String(message);
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
        */
        return "";
    }
}
