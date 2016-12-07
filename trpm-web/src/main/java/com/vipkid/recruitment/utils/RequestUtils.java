package com.vipkid.recruitment.utils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestUtils {
    
    private static Logger logger = LoggerFactory.getLogger(RequestUtils.class);
    
    public static final String readRequestBody(HttpServletRequest request) {
        //优先读取from
        Map<?,?> parame = request.getParameterMap();
        if(MapUtils.isNotEmpty(parame)){
            logger.info("读取到from 信息");
            return JsonTools.getJson(parame);
        }
        
        //读取body
        String str = "";
        /*
        try {
            logger.info("读取到json 信息");
            BufferedReader br = request.getReader();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                str += inputLine;
            }
            br.close();
        } catch (IOException e) {
            logger.error("IOException: " + e);
        }
        */
        return str;
    }
}
