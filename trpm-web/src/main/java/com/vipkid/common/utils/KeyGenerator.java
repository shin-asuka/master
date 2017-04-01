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
    public static final String CREATE_PREADVERSE_LOCK="TEACHER:CREATE_PREADVERSE";//创建sterling candidate

}
