package com.vipkid.recruitment.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.community.dao.support.Entity;

import java.io.Serializable;
import java.util.Date;

public final class TeacherReminder extends Entity implements Serializable {

    private static final long serialVersionUID = 2196568665078114910L;
    /*  */
    private Long id;
    /*  */
    private Long onlineClassId;
    /*  */
    private Date sendScheduledTime;
    /*  */
    private String params;
    /*  */
    private String mailTemplateTitle;
    /*  */
    private String mailTemplateContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOnlineClassId() {
        return onlineClassId;
    }

    public void setOnlineClassId(Long onlineClassId) {
        this.onlineClassId = onlineClassId;
    }

    public Date getSendScheduledTime() {
        return sendScheduledTime;
    }

    public void setSendScheduledTime(Date sendScheduledTime) {
        this.sendScheduledTime = sendScheduledTime;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getMailTemplateTitle() {
        return mailTemplateTitle;
    }

    public void setMailTemplateTitle(String mailTemplateTitle) {
        this.mailTemplateTitle = mailTemplateTitle;
    }

    public String getMailTemplateContent() {
        return mailTemplateContent;
    }

    public void setMailTemplateContent(String mailTemplateContent) {
        this.mailTemplateContent = mailTemplateContent;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
