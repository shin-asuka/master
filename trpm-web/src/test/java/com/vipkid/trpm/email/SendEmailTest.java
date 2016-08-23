/**
 * 
 */
package com.vipkid.trpm.email;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.api.client.util.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailHandle;
import com.vipkid.task.service.UnitAssesssmentService;
import com.vipkid.task.utils.UADateUtils;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;

/**
 * @author zouqinghua
 * @date 2016年8月19日  下午4:27:29
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/applicationContext.xml" })
public class SendEmailTest {

	@Autowired
	private UnitAssesssmentService unitAsusesssmentService;
	
	@Autowired
    private OnlineClassDao onlineClassDao;
	
	@Test
	public void sendUaEmail(){
		
		String email = "zouqinghua@vipkid.com.cn";
		
		Map<String, String> sendMap = Maps.newHashMap();
		sendMap.put("title", "Test Email");
		sendMap.put("content", "hello world!");
		//new EmailEngine().addMailPool(email, sendMap , EmailFormEnum.TEACHVIP);
		
		EmailHandle emailHandle =
                new EmailHandle(email, sendMap.get("title"), sendMap.get("content"), EmailFormEnum.TEACHVIP);
//		try {
//			emailHandle.sendMail();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
	}
	
	@Test
	public void dateTest(){
		//Date date = UADateUtils.getToday(20);
		Calendar calendar = Calendar.getInstance();
		//calendar.add(Calendar.DATE, 0);
		 int hour = calendar.get(Calendar.HOUR_OF_DAY)-8;
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		
		String dateStr = UADateUtils.format(calendar.getTime(), null);
		System.out.println(dateStr);
	}
	
	@Test
	public void findOnlineClass(){
		
		unitAsusesssmentService.remindTeacherUnitAssessmentFor6Hour();
	}
	

}
