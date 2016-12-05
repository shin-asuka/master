package com.vipkid.recruitment.event;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 定义 Audit 事件对象
 *
 * @author Austin.Cao  Date: 05/12/2016
 */
public class AuditEvent implements Serializable {

    private Long sourceId;      // 事件关联的源 Id, 主要指 teacherId
    private String status;      // 事件源的状态, 主要指示 teacher lifeCycle
    private String auditResult; // 审核结果
    private Long dateTime;

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
