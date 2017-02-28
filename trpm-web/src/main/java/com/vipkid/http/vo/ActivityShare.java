package com.vipkid.http.vo;

public class ActivityShare {

    private String teacherName;//当前登陆的老师的名字
    private long  totalFinishedClassesMin;//该老师上过的课的总的分钟数
    private String ratings;//该老师的评价得分
    private Integer studentNum;//该老师上过课的学生总数
    private String token;
    private String joinUsUrl;

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public long getTotalFinishedClassesMin() {
        return totalFinishedClassesMin;
    }

    public void setTotalFinishedClassesMin(long totalFinishedClassesMin) {
        this.totalFinishedClassesMin = totalFinishedClassesMin;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public Integer getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(Integer studentNum) {
        this.studentNum = studentNum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getJoinUsUrl() {
        return joinUsUrl;
    }

    public void setJoinUsUrl(String joinUsUrl) {
        this.joinUsUrl = joinUsUrl;
    }
}
