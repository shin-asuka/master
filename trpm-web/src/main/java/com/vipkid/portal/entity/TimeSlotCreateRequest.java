package com.vipkid.portal.entity;

/**
 * Created by liuguowen on 2016/12/21.
 */
public class TimeSlotCreateRequest {

    private String type;

    private String scheduledDateTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(String scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

}
