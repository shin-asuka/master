/**
 * 
 */
package com.vipkid.email;

import static com.vipkid.trpm.constant.ApplicationConstant.NEW_TEACHER_NAME;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.Maps;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
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
			Map<String, String> emailMap = TempleteUtils.readTemplete(contentTemplate, paramsMap, titleTemplate);
			EmailEngine.addMailPool(email, emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
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
			Map<String, String> emailMap = TempleteUtils.readTemplete("BasicInfoPass.html", paramsMap, "BasicInfoPassTitle.html");
			EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("【EMAIL.sendEmail4BasicInfoPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
		} catch (Exception e) {
			logger.error("【EMAIL.sendEmail4BasicInfoPass】ERROR: {}", e);
		}
	}

	public static void sendEmail4InterviewBook(Teacher teacher, OnlineClass onlineclass){
		try {
			Map<String,String> paramsMap = new HashMap<String,String>();
			paramsMap.put("teacherName",teacher.getRealName());
			paramsMap.put("scheduledDateTime", DateUtils.formatTo(onlineclass.getScheduledDateTime().toInstant(), teacher.getTimezone(), DateUtils.FMT_YMD_HM));
			paramsMap.put("timezone", teacher.getTimezone());
			logger.info("【EMAIL.sendEmail4InterviewBook】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"InterviewBookTitle.html","InterviewBook.html");
			Map<String, String> emailMap = TempleteUtils.readTemplete("InterviewBook.html", paramsMap, "InterviewBookTitle.html");
			EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("【EMAIL.sendEmail4InterviewBook】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"InterviewBookTitle.html","InterviewBook.html");
		} catch (Exception e) {
			logger.error("【EMAIL.sendEmail4InterviewBook】ERROR: {}", e);
		}
	}

//Training Quiz PASS send Email +quizScore
	public static void sendEmail4TrainingPass(Teacher teacher, int quizScore) {
		try {
			Map<String, String> paramsMap = Maps.newHashMap();
			if (teacher.getRealName() != null)
				paramsMap.put("teacherName", teacher.getRealName());
			    paramsMap.put("quizScore",quizScore+"");
			logger.info("【EMAIL.sendEmail4TrainingPass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
			Map<String, String> emailMap = TempleteUtils.readTemplete("InterviewBook.html", paramsMap, "InterviewBookTitle.html");
			EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("【EMAIL.sendEmail4TrainingPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
		} catch (Exception e) {
			logger.error("【EMAIL.sendEmail4TrainingPass】ERROR: {}", e);
		}
	}


	/**
	 * 用户激活邮件
	 * 2016年11月29日 下午3:48:20
	 * @param teacher
	 * void
	 */
	public static void sendActivationEmail(Teacher teacher){
	    try {
            Map<String, String> paramsMap = Maps.newHashMap();
            paramsMap.put("teacherName", NEW_TEACHER_NAME);
            paramsMap.put("link", PropertyConfigurer.stringValue("teacher.www") + "/api/user/activation?uuid="+ teacher.getRecruitmentId());
            Map<String, String> sendMap = TempleteUtils.readTemplete("VIPKIDAccountActivationLink.html", paramsMap,"VIPKIDAccountActivationLink-Title.html");
            EmailEngine.addMailPool(teacher.getEmail(), sendMap, EmailFormEnum.TEACHVIP);
            logger.error("【EMAIL.sendActivationEmail】OK: {}", teacher.getEmail());
        } catch (Exception e) {
            logger.error("【EMAIL.sendActivationEmail】ERROR: {}", e);
        }
	}

	/**
	 * 重置密码邮件
	 * 2016年11月29日 下午3:48:10
	 * @param teacher
	 * void
	 */
	public static void sendRestPasswordEmail(Teacher teacher){
	    try {
            Map<String, String> map = Maps.newHashMap();
            if (StringUtils.isEmpty(teacher.getRealName())) {
                map.put("teacherName", NEW_TEACHER_NAME);
            } else {
                map.put("teacherName", teacher.getRealName());
            }
            map.put("link", PropertyConfigurer.stringValue("teacher.www") + "modifyPassword.shtml?validate_token="+ teacher.getRecruitmentId());
            Map<String, String> sendMap = TempleteUtils.readTemplete("VIPKIDPasswordResetLink.html", map,"VIPKIDPasswordResetLink-Title.html");
            EmailEngine.addMailPool(teacher.getEmail(), sendMap, EmailFormEnum.TEACHVIP);
            logger.error("【EMAIL.sendActivationEmail】OK: {}", teacher.getEmail());
        } catch (Exception e) {
            logger.error("【EMAIL.sendActivationEmail】ERROR: {}", e);
        }
	}
}
