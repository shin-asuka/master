package com.vipkid.trpm.entity;

import org.community.dao.support.Entity;

import java.io.Serializable;
import java.util.Date;
import org.community.dao.support.Entity;

/**
 * Created by luning on 2017/3/11.
 */
public class BackgroundScreening extends Entity implements Serializable{
    private static final long serialVersionUID = -1L;
    private Long id; // BIGINT NULL,
    private Long teacherId; // BIGINT NULL COMMENT '老师ID'
    private String candidateId; // VARCHAR(15) NULL COMMENT 'Sterling 系统的候选人ID'
    private String screeningId; // VARCHAR(15) NULL COMMENT 'Sterling 系统筛选请求标识'
    private String status; // VARCHAR(10) NULL COMMENT 'Sterling 系统状态，new,pending,complete,error,release,cancelled,rejected'
    private String result; // VARCHAR(10) NULL COMMENT 'Sterling 系统 结果 n/a,clear,alert'
    private String disputeStatus; // VARCHAR(10) NULL COMMENT '有争议的状态 可能为空：active,deactivated'
    private Date disputeCreatedAt; // DATE NULL COMMENT '争议开始的时间'
    private Date submittedAt; // DATE NULL COMMENT 'Sterling 系统 screening 提交的时间'
    private Date updateAt; // DATE NULL COMMENT 'Sterling 系统 screening 最后一次修改的时间'
    private String webLink; // TEXT NULL COMMENT '报告结果 web地址'
    private String pdfLink; // TEXT NULL COMMENT '报告结果 pdf 地址'
    private Date createTime; // DATE NULL COMMENT '记录创建的时间'
    private Date updateTime; // DATE NULL COMMENT '记录最后一次更新的时间'

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

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getScreeningId() {
        return screeningId;
    }

    public void setScreeningId(String screeningId) {
        this.screeningId = screeningId;
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

    public String getDisputeStatus() {
        return disputeStatus;
    }

    public void setDisputeStatus(String disputeStatus) {
        this.disputeStatus = disputeStatus;
    }

    public Date getDisputeCreatedAt() {
        return disputeCreatedAt;
    }

    public void setDisputeCreatedAt(Date disputeCreatedAt) {
        this.disputeCreatedAt = disputeCreatedAt;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getPdfLink() {
        return pdfLink;
    }

    public void setPdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
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
