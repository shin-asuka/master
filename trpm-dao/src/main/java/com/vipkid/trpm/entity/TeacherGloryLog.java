package com.vipkid.trpm.entity;

import org.community.dao.support.Entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by LP-813 on 2017/4/28.
 */
public class TeacherGloryLog extends Entity implements Serializable {

    private Integer id;
    private Integer userId;
    private Integer gloryId;
    private Integer status;
    private Timestamp showTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGloryId() {
        return gloryId;
    }

    public void setGloryId(Integer gloryId) {
        this.gloryId = gloryId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

       public Timestamp getShowTime() {
        return showTime;
    }

    public void setShowTime(Timestamp showTime) {
        this.showTime = showTime;
    }
}