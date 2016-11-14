package com.vipkid.recruitment.utils;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateTools {
    public final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public final static DateFormat DATE_FORMAT2 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    public final static DateFormat DATE_FORMAT3 = new SimpleDateFormat("MMM dd", Locale.US);
    public final static DateFormat DATE_FORMAT4 = new SimpleDateFormat("MM-dd", Locale.getDefault());
    public final static DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    public final static DateFormat TIME_FORMAT2 = new SimpleDateFormat("hh:mma", Locale.US);
    public final static DateFormat TIME_FORMAT3 = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
    public final static DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public final static DateFormat DATETIME_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public final static DateFormat DATETIME_FORMAT3 = new SimpleDateFormat("MMM dd, hh:mma", Locale.US);
    public final static DateFormat DATETIME_FORMAT4 = new SimpleDateFormat("EEEE, MMM dd, hh:mma", Locale.US);
    public final static DateFormat DATETIME_FORMAT5 = new SimpleDateFormat("HH:mm", Locale.getDefault());
    public final static DateFormat DATETIME_FORMAT6 = new SimpleDateFormat("MMddHHmm", Locale.getDefault());
    public final static DateFormat DATETIME_FORMAT7 = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
    public static final String FORMAT_YEAR_MONTH_DEFAULT = "yyyyMM";

    /**
     * Interview开始时间 从1小时之后获取
     * @Author:ALong
     * @param date
     * @return 2015年11月9日
     */
    public static List<Date> weeksForInterviewStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        List<Date> scheduleWeeks = new ArrayList<Date>();
        for (int i = 0; i < 7; i++) {
            if( i == 0){
                calendar.add(Calendar.HOUR, 1);
                scheduleWeeks.add(calendar.getTime());
            }else{
                calendar.add(Calendar.DATE, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                scheduleWeeks.add(calendar.getTime());
            }
        }
        return scheduleWeeks;
    }

    
    /**
     * interview开始时间从隔天的00点获取
     * @Author:ALong
     * @param date
     * @return 2015年11月9日
     */
    public static List<Date> weeksForInterviewNext(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        List<Date> scheduleWeeks = new ArrayList<Date>();
        for (int i = 0; i < 7; i++) {
            if( i > 0){
                calendar.add(Calendar.DATE, 1);
            }
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            scheduleWeeks.add(calendar.getTime());
        }

        return scheduleWeeks;
    }
    
    
    /**
     * Practicum开始时间 从24小时之后获取
     * @Author:ALong
     * @param date
     * @return 2015年11月9日
     */
    public static List<Date> weeksForPracticumStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        List<Date> scheduleWeeks = new ArrayList<Date>();
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DATE, 1);
            scheduleWeeks.add(calendar.getTime());
        }

        return scheduleWeeks;
    }
    
    /**
     * Practicum开始时间从隔天的00点获取
     * @Author:ALong
     * @param date
     * @return 2015年11月9日
     */
    public static List<Date> weeksForPracticumNext(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        List<Date> scheduleWeeks = new ArrayList<Date>();
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            scheduleWeeks.add(calendar.getTime());
        }

        return scheduleWeeks;
    }
    
    
    public static Map<Date, List<Date>> getScheduled(List<Date> scheduledWeeks) {
        Map<Date, List<Date>> scheduleWeekTime = new TreeMap<Date, List<Date>>();
        List<Date> scheduledTimes = getScheduledTimes();

        for (Date time : scheduledTimes) {
            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(time);

            List<Date> weekTimes = new ArrayList<Date>();
            for (Date week : scheduledWeeks) {
                Calendar weekCalendar = Calendar.getInstance();
                weekCalendar.setTime(week);
                int year = weekCalendar.get(Calendar.YEAR);
                int month = weekCalendar.get(Calendar.MONTH);
                int day = weekCalendar.get(Calendar.DAY_OF_MONTH);

                timeCalendar.set(Calendar.YEAR, year);
                timeCalendar.set(Calendar.MONTH, month);
                timeCalendar.set(Calendar.DAY_OF_MONTH, day);

                weekTimes.add(timeCalendar.getTime());
            }

            scheduleWeekTime.put(time, weekTimes);
        }

        return scheduleWeekTime;
    }
    
    
    public static List<Date> getScheduledTimes() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        List<Date> scheduleTimes = new ArrayList<Date>();
        int timeRange = 24 * 2;
        for (int i = 0; i < timeRange; i++) {
            scheduleTimes.add(calendar.getTime());
            calendar.add(Calendar.MINUTE, 30);
        }

        return scheduleTimes;
    }


    private static SimpleDateFormat getSimpleDateFormat(DateFormat format) {
        SimpleDateFormat sdf = null;
        if(DATE_FORMAT.equals(format)){
            sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }else if(DATE_FORMAT2.equals(format)){
            sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        }else if(DATE_FORMAT3.equals(format)){
            sdf = new SimpleDateFormat("MMM dd", Locale.US);
        }else if(DATE_FORMAT4.equals(format)){
            sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
        }else if(TIME_FORMAT.equals(format)){
            sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        }else if(TIME_FORMAT2.equals(format)){
            sdf = new SimpleDateFormat("hh:mma", Locale.US);
        }else if(TIME_FORMAT3.equals(format)){
            sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        }else if(DATETIME_FORMAT.equals(format)){
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }else if(DATETIME_FORMAT2.equals(format)){
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        }else if(DATETIME_FORMAT3.equals(format)){
            sdf = new SimpleDateFormat("MMM dd, hh:mma", Locale.US);
        }else if(DATETIME_FORMAT4.equals(format)){
            sdf = new SimpleDateFormat("EEEE, MMM dd, hh:mma", Locale.US);
        }else if(DATETIME_FORMAT5.equals(format)){
            sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        }else if(DATETIME_FORMAT6.equals(format)){
            sdf = new SimpleDateFormat("MMddHHmm", Locale.getDefault());
        }else if(DATETIME_FORMAT7.equals(format)){
            sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());

        }
        return sdf;
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

    public static String format(Date date, DateFormat format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = getSimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String format(Date date, DateFormat format, TimeZone tz) {
        SimpleDateFormat sdf = getSimpleDateFormat(format);
        if (format!= null && tz != null){
            sdf.setTimeZone(tz);
        }
        return (date == null ? null : sdf.format(date));
    }
}
