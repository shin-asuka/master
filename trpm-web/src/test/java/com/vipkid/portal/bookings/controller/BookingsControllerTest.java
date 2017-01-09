package com.vipkid.portal.bookings.controller;

import com.google.common.collect.Lists;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.portal.bookings.entity.*;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.util.CacheUtils;
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

/**
 * Created by liuguowen on 2016/12/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext.xml"})
@WebAppConfiguration
@Transactional
public class BookingsControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(BookingsControllerTest.class);

    private static final String TOKEN = "295b9605-e1e2-4101-8946-aa2bb9e3c658";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private LoginService loginService;

    @Autowired
    private RedisProxy redisProxy;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        String key = CacheUtils.getUserTokenKey(TOKEN);
        User user = loginService.getLoginUser("baoyuxiao1@vipkid.com.cn");
        redisProxy.set(key, JsonTools.getJson(user), 12 * 60 * 60);
    }

    @Test
    public void testScheduled() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/portal/scheduled").header("Authorization", TOKEN)
                        .param("type", "PRACTICUM");
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        if (HttpStatus.OK.value() == mvcResult.getResponse().getStatus()) {
            logger.info("Result Json {}", mvcResult.getResponse().getContentAsString());
        } else {
            logger.info("Result Status {}", mvcResult.getResponse().getStatus());
        }
    }

    @Test
    public void testCreateTimeSlot() throws Exception {
        TimeSlotCreateRequest timeSlotCreateRequest = new TimeSlotCreateRequest();
        timeSlotCreateRequest.setType("MAJOR");
        timeSlotCreateRequest.setScheduledDateTime("2016-12-23 15:30:00");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/portal/createTimeSlot")
                        .header("Authorization", TOKEN).content(JsonUtils.toJSONString(timeSlotCreateRequest));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        if (HttpStatus.OK.value() == mvcResult.getResponse().getStatus()) {
            logger.info("Result Json {}", mvcResult.getResponse().getContentAsString());
        } else {
            logger.info("Result Status {}", mvcResult.getResponse().getStatus());
        }
    }

    @Test
    public void testCancelTimeSlot() throws Exception {
        TimeSlotCancelRequest timeSlotCancelRequest = new TimeSlotCancelRequest();
        timeSlotCancelRequest.setOnlineClassId(7900102);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/portal/cancelTimeSlot")
                        .header("Authorization", TOKEN).content(JsonUtils.toJSONString(timeSlotCancelRequest));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        if (HttpStatus.OK.value() == mvcResult.getResponse().getStatus()) {
            logger.info("Result Json {}", mvcResult.getResponse().getContentAsString());
        } else {
            logger.info("Result Status {}", mvcResult.getResponse().getStatus());
        }
    }

    @Test
    public void testSet24Hours() throws Exception {
        Set24HourRequest set24HourRequest = new Set24HourRequest();
        set24HourRequest.setOnlineClassIds(Lists.newArrayList(7900108L));
        set24HourRequest.setClassType(0);
        set24HourRequest.setWeekOffset(0);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/portal/set24Hours").header("Authorization", TOKEN)
                        .content(JsonUtils.toJSONString(set24HourRequest));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        if (HttpStatus.OK.value() == mvcResult.getResponse().getStatus()) {
            logger.info("Result Json {}", mvcResult.getResponse().getContentAsString());
        } else {
            logger.info("Result Status {}", mvcResult.getResponse().getStatus());
        }
    }

    @Test
    public void testDelete24Hours() throws Exception {
        Delete24HourRequest delete24HourRequest = new Delete24HourRequest();
        delete24HourRequest.setOnlineClassIds(Lists.newArrayList(7900108L));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/portal/delete24Hours")
                        .header("Authorization", TOKEN).content(JsonUtils.toJSONString(delete24HourRequest));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        if (HttpStatus.OK.value() == mvcResult.getResponse().getStatus()) {
            logger.info("Result Json {}", mvcResult.getResponse().getContentAsString());
        } else {
            logger.info("Result Status {}", mvcResult.getResponse().getStatus());
        }
    }

    @Test
    public void testGetTips() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/portal/getTips").header("Authorization", TOKEN);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        if (HttpStatus.OK.value() == mvcResult.getResponse().getStatus()) {
            logger.info("Result Json {}", mvcResult.getResponse().getContentAsString());
        } else {
            logger.info("Result Status {}", mvcResult.getResponse().getStatus());
        }
    }

    @Test
    public void testGetAnnouncements() throws Exception {
        RequestBuilder requestBuilder =
                        MockMvcRequestBuilders.get("/portal/getAnnouncements").header("Authorization", TOKEN);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        if (HttpStatus.OK.value() == mvcResult.getResponse().getStatus()) {
            logger.info("Result Json {}", mvcResult.getResponse().getContentAsString());
        } else {
            logger.info("Result Status {}", mvcResult.getResponse().getStatus());
        }
    }

}
