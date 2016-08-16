package com.vipkid.trpm.weixin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.community.config.PropertyConfigurer;


public class MessageConfig {
    
    public static final String REQUEST_URL = getAppserverUrl()+"/api/service/public/wechatmessage/sendText";
    
    //public static final String REQUEST_URL = "http://wzq.ngrok.natapp.cn/api/service/public/wechatmessage/sendText";
    
    public static final String ENCRYPT_KEY_SUFFIX  = "Teacher";
    
    
    public static final String ENCRYPT_KEY_PREFIX = "Teacher_Port_To_Vipkid_Parent";
    
    /**
     * 最多请求次数
     */
    public static final int MAX_COUNT = 6;
    
    /**
     * 请求失败10秒后自动重新请求
     */
    public static final int MILLISECOND = 10000;

    /**
     * 线程池
     */
    public static ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    
    /**
     * 从参数 classroom.url.request.api 中 获取appserver请求路径
     * @Author:ALong (ZengWeiLong)
     * @return String
     * @date 2016年5月12日
     */
    public static String getAppserverUrl(){
       String roomurl = PropertyConfigurer.stringValue("classroom.url.request.api");
       return roomurl.substring(0,roomurl.indexOf("/api/"));
    }
}

