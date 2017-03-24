package com.vipkid.common.utils;

import com.vipkid.file.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luojiaoxia on 17/3/24.
 */
public class IdentityValidatorUtils {


    public static final String SOCIAL_NO_REG= "^\\d{9}$";//只做9位纯数字校验

    public static final String ZIP_CODE_REG = "^\\d{5}$";//只做5位纯数字校验


    public static boolean validSocialNoForUs(String socialNo){
        boolean flag = false;
        String regExp = SOCIAL_NO_REG;
        flag = match(regExp, socialNo);
        return flag;
    }

    public static boolean validZipCodeForUs(String zipCode){
        boolean flag = false;
        String regExp = ZIP_CODE_REG;
        flag = match(regExp, zipCode);
        return flag;
    }
    /**
     * @param regex
     *            正则表达式字符串
     * @param str
     *            要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    public static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

}
