package com.vipkid.portal.personal.model;


import com.vipkid.rest.validation.annotation.NotNull;

public class ReferralTeacherVo {

	private String lifeCycle;
	
	private String status;
	
	private String result;
	
	private String name;
	
	/**  1.按照注册时间查询  2.按照入职时间查询  3.按照首次上课时间查询 */
	private Integer dataType;
	
	private String startTime;
	
	private String endTime;

	private Integer page = 1;

	private Integer pageSize = 20;

	private int startRows;

	private Long teacherId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLifeCycle() {
		return lifeCycle;
	}

	public void setLifeCycle(String lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getPage() {
		return page == null || page.intValue() == 0 ? 1 : page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize == null || pageSize.intValue() == 0 ? 20 : pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public int getStartRows() {
		return (this.getPage() - 1) * this.getPageSize();
	}

	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}
}
