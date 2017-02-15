package com.vipkid.recruitment.entity;

import org.community.dao.support.Entity;

import java.io.Serializable;

/**
 * Created by terrysun on 2/8/17.
 */
public class InterviewerClassCount extends Entity implements Serializable {

    private int onlinClassId;
    private String scheduledDateTime;
    private String teacherId;
    private int bookedCount=-1;

    public InterviewerClassCount setId(int id)
    {
        this.onlinClassId=id;
        return this;
    }

    public int getOnlinClassId() {
        return onlinClassId;
    }

    public void setOnlinClassId(int onlinClassId) {
        this.onlinClassId = onlinClassId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(String scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public int getBookedCount() {
        return bookedCount;
    }

    public void setBookedCount(int bookedCount) {
        this.bookedCount = bookedCount;
    }
}
