package com.vipkid.task.job;

import com.vipkid.vschedule.client.schedule.JobContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by liuguowen on 2017/4/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext.xml"})
@WebAppConfiguration
@Transactional
public class InterviewMockClassBookedReminderJobTest {

    private static final Logger logger = LoggerFactory.getLogger(InterviewMockClassBookedReminderJobTest.class);

    @Autowired
    private InterviewMockClassBookedReminderJob interviewMockClassBookedReminderJob;

    @Test
    public void testDoJob() throws InterruptedException {
        JobContext jobContext = new JobContext();
        jobContext.setData("2017-04-13 06:00:00");
        interviewMockClassBookedReminderJob.doJob(jobContext);
        Thread.sleep(10 * 1000L);
    }

    @Test
    public void testGetLocalDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Assert.assertEquals(interviewMockClassBookedReminderJob.getLocalDateTime("2017-04-12 18:01:00"),
                        LocalDateTime.parse("2017-04-12 18:00:00", formatter));

        Assert.assertEquals(interviewMockClassBookedReminderJob.getLocalDateTime("2017-04-12 18:14:00"),
                        LocalDateTime.parse("2017-04-12 18:15:00", formatter));
        Assert.assertEquals(interviewMockClassBookedReminderJob.getLocalDateTime("2017-04-12 18:16:08"),
                        LocalDateTime.parse("2017-04-12 18:15:00", formatter));

        Assert.assertEquals(interviewMockClassBookedReminderJob.getLocalDateTime("2017-04-12 18:29:00"),
                        LocalDateTime.parse("2017-04-12 18:30:00", formatter));
        Assert.assertEquals(interviewMockClassBookedReminderJob.getLocalDateTime("2017-04-12 18:32:20"),
                        LocalDateTime.parse("2017-04-12 18:30:00", formatter));

        Assert.assertEquals(interviewMockClassBookedReminderJob.getLocalDateTime("2017-04-12 18:44:00"),
                        LocalDateTime.parse("2017-04-12 18:45:00", formatter));
        Assert.assertEquals(interviewMockClassBookedReminderJob.getLocalDateTime("2017-04-12 18:46:03"),
                        LocalDateTime.parse("2017-04-12 18:45:00", formatter));

        Assert.assertEquals(interviewMockClassBookedReminderJob.getLocalDateTime("2017-04-12 18:58:00"),
                        LocalDateTime.parse("2017-04-12 19:00:00", formatter));

        Assert.assertEquals(interviewMockClassBookedReminderJob.getLocalDateTime("2017-04-12 19:00:40"),
                        LocalDateTime.parse("2017-04-12 19:00:00", formatter));

        logger.info("{}", interviewMockClassBookedReminderJob.getLocalDateTime(null).toString());
    }

}
