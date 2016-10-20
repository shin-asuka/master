package com.vipkid.trpm.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;

import com.google.common.collect.Maps;
import com.vipkid.trpm.constant.ApplicationConstant;

/**
 * 日期工具类
 */
public final class DateUtils {

	public static String AM = "AM";

	public static ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

	public static DateTimeFormatter FMT_HMA_US = DateTimeFormatter.ofPattern("hh:mm a").withLocale(Locale.US);

	public static DateTimeFormatter FMT_YMD_US = DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.US);
	public static DateTimeFormatter FMT_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static DateTimeFormatter FMT_YMD_HMA_US = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a").withLocale(
			Locale.US);

	public static DateTimeFormatter FMT_YMD_HMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static DateTimeFormatter FMT_MMM_YYYY = DateTimeFormatter.ofPattern("MMM yyyy");
	public static DateTimeFormatter FMT_MMM_YYYY_US = DateTimeFormatter.ofPattern("MMM yyyy").withLocale(Locale.US);

	public static DateTimeFormatter FMT_HMS = DateTimeFormatter.ofPattern("HH:mm:ss");

	public static DateTimeFormatter FMT_YM = DateTimeFormatter.ofPattern("yyyy-MM");

	/* 每天的半小时数量 */
	public static final int HALFHOUR_OF_DAY = 24 * 2;
	/* 半小时的分钟数 */
	public static final int MINUTE_OF_HALFHOUR = 30;
	/* 每星期的天数 */
	public static final int DAY_OF_WEEK = 7;

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
     * @param classTime
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
     * 
     * 计算两个时间之间月份跨度 
     * @Author:ALong (ZengWeiLong)
     * @param time1
     * @param time2
     * @return    
     * int
     * @date 2016年10月17日
     */
    public static int countMouth(long time1,long time2){
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);
        int result = (c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH)) * 12 + c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH) + 1;
        return result;
    } 
    
    /**
     * 计算两个日期之间月份跨算后的周数，按照每月4周计算
     * @Author:ALong (ZengWeiLong)
     * @param time1
     * @param time2
     * @return    
     * int
     * @date 2016年10月17日
     */
    public static int countWeeks(long time1,long time2){
        int month = DateUtils.countMouth(time1,time2);
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
}
