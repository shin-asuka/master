package com.vipkid.portal.controller;

import com.vipkid.http.utils.JsonUtils;
import com.vipkid.portal.entity.ScheduledRequest;
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
        User user = loginService.getLoginUser("lilibo1@vipkid.com.cn");
        redisProxy.set(key, JsonTools.getJson(user), 12 * 60 * 60);
    }

    @Test
    public void testScheduled() throws Exception {
        ScheduledRequest scheduledRequest = new ScheduledRequest();
        scheduledRequest.setType("MAJOR");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/portal/scheduled").header("Authorization", TOKEN)
                        .content(JsonUtils.toJSONString(scheduledRequest));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        if (HttpStatus.OK.value() == mvcResult.getResponse().getStatus()) {
            logger.info("Result Json {}", mvcResult.getResponse().getContentAsString());
        } else {
            logger.info("Result Status {}", mvcResult.getResponse().getStatus());
        }
    }

}
