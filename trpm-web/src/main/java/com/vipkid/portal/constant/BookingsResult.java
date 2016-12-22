package com.vipkid.portal.constant;

/**
 * Created by liuguowen on 2016/12/21.
 */
public final class BookingsResult {

    /* 不能放置 TimeSlot */
    public static final String DISABLED_PLACE = "DISABLED_PLACE";

    /* TimeSlot 状态不是 Available */
    public static final String TIMESLOT_NOT_AVAILABLE = "TIMESLOT_NOT_AVAILABLE";

    /* 表示 PeakTime 少于15节课时 */
    public static final String PEAKTIM_LESS_15 = "PEAKTIM_LESS_15";

    /* 非法的 OnlineClass */
    public static final String ILLEGAL_ONLINECLASS = "ILLEGAL_ONLINECLASS";

    /* OnlineClass 开始上课时间在 1 个小时之内 */
    public static final String ONLINECLASS_IN_ONE_HOUR = "ONLINECLASS_IN_ONE_HOUR";

    /* 少于 15 个 TimeSlot */
    public static final String TIMESLOT_LESS_15 = "TIMESLOT_LESS_15";

    private BookingsResult() {}

}
