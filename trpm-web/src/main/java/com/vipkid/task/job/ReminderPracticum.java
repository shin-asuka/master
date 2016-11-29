package com.vipkid.task.job;

import com.google.common.base.Stopwatch;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeacherLockLogDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.task.utils.UADateUtils;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.vschedule.client.common.Vschedule;
import com.vipkid.vschedule.client.schedule.JobContext;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangzhaojun on 2016/11/28.
 */
public class ReminderPracticum {
    private static final Logger logger = LoggerFactory.getLogger(ReminderPracticum.class);


    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    @Autowired
    private TeacherDao teacherDao;

    @Vschedule
    public void doJob (JobContext jobContext) {
        logger.info("【JOB.EMAIL.ReminderPracticum】START: ==================================================");
        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            find(stopwatch, 24, 48);
        } catch (Exception e) {
            logger.error("【JOB.EMAIL.ReminderPracticum】EXCEPTION: Cost {}ms. ", stopwatch.elapsed(TimeUnit.MILLISECONDS), e);
        }

        long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("【JOB.EMAIL.ReminderPracticum】END: Cost {}ms. ==================================================", millis);
    }

    void find (Stopwatch stopwatch, int... beforeHours) {
        List<Long> teacherIds ;
        List<Map> times = UADateUtils.getStartEndOclockTimeMapListByAfterHours(beforeHours);
        teacherIds  = teacherApplicationDao.findPracticumBook(times,TeacherApplicationEnum.Status.PRACTICUM.toString());


        if(teacherIds.size() == 0) return;
        List<Teacher> teachers = teacherDao.findByIds(teacherIds);
        logger.info("【JOB.EMAIL.ReminderPracticum】FIND.3: Cost {}ms. Query: teacherIds = {}; Result: teachers = ",
                stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(teacherIds));
        teachers.forEach(x -> send(stopwatch, x));

    }

    void send (Stopwatch stopwatch, Teacher teacher) {
            String email = teacher.getEmail();
            String name = teacher.getRealName();
            String titleTemplate = "InterviewNoRescheduleTitle.html";
            String contentTemplate = "InterviewNoReschedule.html";
            EmailUtils.sendEmail4Recruitment(email, name, titleTemplate, contentTemplate);
            logger.info("【JOB.EMAIL.ReminderPracticum】SEND: Cost {}ms. email = {}, name = {}, titleTemplate = {}, contentTemplate = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), email, name, titleTemplate, contentTemplate);

    }



    public static Date getDateByOffset(int dayOffset, int minuteOffset){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayOffset);
        calendar.add(Calendar.MINUTE, minuteOffset);
        return calendar.getTime();
    }

}