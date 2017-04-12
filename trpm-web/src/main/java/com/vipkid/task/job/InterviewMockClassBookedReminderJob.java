package com.vipkid.task.job;

import com.alibaba.fastjson.TypeReference;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.handle.EmailEntity;
import com.vipkid.email.template.TemplateUtils;
import com.vipkid.enums.OnlineClassEnum.ClassStatus;
import com.vipkid.recruitment.dao.TeacherReminderDao;
import com.vipkid.recruitment.entity.TeacherReminder;
import com.vipkid.teacher.tools.utils.conversion.JsonUtils;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.util.DateUtils;
import com.vipkid.vschedule.client.common.Vschedule;
import com.vipkid.vschedule.client.schedule.JobContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuguowen on 2017/4/11.
 */
@Component
@Vschedule
public class InterviewMockClassBookedReminderJob {

    private static final Logger logger = LoggerFactory.getLogger(InterviewMockClassBookedReminderJob.class);

    @Autowired
    private TeacherReminderDao teacherReminderDao;

    @Autowired
    private OnlineClassDao onlineClassDao;

    @Autowired
    private TeacherDao teacherDao;

    @Vschedule
    // @Scheduled(cron = "0 15 * * * ?")
    public void doJob(JobContext jobContext) {
        LocalDateTime now =
                        LocalDateTime.now().with(ChronoField.SECOND_OF_MINUTE, 0).with(ChronoField.MILLI_OF_SECOND, 0);
        logger.info("Interview and MockClass reminder time: {}", now);

        Date sendScheduledTime = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        logger.info("Interview and MockClass reminder sendScheduledTime: {}", sendScheduledTime);

        if (null != jobContext.getData()) {
            sendScheduledTime = DateUtils.parseDate(jobContext.getData(), null);
        }

        List<TeacherReminder> teacherReminderList = teacherReminderDao.findBySendScheduledTime(sendScheduledTime);

        if (CollectionUtils.isNotEmpty(teacherReminderList)) {
            for (TeacherReminder teacherReminder : teacherReminderList) {
                OnlineClass onlineClass = onlineClassDao.findById(teacherReminder.getOnlineClassId());

                if (null != onlineClass && ClassStatus.isBooked(onlineClass.getStatus())) {
                    Teacher teacher = teacherDao.findById(teacherReminder.getTeacherId());

                    if (null != teacher) {
                        sendReminder(teacher, teacherReminder);
                    }
                } else {
                    teacherReminderDao.deleteTeacherReminder(teacherReminder.getId());
                }
            }
        } else {
            logger.info("Not any reminder datas...");
        }
    }

    public void sendReminder(Teacher teacher, TeacherReminder teacherReminder) {
        try {
            Map<String, String> paramsMap = JsonUtils.json2Object(teacherReminder.getParams(),
                            new TypeReference<HashMap<String, String>>() {});
            Map<String, String> emailMap = TemplateUtils.readTemplate(teacherReminder.getMailTemplateContent(),
                            paramsMap, teacherReminder.getMailTemplateTitle());

            EmailEntity reviceEntity = new EmailEntity().setToMail(teacher.getEmail())
                            .setMailSubject(emailMap.get("title")).setMailBody(emailMap.get("content"));
            EmailEngine.addMailPool(reviceEntity, EmailConfig.EmailFormEnum.TEACHVIP);

            teacherReminderDao.deleteTeacherReminder(teacherReminder.getId());

            logger.info("【EMAIL.InterviewMockClassBookedReminderJob】toAdd MailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",
                            teacher.getRealName(), teacher.getEmail(), teacherReminder.getMailTemplateTitle(),
                            teacherReminder.getMailTemplateContent());
        } catch (Exception e) {
            logger.error("【EMAIL.InterviewMockClassBookedReminderJob】ERROR: {}", e);
        }
    }

}
