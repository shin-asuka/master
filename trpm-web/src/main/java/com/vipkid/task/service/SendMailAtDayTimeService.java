package com.vipkid.task.service;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.vipkid.recruitment.dao.TeacherReminderDao;
import com.vipkid.recruitment.entity.TeacherReminder;
import com.vipkid.teacher.tools.utils.conversion.JsonUtils;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class SendMailAtDayTimeService {

    private static final Logger logger = LoggerFactory.getLogger(SendMailAtDayTimeService.class);

    private static final int HOURS_48 = 48;
    private static final int HOURS_24 = 24;
    private static final int HOURS_2 = 2;

    private static final int HOURS_15_OF_DAY = 15;

    @Autowired
    private TeacherReminderDao teacherReminderDao;

    /**
     * 保存发送 Interview 已 BOOKED 的课程所有提醒
     * 
     * @param teacher 老师
     * @param scheduledDateTime 课程开始时间，北京时间
     */
    public void saveAllInterviewBookedReminder(Teacher teacher, Timestamp scheduledDateTime, long onlineClassId) {
        List<Reminder> remiderList = getReminders(teacher, scheduledDateTime);
        if (CollectionUtils.isNotEmpty(remiderList)) {
            List<TeacherReminder> teacherReminderList = Lists.newArrayList();

            for (Reminder reminder : remiderList) {
                TeacherReminder teacherReminder = new TeacherReminder();
                teacherReminder.setOnlineClassId(onlineClassId);
                teacherReminder.setSendScheduledTime(reminder.getScheduledTime());
                teacherReminder.setTeacherId(teacher.getId());

                Map<String, String> paramsMap = getSendParams(teacher, scheduledDateTime);
                teacherReminder.setParams(JsonUtils.toJson(paramsMap));

                if (reminder.is2Hours()) {
                    teacherReminder.setMailTemplateTitle("InterviewMockClass2HoursReminderTitle.html");
                    teacherReminder.setMailTemplateContent("InterviewMockClass2HoursReminder.html");
                } else {
                    teacherReminder.setMailTemplateTitle("InterviewReminderTitle.html");
                    teacherReminder.setMailTemplateContent("InterviewReminder.html");
                }
                teacherReminderList.add(teacherReminder);
            }

            teacherReminderDao.saveTeacherReminders(teacherReminderList);
        } else {
            logger.warn("Not any reminder need send for Interview...");
        }
    }

    /**
     * 保存发送 MockClass 已 BOOKED 的课程所有提醒
     *
     * @param teacher 老师
     * @param scheduledDateTime 课程开始时间，北京时间
     */
    public void saveAllMockClassBookedReminder(Teacher teacher, Timestamp scheduledDateTime, long onlineClassId) {
        List<Reminder> remiderList = getReminders(teacher, scheduledDateTime);
        if (CollectionUtils.isNotEmpty(remiderList)) {
            List<TeacherReminder> teacherReminderList = Lists.newArrayList();

            for (Reminder reminder : remiderList) {
                TeacherReminder teacherReminder = new TeacherReminder();
                teacherReminder.setOnlineClassId(onlineClassId);
                teacherReminder.setSendScheduledTime(reminder.getScheduledTime());
                teacherReminder.setTeacherId(teacher.getId());

                Map<String, String> paramsMap = getSendParams(teacher, scheduledDateTime);
                teacherReminder.setParams(JsonUtils.toJson(paramsMap));

                if (reminder.is2Hours()) {
                    teacherReminder.setMailTemplateTitle("InterviewMockClass2HoursReminderTitle.html");
                    teacherReminder.setMailTemplateContent("InterviewMockClass2HoursReminder.html");
                } else {
                    teacherReminder.setMailTemplateTitle("PracticumReminderJobTitle.html");
                    teacherReminder.setMailTemplateContent("PracticumReminderJob.html");
                }
                teacherReminderList.add(teacherReminder);
            }

            teacherReminderDao.saveTeacherReminders(teacherReminderList);
        } else {
            logger.warn("Not any reminder need send for MockClass...");
        }
    }

    /**
     * 获取老师的昵称
     * 
     * @param teacher
     * @return
     */
    public String getTeacherName(Teacher teacher) {
        if (null != teacher.getFirstName()) {
            return teacher.getFirstName();
        } else {
            return teacher.getRealName();
        }
    }

    /**
     * 获取以老师时区格式化后的课程开始时间
     * 
     * @param scheduledDateTime 课程开始时间，北京时间
     * @param timezone 老师所在时区
     * @return
     */
    public String getZoneScheduledDateTime(Timestamp scheduledDateTime, String timezone) {
        return DateUtils.formatTo(scheduledDateTime.toInstant(), timezone, DateUtils.FMT_YMD_HM);
    }

    /**
     * 设置邮件内容参数
     * 
     * @param teacher
     * @param scheduledDateTime
     * @return
     */
    public Map<String, String> getSendParams(Teacher teacher, Timestamp scheduledDateTime) {
        Map<String, String> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherName", getTeacherName(teacher));
        paramsMap.put("scheduledDateTime", getZoneScheduledDateTime(scheduledDateTime, teacher.getTimezone()));
        paramsMap.put("timezone", teacher.getTimezone());
        return paramsMap;
    }

    /**
     * 获取定时发送的邮件提醒对象列表
     * 
     * @param teacher 老师
     * @param scheduledDateTime 课程开始时间，北京时间
     */
    public List<Reminder> getReminders(Teacher teacher, Timestamp scheduledDateTime) {
        List<Reminder> remiderList = Lists.newArrayList();

        // 获取当前时间的不同时区的日期对象
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
                        .withZoneSameInstant(ZoneId.of(teacher.getTimezone()));

        logger.info("Now datetime, local: {}, zoned: {}", localDateTime, zonedDateTime);

        // 获取课程开始时间的不同时区的日期对象
        LocalDateTime localScheduledDateTime =
                        LocalDateTime.ofInstant(scheduledDateTime.toInstant(), ZoneId.systemDefault());
        ZonedDateTime zonedScheduledDateTime = localScheduledDateTime.atZone(ZoneId.systemDefault())
                        .withZoneSameInstant(ZoneId.of(teacher.getTimezone()));

        logger.info("Scheduled datetime, local: {}, zoned: {}", localScheduledDateTime, zonedScheduledDateTime);

        // 48 hours
        ZonedDateTime zoned48Hours = zonedScheduledDateTime.minus(HOURS_48, ChronoUnit.HOURS)
                        .with(ChronoField.HOUR_OF_DAY, HOURS_15_OF_DAY).with(ChronoField.MINUTE_OF_HOUR, 0)
                        .with(ChronoField.SECOND_OF_MINUTE, 0);

        logger.info("Zoned 48Hours: {}", zoned48Hours);

        if (zonedDateTime.isBefore(zoned48Hours)) {
            // 发送 48 小时提醒
            ZonedDateTime local48Hours = zoned48Hours.withZoneSameInstant(ZoneId.systemDefault());
            logger.info("Send 48 hours reminder... at BeiJing time: {}", local48Hours);

            remiderList.add(new Reminder(Date.from(local48Hours.toInstant()), false));
        }

        // 24 hours
        ZonedDateTime zoned24Hours = zonedScheduledDateTime.minus(HOURS_24, ChronoUnit.HOURS)
                        .with(ChronoField.HOUR_OF_DAY, HOURS_15_OF_DAY).with(ChronoField.MINUTE_OF_HOUR, 0)
                        .with(ChronoField.SECOND_OF_MINUTE, 0);
        logger.info("Zoned 24Hours: {}", zoned24Hours);

        if (zonedDateTime.isBefore(zoned24Hours)) {
            // 发送 24 小时提醒
            ZonedDateTime local24Hours = zoned24Hours.withZoneSameInstant(ZoneId.systemDefault());
            logger.info("Send 24 hours reminder... at BeiJing time: {}", local24Hours);

            remiderList.add(new Reminder(Date.from(local24Hours.toInstant()), false));
        } else {
            // 当天下午 3 点发送
            ZonedDateTime zoned3PM = zonedScheduledDateTime.with(ChronoField.HOUR_OF_DAY, HOURS_15_OF_DAY)
                            .with(ChronoField.MINUTE_OF_HOUR, 0).with(ChronoField.SECOND_OF_MINUTE, 0);
            logger.info("Zoned 3:00 PM: {}", zoned3PM);

            if (zonedDateTime.isBefore(zoned3PM) && zonedScheduledDateTime.isAfter(zoned3PM)) {
                ZonedDateTime local3PM = zoned3PM.withZoneSameInstant(ZoneId.systemDefault());
                logger.info("Send 3:00 PM reminder... at BeiJing time: {}", local3PM);

                remiderList.add(new Reminder(Date.from(local3PM.toInstant()), false));
            }
        }

        // 课前 2 小时提醒
        ZonedDateTime zoned2Hours = zonedScheduledDateTime.minus(HOURS_2, ChronoUnit.HOURS);
        logger.info("Zoned 2 hours: {}", zoned2Hours);

        if (zonedDateTime.isBefore(zoned2Hours)) {
            // 发送 2 小时提醒
            ZonedDateTime local2Hours = zoned2Hours.withZoneSameInstant(ZoneId.systemDefault());
            logger.info("Send 2 hours reminder... at BeiJing time: {}", local2Hours);

            remiderList.add(new Reminder(Date.from(local2Hours.toInstant()), true));
        }

        return remiderList;
    }

    private class Reminder {

        // 定时发送的时间，北京时间
        private Date scheduledTime;

        private boolean is2Hours;

        public Reminder(Date scheduledTime, boolean is2Hours) {
            this.scheduledTime = scheduledTime;
            this.is2Hours = is2Hours;
        }

        public Date getScheduledTime() {
            return scheduledTime;
        }

        public void setScheduledTime(Date scheduledTime) {
            this.scheduledTime = scheduledTime;
        }

        public boolean is2Hours() {
            return is2Hours;
        }

        public void setIs2Hours(boolean is2Hours) {
            this.is2Hours = is2Hours;
        }

    }

}
