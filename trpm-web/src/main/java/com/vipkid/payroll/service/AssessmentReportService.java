package com.vipkid.payroll.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.payroll.utils.DateUtils;
import com.vipkid.trpm.constant.ApplicationConstant.UaReportStatus;
import com.vipkid.trpm.dao.AssessmentReportDao;
import com.vipkid.trpm.entity.AssessmentReport;


@Service
public class AssessmentReportService {
	private Logger logger = LoggerFactory.getLogger(AssessmentReportService.class.getSimpleName());

	public final static Date assertReportOldDate = DateUtils.parseDate("2016-01-29 22:27:56");
	
	@Autowired
	private AssessmentReportDao assessmentReportDao;
	
	public AssessmentReport hasAssessmentReportByOnlineClass(Long onlineClassId,Date scheduledDateTime,Long studentId,String lessonSerialNumber){
		AssessmentReport assessmentReport = null;
		if(lessonSerialNumber!=null){
			if(lessonSerialNumber.endsWith("-12") || lessonSerialNumber.endsWith("-6")){
				//时间大于2016-01-29 22:27:56 通过OnlineClassId查询，小于2016-01-29 22:27:56通过student he lessonSerialNumber
				if(assertReportOldDate.before(scheduledDateTime)){
					assessmentReport = getHasUnitAssessmentByOnlineClassId(onlineClassId);
				}else{
					assessmentReport = getHasUnitAssessmentByStudentIdAndLessonSerialNumber(studentId, lessonSerialNumber);
				}
			}
		}
		if(assessmentReport == null){
			assessmentReport = new AssessmentReport();
			assessmentReport.setOnlineClassId(onlineClassId);
			assessmentReport.setName(lessonSerialNumber);
			assessmentReport.setStudentId(studentId);
			assessmentReport.setHasUnitAssessment(false);
		}
		
		return assessmentReport;
	}
	
	/**
	 * 查询在线课程是否已经上传报告
	 * @param onlineClassId
	 * @return
	 */
	public AssessmentReport getHasUnitAssessmentByOnlineClassId(Long onlineClassId) {
		Boolean hasUnitAssessment = false;
		AssessmentReport assessmentReport = null;
		if(onlineClassId!=null){
			assessmentReport = assessmentReportDao.findReportByClassId(onlineClassId);
			hasUnitAssessment = getHasUnitAssessmentByUnitAssessment(assessmentReport);
			if(assessmentReport==null){
				assessmentReport = new AssessmentReport();
				assessmentReport.setOnlineClassId(onlineClassId);
			}
			assessmentReport.setHasUnitAssessment(hasUnitAssessment);
		}
		logger.info("查询在线课程是否已经上传报告  onlineClassId = {} ，hasUnitAssessment = {}" , onlineClassId,hasUnitAssessment);
		return assessmentReport;
	}

	/**
	 * 查询学生课程是否已经上传报告
	 * @param studentId
	 * @param lessonSerialNumber
	 * @return
	 */
	public AssessmentReport getHasUnitAssessmentByStudentIdAndLessonSerialNumber(Long studentId, String lessonSerialNumber) {
		Boolean hasUnitAssessment = false;
		AssessmentReport assessmentReport = null;
		if(studentId!=null && lessonSerialNumber!=null){
			assessmentReport = assessmentReportDao.findReportByStudentIdAndName(lessonSerialNumber, studentId);
			hasUnitAssessment = getHasUnitAssessmentByUnitAssessment(assessmentReport);
			if(assessmentReport==null){
				assessmentReport = new AssessmentReport();
				assessmentReport.setStudentId(studentId);
				assessmentReport.setName(lessonSerialNumber);
			}
			assessmentReport.setHasUnitAssessment(hasUnitAssessment);
		}
		logger.info("查询学生课程是否已经上传报告  studentId = {},lessonSerialNumber = {}, hasUnitAssessment = {}" , studentId,lessonSerialNumber,hasUnitAssessment);
		return assessmentReport;
	}
	
	/**
	 * 查询上传报告 是否有效
	 * url 非空  ；readed = true 审核已通过 
	 * @param assessmentReport
	 * @return
	 */
	public Boolean getHasUnitAssessmentByUnitAssessment(AssessmentReport assessmentReport){
		Boolean hasUnitAssessment = false;
		if(assessmentReport != null){
			if(assessmentReport.getUrl()!=null && UaReportStatus.REVIEWED == assessmentReport.getReaded()){
				hasUnitAssessment = true;
			}
		}
		return hasUnitAssessment;
	}
}
