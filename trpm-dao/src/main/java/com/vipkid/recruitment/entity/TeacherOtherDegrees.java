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
    //教师证书
    private String url;
    //教师证书的类型  文件类型1-other_degrees  2-certificationFiles   3-Identification  4-Diploma 5-Contract  6-Passport   7-Driver's license
    private int fileType;


    private Timestamp createTime;
    private Timestamp updateTime;
    private long createId;
    private long updateId;
    private long teacherApplicationId;
    private String failReason;
    private String result;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public long getTeacherApplicationId() {
        return teacherApplicationId;
    }

    public void setTeacherApplicationId(long teacherApplicationId) {
        this.teacherApplicationId = teacherApplicationId;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public void setUpdateId(long updateId) {
        this.updateId = updateId;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
