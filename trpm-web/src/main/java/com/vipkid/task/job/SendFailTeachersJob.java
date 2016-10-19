package com.vipkid.task.job;

import com.google.api.client.util.Maps;
import com.google.common.base.Stopwatch;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.task.utils.UADateUtils;
import com.vipkid.trpm.dao.TeacherApplicationDao;
import com.vipkid.vschedule.client.common.Vschedule;
import com.vipkid.vschedule.client.schedule.JobContext;
import org.apache.commons.collections.CollectionUtils;
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
public class SendFailTeachersJob {

	private static final Logger logger = LoggerFactory.getLogger(SendFailTeachersJob.class);

	@Autowired
	private TeacherApplicationDao teacherApplicationDao;
	
	@Vschedule
	public void doSendFailTeachersJob(JobContext jobContext) {
		logger.info("开始发邮件给fail掉的老师=======================================");
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			sendFailTeachers(12);
		} catch (Exception e) {
			logger.error("发邮件给fail掉的老师，出现异常{}",e);
		}
		
		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("发邮件给fail掉的老师成功，执行方法 doSendFailTeachersJob()耗时：{} ========================================", millis);
	}

	public void sendFailTeachers(int hours){

		//查询出hours个小时以前fail掉的老师
		Date startDate = UADateUtils.getDateByBeforeHours(hours+1);
		Date endDate = UADateUtils.getDateByBeforeHours(hours);
		String startTime = UADateUtils.format(startDate, UADateUtils.defaultFormat) ;
		String endTime = UADateUtils.format(endDate, UADateUtils.defaultFormat) ;

		logger.info("查询出"+hours+"个小时以前fail掉的老师 startTime = {}, endTime = {}",startTime,endTime);
		List<Map<String, String>> list = teacherApplicationDao.findFailTeachersByAuditTime(startTime, endTime);
		logger.info("fail掉的老师 list = {}", JsonUtils.toJSONString(list));

		if(list!=null && CollectionUtils.isNotEmpty(list)){
			for (Map<String, String> map : list) {
				if(map!=null){
					String email = map.get("teacherEmail"); //获取教师邮箱发送邮件
					String name = map.get("teacherName");
					String status = map.get("status");

					if (StringUtils.isNoneBlank(email) && StringUtils.isNoneBlank(status)){
						String titleTemplete = null;
						String contentTemplete = null;

						if (TeacherEnum.LifeCycle.BASIC_INFO.toString().equals(status)){
							titleTemplete = "BasicInfoFailTitle.html";
							contentTemplete = "BasicInfoFail.html";
						} else if (TeacherEnum.LifeCycle.INTERVIEW.toString().equals(status)){
							titleTemplete = "InterviewFailTitle.html";
							contentTemplete = "InterviewFail.html";
						}

						try {
							Map<String, String> paramsMap = Maps.newHashMap();
							paramsMap.put("teacherName", name);
							logger.info("send Email to teacher name= {},email = {} , contentTemplete = {}, titleTemplete = {}",name,email,contentTemplete,titleTemplete);
							Map<String, String> emailMap = new TempleteUtils().readTemplete(contentTemplete, paramsMap, titleTemplete);
							new EmailEngine().addMailPool(email, emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
							logger.info("send Email success! teacher = {},email = {},contentTemplete = {}, titleTemplete = {}",name,email,contentTemplete,titleTemplete);
						} catch (Exception e) {
							logger.error("Send FailTeachers mail error: {}", e);
						}

					}
				}
			}
		}
	}
}