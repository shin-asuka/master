package com.vipkid.recruitment;



import com.vipkid.file.utils.DateUtils;

import org.junit.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by pankui on 2017-04-14.
 */
public class TeacherRecruitPeakTimeTest {


    @Test
    public void testSave(){

        long timeMillis = System.currentTimeMillis();
        Date date = DateUtils.getDateFromTime(timeMillis);
        String dateStr = DateUtils.formatDateByFormat(date,"yyyy-MM-dd HH:mm:ss");
        System.out.println(date);
        System.out.println(timeMillis);
        System.out.println(dateStr);


        // +8:00 time zone:
        SimpleDateFormat sdf_8 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf_8.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        System.out.println("GMT+8:00 = " + sdf_8.format(timeMillis));
    }

    @Test
    public void testDateUitlsParseFrom(){
        long datetime = System.currentTimeMillis();
        Timestamp t = com.vipkid.trpm.util.DateUtils.parseFrom(String.valueOf(datetime), com.vipkid.trpm.util.DateUtils.FMT_YMD_HMS);
        System.out.println(t);
    }


}
