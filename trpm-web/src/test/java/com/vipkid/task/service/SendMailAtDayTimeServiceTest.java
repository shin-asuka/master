package com.vipkid.task.service;

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

/**
 * Created by liuguowen on 2017/4/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext.xml"})
@WebAppConfiguration
@Transactional
public class SendMailAtDayTimeServiceTest {

    @Resource
    private SendMailAtDayTimeService sendMailAtDayTimeService;

    @Test
    public void testSaveAllInterviewBookedReminder() throws InterruptedException {
        Teacher teacher = new Teacher();
        teacher.setEmail("liuguowen@vipkid.com.cn");
        teacher.setRealName("John Liu");
        teacher.setTimezone("America/Los_Angeles");

        Timestamp scheduledDateTime = Timestamp.from(DateUtils.parseDate("2017-04-13 18:00:00", null).toInstant());
        sendMailAtDayTimeService.saveAllInterviewBookedReminder(teacher, scheduledDateTime, 152456);
    }

}
