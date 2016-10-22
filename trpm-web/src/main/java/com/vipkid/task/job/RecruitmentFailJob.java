package com.vipkid.task.job;

import com.google.common.base.Stopwatch;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.email.EmailUtils;
import com.vipkid.task.utils.UADateUtils;
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
		logger.info("开始发邮件给fail掉的老师=======================================");
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			find(12);
		} catch (Exception e) {
			logger.error("发邮件给fail掉的老师，出现异常{}",e);
		}
		
		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("发邮件给fail掉的老师成功，执行方法 doSendFailTeachersJob()耗时：{} ========================================", millis);
	}

	void find(int hours){
		//查询出hours个小时以前fail掉的老师
		Date startDate = UADateUtils.getDateByBeforeHours(hours+1);
		Date endDate = UADateUtils.getDateByBeforeHours(hours);
		String startTime = UADateUtils.format(startDate, UADateUtils.defaultFormat) ;
		String endTime = UADateUtils.format(endDate, UADateUtils.defaultFormat) ;

		logger.info("查询出"+hours+"个小时以前fail掉的老师 startTime = {}, endTime = {}",startTime,endTime);
		List<Map<String, String>> list = teacherApplicationDao.findFailTeachersByAuditTime(startTime, endTime);
		logger.info("fail掉的老师 list = {}", JsonUtils.toJSONString(list));
		list.forEach(x -> send(x));
	}

	void send(Map<String, String> map) {
		if(map!=null){
			String email = map.get("teacherEmail"); //获取教师邮箱发送邮件
			String name = map.get("teacherName");
			String status = map.get("status");

			if (StringUtils.isNoneBlank(email) && StringUtils.isNoneBlank(status)){
				String titleTemplate = null;
				String contentTemplate = null;

				if (TeacherApplicationEnum.Status.BASIC_INFO.toString().equals(status)){
					titleTemplate = "BasicInfoFailTitle.html";
					contentTemplate = "BasicInfoFail.html";
				} else if (TeacherApplicationEnum.Status.INTERVIEW.toString().equals(status)){
					titleTemplate = "InterviewFailTitle.html";
					contentTemplate = "InterviewFail.html";
				}

				EmailUtils.sendEmail4Recruitment(email, name, titleTemplate, contentTemplate);
			}
		}
	}
}