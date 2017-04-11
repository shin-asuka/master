package com.vipkid.rest.utils;

import com.vipkid.common.utils.ApplicationConfig;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.rest.portal.model.ClassroomDetail;
import com.vipkid.trpm.util.DateUtils;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by liyang on 2017/4/11.
 */
public class ClassroomUtils {


    public static void buildAsyncLessonSN(ClassroomDetail detail){

        //总开关，true则生效，false则不生效下
        if(!Boolean.valueOf(ApplicationConfig.getValue("async.classroom.lesson.switch","true"))){
            return ;
        }

        Calendar currentTime = Calendar.getInstance();
        //是否是星期一
        if(currentTime.get(Calendar.DAY_OF_WEEK) != getDayOfWeek()){
            return ;
        }

        if(!StringUtils.equals(detail.getStatus(),"BOOKED")){
            return ;
        }

        String startTime = ApplicationConfig.getValue("async.classroom.lesson.date.range.start.time", "12:00:00");
        String endTime = ApplicationConfig.getValue("async.classroom.lesson.date.range.end.time", "12:10:00");
        //当前时间是否是要做异步刷新课标的
        if(!DateUtils.hasDateRangeForHHmmss(currentTime.getTime(),startTime,endTime)){
            return ;
        }


        Calendar bookedTime = detail.getBookDateTime();
        //判断bookedTime 是不是今天的
        if(bookedTime == null){
            return ;
        }


        if(currentTime.get(Calendar.YEAR) == bookedTime.get(Calendar.YEAR)
                && currentTime.get(Calendar.MONTH) == bookedTime.get(Calendar.MONTH)
                && currentTime.get(Calendar.DAY_OF_MONTH) == bookedTime.get(Calendar.DAY_OF_MONTH)
                && DateUtils.hasDateRangeForHHmmss(bookedTime.getTime(),startTime,endTime)){
            String  lessonName = ApplicationConfig.getValue("async.classroom.lesson.name", "Booked-info pending");
            String  serialNumber = ApplicationConfig.getValue("async.classroom.lesson.serial.number","Booked-info pending");
            detail.setLessonName(lessonName);
            detail.setLessonSerialNumber(serialNumber);
        }

    }

    public static void buildAsyncLessonSN(Map<String,Object> teacherSchedule){
        //总开关，true则生效，false则不生效下
        if(!Boolean.valueOf(ApplicationConfig.getValue("async.classroom.lesson.switch","true"))){
            return ;
        }

        Calendar currentTime = Calendar.getInstance();
        //是否是星期一
        if(currentTime.get(Calendar.DAY_OF_WEEK) != getDayOfWeek()){
            return ;
        }

        if(!StringUtils.equals((String)teacherSchedule.get("status"),"BOOKED")){
            return ;
        }

        String startTime = ApplicationConfig.getValue("async.classroom.lesson.date.range.start.time", "12:00:00");
        String endTime = ApplicationConfig.getValue("async.classroom.lesson.date.range.end.time", "12:10:00");
        //当前时间是否是要做异步刷新课标的
        if(!DateUtils.hasDateRangeForHHmmss(currentTime.getTime(),startTime,endTime)){
            return ;
        }


        Calendar bookedTime = (Calendar)teacherSchedule.get("bookDateTime");
        //判断bookedTime 是不是今天的
        if(bookedTime == null){
            return ;
        }


        if(currentTime.get(Calendar.YEAR) == bookedTime.get(Calendar.YEAR)
                && currentTime.get(Calendar.MONTH) == bookedTime.get(Calendar.MONTH)
                && currentTime.get(Calendar.DAY_OF_MONTH) == bookedTime.get(Calendar.DAY_OF_MONTH)
                && DateUtils.hasDateRangeForHHmmss(bookedTime.getTime(),startTime,endTime)){
            String  lessonName = ApplicationConfig.getValue("async.classroom.lesson.name", "Booked-info pending");
            String  serialNumber = ApplicationConfig.getValue("async.classroom.lesson.serial.number","Booked-info pending");
            teacherSchedule.put("serialNumber",lessonName);
            teacherSchedule.put("lessonName",serialNumber);
        }
    }



    private static int getDayOfWeek(){
        String dayOfWeek = ApplicationConfig.getValue("async.classroom.lesson.day.of.week","2");
        if(StringUtils.isBlank(dayOfWeek)){
            return Calendar.MONDAY;
        }
        if(StringUtils.isNumeric(dayOfWeek)){
            int day = Integer.valueOf(dayOfWeek);
            if(day<1 || day > 7){
                return day;
            }
        }
        return Calendar.MONDAY;
    }
}
