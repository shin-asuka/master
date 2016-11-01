/**
 * 
 */
package com.vipkid.file.utils;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Administrator
 * 
 */
public class ActionHelp {

    /**
     * 将字符串写入输出流
     * 
     * @param response
     * @param object
     */
    public static void WriteStrToOut(HttpServletResponse response, Object object) {
        String info = toJSONString(object);
        WriteStrToOut(response, info);
    }

    /**
     * 将字符串写入输出流
     * 
     * @param response
     * @param info
     */
    @SuppressWarnings("unused")
    public static void WriteStrToOut(HttpServletResponse response, String info) {
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        if (response == null) {
            return;
        }

        // 输出流
        PrintWriter out = null;
        String outInfo = "";

        if (info != null) {
            outInfo = info;
        }

        // 向前台发送信息
        try {
            out = response.getWriter();
            out.write(outInfo);
            // out.print(outInfo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    public static String toJSONString(Object object) {
        String str = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            str = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
        	e.printStackTrace();
        }
        return str;
    }

}
