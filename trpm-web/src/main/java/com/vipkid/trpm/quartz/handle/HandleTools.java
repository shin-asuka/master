package com.vipkid.trpm.quartz.handle;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;

import com.google.common.collect.Maps;

public class HandleTools {
    
    public static Map<String,String> paramMap() {
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
