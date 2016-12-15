package com.vipkid.portal.entity;

import java.util.Date;
import java.util.List;

public class TimeSlot {

	/* 格式化的本地时间字符串 */
	private String localTimeString;

	/* 本地日期时间对象 */
	private Date localTime;

	/* 对应课程时间对象列表 */
	private List<ClassTime> classTimes;

	/* 是否显示，默认不显示 */
	private boolean show = false;

	/* 是否已过期，默认未过期 */
	private boolean expired = false;

	public TimeSlot(Date date) {
		// this.localTime = formatTo(date.toInstant(), FMT_YMD_HMA_US);
		// this.date = date;
		// this.zoneTime = Lists.newLinkedList();
	}


}
