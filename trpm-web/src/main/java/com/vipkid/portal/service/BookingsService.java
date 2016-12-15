package com.vipkid.portal.service;

import com.google.common.collect.Lists;
import com.vipkid.portal.entity.TimeUnit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Created by liuguowen on 2016/12/15.
 */
@Service
public class BookingsService {

    private static final int DAYS_OF_A_WEEK = 7;
    private static final int HALF_HOURS_OF_DAY = 48;
    private static final int MINUTES_OF_A_HALF_HOUR = 30;

    private static ZoneId SHANG_HAI = ZoneId.of("Asia/Shanghai");
    private static DateTimeFormatter FMT_HMA_US = DateTimeFormatter.ofPattern("hh:mm a").withLocale(Locale.US);

    /**
     * 根据周偏移量获取一周的天数（为了兼容夏令时，这里的取值范围为这个星期一到下个星期一）
     * 
     * @param weekOffset 周偏移量
     * @return List<Date>
     */
    public List<Date> getDaysOfWeek(int weekOffset) {
        Calendar calendar = Calendar.getInstance();
        if (0 != weekOffset) {
            calendar.add(Calendar.DATE, weekOffset * DAYS_OF_A_WEEK);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1);
        }

        List<Date> daysOfWeek = Lists.newLinkedList();
        IntStream.rangeClosed(0, DAYS_OF_A_WEEK).forEach(index -> {
            daysOfWeek.add(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        });
        return daysOfWeek;
    }

    public List<TimeUnit> getTimeUnitsOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        List<TimeUnit> timeUnitsOfDay = Lists.newLinkedList();
        IntStream.range(0, HALF_HOURS_OF_DAY).forEach(index -> {
            Date date = calendar.getTime();
            String title = LocalDateTime.ofInstant(date.toInstant(), SHANG_HAI).format(FMT_HMA_US);
            timeUnitsOfDay.add(new TimeUnit(title, date.getTime()));
            calendar.add(Calendar.MINUTE, MINUTES_OF_A_HALF_HOUR);
        });
        return timeUnitsOfDay;
    }

}
