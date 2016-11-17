package com.vipkid.recruitment.utils;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.Maps;
/**
 * 定义返回Map status 必须 
 * @author Along 
 *
 */
public class ResponseUtils {

    public static Map<String,Object> responseFail(String info,Map<String,Object> map,Class<?> c){
        Logger logger = LoggerFactory.getLogger(c);
        logger.info(info);
        Map<String,Object> prames = Maps.newHashMap();
        prames.put("status", false);
        if(StringUtils.isNotBlank(info)){
            prames.put("info", info);
        }
        if(MapUtils.isNotEmpty(map)){
            prames.putAll(map);
        }
        return prames;
    }
    
    public static Map<String,Object> responseFail(String info,Map<String,Object> map,Object o){
        return responseFail(info,map, o.getClass());
    }
    
    public static Map<String,Object> responseFail(String info,Class<?> c){
        return responseFail(info,null,c);
    }
    
    public static Map<String,Object> responseFail(Map<String,Object> map,Class<?> c){
        return responseFail(null,map,c);
    }
    
    public static Map<String,Object> responseFail(String info,Object o){
        return responseFail(info,null,o.getClass());
    }
    
    public static Map<String,Object> responseFail(Map<String,Object> map,Object o){
        return responseFail(null,map,o.getClass());
    }
    
    //
    
    public static Map<String,Object> responseSuccess(){
        return responseSuccess(null,null);
    }
    
    public static Map<String,Object> responseSuccess(Map<String,Object> map){
        return responseSuccess(null,map);
    }
        
    public static Map<String,Object> responseSuccess(String info,Map<String,Object> map){
        Map<String,Object> prames = Maps.newHashMap();
        prames.put("status", true);
        if(StringUtils.isNotBlank(info)){
            prames.put("info", info);
        }
        if(MapUtils.isNotEmpty(map)){
            prames.putAll(map);
        }
        return prames;
    }
    
    public static boolean isSuccess(Map<String,Object> map){
        return MapUtils.getBooleanValue(map,"status");
    }
    
    public static boolean isFail(Map<String,Object> map){
        return !isSuccess(map);
    }
    
}
