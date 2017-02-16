package com.vipkid.rest.validation.tools;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectUtils {
    
    private static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

    /** 
     * 根据属性名获取属性值 
     * */  
    public static Object getFieldValueByName(String fieldName, Object o) {  
        try {    
           String firstLetter = fieldName.substring(0, 1).toUpperCase();    
           String getter = "get" + firstLetter + fieldName.substring(1);    
           Method method = o.getClass().getMethod(getter, new Class[] {});    
           Object value = method.invoke(o, new Object[] {});    
           return value;    
       } catch (Exception e) {    
           logger.error(e.getMessage(),e);    
           return null;    
       }    
    }   

}
