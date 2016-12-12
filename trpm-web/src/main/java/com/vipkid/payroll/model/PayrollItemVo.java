package com.vipkid.payroll.model;

import java.io.Serializable;


public class PayrollItemVo implements Serializable{

    private static final long serialVersionUID = 6054740402204149278L;
    /*  */
    private int id;
    /* 收入类型 */
    private int itemType;
    /* 收入金额 */
    private int salary;
    /* 对账单ID */
    private int payrollId;
    /* 教师ID */
    private int teacherId;
    /* 教师姓名 */
    private String teacherName;
    /* 工资月份 */
    private int settlementMonth;
    /* onlineclassID */
    private int onlineClassId;
    /* 课程时间 */
    private long scheduledTime;
    /* Lesson序列号 */
    private String lessonSn;
    /* 课程类型 */
    private String lessonType;
    /* 课程结束类型 */
    private String finishType;
    /* 可选值 {0: no, 1: yes} */
    private int shortNotice;
    /* 是否有feed back或者UA */
    private int hasFeedback;
    /* 课程ID */
    private int courseId;

    /* 课程ID */
    private String courseName;
    /*  */
    private java.sql.Timestamp createTime;
    /*  */
    private java.sql.Timestamp updateTime;
    /* 操作人ID */
    private int operatorId;

    /*  */
    private int amount;
    
  

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
   
    }

    public int getItemType() {
        return this.itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
       
    }

    public int getSalary() {
        return this.salary;
    }
    

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getPayrollId() {
        return this.payrollId;
    }

    public void setPayrollId(int payrollId) {
        this.payrollId = payrollId;
       
    }
    
    public int getTeacherId() {
        return this.teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
       
    }
    
    public String getTeacherName() {
        return this.teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
       
    }

    public int getSettlementMonth() {
        return this.settlementMonth;
    }

    public void setSettlementMonth(int settlementMonth) {
        this.settlementMonth = settlementMonth;
       
    }

    public int getOnlineClassId() {
        return this.onlineClassId;
    }

    public void setOnlineClassId(int onlineClassId) {
        this.onlineClassId = onlineClassId;
       
    }
   
    public long getScheduledTime() {
        return this.scheduledTime;
    }
    
    public void setScheduledTime(long scheduledTime) {
        this.scheduledTime = scheduledTime;
       
    }
    public String getLessonSn() {
        return this.lessonSn;
    }

    public void setLessonSn(String lessonSn) {
        this.lessonSn = lessonSn;
       
    }
    public String getLessonType() {
        return this.lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
       
    }
    public String getFinishType() {
        return this.finishType;
    }

    public void setFinishType(String finishType) {
        this.finishType = finishType;
       
    }

    public int getShortNotice() {
        return this.shortNotice;
    }
    public String getShortNoticeForExport() {
        return  this.shortNotice==0?"NO":"YES";
    }

    public void setShortNotice(int shortNotice) {
        this.shortNotice = shortNotice;
       
    }

    public int getHasFeedback() {
        return this.hasFeedback;
    }

    public void setHasFeedback(int hasFeedback) {
        this.hasFeedback = hasFeedback;
       
    }

    public int getCourseId() {
        return this.courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
       
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
  
    public java.sql.Timestamp getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(java.sql.Timestamp createTime) {
        this.createTime = createTime;
       
    }

    public java.sql.Timestamp getUpdateTime() {
        return this.updateTime;
    }

  
    public int getOperatorId() {
        return this.operatorId;
    }


    public void setAmount(int amount) {
        this.amount = amount;
       
    }
    public int getAmount() {
        return amount;
    }

}
