package com.vipkid.portal.entity;

/**
 * Created by liuguowen on 2016/12/21.
 */
public class ScheduledRequest {

    private String type;

    private int weekOffset;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWeekOffset() {
        return weekOffset;
    }

    public void setWeekOffset(int weekOffset) {
        this.weekOffset = weekOffset;
    }

}
