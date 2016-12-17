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
import com.vipkid.trpm.entity.User;
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
public class SignUpNoFinishRegisterJob {

	private static final Logger logger = LoggerFactory.getLogger(SignUpNoFinishRegisterJob.class);

	@Autowired
	private UserDao userDao;
	@Autowired
	private TeacherApplicationDao teacherApplicationDao;
	@Autowired
	private TeacherDao teacherDao;
	@Autowired
	private TeacherLockLogDao teacherLockLogDao;

	@Vschedule
	public void doJob (JobContext jobContext) {
		logger.info("【JOB.EMAIL.SignUpNoFinishRegister】START: ==================================================");
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			find(stopwatch, 24, 48, 72);
		} catch (Exception e) {
			logger.error("【JOB.EMAIL.SignUpNoFinishRegister】EXCEPTION: Cost {}ms. ", stopwatch.elapsed(TimeUnit.MILLISECONDS), e);
		}
		
		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
		logger.info("【JOB.EMAIL.SignUpNoFinishRegister】END: Cost {}ms. ==================================================", millis);
	}

	void find (Stopwatch stopwatch, int... beforeHours) {
		List<Long> teacherIds = new ArrayList<>();
		Map<Long, User> usersMap = new HashedMap();
		List<Map> times = UADateUtils.getStartEndOclockTimeMapListByBeforeHours(beforeHours);

		List<User> users = userDao.findTeachersByRegisterTimes(times);
		logger.info("【JOB.EMAIL.SignUpNoFinishRegister】FIND.1: Cost {}ms. Query: times = {}; Result: users = ",
				stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(times));
		for(User user : users){
			teacherIds.add(user.getId());
			usersMap.put(user.getId(),user);
		}

		if(teacherIds.size() == 0) return;
		List<TeacherApplication> teacherApplications = teacherApplicationDao.findByTeacherIdsStatusNeResult(teacherIds, TeacherApplicationEnum.Status.BASIC_INFO.toString(), null);
		logger.info("【JOB.EMAIL.SignUpNoFinishRegister】FIND.2: Cost {}ms. Query: teacherIds = {}, status = {}, result = {}; Result: teacherApplications = ",
				stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(teacherIds), TeacherApplicationEnum.Status.BASIC_INFO.toString(), "null");
		teacherApplications.forEach(x -> teacherIds.remove(x.getTeacherId()));

		if(teacherIds.size() == 0) return;
		List<Teacher> teachers = teacherDao.findByIds(teacherIds);
		logger.info("【JOB.EMAIL.SignUpNoFinishRegister】FIND.3: Cost {}ms. Query: teacherIds = {}; Result: teachers = ",
				stopwatch.elapsed(TimeUnit.MILLISECONDS), JsonUtils.toJSONString(teacherIds));
		teachers.forEach(x -> send(stopwatch, x, usersMap.get(x.getId()).getRegisterDateTime(), times));
	}

	void send (Stopwatch stopwatch, Teacher teacher, Date registerTime, List<Map> times) {
		Map <String, String> time = times.get(times.size()-1);
		Date startTime = UADateUtils.parse(time.get("startTime"));
		Date endTime = UADateUtils.parse(time.get("endTime"));

		if (registerTime.after(startTime) && registerTime.before(endTime)){
			userDao.doLock(teacher.getId());
			teacherLockLogDao.save(new TeacherLockLog(teacher.getId(), TeacherLockLogEnum.Reason.NO_FINISH_REGISTER.toString(), TeacherEnum.LifeCycle.SIGNUP.toString()));
			logger.info("【JOB.EMAIL.SignUpNoFinishRegister】LOCK: Cost {}ms. teacherId = {}, teacherEmail = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), teacher.getId(), teacher.getEmail());
		} else {
			String email = teacher.getEmail();
			String name = teacher.getRealName();
			String titleTemplate = "SignUpNoFinishRegisterTitle.html";
			String contentTemplate = "SignUpNoFinishRegister.html";
			EmailUtils.sendEmail4Recruitment(teacher, titleTemplate, contentTemplate);
			logger.info("【JOB.EMAIL.SignUpNoFinishRegister】SEND: Cost {}ms. email = {}, name = {}, titleTemplate = {}, contentTemplate = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), email, name, titleTemplate, contentTemplate);
		}
	}
}