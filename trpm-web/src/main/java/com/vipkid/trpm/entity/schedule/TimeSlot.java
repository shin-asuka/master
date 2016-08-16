package com.vipkid.trpm.entity.schedule;

import static com.vipkid.trpm.util.DateUtils.FMT_YMD_HMA_US;
import static com.vipkid.trpm.util.DateUtils.formatTo;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 时间空档类
 */
public class TimeSlot {

	/* 格式化的本地时间 */
	private String localTime;

	/* 日期时间对象 */
	private Date date;

	/* 对应时区时间对象列表 */
	private List<ZoneTime> zoneTime;

	/* 是否显示，默认不显示 */
	private boolean show = false;

	/* 是否已过期，默认未过期 */
	private boolean expired = false;

	private String style;

	public TimeSlot(Date date) {
		this.localTime = formatTo(date.toInstant(), FMT_YMD_HMA_US);
		this.date = date;
		this.zoneTime = Lists.newLinkedList();
	}

	public String getLocalTime() {
		return localTime;
	}

	public Date toDate() {
		return date;
	}

	public List<ZoneTime> getZoneTime() {
		return zoneTime;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
