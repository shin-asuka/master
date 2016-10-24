package com.vipkid.task.job;

import com.google.common.base.Stopwatch;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.email.EmailUtils;
import com.vipkid.task.utils.UADateUtils;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.dao.TeacherApplicationDao;
import com.vipkid.vschedule.client.common.Vschedule;
import com.vipkid.vschedule.client.schedule.JobContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xingxuelin
 * @date 2016年10月19日  下午5:24:23
 *
 */
@Component
@Vschedule
public class RecruitmentFailJob {

	private static final Logger logger = LoggerFactory.getLogger(RecruitmentFailJob.class);

	@Autowired
	private TeacherApplicationDao teacherApplicationDao;
	
	@Vschedule
	public void doJob(JobContext jobContext) {
		logger.info("【JOB.EMAIL.RecruitmentFail】START: ==================================================");
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			find(stopwatch, 12);
		} catch (Exception e) {
			logger.error("【JOB.EMAIL.RecruitmentFail】EXCEPTION: Cost {}ms. ", stopwatch.elapsed(TimeUnit.MILLISECONDS), e);
		}
		
		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("【JOB.EMAIL.RecruitmentFail】END: Cost {}ms. ==================================================", millis);
	}

	void find(Stopwatch stopwatch, int hours){
		//查询出hours个小时以前fail掉的老师
		Date startDate = UADateUtils.getDateOclockByBeforeHours(hours+1);
		Date endDate = UADateUtils.getDateOclockByBeforeHours(hours);
		String startTime = UADateUtils.format(startDate, UADateUtils.defaultFormat) ;
		String endTime = UADateUtils.format(endDate, UADateUtils.defaultFormat) ;

		List<Map<String, String>> list = teacherApplicationDao.findFailTeachersByAuditTime(startTime, endTime);
		logger.info("【JOB.EMAIL.RecruitmentFail】FIND: Cost {}ms. Query: startTime = {}, endTime = {}; Result: list = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), startTime, endTime, JsonUtils.toJSONString(list));
		list.forEach(x -> send(stopwatch, x));
	}

	void send(Stopwatch stopwatch, Map<String, String> map) {
		if(map!=null){
			String email = map.get("teacherEmail"); //获取教师邮箱发送邮件
			String name = map.get("teacherName");
			String status = map.get("status");

			if (StringUtils.isNoneBlank(email) && StringUtils.isNoneBlank(status)){
				String titleTemplate = null;
				String contentTemplate = null;

				if (ApplicationConstant.RecruitmentStatus.BASIC_INFO.equals(status)){
					titleTemplate = "BasicInfoFailTitle.html";
					contentTemplate = "BasicInfoFail.html";
				} else if (ApplicationConstant.RecruitmentStatus.INTERVIEW.equals(status)){
					titleTemplate = "InterviewFailTitle.html";
					contentTemplate = "InterviewFail.html";
				}

				EmailUtils.sendEmail4Recruitment(email, name, titleTemplate, contentTemplate);
				logger.info("【JOB.EMAIL.RecruitmentFail】SEND: Cost {}ms. email = {}, name = {}, titleTemplate = {}, contentTemplate = {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), email, name, titleTemplate, contentTemplate);
			}
		}
	}
}