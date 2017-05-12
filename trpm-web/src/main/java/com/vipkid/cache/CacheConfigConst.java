package com.vipkid.cache;

/**
 * 缓存相关的常量配置
 */
public class CacheConfigConst {
    public static final String PREFIX_TEACHING_KEY = "TEACHING_KEY"; //教师教学信息key
    public static final String PREFIX_TEACHER_FILE_KEY = "TEACHER_FILE_KEY"; //教师文件信息key
    public static final String TEACHER_GLORY_KEY = "TEACHER_GLORY_KEY";//教师荣誉信息key
    /**
     * 教师报税锁前缀
     */
    public static final String TEACHER_TAXPAYER_LOCK_KEY ="TEACHER_TAXPAYER_LOCK_KEY"; //教师报税锁
    /**
     * 锁有效时间
     */
    public static final int LOCK_TIME = 5*60;  //second
}
