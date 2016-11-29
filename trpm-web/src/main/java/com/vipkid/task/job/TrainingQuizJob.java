package com.vipkid.task.job;

import com.google.common.base.Stopwatch;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherQuizEnum;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.task.utils.UADateUtils;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherQuizDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherQuiz;
import com.vipkid.vschedule.client.common.Vschedule;
import com.vipkid.vschedule.client.schedule.JobContext;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangzhaojun on 2016/11/26.
 */
public class TrainingQuizJob {
    private static final Logger logger = LoggerFactory.getLogger(TrainingQuizJob.class);

    @Autowired
    private UserDao userDao;
    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private TeacherQuizDao teacherQuizDao;

    @Vschedule
    public void doJob (JobContext jobContext) {
        logger.info("【JOB.EMAIL.TrainingQuiz】START: ==================================================");
        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            find(stopwatch, 24, 48, 72);
        } catch (Exception e) {
            logger.error("【JOB.EMAIL.TrainingQuiz】EXCEPTION: Cost {}ms. ", stopwatch.elapsed(TimeUnit.MILLISECONDS), e);
        }

        long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("【JOB.EMAIL.TrainingQuiz】END: Cost {}ms. ==================================================", millis);
    }

    void find (Stopwatch stopwatch, int... beforeHours) {
        List<Long> teacherIds = new ArrayList<>();
        Map<Long, TeacherQuiz> teacherQuizsMap = new HashedMap();
        List<Map> times = UADateUtils.getStartEndOclockTimeMapListByBeforeHours(beforeHours);

        List<TeacherQuiz> teacherQuizs = teacherQuizDao.findTAByAuditTimesStatusResult(times, TeacherQuizEnum.Status.NOQUIZ.val());
        logger.info("【JOB.EMAIL.TrainingQuiz】FIND.1: Cost {}ms. Query: times = {}, status = {}, result = {}; Result: users = ",
                stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(times), TeacherApplicationEnum.Status.TRAINING.toString(), TeacherApplicationEnum.Result.FAIL.toString());
        for(TeacherQuiz ta : teacherQuizs){
            teacherIds.add(ta.getTeacherId());
            teacherQuizsMap.put(ta.getTeacherId(), ta);
        }



        if(teacherIds.size() == 0) return;
        List<Teacher> teachers = teacherDao.findByIds(teacherIds);
        logger.info("【JOB.EMAIL.TrainingQuiz】FIND.3: Cost {}ms. Query: teacherIds = {}; Result: teachers = ",
                stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(teacherIds));
        teachers.forEach(x -> send(stopwatch, x, teacherQuizsMap.get(x.getId()).getCreationTime(), times));

    }

    void send (Stopwatch stopwatch, Teacher teacher, Date auditTime, List<Map> times) {
        Map <String, String> time = times.get(times.size()-1);
        Date startTime = UADateUtils.parse(time.get("startTime"));
        Date endTime = UADateUtils.parse(time.get("endTime"));

        if (auditTime.after(startTime) && auditTime.before(endTime)){
            userDao.doLock(teacher.getId());
            logger.info("【JOB.EMAIL.TrainingQuiz】LOCK: Cost {}ms. teacherId = {}, teacherEmail = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), teacher.getId(), teacher.getEmail());
        } else {
            String email = teacher.getEmail();
            String name = teacher.getRealName();
            String titleTemplate = "InterviewNoBookTitle.html";
            String contentTemplate = "InterviewNoBook.html";
            EmailUtils.sendEmail4Recruitment(email, name, titleTemplate, contentTemplate);
            logger.info("【JOB.EMAIL.TrainingQuiz】SEND: Cost {}ms. email = {}, name = {}, titleTemplate = {}, contentTemplate = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), email, name, titleTemplate, contentTemplate);
        }
    }

}
