/**
 * 
 */
package com.vipkid.email;

import static com.vipkid.trpm.constant.ApplicationConstant.NEW_TEACHER_NAME;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.Maps;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.email.template.TemplateUtils;
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

	public static void sendEmail4Recruitment(Teacher teacher, String titleTemplate, String contentTemplate) {
		try {
			Map<String, String> paramsMap = Maps.newHashMap();
			if (teacher.getFirstName() != null){
				paramsMap.put("teacherName", teacher.getFirstName());
			}else if (teacher.getRealName() != null){
				paramsMap.put("teacherName", teacher.getRealName());
			}
			logger.info("【EMAIL.sendEmail4Recruitment】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),titleTemplate,contentTemplate);
			Map<String, String> emailMap = TemplateUtils.readTemplate(contentTemplate, paramsMap, titleTemplate);
			EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("【EMAIL.sendEmail4Recruitment】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),titleTemplate,contentTemplate);
		} catch (Exception e) {
			logger.error("【EMAIL.sendEmail4Recruitment】ERROR: {}", e);
		}
	}

	public static void sendEmail4BasicInfoPass(Teacher teacher) {
		try {
			Map<String, String> paramsMap = Maps.newHashMap();
			if (teacher.getFirstName() != null){
				paramsMap.put("teacherName", teacher.getFirstName());
			}else if (teacher.getRealName() != null){
				paramsMap.put("teacherName", teacher.getRealName());
			}
			logger.info("【EMAIL.sendEmail4BasicInfoPass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
			Map<String, String> emailMap = TemplateUtils.readTemplate("BasicInfoPass.html", paramsMap, "BasicInfoPassTitle.html");
			EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("【EMAIL.sendEmail4BasicInfoPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"BasicInfoPassTitle.html","BasicInfoPass.html");
		} catch (Exception e) {
			logger.error("【EMAIL.sendEmail4BasicInfoPass】ERROR: {}", e);
		}
	}

	public static void sendEmail4InterviewBook(Teacher teacher, OnlineClass onlineclass){
		try {
			Map<String,String> paramsMap = new HashMap<String,String>();
			if (teacher.getFirstName() != null){
				paramsMap.put("teacherName", teacher.getFirstName());
			}else if (teacher.getRealName() != null){
				paramsMap.put("teacherName", teacher.getRealName());
			}
			paramsMap.put("scheduledDateTime", DateUtils.formatTo(onlineclass.getScheduledDateTime().toInstant(), teacher.getTimezone(), DateUtils.FMT_YMD_HM));
			paramsMap.put("timezone", teacher.getTimezone());
			logger.info("【EMAIL.sendEmail4InterviewBook】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"InterviewBookTitle.html","InterviewBook.html");
			Map<String, String> emailMap = TemplateUtils.readTemplate("InterviewBook.html", paramsMap, "InterviewBookTitle.html");
			EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("【EMAIL.sendEmail4InterviewBook】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"InterviewBookTitle.html","InterviewBook.html");
		} catch (Exception e) {
			logger.error("【EMAIL.sendEmail4InterviewBook】ERROR: {}", e);
		}
	}


	public static void sendEmail4PracticumBook(Teacher teacher, OnlineClass onlineclass){
		try {
			Map<String,String> paramsMap = new HashMap<String,String>();
			if (teacher.getFirstName() != null){
				paramsMap.put("teacherName", teacher.getFirstName());
			}else if (teacher.getRealName() != null){
				paramsMap.put("teacherName", teacher.getRealName());
			}
			paramsMap.put("scheduledDateTime", DateUtils.formatTo(onlineclass.getScheduledDateTime().toInstant(), teacher.getTimezone(), DateUtils.FMT_YMD_HM));
			paramsMap.put("timezone", teacher.getTimezone());
			logger.info("【EMAIL.sendEmail4PracticumBook】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"PracticumBookTitle.html","PracticumBook.html");
			Map<String, String> emailMap = TemplateUtils.readTemplate("PracticumBook.html", paramsMap, "PracticumBookTitle.html");
			EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("【EMAIL.sendEmail4PracticumBook】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"PracticumBookTitle.html","PracticumBook.html");
		} catch (Exception e) {
			logger.error("【EMAIL.sendEmail4InterviewBook】ERROR: {}", e);
		}
	}

	public static void sendEmail4Practicum2Book(Teacher teacher, OnlineClass onlineclass){
		try {
			Map<String,String> paramsMap = new HashMap<String,String>();
			if (teacher.getFirstName() != null){
				paramsMap.put("teacherName", teacher.getFirstName());
			}else if (teacher.getRealName() != null){
				paramsMap.put("teacherName", teacher.getRealName());
			}
			paramsMap.put("scheduledDateTime", DateUtils.formatTo(onlineclass.getScheduledDateTime().toInstant(), teacher.getTimezone(), DateUtils.FMT_YMD_HM));
			paramsMap.put("timezone", teacher.getTimezone());
			logger.info("【EMAIL.sendEmail4Practicum2Book】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"Practicum2BookTitle.html","Practicum2Book.html");
			Map<String, String> emailMap = TemplateUtils.readTemplate("Practicum2Book.html", paramsMap, "Practicum2BookTitle.html");
			EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("【EMAIL.sendEmail4Practicum2Book】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"Practicum2BookTitle.html","Practicum2Book.html");
		} catch (Exception e) {
			logger.error("【EMAIL.sendEmail4Practicum2Book】ERROR: {}", e);
		}
	}



//Training Quiz PASS send Email +quizScore
	public static void sendEmail4TrainingPass(Teacher teacher, int quizScore) {
		try {
			Map<String, String> paramsMap = Maps.newHashMap();
			if (teacher.getFirstName() != null){
				paramsMap.put("teacherName", teacher.getFirstName());
			}else if (teacher.getRealName() != null){
				paramsMap.put("teacherName", teacher.getRealName());
			}
			paramsMap.put("quizScore",quizScore+"");
			logger.info("【EMAIL.sendEmail4TrainingPass】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"TrainingPassTitle.html","TrainingPass.html");
			Map<String, String> emailMap = TemplateUtils.readTemplate("TrainingPass.html", paramsMap, "TrainingPassTitle.html");
			EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
			logger.info("【EMAIL.sendEmail4TrainingPass】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"TrainingPassTitle.html","TrainingPass.html");
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
            Map<String, String> sendMap = TemplateUtils.readTemplate("VIPKIDAccountActivationLink.html", paramsMap, "VIPKIDAccountActivationLink-Title.html");
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
			if (teacher.getFirstName() != null){
				map.put("teacherName", teacher.getFirstName());
			}else if (teacher.getRealName() != null){
				map.put("teacherName", teacher.getRealName());
			} else {
				map.put("teacherName", NEW_TEACHER_NAME);
			}
            map.put("link", PropertyConfigurer.stringValue("teacher.www") + "modifyPassword.shtml?validate_token="+ teacher.getRecruitmentId());
            Map<String, String> sendMap = TemplateUtils.readTemplate("VIPKIDPasswordResetLink.html", map, "VIPKIDPasswordResetLink-Title.html");
            EmailEngine.addMailPool(teacher.getEmail(), sendMap, EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendActivationEmail】OK: {}", teacher.getEmail());
        } catch (Exception e) {
            logger.error("【EMAIL.sendActivationEmail】ERROR: {}", e);
        }
	}
}
