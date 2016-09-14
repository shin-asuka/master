package com.vipkid.trpm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LessonSerialNumber {
    
    static Logger logger = LoggerFactory.getLogger(LessonSerialNumber.class);
    
    /**
     * 对serialNumber的处理
     * @Author:ALong (ZengWeiLong)
     * @param serialNumber
     * @return    
     * String
     * @date 2016年5月16日
     */
    public static String serialNumber(String serialNumber){
        try{
            serialNumber = serialNumber.toLowerCase();
            String[] name = serialNumber.split("-");
            if(serialNumber.startsWith("t")){
                return "新生体验课";
            }else if(serialNumber.startsWith("c")){
                if(isInt(name[4])){
                    serialNumber = name[1].replaceAll("l", "Level ") + name[2].replaceAll("u", " Unit ") + " Lesson " + name[4];
                    logger.error("识别为主修课程:" + serialNumber);
                    return serialNumber;
                }
            }
            logger.error("不需要微信发送的serialNumber：" + serialNumber);
        }catch(Exception e){
            logger.error("不需要微信发送的serialNumber：" + serialNumber + "，Error:"+e.getMessage());
        }
        return null;
    }
    
    
    public static boolean isInt(String input) {
        Matcher matcher = Pattern.compile("^-?\\d+$").matcher(input);
        return matcher.matches();
    }

    public static Integer getLessonNoFromSn (String lessonSn){
        String lessonNo = lessonSn.substring(lessonSn.lastIndexOf("-")+1);
        lessonNo = lessonNo.substring(lessonNo.indexOf("L")+1);
        return Integer.parseInt(lessonNo);
    }
}
