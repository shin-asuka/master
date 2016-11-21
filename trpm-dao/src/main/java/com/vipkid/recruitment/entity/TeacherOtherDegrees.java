package com.vipkid.recruitment.entity;

import org.community.dao.support.Entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by zhangzhaojun on 2016/11/15.
 */
public class TeacherOtherDegrees extends Entity implements Serializable {

    private int id;
    private long teacherId;
    //教师的其他证书
    private String degrees;
    private Timestamp createTime;
    private Timestamp updateTime;
    private long createId;
    private long updateId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public String getDegrees() {
        return degrees;
    }

    public void setDegrees(String degrees) {
        this.degrees = degrees;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public long getCreateId() {
        return createId;
    }

    public void setCreateId(long createId) {
        this.createId = createId;
    }

    public long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(long updateId) {
        this.updateId = updateId;
    }
}
