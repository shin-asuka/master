package com.vipkid.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期工具类
 * 
 * @author John
 *
 */
public class DateUtil {

	public static final String YYYY_MM_DASH = "yyyy-MM";

	public static final String MMM_YYYY_DASH = "MMM yyyy";

	public static final String YYYY_MM_DD_DASH = "yyyy-MM-dd";

	public static final String YYYY_MM_DD_HH_MM_SS_DASH = "yyyy-MM-dd HH:mm:ss";

	public final static DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

	public final static DateFormat DATE_FORMAT4 = new SimpleDateFormat("MM月dd日",
			Locale.getDefault());

	public final static DateFormat DATE_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm",
			Locale.US);

	public final static DateFormat DATE_FORMAT3 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	public final static DateFormat DATE_FORMAT5 = new SimpleDateFormat("yyyy/MM/dd",
			Locale.getDefault());

	public final static SimpleDateFormat DATE_FORMAT1 = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss Z yyyy", Locale.US);

	public final static SimpleDateFormat DATE_FORMAT6 = new SimpleDateFormat(
			YYYY_MM_DD_HH_MM_SS_DASH);

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String format(Date date, DateFormat format) {
		return (date == null ? null : format.format(date));
	}

	/**
	 * 获取指定时区的当前日期和时间字符串
	 * 
	 * @param tzid
	 *            时区id
	 * @return 日期和时间字符串，格式为：”yyyy-MM-dd HH:mm:ss“
	 */
	public static String getThisDateTime(String tzid) {
		DateTimeZone tz = DateTimeZone.forID(tzid);
		Long instant = DateTime.now().getMillis();

		DateTimeFormatter fromatter = DateTimeFormat.forPattern(YYYY_MM_DD_HH_MM_SS_DASH);
		return fromatter.withZone(tz).print(instant);
	}

	/**
	 * 转换时间毫秒，从FROM时区到TO时区
	 * 
	 * @param millis
	 * @param fromTZ
	 * @param toTZ
	 * @return
	 */
	public static String convertzDateTime(long millis, String fromTZ, String toTZ) {
		DateTime dt = new DateTime(millis, DateTimeZone.forID(fromTZ));
		Long instant = dt.getMillis();

		DateTimeFormatter fromatter = DateTimeFormat.forPattern(YYYY_MM_DD_HH_MM_SS_DASH);
		return fromatter.withZone(DateTimeZone.forID(toTZ)).print(instant);
	}

	/**
	 * 获取指定时区的当前日期字符串
	 * 
	 * @param tzid
	 *            时区id
	 * @return 日期字符串，格式为：”yyyy-MM-dd“
	 */
	public static String getThisDate(String tzid) {
		DateTimeZone tz = DateTimeZone.forID(tzid);
		Long instant = DateTime.now().getMillis();

		DateTimeFormatter fromatter = DateTimeFormat.forPattern(YYYY_MM_DD_DASH);
		return fromatter.withZone(tz).print(instant);
	}

	/**
	 * 增加月份
	 * 
	 * @param dateStr
	 *            日期字符串，格式为：”yyyy-MM-dd“
	 * @param amount
	 *            增量值
	 * 
	 * @return 日期字符串，格式为：”yyyy-MM-dd“
	 */
	public static String addMonth(String dateStr, int amount) {
		DateTime dt = DateTime.parse(dateStr);

		DateTimeFormatter fromatter = DateTimeFormat.forPattern(YYYY_MM_DD_DASH);
		return fromatter.print(dt.plusMonths(amount).getMillis());
	}

	/**
	 * 获取指定时区的Offset
	 * 
	 * @param tzid
	 *            时区id
	 * @return 时区的Offset字符串，例如：“+08:00”
	 */
	public static String getTZOffset(String tzid) {
		DateTimeZone tz = DateTimeZone.forID(tzid);
		Long instant = DateTime.now().getMillis();

		DateTimeFormatter fromatter = DateTimeFormat.forPattern("ZZ");
		return fromatter.withZone(tz).print(instant);
	}

	/**
	 * 获取指定时区的年月
	 * 
	 * @param tzid
	 *            时区id
	 * @return 年月字符串，格式为：“yyyy-MM”
	 */
	public static String getThisYearMonth(String tzid) {
		String date = DateUtil.getThisDate(tzid);
		DateTime dateTime = DateTime.parse(date);

		DateTimeFormatter fromatter = DateTimeFormat.forPattern(DateUtil.YYYY_MM_DASH);
		return fromatter.print(dateTime.getMillis());
	}

	/**
	 * 获取指定年月的下一个月
	 * 
	 * @param dateStr
	 *            格式为：“yyyy-MM”
	 * 
	 * @return 年月字符串，格式为：“yyyy-MM”
	 */
	public static String getNextYearMonth(String dateStr) {
		String date = addMonth(dateStr, 1);
		DateTime dateTime = DateTime.parse(date);

		DateTimeFormatter fromatter = DateTimeFormat.forPattern(DateUtil.YYYY_MM_DASH);
		return fromatter.print(dateTime.getMillis());
	}

	/**
	 * 获取指定年月的上一个月
	 * 
	 * @param dateStr
	 *            格式为：“yyyy-MM”
	 * 
	 * @return 年月字符串，格式为：“yyyy-MM”
	 */
	public static String getPrevYearMonth(String dateStr) {
		String date = addMonth(dateStr, -1);
		DateTime dateTime = DateTime.parse(date);

		DateTimeFormatter fromatter = DateTimeFormat.forPattern(DateUtil.YYYY_MM_DASH);
		return fromatter.print(dateTime.getMillis());
	}

	/**
	 * 使用Locale.US格式化年月
	 * 
	 * @param dateStr
	 *            格式为：“yyyy-MM”
	 * 
	 * @return 年月字符串，格式为：“MMM yyyy”
	 */
	public static String formatYearMonthWithUSLocale(String dateStr) {
		DateTime dateTime = DateTime.parse(dateStr);

		DateTimeFormatter fromatter = DateTimeFormat.forPattern(DateUtil.MMM_YYYY_DASH);
		return fromatter.withLocale(Locale.US).print(dateTime.getMillis());
	}

	public static Date getThisSunday() {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();
	}

	public static Date getSundayByOffset(int i) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendar.add(Calendar.DATE, 7 * i);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();
	}

	public static Date getMondayByOffset(int i) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.add(Calendar.DATE, 7 * i);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();
	}

	public static Date getSundayByOffsetAndTimeZone(int i, TimeZone tz) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendar.add(Calendar.DATE, 7 * (i + 1));
		calendar.set(Calendar.HOUR_OF_DAY, 24);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.setTimeZone(tz);
		return calendar.getTime();
	}

	public static Date getSundayByOffsetAndDate(int offset, Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		calendar.add(Calendar.DATE, 7 * offset);
		calendar.set(Calendar.HOUR_OF_DAY, 24);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public static Date getMondayByOffsetAndDate(int offset, Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.add(Calendar.DATE, 7 * offset);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public static Date getThisSaturday(int i) {
		Calendar calendar = Calendar.getInstance();

		// 这种输出的是上个星期周日的日期，因为老外那边把周日当成第一天
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendar.add(Calendar.DATE, 7 * (i + 1));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public static Date getNextDay(Date date, int i) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, i);
		return cal.getTime();
	}

	public static Date getOffsetTime(Date date, int offset) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, offset);
		return cal.getTime();
	}

	public static Date getThisMonday() {
		Calendar calendar = Calendar.getInstance();

		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendar.add(Calendar.WEEK_OF_YEAR, -1);
		}
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date dtime = new Date(new Date().getTime() - 30 * 60 * 1000);
		if (dtime.after(calendar.getTime())) {
			return dtime;
		}
		return calendar.getTime();
	}

	public static Date getThisMd() {
		Calendar calendar = Calendar.getInstance();

		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendar.add(Calendar.WEEK_OF_YEAR, -1);
		}
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public static Date getTomorrow() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public static Date getNextMonday() {
		Calendar calendar = Calendar.getInstance();

		if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
		}
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public static Date getNextNextMonday() {
		Calendar calendar = Calendar.getInstance();

		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
		} else {
			calendar.add(Calendar.WEEK_OF_YEAR, 2);
		}
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 判断当前日期是星期几<br>
	 * <br>
	 * 
	 * @param pTime
	 *            修要判断的时间<br>
	 * @return dayForWeek 判断结果<br>
	 * @Exception 发生异常<br>
	 */
	public static int dayForWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			day = 7;
		} else {
			day = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return day;
	}

	public static Date getLocalTime(TimeZone tz, int offset) {
		TimeZone.setDefault(tz);// 设置时区
		Calendar calendar = Calendar.getInstance();// 获取实例
		calendar.add(Calendar.DATE, 7 * offset);
		Date date = calendar.getTime(); // 获取Date对象
		return date;
	}

	public static String formatDateWithUSLocale(String dateStr) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = dateFormat.parse(dateStr);

			DateTime dateTime = new DateTime(date.getTime());
			DateTimeFormatter fromatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm a");
			return fromatter.withLocale(Locale.US).print(dateTime.getMillis());
		} catch (ParseException e) {
			throw new RuntimeException("Parse dateString error.", e);
		}
	}

	public static long formatDateToMillis(String dateStr) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = dateFormat.parse(dateStr);
			
			return date.getTime();
		} catch (ParseException e) {
			throw new RuntimeException("Parse dateString error.", e);
		}
	}
}
