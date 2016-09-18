package com.vipkid.mq.message;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by zfl on 2016/9/18.
 * UA Report message
 */
public class UaReportMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private java.sql.Timestamp createDateTime;
    private java.sql.Timestamp updateDateTime; // 更新时间
    private java.sql.Timestamp uploadDateTime; // 上传时间
    private String name;
    private int readed;
    private String url;
    private long studentId;
    private int score;
    private long onlineClassId;

    //扩展字段
    private Boolean hasUnitAssessment; //是否上传ua报告并审核是通过

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Timestamp getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Timestamp updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public Timestamp getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(Timestamp uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReaded() {
        return readed;
    }

    public void setReaded(int readed) {
        this.readed = readed;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

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
}
