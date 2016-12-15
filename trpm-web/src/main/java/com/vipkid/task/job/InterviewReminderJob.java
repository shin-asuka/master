package com.vipkid.task.job;

import com.google.common.base.Stopwatch;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.task.utils.UADateUtils;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.vschedule.client.common.Vschedule;
import com.vipkid.vschedule.client.schedule.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public static void main(String[] args){
        JobContext jobContext = new JobContext();
        new InterviewReminderJob().doJob(jobContext);
    }
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
        String email = teacher.getEmail();
        String name = teacher.getRealName();
        String titleTemplate = "reminderApplicantBefore24HoursEmailSubjectTemplate.html";
        String contentTemplate = "reminderApplicantBefore24HoursEmailContentTemplate.html";
        EmailUtils.sendEmail4Recruitment(email, name, titleTemplate, contentTemplate);
        logger.info("【JOB.EMAIL.ReminderInterview】SEND: Cost {}ms. email = {}, name = {}, titleTemplate = {}, contentTemplate = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), email, name, titleTemplate, contentTemplate);

    }
}
