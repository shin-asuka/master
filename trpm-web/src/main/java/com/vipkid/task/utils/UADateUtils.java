/**
 * 
 */
package com.vipkid.task.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zouqinghua
 * @date 2016年8月19日  下午6:18:18
 *
 */
public class UADateUtils {

	public final static String defaultFormat = "yyyy-MM-dd HH:mm:ss";
	
	public static Date getTomorrow(int interval) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, interval);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getToday(int interval) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, interval);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 获取几小时之前的日期
	 * 
	 * @param interval
	 * @return
	 */
	public static Date getDateByBeforeHours(int interval ){
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY)-interval;
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		return calendar.getTime();
	}

	public static Date getDateOclockByBeforeHours(int interval ){
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY)-interval;
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	}

	public static String format(Date date){
		return format(date, defaultFormat);
	}

	public static String format(Date date, String pattern){
        return format(date, pattern, Locale.getDefault());
    }
	
	public static String format(Date date, String pattern, Locale locale) {
        String dateStr = "";

        if(locale == null){
            locale = Locale.getDefault();
        }
        if(StringUtils.isBlank(pattern)){
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        if(date!=null){
            DateFormat format = new SimpleDateFormat(pattern, locale);
            dateStr = format.format(date);
        }
        return dateStr;
    }
	
	public static String format(Date date , String pattern , String timezone){
		String dateStr = "";
		Locale locale = Locale.getDefault();
        if(StringUtils.isBlank(pattern)){
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        if(date!=null){
            DateFormat format = new SimpleDateFormat(pattern, locale);
            if(StringUtils.isNotBlank(timezone)){
            	 format.setTimeZone(TimeZone.getTimeZone(timezone));
            }
            dateStr = format.format(date);
        }
        return dateStr;
	}

	public static Date parse(String date){
		return parse(date, defaultFormat);
	}

	public static Date parse(String date, String pattern){
		return parse(date, pattern, Locale.getDefault());
	}

	public static Date parse(String date, String pattern, Locale locale){
		Date dateVal = null;

		if(locale == null){
			locale = Locale.getDefault();
		}
		if(StringUtils.isBlank(pattern)){
			pattern = "yyyy-MM-dd HH:mm:ss";
		}
		if(date!=null){
			DateFormat format = new SimpleDateFormat(pattern, locale);
			try {
				dateVal = format.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return dateVal;
	}

	public static Date parse(String date , String pattern , String timezone){
		Date dateVal = null;
		Locale locale = Locale.getDefault();
		if(StringUtils.isBlank(pattern)){
			pattern = "yyyy-MM-dd HH:mm:ss";
		}
		if(date!=null){
			DateFormat format = new SimpleDateFormat(pattern, locale);
			if(StringUtils.isNotBlank(timezone)){
				format.setTimeZone(TimeZone.getTimeZone(timezone));
			}
			try {
				dateVal = format.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return dateVal;
	}

	public static List<Map> getStartEndTimeMapListByBeforeHours(int... beforeHours){
		return getStartEndTimeMapListByBeforeHours(new Integer(1), beforeHours);
	}

	//UADateUtils.getStartEndTimeMapListByBeforeHours(new Integer(1), 24, 48, 72)
	public static List<Map> getStartEndTimeMapListByBeforeHours(Integer interval, int... beforeHours){
		List<Map> startEndTimes = new ArrayList<>();
		for (int i : beforeHours){
			String startTime = UADateUtils.format(UADateUtils.getDateByBeforeHours(i + interval)) ;
			String endTime = UADateUtils.format(UADateUtils.getDateByBeforeHours(i)) ;

			Map<String, String> time = new HashMap<>();
			time.put("startTime",startTime);
			time.put("endTime",endTime);

			startEndTimes.add(time);
		}
		return startEndTimes;
	}

	public static List<Map> getStartEndOclockTimeMapListByBeforeHours(int... beforeHours){
		return getStartEndOclockTimeMapListByBeforeHours(new Integer(1), beforeHours);
	}

	//UADateUtils.getStartEndOclockTimeMapListByBeforeHours(new Integer(1), 24, 48, 72)
	public static List<Map> getStartEndOclockTimeMapListByBeforeHours(Integer interval, int... beforeHours){
		List<Map> startEndTimes = new ArrayList<>();
		for (int i : beforeHours){
			String startTime = UADateUtils.format(UADateUtils.getDateOclockByBeforeHours(i + interval)) ;
			String endTime = UADateUtils.format(UADateUtils.getDateOclockByBeforeHours(i)) ;

			Map<String, String> time = new HashMap<>();
			time.put("startTime",startTime);
			time.put("endTime",endTime);

			startEndTimes.add(time);
		}
		return startEndTimes;
	}
}
