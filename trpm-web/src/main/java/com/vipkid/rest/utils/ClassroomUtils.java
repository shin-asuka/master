package com.vipkid.rest.utils;

import com.vipkid.file.utils.StringUtils;
import com.vipkid.rest.portal.model.ClassroomDetail;
import com.vipkid.trpm.util.DateUtils;
import sun.util.calendar.CalendarUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by liyang on 2017/4/11.
 */
public class ClassroomUtils {


    public static void buildAsyncLessonSN(ClassroomDetail detail){
        Calendar currentTime = Calendar.getInstance();
        //是否是星期一
        if(currentTime.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
            return ;
        }

        if(!StringUtils.equals(detail.getStatus(),"BOOKED")){
            return ;
        }

        //当前时间是否是要做异步刷新课标的
        if(!DateUtils.hasDateRangeForHHmmss(currentTime.getTime(),"12:00:00","12:10:00")){
            return ;
        }


        Calendar bookedTime = detail.getBookDateTime();
        //判断bookedTime 是不是今天的
        if(bookedTime == null){
            return ;
        }


        if(currentTime.get(Calendar.YEAR) == bookedTime.get(Calendar.YEAR)
                && currentTime.get(Calendar.MONTH) == bookedTime.get(Calendar.MONTH)
                && currentTime.get(Calendar.DAY_OF_MONTH) == bookedTime.get(Calendar.DAY_OF_MONTH)){
            detail.setLessonName("Booked-info pending");
            detail.setLessonSerialNumber("Booked-info pending");
        }

    }

    public static void buildAsyncLessonSN(Map<String,Object> teacherSchedule){
        Calendar currentTime = Calendar.getInstance();
        //是否是星期一
        if(currentTime.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
            return ;
        }

        if(!StringUtils.equals((String)teacherSchedule.get("status"),"BOOKED")){
            return ;
        }

        //当前时间是否是要做异步刷新课标的
        if(!DateUtils.hasDateRangeForHHmmss(currentTime.getTime(),"12:00:00","12:10:00")){
            return ;
        }


        Calendar bookedTime = (Calendar)teacherSchedule.get("bookDateTime");
        //判断bookedTime 是不是今天的
        if(bookedTime == null){
            return ;
        }


        if(currentTime.get(Calendar.YEAR) == bookedTime.get(Calendar.YEAR)
                && currentTime.get(Calendar.MONTH) == bookedTime.get(Calendar.MONTH)
                && currentTime.get(Calendar.DAY_OF_MONTH) == bookedTime.get(Calendar.DAY_OF_MONTH)){
            teacherSchedule.put("serialNumber","Booked-info pending");
            teacherSchedule.put("lessonName","Booked-info pending");
        }
    }



}
