package com.vipkid.trpm.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtilsTest {

	private static final Logger logger = LoggerFactory.getLogger(DateUtilsTest.class);

	@Test
	public void testFormatFrom() {
		logger.info("Timestamp: {}",
				DateUtils.parseFrom("2015-12-01 13:30:00", DateUtils.FMT_YMD_HMS));
	}

	@Test
	public void testMonthOfYear() {
		logger.info("MonthOfYear: {}", DateUtils.monthOfYear(0, DateUtils.FMT_YM));
		logger.info("MonthOfYear: {}", DateUtils.monthOfYear(0, DateUtils.FMT_MMM_YYYY_US));
		
		logger.info("MonthOfYear plus 1: {}", DateUtils.monthOfYear(1, DateUtils.FMT_YM));
		logger.info("MonthOfYear plus 1: {}", DateUtils.monthOfYear(1, DateUtils.FMT_MMM_YYYY_US));
		
		logger.info("MonthOfYear plus -1: {}", DateUtils.monthOfYear(-1, DateUtils.FMT_YM));
		logger.info("MonthOfYear plus -1: {}", DateUtils.monthOfYear(-1, DateUtils.FMT_MMM_YYYY_US));
	}

}
