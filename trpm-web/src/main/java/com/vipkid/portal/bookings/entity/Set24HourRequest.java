package com.vipkid.portal.bookings.entity;

import java.util.List;

/**
 * Created by liuguowen on 2016/12/22.
 */
public class Set24HourRequest {

    private int classType;

    private List<Long> onlineClassIds;

    private int weekOffset;

    public List<Long> getOnlineClassIds() {
        return onlineClassIds;
    }

    public void setOnlineClassIds(List<Long> onlineClassIds) {
        this.onlineClassIds = onlineClassIds;
    }

    public int getWeekOffset() {
        return weekOffset;
    }

    public void setWeekOffset(int weekOffset) {
        this.weekOffset = weekOffset;
    }

    public int getClassType() {
        return classType;
    }

    public void setClassType(int classType) {
        this.classType = classType;
    }

}
