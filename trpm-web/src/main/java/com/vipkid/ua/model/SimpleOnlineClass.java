package com.vipkid.ua.model;

import java.io.Serializable;

/**
 * Created by zfl on 2016/11/9.
 */
public class SimpleOnlineClass implements Serializable{
    private static final long serialVersionUID = 7050207999877620114L;
    private long id;
    private String studentName;
    private String teacherName;
    private String scheduleDatetime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getScheduleDatetime() {
        return scheduleDatetime;
    }

    public void setScheduleDatetime(String scheduleDatetime) {
        this.scheduleDatetime = scheduleDatetime;
    }
}
