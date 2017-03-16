package com.vipkid.trpm.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by luning on 2017/3/15.
 */
public class CanadaBackgroundScreening implements Serializable {

    private static final long serialVersionUID = 8875446883189274133L;

    private long id;
    private long teacherId;
    private String result; // 背调结果 PASS／FAIL
    private long createId; // 创建者ID
    private long updateId; // 更新者ID
    private Date createTime;
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
