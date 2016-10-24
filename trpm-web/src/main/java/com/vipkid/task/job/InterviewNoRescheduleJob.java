package com.vipkid.task.job;

import com.google.common.base.Stopwatch;
import com.vipkid.email.EmailUtils;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.task.utils.UADateUtils;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.dao.TeacherApplicationDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherApplication;
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
public class InterviewNoRescheduleJob {

	private static final Logger logger = LoggerFactory.getLogger(InterviewNoRescheduleJob.class);

	@Autowired
	private UserDao userDao;
	@Autowired
	private TeacherApplicationDao teacherApplicationDao;
	@Autowired
	private TeacherDao teacherDao;

	@Vschedule
	public void doJob (JobContext jobContext) {
		logger.info("【JOB.EMAIL.InterviewNoReschedule】START: ==================================================");
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			find(stopwatch, 24, 48);
		} catch (Exception e) {
			logger.error("【JOB.EMAIL.InterviewNoReschedule】EXCEPTION: Cost {}ms. ", stopwatch.elapsed(TimeUnit.MILLISECONDS), e);
		}
		
		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
		logger.info("【JOB.EMAIL.InterviewNoReschedule】END: Cost {}ms. ==================================================", millis);
	}

	void find (Stopwatch stopwatch, int... beforeHours) {
		List<Long> teacherIds = new ArrayList<>();
		Map<Long, TeacherApplication> teacherApplicationsMap = new HashedMap();
		List<Map> times = UADateUtils.getStartEndTimeMapListByBeforeHours(beforeHours);

		List<TeacherApplication> teacherApplications = teacherApplicationDao.findByAuditTimesStatusResult(times, ApplicationConstant.RecruitmentStatus.INTERVIEW, ApplicationConstant.RecruitmentResult.REAPPLY);
		logger.info("【JOB.EMAIL.InterviewNoReschedule】FIND.1: Cost {}ms. Query: times = {}, status = {}, result = {}; Result: users = {}",
				stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(times), ApplicationConstant.RecruitmentStatus.INTERVIEW, ApplicationConstant.RecruitmentResult.REAPPLY, JsonUtils.toJSONString(teacherApplications));
		for(TeacherApplication ta : teacherApplications){
			teacherIds.add(ta.getTeacherId());
			teacherApplicationsMap.put(ta.getTeacherId(), ta);
		}

		if(teacherIds.size() == 0) return;
		List<TeacherApplication> teacherApplicationsToRemove = teacherApplicationDao.findByTeacherIdsStatusNeResult(teacherIds, ApplicationConstant.RecruitmentStatus.INTERVIEW, ApplicationConstant.RecruitmentResult.REAPPLY);
		logger.info("【JOB.EMAIL.InterviewNoReschedule】FIND.2: Cost {}ms. Query: teacherIds = {}, status = {}, result = {}; Result: teacherApplications = {}",
				stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(teacherIds), ApplicationConstant.RecruitmentStatus.INTERVIEW, ApplicationConstant.RecruitmentResult.REAPPLY, JsonUtils.toJSONString(teacherApplicationsToRemove));
		teacherApplicationsToRemove.forEach(x -> teacherIds.remove(x.getTeacherId()));

		if(teacherIds.size() == 0) return;
		List<Teacher> teachers = teacherDao.findByIds(teacherIds);
		logger.info("【JOB.EMAIL.InterviewNoReschedule】FIND.3: Cost {}ms. Query: teacherIds = {}; Result: teachers = {}",
				stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(teacherIds), JsonUtils.toJSONString(teachers));
		teachers.forEach(x -> send(stopwatch, x, teacherApplicationsMap.get(x.getId()).getAuditDateTime(), times));

	}

	void send (Stopwatch stopwatch, Teacher teacher, Date auditTime, List<Map> times) {
		Map <String, String> time = times.get(times.size()-1);
		Date startTime = UADateUtils.parse(time.get("startTime"));
		Date endTime = UADateUtils.parse(time.get("endTime"));

		if (auditTime.after(startTime) && auditTime.before(endTime)){
			userDao.doLock(teacher.getId());
			logger.info("【JOB.EMAIL.InterviewNoReschedule】LOCK: Cost {}ms. teacher = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(teacher));
		} else {
			String email = teacher.getEmail();
			String name = teacher.getRealName();
			String titleTemplate = "InterviewNoRescheduleTitle.html";
			String contentTemplate = "InterviewNoReschedule.html";
			EmailUtils.sendEmail4Recruitment(email, name, titleTemplate, contentTemplate);
			logger.info("【JOB.EMAIL.InterviewNoReschedule】SEND: Cost {}ms. email = {}, name = {}, titleTemplate = {}, contentTemplate = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), email, name, titleTemplate, contentTemplate);
		}
	}

}