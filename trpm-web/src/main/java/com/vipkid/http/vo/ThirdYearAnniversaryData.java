package com.vipkid.http.vo;

import java.util.List;

import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;

public class ThirdYearAnniversaryData {
	private String teacherName;//当前登陆的老师的名字
	private String teacherGender;//当前登陆的老师的性别
	private String teacherAvatar;//当前登陆的老师的头像
	private long  lengthOfTime;//老师注册成功，通过测试，具备上课资格的天数
	private int  numberOfReferrals;//推荐成功的老师数量
	private int  stuNumber;//成功上过课的中国学生数量
	private String  stuName;//上课次数最多的学生名字
	private String  stuAvatar;//上课次数最多的学生头像
	private String stuGender;//上课次数最多的学生性别
	private long  numberOfClasses;//上课次数最多的学生上过的课的次数
	private long  totalFinishedClasses;//该老师成功上过的的课的次数
	private long  totalFinishedClassesMin;//该老师上过的课的总的分钟数
	private String becomeRegularDate;//该老师成为正式老师的日期
	private List<String> referralsAvatarList;//该老师推荐的老师的头像列表
	private String token;//教师id的加密token，用于facebook分享链接
	private String joinUsUrl;//joinUs按钮链向的url，带有招募渠道的id
	
	public String getTeacherName() {
		return teacherName;
	}
	public String getTeacherGender() {
		return teacherGender;
	}
	public String getTeacherAvatar() {
		return teacherAvatar;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public void setTeacherGender(String teacherGender) {
		this.teacherGender = teacherGender;
	}
	public void setTeacherAvatar(String teacherAvatar) {
		this.teacherAvatar = teacherAvatar;
	}
	
	public long getLengthOfTime() {
		return lengthOfTime;
	}
	public int getNumberOfReferrals() {
		return numberOfReferrals;
	}
	public int getStuNumber() {
		return stuNumber;
	}
	public String getStuName() {
		return stuName;
	}
	public long getNumberOfClasses() {
		return numberOfClasses;
	}
	public long getTotalFinishedClasses() {
		return totalFinishedClasses;
	}
	public long getTotalFinishedClassesMin() {
		return totalFinishedClassesMin;
	}
	public void setLengthOfTime(long lengthOfTime) {
		this.lengthOfTime = lengthOfTime;
	}
	public void setNumberOfReferrals(int numberOfReferrals) {
		this.numberOfReferrals = numberOfReferrals;
	}
	public void setStuNumber(int stuNumber) {
		this.stuNumber = stuNumber;
	}
	public void setStuName(String stuName) {
		this.stuName = stuName;
	}
	public void setNumberOfClasses(long numberOfClasses) {
		this.numberOfClasses = numberOfClasses;
	}
	public void setTotalFinishedClasses(long totalFinishedClasses) {
		this.totalFinishedClasses = totalFinishedClasses;
	}
	public void setTotalFinishedClassesMin(long totalFinishedClassesMin) {
		this.totalFinishedClassesMin = totalFinishedClassesMin;
	}
	public String getBecomeRegularDate() {
		return becomeRegularDate;
	}
	public void setBecomeRegularDate(String firstClassDate) {
		this.becomeRegularDate = firstClassDate;
	}
	public List<String> getReferralsAvatarList() {
		return referralsAvatarList;
	}
	public void setReferralsAvatarList(List<String> referralsAvatarList) {
		this.referralsAvatarList = referralsAvatarList;
	}
	public String getStuAvatar() {
		return stuAvatar;
	}
	public void setStuAvatar(String stuAvatar) {
		this.stuAvatar = stuAvatar;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getStuGender() {
		return stuGender;
	}
	public void setStuGender(String stuGender) {
		this.stuGender = stuGender;
	}
	public String getJoinUsUrl() {
		return joinUsUrl;
	}
	public void setJoinUsUrl(String joinUsUrl) {
		this.joinUsUrl = joinUsUrl;
	}
}
