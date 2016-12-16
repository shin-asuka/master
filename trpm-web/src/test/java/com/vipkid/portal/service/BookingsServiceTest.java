package com.vipkid.portal.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by liuguowen on 2016/12/15.
 */
public class BookingsServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(BookingsServiceTest.class);

    @Test
    public void testGetDaysOfWeek() {
        List<Date> list = new BookingsService().getDaysOfWeek(0);
        list.stream().forEach(date -> logger.info(date.toString()));
    }

}
