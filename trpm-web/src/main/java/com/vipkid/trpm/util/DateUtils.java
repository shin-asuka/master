package com.vipkid.trpm.util;

import com.google.common.collect.Maps;
import com.vipkid.trpm.constant.ApplicationConstant;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * 日期工具类
 */
public final class DateUtils {
	private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

	public static final String YYYY_MM_DD = "yyyy-MM-dd";

	public static String AM = "AM";

	public static ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

	public static DateTimeFormatter FMT_HMA_US = DateTimeFormatter.ofPattern("hh:mm a").withLocale(Locale.US);

	public static DateTimeFormatter FMT_YMD_US = DateTimeFormatter.ofPattern(YYYY_MM_DD).withLocale(Locale.US);
	public static DateTimeFormatter FMT_YMD = DateTimeFormatter.ofPattern(YYYY_MM_DD);

	public static DateTimeFormatter FMT_YMD_HMA_US = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a").withLocale(Locale.US);

	public static DateTimeFormatter FMT_YMD_HMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static DateTimeFormatter FMT_YMD_HM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withLocale(Locale.getDefault());

	public static DateTimeFormatter FMT_MMM_YYYY = DateTimeFormatter.ofPattern("MMM yyyy");
	public static DateTimeFormatter FMT_MMM_YYYY_US = DateTimeFormatter.ofPattern("MMM yyyy").withLocale(Locale.US);

	public static DateTimeFormatter FMT_HMS = DateTimeFormatter.ofPattern("HH:mm:ss");

	public static DateTimeFormatter FMT_YM = DateTimeFormatter.ofPattern("yyyy-MM");

	public static DateTimeFormatter FMT_YMD_EMd = DateTimeFormatter.ofPattern("E, MMM d h:mma").withLocale(Locale.US);

	public static String DEFAULT_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String MMMM_DD_YYYY = "MMMM dd,YYYY";

	public static String HH_MM_SS ="HH:mm:ss";

	/* 每天的半小时数量 */
	public static final int HALFHOUR_OF_DAY = 24 * 2;
	/* 半小时的分钟数 */
	public static final int MINUTE_OF_HALFHOUR = 30;
	/* 每星期的天数 */
	public static final int DAY_OF_WEEK = 7;




	public final static String DATE_CHINESE_PATTERN = "yyyy年MM月dd日";
	public final static String DATE_MONTH_DAY_CHINESE_PATTERN = "MM月dd日";
	public final static String HOUR_MINUTE_CHINESE_PATTERN = "HH点mm分";
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	public final static String DATE_YYYY_MM = "yyyy-MM";
	public final static String DATE_SHORT_PATTERN = "yyyyMMdd";
	public final static String DATE_SLASH_PATTERN = "yyyy/MM/dd";
	public final static String DATETIME_MILL_SECOND = "yyyy-MM-dd HH:mm:ss.SSS";
	public final static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public final static String DATETIME_PATTERN_SHORT = "yyyy-MM-dd HH:mm";
	public final static String DATETIME_PATTERN_CREAT_ORDER = "yyyy-MM-ddHH:mm";
	public final static String DATETIME_SHORT_PATTERN = "yyyyMMddHHmmss";
	public final static String HOUR_MINUTE = "HH:mm";
	public final static String HOUR_MINUTE_SECOND = "HH:mm:ss";


	/**
	 * 使用上海时区，解析指定格式的日期时间字符串到Timestamp对象
	 * 
	 * @author LiuGuoWen
	 * 
	 * @param dateTime
	 * @param formatter
	 * @return Timestamp
	 */
	public static Timestamp parseFrom(String dateTime, DateTimeFormatter formatter) {
		LocalDateTime t = LocalDateTime.parse(dateTime, formatter);
		return Timestamp.from(t.atZone(SHANGHAI).toInstant());
	}

	/**
	 * 以上海时区格式化Instant对象
	 * 
	 * @author LiuGuoWen
	 * 
	 * @param instant
	 * @param formatter
	 * @return String
	 */
	public static String formatTo(Instant instant, DateTimeFormatter formatter) {
		return LocalDateTime.ofInstant(instant, SHANGHAI).format(formatter);
	}

	/**
	 * 以指定时区格式化Instant对象
	 * 
	 * @author LiuGuoWen
	 * 
	 * @param instant
	 * @param timezone
	 * @param formatter
	 * @return String
	 */
	public static String formatTo(Instant instant, String timezone, DateTimeFormatter formatter) {
		return LocalDateTime.ofInstant(instant, ZoneId.of(timezone)).format(formatter);
	}

	/**
	 * 获取指定时区的当前日期时间字符串
	 * 
	 * @param tzid
	 * @return String
	 */
	public static String getThisYearMonth(String tzid) {
		return LocalDateTime.now(ZoneId.of(tzid)).format(FMT_YMD_HMS);
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
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		LocalDateTime t = LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.of(fromTZ));
		return t.atZone(ZoneId.of(fromTZ)).format(FMT_YMD_HMS);
	}

	public static Date convertzDateTime(String TZ){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Date date = null;//注意是空格+UTC
		try {
			date = format.parse(TZ.replace("Z", " UTC"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	/**
	 * 获取以当前日期月份为基础，指定月份偏移量的，时区为上海的日期字符串；并以指定的日期格式输出。
	 * 
	 * @author LiuGuoWen
	 * 
	 * @param offsetOfMonth
	 * @param formatter
	 * @return String
	 */
	public static String monthOfYear(int offsetOfMonth, DateTimeFormatter formatter) {
		if (0 == offsetOfMonth) {
			return LocalDateTime.now(SHANGHAI).format(formatter);
		} else {
			return LocalDateTime.now(SHANGHAI).plusMonths(offsetOfMonth).format(formatter);
		}
	}
	
    /**
     * 超过时间是否按照id查询UA 
     * @Author:ALong (ZengWeiLong)
     * @return    
     * boolean
     * @date 2016年5月12日
     */
    public static boolean isSearchById(long classtime){
        try{
            long time = new SimpleDateFormat("yyyyMMdd").parse(ApplicationConstant.UA_FOR_CLASS_ID.toString()).getTime();
            return classtime > time;
        }catch(Exception e){
            return true;
        }
    }
	/**
	 * 检查当前时间离传入时间是否超过30分钟
	 * @Author:ALong (ZengWeiLong)
	 * @param classtime
	 * @return true 超过
	 * boolean
	 */
	public static boolean countXMinute(long classtime, int x){
		long currentTime = (System.currentTimeMillis() - x*60*1000);
		if( currentTime > classtime){
			return true;
		}
		return false;
	}
	/**
     * 检查上课时间是否已经上了15分钟的课程
     * @Author:ALong (ZengWeiLong)
     * @param classTime
     * @return    
     * long
     * @date 2016年6月30日
     */
    public static boolean count15Mine(long classTime){
        classTime += 15*60*1000;
        if(System.currentTimeMillis() > classTime){
            return true;
        }
        return false;
    } 
    
    /**
     * 检查两个时间差是否大于11.5个小时
     * @Author:ALong (ZengWeiLong)
     * @param auditTime
     * @return    
     * long
     * @date 2016年6月30日
     */
    public static boolean count11hrlf(long auditTime){
        auditTime += 11.5*(3600*1000);
        if(System.currentTimeMillis() > auditTime){
            return true;
        }
        return false;
    }
	/**
	 * 检查当前时间离传入时间是否超过1个小时
	 * @Author:ALong (ZengWeiLong)
	 * @param classtime
	 * @return true 超过
	 * boolean
	 */
	public static boolean count1h(long classtime){
		long currentTime = (System.currentTimeMillis() - 60*60*1000);
		if( currentTime > classtime){
			return true;
		}
		return false;
	}

	/**
	 * 检查当前时间离传入时间是否超过54week
	 * @Author:ALong (ZengWeiLong)
	 * @param classtime
	 * @return true 超过
	 * boolean
	 */
	public static boolean count54week(long classtime){
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		LocalDateTime source = LocalDateTime.ofInstant(timestamp.toInstant(),SHANGHAI).plusYears(-1);
		Date convertToDate = Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
		long currentTime = convertToDate.getTime();
		if(currentTime > classtime){
			return true;
		}
		return false;
	}
	/**
     * 
     * 计算两个时间之间月份跨度 
     * @Author:ALong (ZengWeiLong)
     * @param start 开始时间
     * @param end 结束时间
     * @return    
     * int
     * @date 2016年10月17日
     */
    public static int countMouth(long start,long end){
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(start);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(end);
        int result = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 12 + c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH) + 1;
        return result;
    } 
    
    /**
     * 计算两个日期之间月份跨算后的周数，按照每月4周计算
     * @Author:ALong (ZengWeiLong)
     * @param start 开始时间
     * @param end 结束时间
     * @return    
     * int
     * @date 2016年10月17日
     */
    public static int countWeeks(long start,long end){
        int month = DateUtils.countMouth(start,end);
        int totalWeek = month * 4;
        return totalWeek;
    }
    
    public static Map<String,String> yesterdayParamMap() {
        Map<String, String> resultMap = Maps.newHashMap();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startTime = cal.getTime();
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        Date endTime = cal.getTime();
        resultMap.put("startTime", DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss"));
        resultMap.put("endTime", DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss"));
        return resultMap;
    }
    
    public static String formatDate(Date date){
    	return formatDate(date, null);
    }
    public static String formatDate(Date date, String format) {
		String dateStr = null;
		if (StringUtils.isEmpty(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		if (date != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			dateStr = dateFormat.format(date);
		}
		return dateStr;
	}

	public static String formatDate(Date date, String format,Locale locale) {
		String dateStr = null;
		if (StringUtils.isEmpty(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		if (date != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format,locale);
			dateStr = dateFormat.format(date);
		}
		return dateStr;
	}

	public static Date parseDate(String dateStr, String format) {
		Date date = null;
		if (StringUtils.isEmpty(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		if (StringUtils.isNotBlank(dateStr)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			try {
				date = dateFormat.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * 是否在时间区间中
	 * @param date
	 * @param start  HH:mm:ss
	 * @param end HH:mm:ss
	 * @return
	 */
	public static boolean hasDateRangeForHHmmss(Date date,String start,String end){
		Date startTime=DateUtils.parseDate(start,DateUtils.HH_MM_SS);
		Date endTime=DateUtils.parseDate(end,DateUtils.HH_MM_SS);
		Long dateTime=DateUtils.parseDate(DateUtils.formatDate(date,DateUtils.HH_MM_SS),DateUtils.HH_MM_SS).getTime();
		return startTime.getTime() <= dateTime && dateTime <= endTime.getTime();
	}

    public static Date getTheDayOfNextMonth(Date date, int dayOfMonth){
    	Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        Date startTime = cal.getTime();
        return startTime;
    }

	public static Date getPrevMinutes(int offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -offset);
		return calendar.getTime();
	}




	/**
	 * 比较两个日期date1大于date2 返回1 等于返回0 小于返回-1
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareDate(Object date1, Object date2) {
		if (date1 == null || date2 == null) {
			String msg = "illegal arguments,date1 and date2 must be not null.";
			throw new IllegalArgumentException(msg);
		}
		Date d1 = (Date) (date1 instanceof String ? convertDate((String) date1) : date1);
		Date d2 = (Date) (date2 instanceof String ? convertDate((String) date2) : date2);
		return DateUtils.round(d1, Calendar.DATE).compareTo(DateUtils.round(d2, Calendar.DATE));
	}

	/**
	 * 将日期或者时间戳转化为日期对象
	 *
	 * @param date yyyy-MM-dd or yyyy-MM-dd HH:mm:ss or yyyy-MM-dd HH:mm:ss.SSS
	 * @return
	 */
	public static Date convertDate(String date) {
		if (StringUtils.isBlank(date)) {
			return null;
		}
		if (StringUtils.isNumeric(date)) {
			long timestamp = Long.parseLong(date);
			if (timestamp > 0 && timestamp < Long.MAX_VALUE) {
				return new Date(timestamp);
			} else {
				return null;
			}
		}
		if (date.indexOf(":") > 0) {
			return convertDate(date, DateUtils.DATETIME_PATTERN);
		} else if (date.indexOf(".") > 0) {
			return convertDate(date, DateUtils.DATETIME_MILL_SECOND);
		} else {
			return convertDate(date, DateUtils.DATE_PATTERN);
		}
	}

	/**
	 * 将日期或者时间字符串转化为日期对象
	 *
	 * @param date 日期字符串
	 * @param pattern 格式字符串</br> yyyy-MM-DD, yyyy/MM/DD, yyyyMMdd</br> yyyy-MM-dd-HH:mm:ss, yyyy-MM-dd HH:mm:ss
	 *            格式字符串可选字符："GyMdkHmsSEDFwWahKzZ"
	 * @return Date
	 */
	public static Date convertDate(String date, String pattern) {
		try {
			if (StringUtils.isEmpty(pattern) || StringUtils.isEmpty(date)) {
				String msg = "the date or pattern is empty.";
				throw new IllegalArgumentException(msg);
			}
			SimpleDateFormat df = new SimpleDateFormat(pattern.trim());
			return df.parse(date.trim());
		} catch (Exception e) {
			logger.error("Method===DateUtils.convertDate error!", e);
			return null;
		}
	}

	/**
	 * 按指定roundType格式化日期。
	 *
	 * @param date 日期
	 * @param roundType
	 * @return Date
	 */
	public static Date round(Date date, int roundType) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date.getTime());
		switch (roundType) {
			case Calendar.MONTH:
				c.set(Calendar.DAY_OF_MONTH, 1);
			case Calendar.DATE:
				c.set(Calendar.HOUR_OF_DAY, 0);
			case Calendar.HOUR:
				c.set(Calendar.MINUTE, 0);
			case Calendar.MINUTE:
				c.set(Calendar.SECOND, 0);
			case Calendar.SECOND:
				c.set(Calendar.MILLISECOND, 0);
				return c.getTime();
			default:
				throw new IllegalArgumentException("invalid round roundType.");
		}
	}
}
