/**
 * 
 */
package com.vipkid.task.job;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Stopwatch;
import com.vipkid.task.service.UnitAssesssmentService;
import com.vipkid.vschedule.client.common.Vschedule;
import com.vipkid.vschedule.client.schedule.JobContext;

/**
 * @author zouqinghua
 * @date 2016年8月19日  下午5:24:23
 *
 */
@Component
@Vschedule
public class CheckUnitAssessmentReportJob {

	private static final Logger logger = LoggerFactory.getLogger(CheckUnitAssessmentReportJob.class);
	
	@Autowired
	private UnitAssesssmentService unitAssesssmentService;
	
	@Vschedule
	public void do6hourRemindJob(JobContext jobContext) {
		logger.info("开始检查教师结束课程6小时后是否填写UA报告=======================================");
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			unitAssesssmentService.remindTeacherUnitAssessmentFor6Hour();
		} catch (Exception e) {
			logger.error("执行检查教师结束课程6小时后是否填写UA报告时 checkTeacherUnitAssessment，出现异常",e);
		}
		
		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("执行方法doJob()耗时：{} ", millis);
		logger.info("执行检查教师结束课程6小时后是否填写UA报告成功========================================");
	}
	
	@Vschedule
	public void do12hourRemindJob(JobContext jobContext) {
		logger.info("开始检查教师结束课程12小时后是否填写UA报告=======================================");
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			unitAssesssmentService.remindTeacherUnitAssessmentFor12Hour();
		} catch (Exception e) {
			logger.error("执行检查教师结束课程12小时后是否填写UA报告时 checkTeacherUnitAssessment，出现异常",e);
		}
		
		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("执行方法doJob()耗时：{} ", millis);
		logger.info("执行检查教师结束课程12小时后是否填写UA报告成功========================================");
	}

	@Vschedule
	public void do24hourRemindJob(JobContext jobContext) {
		logger.info("开始检查教师结束课程24小时后是否填写UA报告=======================================");
		Stopwatch stopwatch = Stopwatch.createStarted();

		try {
			unitAssesssmentService.remindTeacherUnitAssessmentFor24Hour();
		} catch (Exception e) {
			logger.error("执行检查教师结束课程24小时后是否填写UA报告时 checkTeacherUnitAssessment，出现异常",e);
		}

		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
		logger.info("执行方法doJob()耗时：{} ", millis);
		logger.info("执行检查教师结束课程24小时后是否填写UA报告成功========================================");
	}
}
