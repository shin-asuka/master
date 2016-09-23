package com.vipkid.mq.message;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by zfl on 2016/9/18.
 * UA Report message
 */
public class UaReportMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private java.sql.Timestamp submitDateTime;//新UA提交时间
    private long onlineClassId;

    //扩展字段
    private Boolean hasUnitAssessment; //是否上传ua报告并审核是通过

    public long getOnlineClassId() {
        return onlineClassId;
    }

    public void setOnlineClassId(long onlineClassId) {
        this.onlineClassId = onlineClassId;
    }

    public Boolean getHasUnitAssessment() {
        return hasUnitAssessment;
    }

    public void setHasUnitAssessment(Boolean hasUnitAssessment) {
        this.hasUnitAssessment = hasUnitAssessment;
    }

    public Timestamp getSubmitDateTime() {
        return submitDateTime;
    }

    public void setSubmitDateTime(Timestamp submitDateTime) {
        this.submitDateTime = submitDateTime;
    }
}
