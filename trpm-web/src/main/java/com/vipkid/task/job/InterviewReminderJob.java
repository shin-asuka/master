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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangzhaojun on 2016/12/1.
 */
@Component
@Vschedule
public class InterviewReminderJob {
    private static final Logger logger = LoggerFactory.getLogger(InterviewReminderJob.class);


    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private OnlineClassDao onlineClassDao;

    @Vschedule
    public void doJob (JobContext jobContext) {
        logger.info("【JOB.EMAIL.ReminderInterview】START: ==================================================");
        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            find(stopwatch, 24, 48);
        } catch (Exception e) {
            logger.error("【JOB.EMAIL.ReminderInterview】EXCEPTION: Cost {}ms. ", stopwatch.elapsed(TimeUnit.MILLISECONDS), e);
        }

        long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("【JOB.EMAIL.ReminderInterview】END: Cost {}ms. ==================================================", millis);
    }

    void find (Stopwatch stopwatch, int... beforeHours) {

        List<Map> times = UADateUtils.getStartEndOclockTimeMapListByAfterHours(beforeHours);
        List<Long>  teacherIds  = teacherApplicationDao.findPracticumBook(times, TeacherApplicationEnum.Status.INTERVIEW.toString());
        logger.info("【JOB.EMAIL.ReminderInterview】FIND.1: Cost {}ms. Query: times = {}, status = {}; Result: teacherIds = {}",
                stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(times), TeacherApplicationEnum.Status.INTERVIEW.toString(), JsonUtils.toJSONString(teacherIds));


        if(teacherIds.size() == 0) return;
        List<Teacher> teachers = teacherDao.findByIds(teacherIds);
        logger.info("【JOB.EMAIL.ReminderInterview】FIND.2: Cost {}ms. Query: teacherIds = {}; Result: teachers = ",
                stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(teacherIds));
        teachers.forEach(x -> send(stopwatch, x));

    }

    void send (Stopwatch stopwatch, Teacher teacher) {
        List<TeacherApplication> list = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if (CollectionUtils.isNotEmpty(list)) {
            //更新上一个版本的 application
            TeacherApplication teacherApplication = list.get(0);
            OnlineClass onlineClass = onlineClassDao.findById(teacherApplication.getOnlineClassId());
            sendEmail4InterviewReminderJob(teacher, onlineClass);
        }
    }

    public static void sendEmail4InterviewReminderJob(Teacher teacher, OnlineClass onlineclass){
        try {
            Map<String,String> paramsMap = new HashMap<>();
            paramsMap.put("teacherName",teacher.getRealName());
            paramsMap.put("scheduledDateTime", DateUtils.formatTo(onlineclass.getScheduledDateTime().toInstant(), teacher.getTimezone(), DateUtils.FMT_YMD_HM));
            paramsMap.put("timezone", teacher.getTimezone());
            logger.info("【EMAIL.sendEmail4InterviewReminderJob】toAddMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"InterviewReminderTitle.html","InterviewReminder.html");
            Map<String, String> emailMap = TemplateUtils.readTemplate("InterviewReminder.html", paramsMap, "InterviewReminderTitle.html");
            EmailEngine.addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
            logger.info("【EMAIL.sendEmail4InterviewReminderJob】addedMailPool: teacher name = {}, email = {}, titleTemplate = {}, contentTemplate = {}",teacher.getRealName(),teacher.getEmail(),"InterviewReminderTitle.html","InterviewReminder.html");
        } catch (Exception e) {
            logger.error("【EMAIL.sendEmail4InterviewReminderJob】ERROR: {}", e);
        }
    }
}
