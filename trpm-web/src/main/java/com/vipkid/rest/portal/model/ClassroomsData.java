package com.vipkid.rest.portal.model;

import java.util.List;
import java.util.Map;

/*classrooms页面的接口返回的数据模型*/
public class ClassroomsData {
	private Long teacherId;
	private String monthOfYear;
	private Integer curPage;
	private Integer totalPage;
	private List<Map<String, Object>> tagList;
	private List<ClassroomDetail> dataList;
	private List<Map<String, Object>> stateList;
	public Long getTeacherId() {
		return teacherId;
	}
	public String getMonthOfYear() {
		return monthOfYear;
	}
	public Integer getCurPage() {
		return curPage;
	}
	public Integer getTotalPage() {
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
	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}
	public void setMonthOfYear(String monthOfYear) {
		this.monthOfYear = monthOfYear;
	}
	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}
	public void setTotalPage(Integer totalPage) {
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