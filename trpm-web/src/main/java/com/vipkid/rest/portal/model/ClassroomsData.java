package com.vipkid.rest.portal.model;

import java.util.List;
import java.util.Map;

/*classrooms页面的接口返回的数据模型*/
public class ClassroomsData {
	private long teacherId;
	private String monthOfYear;
	private int curPage;
	private int totalPage;
	private List<Map<String, Object>> tagList;
	private List<ClassroomDetail> dataList;
	private List<Map<String, Object>> stateList;
	
	public long getTeacherId() {
		return teacherId;
	}
	public String getMonthOfYear() {
		return monthOfYear;
	}
	public int getCurPage() {
		return curPage;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public List<Map<String, Object>> getTagList() {
		return tagList;
	}
	public List<ClassroomDetail> getDataList() {
		return dataList;
	}
	public List<Map<String, Object>> getStateList() {
		return stateList;
	}
	public void setTeacherId(long teacherId) {
		this.teacherId = teacherId;
	}
	public void setMonthOfYear(String monthOfYear) {
		this.monthOfYear = monthOfYear;
	}
	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public void setTagList(List<Map<String, Object>> tagList) {
		this.tagList = tagList;
	}
	public void setDataList(List<ClassroomDetail> dataList) {
		this.dataList = dataList;
	}
	public void setStateList(List<Map<String, Object>> stateList) {
		this.stateList = stateList;
	}
}