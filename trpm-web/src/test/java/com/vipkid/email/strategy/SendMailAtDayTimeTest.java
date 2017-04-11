package com.vipkid.email.strategy;

import com.vipkid.task.service.SendMailAtDayTimeService;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.util.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Date;

/**
 * Created by liuguowen on 2017/4/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext.xml"})
@WebAppConfiguration
@Transactional
public class SendMailAtDayTimeTest {

    @Resource
    private SendMailAtDayTimeService sendMailAtDayTime;

    @Test
    public void testInterviewBooked48And24Reminder() throws InterruptedException {
        Teacher teacher = new Teacher();
        teacher.setEmail("liuguowen@vipkid.com.cn");
        teacher.setRealName("John Liu");
        teacher.setTimezone("America/Los_Angeles");

        Timestamp scheduledDateTime = Timestamp.from(DateUtils.parseDate("2017-04-11 18:00:00", null).toInstant());

        LocalDateTime now = LocalDateTime.now().with(ChronoField.HOUR_OF_DAY, 16).with(ChronoField.MINUTE_OF_HOUR, 10)
                        .with(ChronoField.SECOND_OF_MINUTE, 0);
        Date sendScheduledTime = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

        sendMailAtDayTime.interviewBooked48And24HoursReminder(teacher, scheduledDateTime, sendScheduledTime);
        Thread.sleep(100 * 1000L);
    }

    @Test
    public void testSendAllInterviewBookedReminder() throws InterruptedException {
        Teacher teacher = new Teacher();
        teacher.setEmail("liuguowen@vipkid.com.cn");
        teacher.setRealName("John Liu");
        teacher.setTimezone("America/Los_Angeles");

        Timestamp scheduledDateTime = Timestamp.from(DateUtils.parseDate("2017-04-13 18:00:00", null).toInstant());

        sendMailAtDayTime.sendAllInterviewBookedReminder(teacher, scheduledDateTime);
        Thread.sleep(100 * 1000L);
    }

}
