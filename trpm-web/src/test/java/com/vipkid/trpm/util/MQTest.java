package com.vipkid.trpm.util;

import com.vipkid.mq.message.FinishOnlineClassMessage;
import com.vipkid.mq.service.impl.PayrollMessageServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by LP-813 on 2016/10/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/applicationContext.xml" })
public class MQTest {
    private static final Logger logger = LoggerFactory.getLogger(MQTest.class);

    @Autowired
    private PayrollMessageServiceImpl payrollMessageService;

    @Test
    public void testMonthOfYear() {
        //payrollMessageService.sendFinishOnlineClassMessage(4089565l, FinishOnlineClassMessage.OperatorType.ADD_UNIT_ASSESSMENT);
    }
}
