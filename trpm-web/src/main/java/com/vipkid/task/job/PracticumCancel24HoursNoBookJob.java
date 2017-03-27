package com.vipkid.task.job;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.common.base.Stopwatch;
import com.vipkid.dataSource.annotation.Slave;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeacherApplicationLogDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.task.utils.UADateUtils;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.vschedule.client.common.Vschedule;
import com.vipkid.vschedule.client.schedule.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * author: rentj
 * 取消模拟课并且24小时后没有约课的提醒邮件任务
 */
@Component
@Vschedule
public class PracticumCancel24HoursNoBookJob {
    private static final Logger logger = LoggerFactory.getLogger(PracticumCancel24HoursNoBookJob.class);


    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private TeacherApplicationLogDao teacherApplicationLogDao;


    @Vschedule
    public void doJob(JobContext jobContext) {
        logger.info("【JOB.EMAIL.PracticumCancel24HoursNoBookJob】START: ==================================================");
        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            find(stopwatch, 24);
        } catch (Exception e) {
            logger.error("【JOB.EMAIL.PracticumCancel24HoursNoBookJob】EXCEPTION: Cost {}ms. ", stopwatch.elapsed(TimeUnit.MILLISECONDS), e);
        }

        long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("【JOB.EMAIL.PracticumCancel24HoursNoBookJob】END: Cost {}ms. ==================================================", millis);
    }

    @Slave
    void find(Stopwatch stopwatch, int... beforeHours) {
        List<Long> teacherIds = Lists.newArrayList();

        Map<Long, TeacherApplication> teacherApplicationsMap = Maps.newHashMap();
        List<Map> times = UADateUtils.getStartEndOclockTimeMapListByBeforeHours(beforeHours);

        Map<String,Object> paramsMap=Maps.newHashMap();
        paramsMap.put("auditTimes", times);
        paramsMap.put("aStatus", TeacherApplicationEnum.Status.PRACTICUM.toString());
        paramsMap.put("lStatus", TeacherApplicationEnum.Status.PRACTICUM.toString());
        paramsMap.put("lResult", TeacherApplicationEnum.Result.CANCEL.toString());
        //查出所有满足条件的数据
        List<TeacherApplication> teacherApplications = teacherApplicationDao.findMockCancelNoBook(paramsMap);


        logger.info("【JOB.EMAIL.PracticumCancel24HoursNoBookJob】FIND.1: Cost {}ms. Query: times = {}, status = {}, result = {}; Result: users = ",
                stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(times), TeacherApplicationEnum.Status.PRACTICUM.toString(), TeacherApplicationEnum.Result.CANCEL.toString());
        //组装成MAP，同时过滤重复的数据
        for(TeacherApplication ta : teacherApplications){
            teacherIds.add(ta.getTeacherId());
            teacherApplicationsMap.put(ta.getTeacherId(), ta);
        }
        if(teacherIds.size() == 0) return;
        List<Teacher> teachers = teacherDao.findByIds(teacherIds);
        logger.info("【JOB.EMAIL.PracticumCancel24HoursNoBookJob】FIND.3: Cost {}ms. Query: teacherIds = {}; Result: teachers = ",
                stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(teacherIds));
        teachers.forEach(x -> send(stopwatch, x, teacherApplicationsMap.get(x.getId()).getAuditDateTime(), times));

    }

    void send(Stopwatch stopwatch, Teacher teacher, Date auditTime, List<Map> times) {
        Map<String, String> time = times.get(times.size() - 1);
        Date startTime = UADateUtils.parse(time.get("startTime"));
        Date endTime = UADateUtils.parse(time.get("endTime"));

        if (auditTime.after(startTime) && auditTime.before(endTime)) {
            logger.info("【JOB.EMAIL.PracticumCancel24HoursNoBookJob】LOCK: Cost {}ms. teacherId = {}, teacherEmail = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), teacher.getId(), teacher.getEmail());
            String email = teacher.getEmail();
            String name = teacher.getRealName();
            String titleTemplate = "PracticumReapplyTitle.html";
            String contentTemplate = "PracticumReapply.html";
            EmailUtils.sendEmail4Recruitment(teacher, titleTemplate, contentTemplate);
            logger.info("【JOB.EMAIL.PracticumCancel24HoursNoBookJob】SEND: Cost {}ms. email = {}, name = {}, titleTemplate = {}, contentTemplate = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), email, name, titleTemplate, contentTemplate);
        }
    }

}