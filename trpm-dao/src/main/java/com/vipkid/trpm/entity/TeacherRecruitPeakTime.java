package com.vipkid.trpm.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.community.dao.support.Entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by pankui on 2017-04-14.
 *  老师约面试 实体类
 */
public class TeacherRecruitPeakTime extends Entity implements Serializable{

    private Long id;
    /*老师id*/
    private Long teacherId;
    /*面试状态*/
    private String status;
    /*越面试的时间*/
    private Date scheduledDateTime;
    /*创建时间*/
    private Date createTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(Date scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
