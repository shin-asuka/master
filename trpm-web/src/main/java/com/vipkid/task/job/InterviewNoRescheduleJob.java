package com.vipkid.task.job;

import com.google.common.base.Stopwatch;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.task.utils.UADateUtils;
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
		logger.info("开始发邮件给fail掉的老师=======================================");
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			find(24, 48);
		} catch (Exception e) {
			logger.error("发邮件给fail掉的老师，出现异常{}",e);
		}
		
		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("发邮件给fail掉的老师成功，执行方法 doSendFailTeachersJob()耗时：{} ========================================", millis);
	}

	void find (int... beforeHours) {

		//logger.info("查询出"+hours+"个小时以前fail掉的老师 startTime = {}, endTime = {}",startTime,endTime);
		//logger.info("fail掉的老师 list = {}", JsonUtils.toJSONString(list));

		List<Long> teacherIds = new ArrayList<>();
		Map<Long, TeacherApplication> teacherApplicationsMap = new HashedMap();
		List<Map> times = UADateUtils.getStartEndTimeMapListByBeforeHours(beforeHours);

		List<TeacherApplication> teacherApplications = teacherApplicationDao.findByAuditTimesStatusResult(times, TeacherApplicationEnum.Status.INTERVIEW.toString(), TeacherApplicationEnum.Result.REAPPLY.toString());
		for(TeacherApplication ta : teacherApplications){
			teacherIds.add(ta.getTeacherId());
			teacherApplicationsMap.put(ta.getTeacherId(), ta);
		}

		if(teacherIds.size() == 0) return;
		List<TeacherApplication> teacherApplicationsToRemove = teacherApplicationDao.findByTeacherIdsStatusNeResult(teacherIds, TeacherApplicationEnum.Status.INTERVIEW.toString(), TeacherApplicationEnum.Result.REAPPLY.toString());
		teacherApplicationsToRemove.forEach(x -> teacherIds.remove(x.getTeacherId()));

		if(teacherIds.size() == 0) return;
		List<Teacher> teachers = teacherDao.findByIds(teacherIds);
		teachers.forEach(x -> send(x, teacherApplicationsMap.get(x.getId()).getAuditDateTime(), times));

	}

	void send (Teacher teacher, Date auditTime, List<Map> times) {
		Map <String, String> time = times.get(times.size()-1);
		Date startTime = UADateUtils.parse(time.get("startTime"));
		Date endTime = UADateUtils.parse(time.get("endTime"));

		if (auditTime.after(startTime) && auditTime.before(endTime)){
			userDao.doLock(teacher.getId());
		} else {
			EmailUtils.sendEmail4Recruitment(teacher.getEmail(), teacher.getRealName(), "InterviewNoRescheduleTitle.html", "InterviewNoReschedule.html");
		}
	}

}