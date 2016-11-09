/**
 * 
 */
package com.vipkid.email;

import com.google.api.client.util.Maps;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.trpm.entity.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author xingxuelin
 * @date 2016年10月21日  下午6:18:18
 *
 */
public class EmailUtils {

	private static final Logger logger = LoggerFactory.getLogger(EmailUtils.class);

	public static void sendEmail4Recruitment(String email, String name, String titleTemplate, String contentTemplate) {
		try {
			email="lilibo@vipkid.com.cn";
			Map<String, String> paramsMap = Maps.newHashMap();
			if (name != null)
			paramsMap.put("teacherName", name);
			logger.info("send Email to teacher name = {}, email = {}, contentTemplate = {}, titleTemplate = {}",name,email,contentTemplate,titleTemplate);
			Map<String, String> emailMap = new TempleteUtils().readTemplete(contentTemplate, paramsMap, titleTemplate);
			new EmailEngine().addMailPool(email, emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("send Email success! teacher name = {}, email = {}, contentTemplate = {}, titleTemplate = {}",name,email,contentTemplate,titleTemplate);
		} catch (Exception e) {
			logger.error("Send mail error: {}", e);
		}
	}

	public static void sendEmail4BasicInfoPass(Teacher teacher) {
		Map<String, String> paramsMap = Maps.newHashMap();
		if (teacher.getRealName() != null)
		paramsMap.put("teacherName", teacher.getRealName());
		Map<String, String> emailMap = new TempleteUtils().readTemplete("BasicInfoPass.html", paramsMap, "BasicInfoPassTitle.html");
		new EmailEngine().addMailPool("lilibo@vipkid.com.cn", emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
	}
}
