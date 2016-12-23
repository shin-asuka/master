package com.vipkid.task.job;

import com.google.common.base.Stopwatch;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.EmailUtils;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.template.TemplateUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.task.utils.UADateUtils;
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

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangzhaojun on 2016/11/28.
 */
@Component
@Vschedule
public class PracticumReminderJob {
    private static final Logger logger = LoggerFactory.getLogger(PracticumReminderJob.class);


    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private OnlineClassDao onlineClassDao;

    @Vschedule
    public void doJob (JobContext jobContext) {
        logger.info("【JOB.EMAIL.PracticumReminderJob】START: ==================================================");
        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            find(stopwatch, 24, 48);
        } catch (Exception e) {
            logger.error("【JOB.EMAIL.PracticumReminderJob】EXCEPTION: Cost {}ms. ", stopwatch.elapsed(TimeUnit.MILLISECONDS), e);
        }

        long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("【JOB.EMAIL.PracticumReminderJob】END: Cost {}ms. ==================================================", millis);
    }

    void find (Stopwatch stopwatch, int... beforeHours) {
        List<Long> teacherIds = new ArrayList<>();
        Map<Long, Timestamp> teacherIdScheduledDateTimeMap = new HashMap<>();
        List<Map> times = UADateUtils.getStartEndOclockTimeMapListByAfterHours(beforeHours);

        List<Map<String, Object>> teacherIdScheduledDateTimeList = teacherApplicationDao.findRecruitmentBook(times,TeacherApplicationEnum.Status.PRACTICUM.toString());
        logger.info("【JOB.EMAIL.PracticumReminderJob】FIND.1: Cost {}ms. Query: times = {}, status = {}; Result: teacherIds = {}",
                stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(times), TeacherApplicationEnum.Status.PRACTICUM.toString(), JsonUtils.toJSONString(teacherIds));

        for(Map<String, Object> ts : teacherIdScheduledDateTimeList){
            Long teacherId = Long.parseLong(ts.get("teacherId").toString());
            Timestamp scheduledDateTime = (Timestamp)ts.get("scheduledDateTime");
            teacherIds.add(teacherId);
            teacherIdScheduledDateTimeMap.put(teacherId, scheduledDateTime);
        }

        if(teacherIds.size() == 0) return;
        List<Teacher> teachers = teacherDao.findByIds(teacherIds);
        logger.info("【JOB.EMAIL.PracticumReminderJob】FIND.2: Cost {}ms. Query: teacherIds = {}; Result: teachers = ",
                stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(teacherIds));
        teachers.forEach(x -> send(stopwatch, x, teacherIdScheduledDateTimeMap.get(x.getId())));

    }

    void send (Stopwatch stopwatch, Teacher teacher, Timestamp scheduledDateTime){
        try {
            Map<String,String> paramsMap = new HashMap<>();
            if (teacher.getFirstName() != null){
                paramsMap.put("teacherName", teacher.getFirstName());
            }else if (teacher.getRealName() != null){
                paramsMap.put("teacherName", teacher.getRealName());
            }
            paramsMap.put("scheduledDateTime", DateUtils.formatTo(scheduledDateTime.toInstant(), teacher.getTimezone(), DateUtils.FMT_YMD_HM));
            paramsMap.put("timezone", teacher.getTimezone());
            logger.info("【JOB.EMAIL.PracticumReminderJob】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"InterviewBookTitle.html","InterviewBook.html");
            Map<String, String> emailMap = TemplateUtils.readTemplate("PracticumReminderJob.html", paramsMap, "PracticumReminderJobTitle.html");
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【JOB.EMAIL.PracticumReminderJob】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"InterviewBookTitle.html","InterviewBook.html");
        } catch (Exception e) {
            logger.error("【JOB.EMAIL.PracticumReminderJob】ERROR: {}", e);
        }
    }

}