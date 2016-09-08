package com.vipkid.mq.service.impl;

import javax.annotation.Resource;
import javax.jms.Destination;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.vipkid.mq.message.FinishOnlineClassMessage;
import com.vipkid.mq.message.FinishOnlineClassMessage.OperatorType;
import com.vipkid.mq.message.LessonMessage;
import com.vipkid.mq.message.OnlineClassMessage;
import com.vipkid.mq.producer.ProducerService;
import com.vipkid.mq.service.PayrollMessageService;
import com.vipkid.payroll.service.AssessmentReportService;
import com.vipkid.payroll.service.StudentService;
import com.vipkid.payroll.service.TeacherCommentService;
import com.vipkid.payroll.utils.DateUtils;
import com.vipkid.trpm.dao.CourseDao;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.*;

/**
 * 结束课程，发送消息服务
 * 
 * @author zouqinghua
 * @date 2016年5月6日 下午1:34:26
 *
 */
@Service
public class PayrollMessageServiceImpl implements PayrollMessageService {

	private Logger logger = LoggerFactory.getLogger(PayrollMessageServiceImpl.class);

	@Resource
	private ProducerService producerService;
	
	@Autowired
    private AssessmentReportService assessmentReportService;

    @Autowired
    private TeacherCommentService teacherCommentService;

    @Autowired
    private OnlineClassDao onlineClassDao;

    @Autowired
    private LessonDao lessonDao;

    @Autowired
    private StudentService studentService;

	@Autowired
	private TeacherDao teacherDao;
	
	@Autowired
	private CourseDao courseDao;

	@Autowired
	@Qualifier("finishOnlineClassDestination")
	private Destination finishOnlineClassDestination;

	
	@Override
	public FinishOnlineClassMessage sendFinishOnlineClassMessage(Long onlineClassId, OperatorType operatorType) {
		FinishOnlineClassMessage message = new FinishOnlineClassMessage();
		message.setOperatorType(operatorType);
		if(onlineClassId == null || onlineClassId ==0){
			logger.info("PayrollMessageService，消息发送失败 onlineClassId = {}" ,onlineClassId);
			return message;
		}
		try {
			OnlineClass onlineClass = onlineClassDao.findById(onlineClassId);
			Course course = courseDao.findByLessonId(onlineClass.getLessonId());
            Lesson lesson = lessonDao.findById(onlineClass.getLessonId());
            Student student = studentService.getFirstStudentByOnlineClass(onlineClassId);
            Teacher teacher = teacherDao.findById(onlineClass.getTeacherId());
            
            //注入学生约课信息
			message.setOnlineClass(onlineClass);
			message.setCourse(course);
			message.setLesson(lesson);
			message.setTeacher(teacher);
			message.setStudent(student);
			
			OnlineClassMessage onlineClassMessage = message.getOnlineClassMessage();
			Long studentId = student.getId();
			if(onlineClassMessage!=null && onlineClassMessage.getFinishType()!=null){
				
				//查询是否有评语
				TeacherComment teacherComment = teacherCommentService.hasCommentsByOnlineClassIdAndStudentId(onlineClassId, studentId);
				Boolean hasComments = false;
				if(teacherComment!=null){
					hasComments = teacherComment.getHasComment();
					Long tcUpdateDateTime = teacherComment.getLastDateTime()==null?null:teacherComment.getLastDateTime().getTime();
					onlineClassMessage.setTcUpdateDateTime(tcUpdateDateTime );
				}
				onlineClassMessage.setHasComments(hasComments);
				
				//是否新生体验课
				Boolean isTrialOnly = "TRIAL".equals(course.getType());
				onlineClassMessage.setIsTrialOnly(isTrialOnly);
				onlineClassMessage.setStudentEnrollmentTime(student.getCreateDateTime().getTime());
				
				//学生是否在约课月内支付
				Boolean isPaidForTrial = null;
				if(isTrialOnly == true){
					String paidDateTime = DateUtils.formatDate(onlineClass.getScheduledDateTime(), "yyyy-MM") ;
					isPaidForTrial = studentService.findIsPaidByStudentIdAndPayDate(studentId, paidDateTime );
					onlineClassMessage.setPaidForTrial(isPaidForTrial);
				}
				
				//是否有unitAssessment
				Boolean hasAssessmentReport = false;
				LessonMessage lessonMessage = message.getLessonMessage();
				if(lessonMessage!=null && lessonMessage.getSerialNumber()!=null){
					AssessmentReport assessmentReport = assessmentReportService.hasAssessmentReportByOnlineClass(onlineClassId, onlineClass.getScheduledDateTime(), studentId, lessonMessage.getSerialNumber());
					hasAssessmentReport = assessmentReport.getHasUnitAssessment();
					Long uaUpdateDateTime = assessmentReport.getUpdateDateTime()==null?null:assessmentReport.getUpdateDateTime().getTime();
					onlineClassMessage.setUaUpdateDateTime(uaUpdateDateTime );
					Long uaUploadDateTime = assessmentReport.getUploadDateTime()==null?null:assessmentReport.getUploadDateTime().getTime();
					onlineClassMessage.setUaUploadDateTime(uaUploadDateTime );
				}
				onlineClassMessage.setHasAssessmentReport(hasAssessmentReport);
				
				logger.info("PayrollMessageService 结束课程，消息发送成功  destination={}, message={} ", finishOnlineClassDestination,
						JSONObject.fromObject(message));
				producerService.sendJsonMessage(finishOnlineClassDestination, message);
			}else{
				logger.info("课程信息为空，不发送消息 onlineClassId = {}",onlineClassId);
			}
		} catch (Exception e) {
			logger.error("PayrollMessageService 更新TeacherComment，消息发送失败   destination={},operatorType={},e={}",
					finishOnlineClassDestination, operatorType,e.getMessage());
		}
		return message;
	}


	@Override
	public FinishOnlineClassMessage sendFinishOnlineClassMessage(TeacherComment teacherComment, Long onlineClassId,
			OperatorType operatorType) {

		FinishOnlineClassMessage message = new FinishOnlineClassMessage();
		message.setOperatorType(operatorType);
		if(onlineClassId == null || onlineClassId ==0){
			logger.info("PayrollMessageService，消息发送失败 onlineClassId = {}" ,onlineClassId);
			return message;
		}
		try {
			OnlineClass onlineClass = onlineClassDao.findById(onlineClassId);
			Course course = courseDao.findByLessonId(onlineClass.getLessonId());
            Lesson lesson = lessonDao.findById(onlineClass.getLessonId());
            Student student = studentService.getFirstStudentByOnlineClass(onlineClassId);
            Teacher teacher = teacherDao.findById(onlineClass.getTeacherId());
            
            //注入学生约课信息
			message.setOnlineClass(onlineClass);
			message.setCourse(course);
			message.setLesson(lesson);
			message.setTeacher(teacher);
			message.setStudent(student);
			
			OnlineClassMessage onlineClassMessage = message.getOnlineClassMessage();
			Long studentId = student.getId();
			if(onlineClassMessage!=null && onlineClassMessage.getFinishType()!=null){
				Boolean hasComments = false;
				if (teacherComment == null 
	                    || StringUtils.isBlank(teacherComment.getTeacherFeedback())) {
					hasComments = false;
	            }else{
	            	hasComments = true;
	            }
				//查询是否有评语
				if (teacherComment != null) {
					hasComments = teacherComment.getHasComment();
					Long tcUpdateDateTime = teacherComment.getLastDateTime() == null ? null : teacherComment
							.getLastDateTime().getTime();
					onlineClassMessage.setTcUpdateDateTime(tcUpdateDateTime);
				}
				onlineClassMessage.setHasComments(hasComments);
				
				//是否新生体验课
				Boolean isTrialOnly = "TRIAL".equals(course.getType());
				onlineClassMessage.setIsTrialOnly(isTrialOnly);
				onlineClassMessage.setStudentEnrollmentTime(student.getCreateDateTime().getTime());
				
				//学生是否在约课月内支付
				Boolean isPaidForTrial = null;
				if(isTrialOnly == true){
					String paidDateTime = DateUtils.formatDate(onlineClass.getScheduledDateTime(), "yyyy-MM") ;
					isPaidForTrial = studentService.findIsPaidByStudentIdAndPayDate(studentId, paidDateTime );
					onlineClassMessage.setPaidForTrial(isPaidForTrial);
				}
				
				
				logger.info("PayrollMessageService 结束课程，消息发送成功  destination={}, message={} ", finishOnlineClassDestination,
						JSONObject.fromObject(message));
				producerService.sendJsonMessage(finishOnlineClassDestination, message);
			}else{
				logger.info("课程信息为空，不发送消息 onlineClassId = {}",onlineClassId);
			}
		} catch (Exception e) {
			logger.error("PayrollMessageService 更新TeacherComment，消息发送失败   destination={},operatorType={},e={}",
					finishOnlineClassDestination, operatorType,e.getMessage());
		}
		return message;
	
	}


	@Override
	public FinishOnlineClassMessage sendFinishOnlineClassMessage(AssessmentReport assessmentReport,
			Long onlineClassId, OperatorType operatorType) {

		FinishOnlineClassMessage message = new FinishOnlineClassMessage();
		message.setOperatorType(operatorType);
		if(onlineClassId == null || onlineClassId ==0){
			logger.info("PayrollMessageService，消息发送失败 onlineClassId = {}" ,onlineClassId);
			return message;
		}
		try {
			OnlineClass onlineClass = onlineClassDao.findById(onlineClassId);
			Course course = courseDao.findByLessonId(onlineClass.getLessonId());
            Lesson lesson = lessonDao.findById(onlineClass.getLessonId());
            Student student = studentService.getFirstStudentByOnlineClass(onlineClassId);
            Teacher teacher = teacherDao.findById(onlineClass.getTeacherId());
            
            //注入学生约课信息
			message.setOnlineClass(onlineClass);
			message.setCourse(course);
			message.setLesson(lesson);
			message.setTeacher(teacher);
			message.setStudent(student);
			
			OnlineClassMessage onlineClassMessage = message.getOnlineClassMessage();
			Long studentId = student.getId();
			if(onlineClassMessage!=null && onlineClassMessage.getFinishType()!=null){
				
				//是否新生体验课
				Boolean isTrialOnly = "TRIAL".equals(course.getType());
				onlineClassMessage.setIsTrialOnly(isTrialOnly);
				onlineClassMessage.setStudentEnrollmentTime(student.getCreateDateTime().getTime());
				
				//学生是否在约课月内支付
				Boolean isPaidForTrial = null;
				if(isTrialOnly == true){
					String paidDateTime = DateUtils.formatDate(onlineClass.getScheduledDateTime(), "yyyy-MM") ;
					isPaidForTrial = studentService.findIsPaidByStudentIdAndPayDate(studentId, paidDateTime );
					onlineClassMessage.setPaidForTrial(isPaidForTrial);
				}
				
				//是否有unitAssessment
				assessmentReport.setHasUnitAssessment(true);
				Boolean hasAssessmentReport = false;
				LessonMessage lessonMessage = message.getLessonMessage();
				if(lessonMessage!=null && lessonMessage.getSerialNumber()!=null){
					hasAssessmentReport = assessmentReport.getHasUnitAssessment();
					Long uaUpdateDateTime = assessmentReport.getUpdateDateTime()==null?null:assessmentReport.getUpdateDateTime().getTime();
					onlineClassMessage.setUaUpdateDateTime(uaUpdateDateTime );
					Long uaUploadDateTime = assessmentReport.getUploadDateTime()==null?null:assessmentReport.getUploadDateTime().getTime();
					onlineClassMessage.setUaUploadDateTime(uaUploadDateTime);
				}
				onlineClassMessage.setHasAssessmentReport(hasAssessmentReport);
				
				logger.info("PayrollMessageService 结束课程，消息发送成功  destination={}, message={} ", finishOnlineClassDestination,
						JSONObject.fromObject(message));
				producerService.sendJsonMessage(finishOnlineClassDestination, message);
			}else{
				logger.info("课程信息为空，不发送消息 onlineClassId = {}",onlineClassId);
			}
		} catch (Exception e) {
			logger.error("PayrollMessageService 更新TeacherComment，消息发送失败   destination={},operatorType={},e={}",
					finishOnlineClassDestination, operatorType,e.getMessage());
		}
		return message;
	
	}
	
	
	
}
