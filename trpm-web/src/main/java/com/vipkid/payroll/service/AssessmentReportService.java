package com.vipkid.payroll.service;

import java.sql.Timestamp;
import java.util.Date;

import com.vipkid.http.service.AssessmentHttpService;
import com.vipkid.http.vo.StudentUnitAssessment;
import org.apache.commons.lang.StringUtils;
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
    @Autowired
    private AssessmentHttpService assessmentHttpService;

    public AssessmentReport getAssessmentReportByOnlineClass(Long onlineClassId, Date scheduledDateTime, Long studentId,
                                                             String lessonSerialNumber) {
        AssessmentReport assessmentReport = null;
        if (StringUtils.isNotEmpty(lessonSerialNumber)) {
            // 时间大于2016-01-29 22:27:56 通过OnlineClassId查询，小于2016-01-29 22:27:56通过student he lessonSerialNumber
            if (assertReportOldDate.before(scheduledDateTime)) {
                assessmentReport = getUnitAssessmentByOnlineClassId(onlineClassId);
            } else {
                assessmentReport = getUnitAssessmentByStudentIdAndLessonSerialNumber(studentId, lessonSerialNumber);
            }
        }
        if (assessmentReport == null) {
            // 查询下新UA
            StudentUnitAssessment studentUnitAssessment = assessmentHttpService
                    .findStudentUnitAssessmentByOnlineClassId(onlineClassId);
            if (null != studentUnitAssessment) {
                assessmentReport = new AssessmentReport();
                assessmentReport.setOnlineClassId(onlineClassId);
                assessmentReport.setName(lessonSerialNumber);
                assessmentReport.setStudentId(studentId);
                assessmentReport.setHasUnitAssessment(false);
                if (1 == studentUnitAssessment.getSubmitStatus()) {
                    assessmentReport.setHasUnitAssessment(true);
                    assessmentReport.setCreateDateTime(new Timestamp(studentUnitAssessment.getCreateDateTime().getTime()));
                }
                return assessmentReport;
            }
        }
        if (assessmentReport == null) {
            assessmentReport = new AssessmentReport();
            assessmentReport.setOnlineClassId(onlineClassId);
            assessmentReport.setName(lessonSerialNumber);
            assessmentReport.setStudentId(studentId);
            assessmentReport.setHasUnitAssessment(false);
        }

        return assessmentReport;
    }

    /**
     * 查询在线课程是否已经上传报告（老的UA报告）
     * 
     * @param onlineClassId
     * @return
     */
    public AssessmentReport getUnitAssessmentByOnlineClassId(Long onlineClassId) {
        Boolean hasUnitAssessment = false;
        AssessmentReport assessmentReport = null;
        if (onlineClassId != null) {
            assessmentReport = assessmentReportDao.findReportByClassId(onlineClassId);
            hasUnitAssessment = getHasUnitAssessmentByUnitAssessment(assessmentReport);
            if (assessmentReport == null) {
                assessmentReport = new AssessmentReport();
                assessmentReport.setOnlineClassId(onlineClassId);
            }
            assessmentReport.setHasUnitAssessment(hasUnitAssessment);
        }
        logger.info("查询在线课程是否已经上传报告  onlineClassId = {} ，hasUnitAssessment = {}", onlineClassId, hasUnitAssessment);
        return assessmentReport;
    }

    /**
     * 查询学生课程是否已经上传报告（老的UA报告）
     * 
     * @param studentId
     * @param lessonSerialNumber
     * @return
     */
    public AssessmentReport getUnitAssessmentByStudentIdAndLessonSerialNumber(Long studentId,
                                                                              String lessonSerialNumber) {
        Boolean hasUnitAssessment = false;
        AssessmentReport assessmentReport = null;
        if (studentId != null && lessonSerialNumber != null) {
            assessmentReport = assessmentReportDao.findReportByStudentIdAndName(lessonSerialNumber, studentId);
            hasUnitAssessment = getHasUnitAssessmentByUnitAssessment(assessmentReport);
            if (assessmentReport == null) {
                assessmentReport = new AssessmentReport();
                assessmentReport.setStudentId(studentId);
                assessmentReport.setName(lessonSerialNumber);
            }
            assessmentReport.setHasUnitAssessment(hasUnitAssessment);
        }
        logger.info("查询学生课程是否已经上传报告  studentId = {},lessonSerialNumber = {}, hasUnitAssessment = {}", studentId,
                lessonSerialNumber, hasUnitAssessment);
        return assessmentReport;
    }

    /**
     * 查询上传报告 是否有效 url 非空 ；readed = true 审核已通过
     * 
     * @param assessmentReport
     * @return
     */
    public Boolean getHasUnitAssessmentByUnitAssessment(AssessmentReport assessmentReport) {
        Boolean hasUnitAssessment = false;
        if (assessmentReport != null) {
            if (assessmentReport.getUrl() != null && UaReportStatus.REVIEWED == assessmentReport.getReaded()) {
                hasUnitAssessment = true;
            }
        }
        return hasUnitAssessment;
    }
}
