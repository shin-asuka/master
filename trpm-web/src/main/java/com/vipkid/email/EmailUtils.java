/**
 * 
 */
package com.vipkid.email;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.Maps;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.util.DateUtils;

/**
 * @author xingxuelin
 * @date 2016年10月21日  下午6:18:18
 *
 */
public class EmailUtils {

	private static final Logger logger = LoggerFactory.getLogger(EmailUtils.class);

	public static void sendEmail4Recruitment(String email, String name, String titleTemplate, String contentTemplate) {
		try {
			Map<String, String> paramsMap = Maps.newHashMap();
			if (name != null)
			paramsMap.put("teacherName", name);
			logger.info("【EMAIL.sendEmail4Recruitment】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",name,email,titleTemplate,contentTemplate);
			Map<String, String> emailMap = new TempleteUtils().readTemplete(contentTemplate, paramsMap, titleTemplate);
			new EmailEngine().addMailPool(email, emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("【EMAIL.sendEmail4Recruitment】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",name,email,titleTemplate,contentTemplate);
		} catch (Exception e) {
			logger.error("【EMAIL.sendEmail4Recruitment】ERROR: {}", e);
		}
	}

	public static void sendEmail4BasicInfoPass(Teacher teacher) {
		try {
			Map<String, String> paramsMap = Maps.newHashMap();
			if (teacher.getRealName() != null)
			paramsMap.put("teacherName", teacher.getRealName());
			logger.info("【EMAIL.sendEmail4BasicInfoPass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
			Map<String, String> emailMap = new TempleteUtils().readTemplete("BasicInfoPass.html", paramsMap, "BasicInfoPassTitle.html");
			new EmailEngine().addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("【EMAIL.sendEmail4BasicInfoPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
		} catch (Exception e) {
			logger.error("【EMAIL.sendEmail4BasicInfoPass】ERROR: {}", e);
		}
	}

	public void sendEmial4InterviewBook(Teacher teacher, OnlineClass onlineclass){
		try {
			Map<String,String> paramsMap = new HashMap<String,String>();
			paramsMap.put("teacherName",teacher.getRealName());
			paramsMap.put("scheduledDateTime", DateUtils.formatTo(onlineclass.getScheduledDateTime().toInstant(), teacher.getTimezone(), DateUtils.FMT_YMD_HM));
			paramsMap.put("timezone", teacher.getTimezone());
			logger.info("【EMAIL.sendEmial4InterviewBook】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"InterviewBookTitle.html","InterviewBook.html");
			Map<String, String> emailMap = new TempleteUtils().readTemplete("InterviewBook.html", paramsMap, "InterviewBookTitle.html");
			new EmailEngine().addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("【EMAIL.sendEmial4InterviewBook】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"InterviewBookTitle.html","InterviewBook.html");
		} catch (Exception e) {
			logger.error("【EMAIL.sendEmial4InterviewBook】ERROR: {}", e);
		}
	}

}
