package com.vipkid.portal.glory.model;

import com.vipkid.trpm.entity.TeacherGloryLog;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by LP-813 on 2017/4/24.
 */
public class TeacherGlory {

    private Integer id;
    private String name;
    private Integer userId;
    private String finishTime;
    private Integer priority;
    private String avatar;
    private String title;
    private String description;
    private String shareTitle;
    private String shareDescription;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareDescription() {
        return shareDescription;
    }

    public void setShareDescription(String shareDescription) {
        this.shareDescription = shareDescription;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TeacherGloryLog toTeacherGloryLog(TeacherGlory teacherGlory){
        TeacherGloryLog teacherGloryLog = new TeacherGloryLog();
        teacherGloryLog.setId(null);
        teacherGloryLog.setUserId(teacherGlory.getUserId());
        teacherGloryLog.setGloryId(teacherGlory.getId());
        teacherGloryLog.setShowTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
        return teacherGloryLog;
    };
}
