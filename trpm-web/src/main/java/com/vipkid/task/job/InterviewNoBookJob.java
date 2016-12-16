package com.vipkid.task.job;

import com.google.common.base.Stopwatch;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherLockLogEnum;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeacherLockLogDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeacherLockLog;
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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xingxuelin
 * @date 2016年10月21日  下午5:24:23
 *
 */
@Component
@Vschedule
public class InterviewNoBookJob {

	private static final Logger logger = LoggerFactory.getLogger(InterviewNoBookJob.class);

	@Autowired
	private TeacherApplicationDao teacherApplicationDao;
	@Autowired
	private TeacherDao teacherDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private TeacherLockLogDao teacherLockLogDao;
	@Vschedule
	public void doJob (JobContext jobContext) {
		logger.info("【JOB.EMAIL.InterviewNoBook】START: ==================================================");
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			find(stopwatch, 24, 48, 72);
		} catch (Exception e) {
			logger.error("【JOB.EMAIL.InterviewNoBook】EXCEPTION: Cost {}ms. ", stopwatch.elapsed(TimeUnit.MILLISECONDS), e);
		}
		
		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
		logger.info("【JOB.EMAIL.InterviewNoBook】END: Cost {}ms. ==================================================", millis);
	}

	void find (Stopwatch stopwatch, int... beforeHours) {
		List<Long> teacherIds = new ArrayList<>();
		Map<Long, TeacherApplication> teacherApplicationsMap = new HashedMap();
		List<Map> times = UADateUtils.getStartEndOclockTimeMapListByBeforeHours(beforeHours);

		List<TeacherApplication> teacherApplications = teacherApplicationDao.findByAuditTimesStatusResult(times, TeacherApplicationEnum.Status.BASIC_INFO.toString(), TeacherApplicationEnum.Result.PASS.toString());
		logger.info("【JOB.EMAIL.InterviewNoBook】FIND.1: Cost {}ms. Query: times = {}, status = {}, result = {}; Result: users = ",
				stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(times), TeacherApplicationEnum.Status.BASIC_INFO.toString(), TeacherApplicationEnum.Result.PASS.toString());
		for(TeacherApplication ta : teacherApplications){
			teacherIds.add(ta.getTeacherId());
			teacherApplicationsMap.put(ta.getTeacherId(), ta);
		}

		if(teacherIds.size() == 0) return;
		List<TeacherApplication> teacherApplicationsToRemove = teacherApplicationDao.findByTeacherIdsStatusNeResult(teacherIds, TeacherApplicationEnum.Status.INTERVIEW.toString(), null);
		logger.info("【JOB.EMAIL.InterviewNoBook】FIND.2: Cost {}ms. Query: teacherIds = {}, status = {}, result = {}; Result: teacherApplications = ",
				stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(teacherIds), TeacherApplicationEnum.Status.INTERVIEW.toString(), "null");
		teacherApplicationsToRemove.forEach(x -> teacherIds.remove(x.getTeacherId()));

		if(teacherIds.size() == 0) return;
		List<Teacher> teachers = teacherDao.findByIds(teacherIds);
		logger.info("【JOB.EMAIL.InterviewNoBook】FIND.3: Cost {}ms. Query: teacherIds = {}; Result: teachers = ",
				stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(teacherIds));
		teachers.forEach(x -> send(stopwatch, x, teacherApplicationsMap.get(x.getId()).getAuditDateTime(), times));

	}

	void send (Stopwatch stopwatch, Teacher teacher, Date auditTime, List<Map> times) {
		Map <String, String> time = times.get(times.size()-1);
		Date startTime = UADateUtils.parse(time.get("startTime"));
		Date endTime = UADateUtils.parse(time.get("endTime"));

		if (auditTime.after(startTime) && auditTime.before(endTime)){
			userDao.doLock(teacher.getId());
            teacherLockLogDao.save(new TeacherLockLog(teacher.getId(), TeacherLockLogEnum.Reason.INTERVIEW_NO_BOOK.toString(), TeacherEnum.LifeCycle.INTERVIEW.toString()));
            logger.info("【JOB.EMAIL.InterviewNoBook】LOCK: Cost {}ms. teacherId = {}, teacherEmail = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), teacher.getId(), teacher.getEmail());
        } else {
			String email = teacher.getEmail();
			String name = teacher.getRealName();
			String titleTemplate = "InterviewNoBookTitle.html";
			String contentTemplate = "InterviewNoBook.html";
			EmailUtils.sendEmail4Recruitment(email, name, titleTemplate, contentTemplate);
			logger.info("【JOB.EMAIL.InterviewNoBook】SEND: Cost {}ms. email = {}, name = {}, titleTemplate = {}, contentTemplate = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), email, name, titleTemplate, contentTemplate);
		}
	}

}