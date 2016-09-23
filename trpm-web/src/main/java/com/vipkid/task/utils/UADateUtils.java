/**
 * 
 */
package com.vipkid.task.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
	
	public static String format(Date date, String pattern){
        return format(date, pattern, null);
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
	
}
