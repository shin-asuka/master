package com.vipkid.trpm.entity;

import java.io.Serializable;
import java.util.Date;
import org.community.dao.support.Entity;

/**
 * Created by luning on 2017/3/11.
 */
public class BackgroundAdverse implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id; // BIGINT NULL,
    private String screeningId; // VARCHAR(15) NULL COMMENT 'Sterling 系统的 screening id',
    private Long bgSterlingScreeningId; // BIGINT NULL COMMENT '数据库表 bgSterlingScreening 的主键',
    private String actionsId; // VARCHAR(15) NULL COMMENT 'Sterling 系统的 actions id',
    private String actionsStatus; // VARCHAR(15) NULL COMMENT 'Sterling 系统 actions status  ：initated,awaiting,complete,cancelled',
    private Date actionsUpdatedAt; // DATE NULL COMMENT 'adverse 最后一次变更的时间',
    private Date createTime; // DATE NULL COMMENT '本记录创建的时间',
    private Date updateTime; // DATE NULL COMMENT '本记录最后一次修改的时间',

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScreeningId() {
        return screeningId;
    }

    public void setScreeningId(String screeningId) {
        this.screeningId = screeningId;
    }

    public Long getBgSterlingScreeningId() {
        return bgSterlingScreeningId;
    }

    public void setBgSterlingScreeningId(Long bgSterlingScreeningId) {
        this.bgSterlingScreeningId = bgSterlingScreeningId;
    }

    public String getActionsId() {
        return actionsId;
    }

    public void setActionsId(String actionsId) {
        this.actionsId = actionsId;
    }

    public String getActionsStatus() {
        return actionsStatus;
    }

    public void setActionsStatus(String actionsStatus) {
        this.actionsStatus = actionsStatus;
    }

    public Date getActionsUpdatedAt() {
        return actionsUpdatedAt;
    }

    public void setActionsUpdatedAt(Date actionsUpdatedAt) {
        this.actionsUpdatedAt = actionsUpdatedAt;
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
