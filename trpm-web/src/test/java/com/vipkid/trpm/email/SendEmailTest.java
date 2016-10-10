/**
 * 
 */
package com.vipkid.trpm.email;

import java.util.Calendar;
import java.util.Map;

import com.google.api.client.util.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.task.utils.UADateUtils;

/**
 * @author zouqinghua
 * @date 2016年8月19日  下午4:27:29
 *
 */
public class SendEmailTest {

	
    public static void main(String[] args) {
     
		for(int i = 0 ; i < 10 ; i++){
		    Map<String, String> sendMap = Maps.newHashMap();
	        sendMap.put("title", "Email"+"第gmail:"+i);
	        sendMap.put("content", "hello world!");
		    new EmailEngine().addMailPool("zwlzwl376@gmail.com", sendMap , EmailFormEnum.TEACHVIP);
            Map<String, String> sendMap2 = Maps.newHashMap();
            sendMap2.put("title", "Email"+"163第"+i);
            sendMap2.put("content", "hello world!");
		    new EmailEngine().addMailPool("zwlzwl376@126.com", sendMap2, EmailFormEnum.TEACHVIP);
		}
		
	}
	
	//@Test
	public void dateTest(){
		//Date date = UADateUtils.getToday(20);
		Calendar calendar = Calendar.getInstance();
		//calendar.add(Calendar.DATE, 0);
		 int hour = calendar.get(Calendar.HOUR_OF_DAY)-8;
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		
		String dateStr = UADateUtils.format(calendar.getTime(), null);
		System.out.println(dateStr);
	}
	
	//@Test
	public void findOnlineClass(){
		
		//unitAsusesssmentService.remindTeacherUnitAssessmentFor6Hour();
	}
	

}
