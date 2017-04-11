package com.vipkid.email.strategy;

import com.google.common.collect.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.handle.EmailEntity;
import com.vipkid.email.template.TemplateUtils;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Map;

@Component
public class SendMailAtDayTime {

    private static final Logger logger = LoggerFactory.getLogger(SendMailAtDayTime.class);

    public void sendInterviewBookedReminder(Teacher teacher, Timestamp scheduledDateTime) {
        try {
            logger.info("【EMAIL.InterviewReminderJob】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                            teacher.getRealName(), teacher.getEmail(), "InterviewReminderTitle.html",
                            "InterviewReminder.html");

            Map<String, String> paramsMap = getSendParams(teacher, scheduledDateTime);
            Map<String, String> emailMap = TemplateUtils.readTemplate("InterviewReminder.html", paramsMap,
                            "InterviewReminderTitle.html");

            EmailEntity reviceEntity = new EmailEntity().setToMail(teacher.getEmail())
                            .setMailSubject(emailMap.get("title")).setMailBody(emailMap.get("content"));

            Date sendScheduledTime = getSendScheduledTime(scheduledDateTime, teacher.getTimezone());
            reviceEntity.setScheduledTime(sendScheduledTime);

            EmailEngine.addMailPool(reviceEntity, EmailConfig.EmailFormEnum.TEACHVIP);

            logger.info("【EMAIL.InterviewReminderJob】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                            teacher.getRealName(), teacher.getEmail(), "InterviewReminderTitle.html",
                            "InterviewReminder.html");
        } catch (Exception e) {
            logger.error("【EMAIL.InterviewReminderJob】ERROR: {}", e);
        }
    }

    public String getTeacherName(Teacher teacher) {
        if (null != teacher.getFirstName()) {
            return teacher.getFirstName();
        } else {
            return teacher.getRealName();
        }
    }

    public String getZoneScheduledDateTime(Timestamp scheduledDateTime, String timezone) {
        return DateUtils.formatTo(scheduledDateTime.toInstant(), timezone, DateUtils.FMT_YMD_HM);
    }

    public Map<String, String> getSendParams(Teacher teacher, Timestamp scheduledDateTime) {
        Map<String, String> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherName", getTeacherName(teacher));
        paramsMap.put("scheduledDateTime", getZoneScheduledDateTime(scheduledDateTime, teacher.getTimezone()));
        paramsMap.put("timezone", teacher.getTimezone());
        return paramsMap;
    }

    public Date getSendScheduledTime(Timestamp scheduledDateTime, String timezone) {
        LocalDateTime now = LocalDateTime.now().with(ChronoField.HOUR_OF_DAY, 12).with(ChronoField.MINUTE_OF_HOUR, 30)
                        .with(ChronoField.SECOND_OF_MINUTE, 0);
        Date scheduledTime = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        return scheduledTime;
    }

}
