package com.vipkid.trpm.job;

import com.vipkid.portal.bookings.service.BookingsService;
import com.vipkid.task.job.PracticumCancel24HoursNoBookJob;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.util.CacheUtils;
import com.vipkid.vschedule.client.schedule.JobContext;
import org.community.tools.JsonTools;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;

/**
 * Created by rentingji on 2017/3/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext.xml"})
@WebAppConfiguration
@Transactional
public class PracticumCancel24HoursNoBookJobTest {

    private static final Logger logger = LoggerFactory.getLogger(PracticumCancel24HoursNoBookJobTest.class);

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

    }


    @Test
    public void PracticumCancel24HoursNoBookJob() {
        PracticumCancel24HoursNoBookJob p=new PracticumCancel24HoursNoBookJob();
        p.doJob(new JobContext());
    }
}
