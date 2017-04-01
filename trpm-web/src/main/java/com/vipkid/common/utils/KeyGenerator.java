package com.vipkid.common.utils;


import com.vipkid.file.utils.StringUtils;

/**
 * Created by davieli on 2015/6/8.
 */
public class KeyGenerator {
    private KeyGenerator() {
    }

    public static String generateKey(String prefix, Long id) {
        String key = null;
        if(StringUtils.isNotBlank(prefix) && id!=null){
            key = prefix +":"+id;
        }
        return key;
    }

    public static String generateKey(String prefix, Integer id) {
        String key = null;
        if(StringUtils.isNotBlank(prefix) && id!=null){
            key = prefix +"_"+id;
        }
        return key;
    }

    public static String generateKey(String prefix, String str) {
        String key = null;
        if(StringUtils.isNotBlank(prefix) && StringUtils.isNotBlank(str)){
            key = prefix +":"+str;
        }
        return key;
    }




    public static final String prefixPermissions = "permissions";

    public static final String CREATE_SCREENING_LOCK="TEACHER:CREATE_SCREENING";//创建sterling screening
    public static final String CREATE_CANDIDATE_LOCK="TEACHER:CREATE_CANDIDATE";//创建sterling candidate

    public static final String PREFIX_TEACHER_DETAIL_KEY = "TEACHER:DETAIL"; //教师信息key
    public static final String PREFIX_TEACHER_TEACHING_KEY = "TEACHER:TEACHING"; //教师教学信息key
    public static final String PREFIX_TEACHER_FILE_KEY = "TEACHER:FILE"; //教师文件信息key
    public static final String PREFIX_TEACHER_FILE_LOCK_KEY ="TEACHER:FILE_LOCK"; //教师文件锁

    public static final String COUNTRY_LIST_KEY = "TEACHER:LOCATION:COUNTRY_LIST"; //获取国家信息
    public static final String STATE_LIST_KEY = "TEACHER:LOCATION:STATE_LIST";//根据国家ID获取洲，省份
    public static final String CITY_LIST_KEY = "TEACHER:LOCATION:CITY_LIST";//根据洲，省份获取城市
    public static final String NATIONALITYCODE_LIST_KEY = "TEACHER:NATIONALITY:NATIONALITYCODE_LIST"; //国籍列表
}
