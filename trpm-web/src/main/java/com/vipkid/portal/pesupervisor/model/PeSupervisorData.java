package com.vipkid.portal.pesupervisor.model;

import java.util.List;

/*教师端前后端分离，PE Supervisor页面的数据模型*/
public class PeSupervisorData {
	private long teacherId;
	private int curPage;
	private int totalPage;
	private List<PeSupervisorClassDetail> dataList;
	
	public long getTeacherId() {
		return teacherId;
	}
	public int getCurPage() {
		return curPage;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public List<PeSupervisorClassDetail> getDataList() {
		return dataList;
	}
	public void setTeacherId(long teacherId) {
		this.teacherId = teacherId;
	}
	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public void setDataList(List<PeSupervisorClassDetail> dataList) {
		this.dataList = dataList;
	}
}
