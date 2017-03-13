package com.vipkid.payroll.model;

import java.io.Serializable;

public class ReferralPayrollItem implements Serializable{

	private static final long serialVersionUID = 6054740402204149278L;
	private int RappliedTeacherId;
	private long totalSalary;
	private String teacherName;
	private int category;
	private long firstClassTime;
	private long categoryName;
	private long signingTime;
	private long registerDate;
	public int getRappliedTeacherId() {
		return RappliedTeacherId;
	}
	public void setRappliedTeacherId(int rappliedTeacherId) {
		RappliedTeacherId = rappliedTeacherId;
	}
	public long getTotalSalary() {
		return totalSalary;
	}
	public void setTotalSalary(long totalSalary) {
		this.totalSalary = totalSalary;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public long getFirstClassTime() {
		return firstClassTime;
	}
	public void setFirstClassTime(long firstClassTime) {
		this.firstClassTime = firstClassTime;
	}
	public long getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(long categoryName) {
		this.categoryName = categoryName;
	}
	public long getSigningTime() {
		return signingTime;
	}
	public void setSigningTime(long signingTime) {
		this.signingTime = signingTime;
	}
	public long getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(long registerDate) {
		this.registerDate = registerDate;
	}

}
