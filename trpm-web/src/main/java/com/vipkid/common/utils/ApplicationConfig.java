package com.vipkid.common.utils;

import com.vipkid.file.utils.StringUtils;
import org.community.config.PropertyConfigurer;

/**
 * Created by liyang on 2017/4/11.
 */
public class ApplicationConfig {


    public static String getValue(String key,String defalut){
        String value = PropertyConfigurer.stringValue(key);
        if(StringUtils.isBlank(value)){
            return defalut;
        }
        return value;
    }

    public static String getValue(String key){
        return getValue(key,null);
    }
}
