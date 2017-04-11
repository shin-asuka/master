package com.vipkid.task.job;

import com.vipkid.vschedule.client.schedule.JobContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liuguowen on 2017/4/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext.xml"})
@WebAppConfiguration
@Transactional
public class InterviewMockClassBookedReminderJobTest {

    @Autowired
    private InterviewMockClassBookedReminderJob interviewMockClassBookedReminderJob;

    @Test
    public void testDoJob() {
        interviewMockClassBookedReminderJob.doJob(new JobContext());
    }

}
