package com.vipkid.portal.activity.vo;

public class DrawListVo {

	/**
	 * 老师Id
	 */
	private Long teacherId;
	
	/**
	 * 老师名称
	 */
	private String name;
	
	/**
	 * 中奖奖项编号
	 */
	private String drawLevelNo;
	
	/**
	 * 抽奖时间
	 */
	private Long activityDateTime;
	

	public Long getTeacherId() {
		return teacherId;
	}

	public String getName() {
		return name;
	}

	public String getDrawLevelNo() {
		return drawLevelNo;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDrawLevelNo(String drawLevelNo) {
		this.drawLevelNo = drawLevelNo;
	}

	public Long getActivityDateTime() {
		return activityDateTime;
	}

	public void setActivityDateTime(Long activityDateTime) {
		this.activityDateTime = activityDateTime;
	}
	
	

}
