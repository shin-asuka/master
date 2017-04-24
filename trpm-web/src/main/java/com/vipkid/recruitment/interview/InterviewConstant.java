package com.vipkid.recruitment.interview;

public class InterviewConstant {

    /***
     * 课前1小时不允许book
     */
    public static final long BOOK_TIME = 60 * 60 * 1000;
    /***
     * 上课前都可以book，给quickInterview用
     */
    public static final long BOOK_TIME_0 = 0;
    /***
     * 创建5分钟后的onlineClass，给quickInterview用
     */
    public static final int CREATE_TIME_AFTER = 600000;
    /***
     *开课前30分钟 可以进入onlineClass
     */
    public static final int ENTER_CLASS_MINUTES = 30;
    /***
     *除了今天还展示几天的timeSlots
     */
    public static final int SHOW_DAYS_EXCLUDE_TODAY = 3;
}
