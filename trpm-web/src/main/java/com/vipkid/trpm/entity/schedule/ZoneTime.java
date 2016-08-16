package com.vipkid.trpm.entity.schedule;

import static com.vipkid.trpm.util.DateUtils.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 时区时间类
 */
public class ZoneTime {

	/* 格式化的中国时间 */
	private String formatToBeiJing;

	/* 中国日期时间对象 */
	private Date dateFromBeiJing;

	/* 格式化的本地时区时间 */
	private String localTime;

	public ZoneTime(TimeSlot timeSlot, String timezone) {
		Instant instant = timeSlot.toDate().toInstant();

		LocalDateTime localDateTimeBeiJing = LocalDateTime.ofInstant(instant, SHANGHAI);
		this.formatToBeiJing = localDateTimeBeiJing.format(FMT_YMD_HMS);
		this.dateFromBeiJing = Date.from(localDateTimeBeiJing.atZone(SHANGHAI).toInstant());

		this.localTime = formatTo(instant, timezone, FMT_YMD_HMA_US);
	}

	public String getFormatToBeiJing() {
		return formatToBeiJing;
	}

	public Date getDateFromBeiJing() {
		return dateFromBeiJing;
	}

	public String getLocalTime() {
		return localTime;
	}

}
