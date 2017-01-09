package com.vipkid.portal.bookings.entity;

import java.util.List;

/**
 * Created by liuguowen on 2016/12/22.
 */
public class Delete24HourRequest {

    private List<Long> onlineClassIds;

    public List<Long> getOnlineClassIds() {
        return onlineClassIds;
    }

    public void setOnlineClassIds(List<Long> onlineClassIds) {
        this.onlineClassIds = onlineClassIds;
    }

}
