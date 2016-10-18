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
import com.vipkid.task.service.CheckTeacherCommentService;
import com.vipkid.vschedule.client.common.Vschedule;
import com.vipkid.vschedule.client.schedule.JobContext;

/**
 * @author xingxuelin
 * @date 2016年9月9日  下午5:24:23
 *
 */
@Component
@Vschedule
public class CheckTeacherCommentJob {

	private static final Logger logger = LoggerFactory.getLogger(CheckTeacherCommentJob.class);
	
	@Autowired
	private CheckTeacherCommentService checkTeacherCommentService;
	
	@Vschedule
	public void do6hourRemindJob(JobContext jobContext) {
		logger.info("开始检查教师结束课程6小时后是否填写TeacherComment=======================================");
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			checkTeacherCommentService.remindTeacherComment(6);
		} catch (Exception e) {
			logger.error("执行检查教师结束课程6小时后是否填写TeacherComment时 checkTeacherComment，出现异常",e);
		}
		
		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("执行方法doJob()耗时：{} ", millis);
		logger.info("执行检查教师结束课程6小时后是否填写TeacherComment成功========================================");
	}
	
	@Vschedule
	public void do12hourRemindJob(JobContext jobContext) {
		logger.info("开始检查教师结束课程12小时后是否填写TeacherComment=======================================");
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			checkTeacherCommentService.remindTeacherComment(12);
		} catch (Exception e) {
			logger.error("执行检查教师结束课程12小时后是否填写TeacherComment时 checkTeacherComment，出现异常",e);
		}
		
		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        logger.info("执行方法doJob()耗时：{} ", millis);
		logger.info("执行检查教师结束课程12小时后是否填写TeacherComment成功========================================");
	}

	@Vschedule
	public void do24hourRemindJob(JobContext jobContext) {
		logger.info("开始检查教师结束课程24小时后是否填写TeacherComment=======================================");
		Stopwatch stopwatch = Stopwatch.createStarted();

		try {
			checkTeacherCommentService.remindTeacherComment(24);
		} catch (Exception e) {
			logger.error("执行检查教师结束课程24小时后是否填写TeacherComment时 checkTeacherComment，出现异常",e);
		}

		long millis =stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
		logger.info("执行方法doJob()耗时：{} ", millis);
		logger.info("执行检查教师结束课程24小时后是否填写TeacherComment成功========================================");
	}
}
