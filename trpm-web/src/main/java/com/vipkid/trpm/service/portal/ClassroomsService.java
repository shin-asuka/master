package com.vipkid.trpm.service.portal;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.vipkid.http.service.AssessmentHttpService;
import com.vipkid.http.vo.OnlineClassVo;
import com.vipkid.http.vo.StudentUnitAssessment;
import com.vipkid.trpm.constant.ApplicationConstant.CourseType;
import com.vipkid.trpm.constant.ApplicationConstant.LoginType;
import com.vipkid.trpm.constant.ApplicationConstant.UaReportStatus;
import com.vipkid.trpm.dao.AssessmentReportDao;
import com.vipkid.trpm.dao.CourseDao;
import com.vipkid.trpm.dao.DemoReportDao;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherApplicationDao;
import com.vipkid.trpm.dao.TeacherCommentDao;
import com.vipkid.trpm.dao.TeacherPageLoginDao;
import com.vipkid.trpm.entity.AssessmentReport;
import com.vipkid.trpm.entity.Course;
import com.vipkid.trpm.entity.DemoReport;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherComment;
import com.vipkid.trpm.util.DateUtils;

/**
 * classrooms显示规则：
 * <p>
 * 1.BOOKED课程全部显示
 * </p>
 * <p>
 * 2.FINISHED课程，AS_SCHEDULED和STUDENT_NO_SHOW全部显示
 * </p>
 * <p>
 * 3.INVALID课程，只显示online_class_id最小的课程记录
 * </p>
 * <p>
 * 4.FINISHED课程，TEACHER_CANCELLATION，TEACHER_CANCELLATION_24H，TEACHER_NO_SHOW_2H <BR/>
 * TEACHER_NO_SHOW，STUDENT_IT_PROBLEM <BR/>
 * TEACHER_IT_PROBLEM，SYSTEM_PROBLEM 同一时间点都只显示一条记录
 * </p>
 * <p>
 * 5.如果同一时间点SCHEDULED_DATE_TIME既有INVALID和BOOKED，则只显示BOOKED课程
 * </p>
 * <p>
 * 6.如果同一时间点SCHEDULED_DATE_TIME既有STUDENT_NO_SHOW和BOOKED，则只显示BOOKED课程
 * </p>
 * <p>
 * 按时间倒序，分页时需按当前日期定位到指定的页面
 * </p>
 */
@Service
public class ClassroomsService {

    @Autowired
    private OnlineClassDao onlineClassDao;

    @Autowired
    private LessonDao lessonDao;

    @Autowired
    private AssessmentReportDao assessmentReportDao;

    @Autowired
    private DemoReportDao demoReportDao;

    @Autowired
    private TeacherCommentDao teacherCommentDao;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private TeacherPageLoginDao teacherLoginTypeDao;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    @Autowired
	private AssessmentHttpService assessmentHttpService;
    
    @Autowired
    private TeacherPageLoginDao teacherPageLoginDao;
    
    /**
     * 获取Major classrooms的分页总行数
     *
     * @param teacher
     * @param monthOfYear
     * @return Map<String, Object>
     */
    public Map<String, Object> majorTotal(Teacher teacher, String monthOfYear) {
        Map<String, Object> modelMap = Maps.newHashMap();

        modelMap.put("totalLine",
                onlineClassDao.countMajorBy(teacher.getId(), teacher.getTimezone(), monthOfYear));

        return modelMap;
    }

    /**
     * 分页查询Major课程列表
     *
     * @param teacher
     * @param monthOfYear
     * @param curPage
     * @param linePerPage
     * @return Map<String, Object>
     */
    public Map<String, Object> majorList(Teacher teacher, String monthOfYear, int curPage,
            int linePerPage) {
        Map<String, Object> modelMap = Maps.newHashMap();

        List<Map<String, Object>> dataList = onlineClassDao.findMajorBy(teacher.getId(),
                teacher.getTimezone(), monthOfYear, curPage, linePerPage);
        modelMap.put("dataList", dataList);

        return modelMap;
    }

    /**
     * 获取Practicum classrooms的分页总行数
     * 
     * @param teacher
     * @param monthOfYear
     * @return Map<String, Object>
     */
    public Map<String, Object> practicumTotal(Teacher teacher, String monthOfYear) {
        Map<String, Object> modelMap = Maps.newHashMap();

        modelMap.put("totalLine", onlineClassDao.countPracticumBy(teacher.getId(),
                teacher.getTimezone(), monthOfYear));

        return modelMap;
    }

    /**
     * 分页查询Practicum课程列表
     *
     * @param teacher
     * @param monthOfYear
     * @param curPage
     * @param linePerPage
     * @return Map<String, Object>
     */
    public Map<String, Object> practicumList(Teacher teacher, String monthOfYear, int curPage,
            int linePerPage) {
        Map<String, Object> modelMap = Maps.newHashMap();

        modelMap.put("dataList", onlineClassDao.findPracticumBy(teacher.getId(),
                teacher.getTimezone(), monthOfYear, curPage, linePerPage));

        return modelMap;
    }

    /**
     * 处理classrooms逻辑
     *
     * @param teacher
     * @param monthOfYear
     * @param courseType
     * @return Map<String, Object>
     */
    public Map<String, Object> doClassrooms(Teacher teacher, String monthOfYear, String courseType,
            int linePerPage) {
        Map<String, Object> modelMap = Maps.newHashMap();

        if (CourseType.isPracticum(courseType)) {
            modelMap.putAll(practicumTotal(teacher, monthOfYear));

            /* 计算当前日期在第几页 */
            modelMap.put("inPage", practicumInPage(teacher, monthOfYear, linePerPage));

            /* 查询统计数据 */
            modelMap.put("statFinishTypeList", onlineClassDao.findStatPracticumFinishTypeBy(
                    teacher.getId(), teacher.getTimezone(), monthOfYear));
        } else {
            modelMap.putAll(majorTotal(teacher, monthOfYear));

            /* 计算当前日期在第几页 */
            modelMap.put("inPage", majorInPage(teacher, monthOfYear, linePerPage));

            /* 查询统计数据 */
            modelMap.put("statFinishTypeList", onlineClassDao.findStatMajorFinishTypeBy(
                    teacher.getId(), teacher.getTimezone(), monthOfYear));
        }

        return modelMap;
    }

    /**
     * 计算Major课程当前日期所在的分页
     *
     * @param teacher
     * @param monthOfYear
     * @param linePerPage
     * @return int
     */
    public int majorInPage(Teacher teacher, String monthOfYear, int linePerPage) {
        int count = onlineClassDao.countMajorFromNowBy(teacher.getId(), teacher.getTimezone(),
                monthOfYear);
        return computePage(count, linePerPage);
    }

    /**
     * 计算分页
     *
     * @param totalCount
     * @param linePerPage
     * @return int
     */
    private int computePage(int totalCount, int linePerPage) {
        int page = (totalCount % linePerPage == 0) ? (totalCount / linePerPage)
                : (Math.abs(totalCount / linePerPage) + 1);
        return (0 == page) ? 1 : page;
    }

    /**
     * 计算Practicum课程当前日期所在的分页
     *
     * @param teacher
     * @param monthOfYear
     * @param linePerPage
     * @return int
     */
    public int practicumInPage(Teacher teacher, String monthOfYear, int linePerPage) {
        int count = onlineClassDao.countPracticumFromNowBy(teacher.getId(), teacher.getTimezone(),
                monthOfYear);
        return computePage(count, linePerPage);
    }

    /**
     * 处理practicumReport逻辑
     *
     * @param serialNumber
     * @param onlineClassId
     * @param lessonId
     * @param studentId
     * @return
     */
    public Map<String, Object> practicumReport(String serialNumber, long onlineClassId,
            long lessonId, long studentId, long scheduledTime) {
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.put("viewName", "practicum_report_entry");
        /** Practicum课按照onlineClassId进行查询 */
        int count = teacherApplicationDao.countApplicationByOlineclassId(onlineClassId);
        if (0 != count) {
            modelMap.put("lifeCycle", "(Submitted)");
        } else {
            modelMap.put("lifeCycle", "(empty)");
        }
        return modelMap;
    }

    /**
     * 处理showReport逻辑
     *
     * @param serialNumber
     * @param onlineClassId
     * @param lessonId
     * @param studentId
     * @return
     */
    public Map<String, Object> doShowReport(String serialNumber, long onlineClassId, long lessonId,
            long studentId, long scheduledTime) {
        /*
         * 课程开始标记:用于判断课程是否开始，仅仅UAReport在课程未开始时显示(方便老师下载模板)，其他报告课程未开始不显示 开始:true,未开始:false
         */
        boolean isClassStarted = System.currentTimeMillis() >= scheduledTime;
        // UAReport显示
        if (isUaReport(serialNumber)) {
            return uaReportEntry(serialNumber, onlineClassId, studentId);
            // DemoReport显示
        } else if (isDemoReport(serialNumber) && isClassStarted) {
            return demoReportEntry(serialNumber, onlineClassId, studentId);
            // 其他显示
        } else if (isClassStarted) {
            return normalReportEntry(lessonId, onlineClassId, studentId);
            // 为空显示
        } else {
            Map<String, Object> modelMap = Maps.newHashMap();
            modelMap.put("viewName", "report_emty");
            return modelMap;
        }
    }

    /**
     * 是否是DemoReport的课程
     *
     * @param serialNumber
     * @return boolean
     */
    public boolean isDemoReport(String serialNumber) {
        return serialNumber.startsWith("A");
    }

    /**
     * 显示DemoReport链接
     *
     * @param onlineClassId
     * @param studentId
     * @return Map<String, Object>
     */
    public Map<String, Object> demoReportEntry(String serialNumber, long onlineClassId,
            long studentId) {
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.put("viewName", "demo_report_entry");
        DemoReport demoReport =
                demoReportDao.findByStudentIdAndOnlineClassId(studentId, onlineClassId);

        if (null != demoReport) {
            String lifeCycle = demoReport.getLifeCycle();

            if (StringUtils.isEmpty(lifeCycle))
                lifeCycle = "UNFINISHED";

            switch (lifeCycle) {
                case "UNFINISHED":
                    lifeCycle = "(unsubmitted)";
                    break;
                case "SUBMITTED":
                    lifeCycle = "(submitted)";
                    break;
                case "CONFIRMED":
                    lifeCycle = "(confirmed)";
                    break;
            }

            modelMap.put("lifeCycle", lifeCycle);
        }

        return modelMap;
    }

    /**
     * 是否是UaReport的课程
     *
     * @param serialNumber
     * @return boolean
     */
    public boolean isUaReport(String serialNumber) {
        return ((serialNumber.startsWith("C") || serialNumber.startsWith("MC"))
                && (serialNumber.endsWith("6") || serialNumber.endsWith("12")));
    }

    /**
     * 显示UaReport链接
     *
     * @param serialNumber
     * @param onlineClassId
     * @param studentId
     * @return Map<String, Object>
     */
    public Map<String, Object> uaReportEntry(String serialNumber, long onlineClassId,
            long studentId) {
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.put("viewName", "ua_report_entry");

        modelMap.put("serialNumber", serialNumber);
        modelMap.put("onlineClassId", onlineClassId);
        modelMap.put("studentId", studentId);
        AssessmentReport assessmentReport = null;
        long schedDateTime =
                onlineClassDao.findById(onlineClassId).getScheduledDateTime().getTime();
        if (DateUtils.isSearchById(schedDateTime)) {
            assessmentReport = assessmentReportDao.findReportByClassId(onlineClassId);
        } else {
            assessmentReport = assessmentReportDao.findReportByStudentIdAndName(serialNumber, studentId);
        }
        Integer isNewUa = 0;
        String lifeCycle = "";  //旧版UA
        if (null == assessmentReport || StringUtils.isEmpty(assessmentReport.getUrl())) {
            //odelMap.put("lifeCycle", "(empty)");
            lifeCycle = "(empty)";
        } else if (assessmentReport.getReaded() == UaReportStatus.RESUBMIT) {
            //modelMap.put("lifeCycle", "(Resubmit)");
            lifeCycle = "(Resubmit)";
        }
        
        if(null == assessmentReport){ //基于UA系统查询在线课程UA填写情况
        	OnlineClassVo onlineClassVo = new OnlineClassVo();
        	onlineClassVo.getIdList().add(onlineClassId);
			List<StudentUnitAssessment> suaList = assessmentHttpService.findOnlineClassVo(onlineClassVo);
			if(CollectionUtils.isNotEmpty(suaList)){
				StudentUnitAssessment sua = suaList.get(0);
				if(sua.getSubmitStatus() == 1){
					lifeCycle = "(submitted)";
				}else{
					lifeCycle = "(saved)";
				}
			}else{
				lifeCycle = "(empty)";
			}
			isNewUa = 1;
        }
        modelMap.put("isNewUa", isNewUa);
        modelMap.put("lifeCycle", lifeCycle);
        return modelMap;
    }

    /**
     * 显示NormalReport链接
     *
     * @param lessonId
     * @param onlineClassId
     * @param studentId
     * @return Map<String, Object>
     */
    public Map<String, Object> normalReportEntry(long lessonId, long onlineClassId,
            long studentId) {
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.put("viewName", "normal_report_entry");

        modelMap.put("lessonId", lessonId);
        modelMap.put("onlineClassId", onlineClassId);
        modelMap.put("studentId", studentId);

        TeacherComment teacherComment =
                teacherCommentDao.findByStudentIdAndOnlineClassId(studentId, onlineClassId);

        if (null == teacherComment || StringUtils.isBlank(teacherComment.getTeacherFeedback())) {
            modelMap.put("lifeCycle", "(empty)");
        }

        return modelMap;
    }

    /**
     * 处理显示materials逻辑
     *
     * @param lessonId
     * @param courseId
     * @return Map<String, Object>
     */
    public Map<String, Object> doShowMaterials(long lessonId) {
        Map<String, Object> modelMap = Maps.newHashMap();

        Course course = courseDao.findByLessonId(lessonId);
        if (null != course) {
            modelMap.put("lessonList", lessonDao.findByCourseId(course.getId()));
        }

        return modelMap;
    }

}
