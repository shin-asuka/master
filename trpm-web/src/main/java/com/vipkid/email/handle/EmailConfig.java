package com.vipkid.email.handle;

public class EmailConfig {

    public enum EmailFormEnum {
        TEACHVIP, EDUCATION
    }

    public enum EmailTypeEnum {
        ACTIVATION, SUBMITBASICINFO, CLASSNOTE
    }

    public static final String MAILGUN_API = "key-5f57bf6ed1fe62dfed144e551c60b07f";
    public static final String MAILGUN_URL = "https://api.mailgun.net/v3/vipkid.net/messages";
    public static final String HOST = "smtp.mailgun.org";

    // 教师招聘组的邮箱
    public static final String TC_USERNAME = "teachvip@vipkid.net";
    public static final String TC_PASSWORD = "vipkid365";
    public static final String TC_FROM = "teachvip@vipkid.com.cn";

    // 教师管理组的邮箱
    public static final String ED_USERNAME = "education@vipkid.net";
    public static final String ED_PASSWORD = "eduedu";
    public static final String ED_FROM = "education@vipkid.com.cn";

    /**
     * 正式环境识别
     * 
     */
    public static final String SEND_EMIAL_EVN = "http://t.vipkid.com.cn/";

    /**
     * 测试环境发送地址
     * 
     */
    public static final String TEST_EMIAL_TO = "zengweilong@vipkid.com.cn";

    /**
     * 邮件发送失败重试时间
     */
    public static final int DEFAULT_TIME = 15000;

    /**
     * 重试次数
     */
    public static final int REPLY_COUNT = 10;

}
