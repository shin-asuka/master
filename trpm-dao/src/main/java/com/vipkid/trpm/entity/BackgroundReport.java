package com.vipkid.trpm.entity;

import java.io.Serializable;
import java.util.Date;
import org.community.dao.support.Entity;

/**
 * Created by luning on 2017/3/11.
 */
public class BackgroundReport implements Serializable{
    private static final long serialVersionUID = -1L;
    private Long id; // BIGINT NULL COMMENT '',
    private String reportId; // VARCHAR(15) NULL COMMENT 'Sterling 系统report item id',
    private String screeningId; // VARCHAR(15) NULL COMMENT 'Sterling 系统的screening 的id',
    private Long bgSterlingScreeningId; // INTEGER NULL COMMENT '系统表 bgSterlingScreening 主键',
    private String type; // TEXT NULL COMMENT '报告的类型',
    private String status; // VARCHAR(10) NULL COMMENT '报告的状态 new,pending,complete,error,release,cancelled,rejected',
    private String result; // VARCHAR(10) NULL COMMENT 'n/a,alert,clear,not verified,verified,discrepancy,no record,complete,success,partial match,no data,error,review needed',
    private Date updatedAt; // DATE NULL COMMENT '最后一次修改的时间',
    private Date createTime; // DATE NULL COMMENT '本记录创建的时间',
    private Date updateTime; // DATE NULL COMMENT '本记录最后一次修改的时间',

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
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
