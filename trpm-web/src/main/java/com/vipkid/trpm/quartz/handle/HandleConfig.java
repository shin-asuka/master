package com.vipkid.trpm.quartz.handle;


public interface HandleConfig {

    /**
     * Redis队列Key
     */
    public final static String KEY = "TRPM-REGULAR-TEACHER";
    
    /**
     * 查询条件
     */
    public final static String TEACHERIDKEY = "teacher_id";
    
    /**
     * 每台服务轮询最大次数  1 万次
     */
    public static Integer MAX_LIMIT = 10000; 
    
    /**
     * 每次查询最大课程量 48 节
     */
    public static Integer MAXROWS = 48;   
    
    /**
     * 从JediServer查询失败,每个老师重试最大次数 2 次
     */
    public static Integer RETRY_LIMIT = 10;
    
    /**
     * 定义Jedi请求超时
     */
    public static Integer JEDI_TIME_OUT = 3;
    
    /**
     * 从JediServer查询失败后重试等待时间 1s
     */
    public static Integer RETRY_WAIT_TIME = 1000;
    
    /**
     * WAIT 从redis中取值异常
     */
    public static String HE_WAIT_STR = "WAIT";
    
}
