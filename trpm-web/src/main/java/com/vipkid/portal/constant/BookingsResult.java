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

    private BookingsResult() {}

}
