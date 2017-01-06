package com.vipkid.rest.portal.service.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.google.api.client.util.Maps;
import com.vipkid.file.service.QNService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Lists;
import com.vipkid.enums.OnlineClassEnum.CourseType;
import com.vipkid.http.service.AssessmentHttpService;
import com.vipkid.http.vo.OnlineClassVo;
import com.vipkid.http.vo.StudentUnitAssessment;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.rest.portal.model.ClassroomDetail;
import com.vipkid.rest.portal.model.ClassroomsData;
import com.vipkid.rest.portal.service.ClassroomsRestService;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.constant.ApplicationConstant.UaReportStatus;
import com.vipkid.trpm.dao.AssessmentReportDao;
import com.vipkid.trpm.dao.CourseDao;
import com.vipkid.trpm.dao.DemoReportDao;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.AssessmentReport;
import com.vipkid.trpm.entity.Course;
import com.vipkid.trpm.entity.DemoReport;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.teachercomment.TeacherComment;
import com.vipkid.trpm.service.portal.ClassroomsService;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.util.DateUtils;

@Service
public class ClassroomsRestServiceImpl implements ClassroomsRestService{
	private static final int LINE_PER_PAGE = PropertyConfigurer.intValue("page.linePerPage");

	private static final Logger logger = LoggerFactory.getLogger(ClassroomsRestServiceImpl.class);

	@Autowired
	private LoginService loginService;

	@Autowired
	private ClassroomsService classroomsService;

	@Autowired
	private TeacherDao teacherdao;

	@Autowired
	private TeacherApplicationDao teacherApplicationDao;

	@Autowired
	private DemoReportDao demoReportDao;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private OnlineClassDao onlineClassDao;

	@Autowired
	private AssessmentReportDao assessmentReportDao;

	@Autowired
	private AssessmentHttpService assessmentHttpService;

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private LessonDao lessonDao;

	@Autowired
	private QNService qnService;

	public Map<String, Object> getClassroomsData(long teacherId, int offsetOfMonth, String courseType, int page) {
		if (!CourseType.isPracticum(courseType)) {// 只要不是"PRACTICUM"，就赋值"MAJOR"
			courseType = "MAJOR";
		}
		Map<String, Object> result = null;

		ClassroomsData classroomsData = new ClassroomsData();

		Teacher teacher = loginService.getTeacher();
		
		if(teacher == null){
			return ApiResponseUtils.buildErrorResp(1002, "没有权限请求此接口");
		}
		
		if(teacher.getId() != teacherId){
			return ApiResponseUtils.buildErrorResp(1003, "teacherId非法");
		}

		classroomsData.setTeacherId(teacherId);

		/* 生成monthOfYear */
		classroomsData.setMonthOfYear(DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_MMM_YYYY_US));

		/* 生成totalPage, curPage与stateList三个参数 */
		String monthOfYear = DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_YM);
		Map<String, Object> threeDate = doClassrooms(teacher, monthOfYear, courseType, LINE_PER_PAGE, page);
		if (null == threeDate) {
			return ApiResponseUtils.buildErrorResp(1001, "服务器端错误");
		}
		int totalPage = (int) threeDate.get("totalPage");
		int curPage = (int) threeDate.get("curPage");
		List<Map<String, Object>> stateList = (List<Map<String, Object>>) threeDate.get("stateList");
		// 添加totalPage, curPage与stateList三个参数
		classroomsData.setTotalPage(totalPage);
		classroomsData.setCurPage(curPage);
		classroomsData.setStateList(stateList);

		/* 生成dataList */
		List<ClassroomDetail> dataList = getDataList(courseType, teacher, monthOfYear, curPage);
		classroomsData.setDataList(dataList);

		/* 生成tagList */
		List<Map<String, Object>> tagList = getTagList(courseType, teacher.getId());
		classroomsData.setTagList(tagList);

		result = ApiResponseUtils.buildSuccessDataResp(classroomsData);
		return result;
	}

	public Map<String, Object> getClassroomsMaterialByLessonId(long lessonId) {
		Map<String, Object> result = null;
		Map<String, Object> data = Maps.newHashMap();

		Long preLessonId = null;
		Long nextLessonId = null;
		String preLessonSerialNum = null;
		String nextLessonSerialNum = null;

		Course course = courseDao.findByLessonId(lessonId);
		List<Lesson> lessonList = null;
		if (null != course) {
			lessonList = lessonDao.findByCourseId(course.getId());
			int lessonListSize = lessonList.size();

			int index = 0;
			for (index = 0; index < lessonListSize; index++) {
				Lesson eachLesson = lessonList.get(index);
				if (eachLesson.getId() == lessonId) {
					break;
				}
			}
			if (index < lessonListSize) {// 如果上面的循环break退出，即lessonList里面有当前的lessonId对应的课程
				if (index < lessonListSize - 1) {// 如果不是最后一个
					Lesson nextLesson = lessonList.get(index + 1);
					nextLessonId = nextLesson.getId();
					nextLessonSerialNum = nextLesson.getSerialNumber();
				} else {
					Lesson nextLesson = lessonList.get(index);// 把当前这个当成下一个
					nextLessonId = nextLesson.getId();
					nextLessonSerialNum = nextLesson.getSerialNumber();
				}
				if (index > 0) {
					Lesson preLesson = lessonList.get(index - 1);
					preLessonId = preLesson.getId();
					preLessonSerialNum = preLesson.getSerialNumber();
				} else {
					Lesson preLesson = lessonList.get(index);
					preLessonId = preLesson.getId();// 把当前这个当成前一个
					preLessonSerialNum = preLesson.getSerialNumber();
				}
			} else {
				logger.error("lessonId为{}的课程，所属的courseId为{}，但此course下的所有lesson不包括此lesson", lessonId, course);
			}
		}

		data.put("preLessonId", preLessonId);
		data.put("nextLessonId", nextLessonId);
		data.put("preLessonSerialNum", preLessonSerialNum);
		data.put("nextLessonSerialNum", nextLessonSerialNum);

		Lesson lesson = lessonDao.findById(lessonId);
		if(null == lesson){
			return ApiResponseUtils.buildErrorResp(1001, "lessonId非法");
		}

		data.put("lessonId", lesson.getId());

		String lessonName = lesson.getName();
		if (null != lessonName) {
			lessonName = lessonName.replace("\n", "").replace("\t", "");
		}
		data.put("lessonName", lessonName);

		data.put("lessonSerialNumber", lesson.getSerialNumber());

		String lessonObjective = lesson.getObjective();
		if (null != lessonObjective) {
			lessonObjective = lessonObjective.replace("/t", "").replace("/n", "");
		}
		data.put("lessonObjective", lesson.getObjective());

		String lessonVocabularies = lesson.getVocabularies();
		if (null != lessonVocabularies) {
			lessonVocabularies = lessonVocabularies.replace("\n", "").replace("\t", "");
		}
		data.put("lessonVocabularies", lessonVocabularies);

		String sentencePatterns = lesson.getSentencePatterns();
//		String[] sentencePatternsGroup = null;
//		if (null != sentencePatterns) {
//			sentencePatternsGroup = sentencePatterns.replace("\n", "").replace("\t", "").replace("<br>", "<br/>")
//					.split("<br/>");
//		}
		if(sentencePatterns != null){
			sentencePatterns = sentencePatterns.replace("\n", "").replace("\t", "");
		}
		data.put("lessonSentencePatterns", sentencePatterns);

		result = ApiResponseUtils.buildSuccessDataResp(data);
		return result;
	}

	/**
	 * 处理classrooms逻辑。
	 *
	 * @param teacher
	 * @param monthOfYear
	 * @param courseType
	 * @param page
	 * @author zhangbole
	 * @return Map<String, Object>
	 */
	private Map<String, Object> doClassrooms(Teacher teacher, String monthOfYear, String courseType, int linePerPage,
			int page) {
		Map<String, Object> modelMap = Maps.newHashMap();

		if (CourseType.isPracticum(courseType)) {
			Map<String, Object> totalLineMap = classroomsService.practicumTotal(teacher, monthOfYear);
			int totalLine = (int) totalLineMap.get("totalLine");
			int totalPage = 1;
			if (linePerPage != 0) {
				totalPage = (totalLine + linePerPage -1) / linePerPage;
			}
			modelMap.put("totalPage", totalPage);

			if (page < 0 || page > totalPage) {
				page = 0;
			}
			if (page == 0) {
				/* 如果page为默认值0，就设page为当前日期对应的page */
				page = classroomsService.practicumInPage(teacher, monthOfYear, linePerPage);//
			}
			modelMap.put("curPage", page);

			/* 查询统计数据 */
			List<Map<String, Object>> stateList = onlineClassDao.findStatPracticumFinishTypeBy(teacher.getId(), teacher.getTimezone(), monthOfYear);
			rebuildStateList(stateList);
			modelMap.put("stateList", stateList);
		} else {
			Map<String, Object> totalLineMap = classroomsService.majorTotal(teacher, monthOfYear);
			int totalLine = (int) totalLineMap.get("totalLine");
			int totalPage = 1;
			if (linePerPage != 0) {
				totalPage = (totalLine + linePerPage -1) / linePerPage;
			}
			modelMap.put("totalPage", totalPage);

			if (page < 0 || page > totalPage) {
				page = 0;
			}
			if (page == 0) {
				/* 如果page为默认值0，就设page为当前日期对应的page */
				page = classroomsService.majorInPage(teacher, monthOfYear, linePerPage);//
			}
			modelMap.put("curPage", page);

			/* 查询统计数据 */
			List<Map<String, Object>> stateList = onlineClassDao.findStatMajorFinishTypeBy(teacher.getId(), teacher.getTimezone(), monthOfYear);
			rebuildStateList(stateList);
			modelMap.put("stateList",stateList);
		}

		return modelMap;
	}
	
	//修复SQL查询出的stateList数据缺陷
	private void rebuildStateList(List<Map<String, Object>> stateList){
		if(null != stateList && !stateList.isEmpty()){
			int i = 0;
			int size = stateList.size();
			for (; i < size; i++) {
				String finishType = (String) stateList.get(i).get("finishType");
				if(StringUtils.isEmpty(finishType)){
					break;
				}
			}
			if(i < size){
				stateList.remove(i);
			}
		}
	}
	
	/**
	 * 生成并重新包装classrooms接口的dataList属性
	 * 
	 * @author zhangbole
	 */
	private List<ClassroomDetail> getDataList(String courseType, Teacher teacher, String monthOfYear,
			int curPage) {
		List<ClassroomDetail> result = Lists.newArrayList();
		Map<String, Object> dataListMap = null;
		if (CourseType.isPracticum(courseType)) {
			dataListMap = classroomsService.practicumList(teacher, monthOfYear, curPage, LINE_PER_PAGE);
		} else {
			dataListMap = classroomsService.majorList(teacher, monthOfYear, curPage, LINE_PER_PAGE);
		}
		List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataListMap.get("dataList");// 复用以前代码产生的dataList，后面对其重新包装
		if (dataList == null)
			return null;
		int id = 0;//加一个id方便前端排序
		for (Map<String, Object> eachMap : dataList) {
			ClassroomDetail classroomDetail = new ClassroomDetail();
			classroomDetail.setId(id);
			classroomDetail.setFinishType((String) eachMap.get("finishType"));
			classroomDetail.setIsPaidTrail((int) eachMap.get("isPaidTrail"));
			classroomDetail.setLearningCycleId((long) eachMap.get("learningCycleId"));
			classroomDetail.setLessonId((long) eachMap.get("lessonId"));
			classroomDetail.setLessonName((String) eachMap.get("lessonName"));
			classroomDetail.setLessonSerialNumber((String) eachMap.get("serialNumber"));
			classroomDetail.setOnlineClassId((long) eachMap.get("id"));
			classroomDetail.setShortNotice((int) eachMap.get("shortNotice"));
			classroomDetail.setStatus((String) eachMap.get("status"));
			classroomDetail.setStudentId((long) eachMap.get("studentId"));
			classroomDetail.setStudentName((String) eachMap.get("englishName"));
			classroomDetail.setTeacherId((long) eachMap.get("teacherId"));

			String serialNumber = (String) eachMap.get("serialNumber");
			if(StringUtils.isNotEmpty(serialNumber)){
				boolean isPrevipLesson = false;
				int index = serialNumber.lastIndexOf("-");
				serialNumber = serialNumber.substring(0,index-4);
				if (serialNumber.contains("MC-L1-")){
					isPrevipLesson = true;
					classroomDetail.setIsPrevipLesson(isPrevipLesson);
				}

				if(isPrevipLesson){
					Map<String,Object> showUrl = qnService.getShowUrl(serialNumber);
					Map<String,Object> downloadUrl = qnService.getDownloadUrl(serialNumber);
					String lyricsShowUrl = (String) showUrl.get("lyricsShowUrl");
					String videoShowUrl = (String) showUrl.get("videoShowUrl");
					String videoDownloadUrl = (String)downloadUrl.get("videoDownloadUrl");
					classroomDetail.setLyricsShowUrl(lyricsShowUrl);
					classroomDetail.setVideoShowUrl(videoShowUrl);
					classroomDetail.setVideoDownloadUrl(videoDownloadUrl);
				}
			}

			Timestamp timeStamp = (Timestamp) eachMap.get("scheduledDateTime");
			Date date = new Date(timeStamp.getTime());
			DateFormat df = new SimpleDateFormat("MMM dd yyyy, hh:mma", Locale.ENGLISH);
			df.setTimeZone(TimeZone.getTimeZone(teacher.getTimezone())); 
			String scheduledDateTime = df.format(date);
			classroomDetail.setScheduledDateTime(scheduledDateTime);

			addReportTypeAndStatus(eachMap, date, classroomDetail);

			result.add(classroomDetail);
			id ++;
		}
		return result;
	}

	/**
	 * 生成classrooms接口的tagList属性
	 * 
	 */
	private List<Map<String, Object>> getTagList(String courseType, long teacherId) {
		List<Map<String, Object>> tagList = Lists.newArrayList();
		Map<String, Object> coursesTagMap = Maps.newHashMap();
		Map<String, Object> practicumTagMap = Maps.newHashMap();
		coursesTagMap.put("tagName", "Courses");
		practicumTagMap.put("tagName", "Mock Class");//Practicum改为Mock Class
		if (CourseType.isPracticum(courseType)) {
			coursesTagMap.put("currently", new Boolean(false));
			practicumTagMap.put("currently", new Boolean(true));
		} else {
			coursesTagMap.put("currently", new Boolean(true));
			practicumTagMap.put("currently", new Boolean(false));
		}
		tagList.add(coursesTagMap);
		if (loginService.isPe(teacherId)) {
			tagList.add(practicumTagMap);
		}
		return tagList;
	}

	private void addReportTypeAndStatus(Map<String, Object> data, Date scheduledDateTime,
			ClassroomDetail classroomsEachClassInfo) {
		int reportType = 0;
		int reportStatus = 0;

		long onlineClassId = (long) data.get("id");
		String lessonSerialNumber = (String) data.get("serialNumber");
		long studentId = (long) data.get("studentId");

		boolean isClassStarted = new Date().after(scheduledDateTime);
        Lesson lesson = lessonDao.findById(classroomsEachClassInfo.getLessonId());
		if (lessonSerialNumber.startsWith("P")) {// 如果是PracticumReport
			reportType = ReportType.PracticumReport.getCode();// 代表是PracticumReport
			reportStatus = getPracticumReportStatus(onlineClassId);
		} else if (classroomsService.isUaReport(lesson)) {// 如果是UaReport
			if (isOldUaReport(onlineClassId, studentId, lessonSerialNumber)) {
				reportType = ReportType.OldUAReport.getCode();// 代表是旧UaReport
				Map<String, Object> map = getOldUaReportStatus(onlineClassId, studentId, lessonSerialNumber);
				reportStatus = (int) map.get("oldUaReportStatus");
				classroomsEachClassInfo.setUaReportUrl((String)map.get("UaReportUrl"));// 如果是老UA，会多一个UaReportUrl参数
			} else {
				reportType = ReportType.NewUAReprot.getCode();// 代表是新UaReport
				reportStatus = getNewUaReportStatus(onlineClassId);
			}
		} else if (classroomsService.isDemoReport(lessonSerialNumber) && isClassStarted) {// 如果是demoReport
			reportType = ReportType.DemoReport.getCode();// 代表是demoReport
			reportStatus = getDemoReportStatus(studentId, onlineClassId);

		} else if (isClassStarted) {
			reportType = ReportType.Feedback.getCode();// 代表是normalReport,也就是feedback
			reportStatus = getNormalReportStatus(studentId, onlineClassId);

		} else {
			reportType = ReportType.EmptyReport.getCode();// 代表是空报告，不显示
			reportStatus = 0;
		}

		classroomsEachClassInfo.setReportType(reportType);
		classroomsEachClassInfo.setReportStatus(reportStatus);
	}

	private int getPracticumReportStatus(long onlineClassId) {
		int count = teacherApplicationDao.countApplicationByOlineclassId(onlineClassId);
		if (0 != count) {
			return 1;// 代表状态为(submitted)
		} else {
			return 0;// 代表状态为(empty)
		}
	}

	private boolean isOldUaReport(long onlineClassId, long studentId, String serialNumber) {
		AssessmentReport assessmentReport = null;
		long scheduleDateTime = onlineClassDao.findById(onlineClassId).getScheduledDateTime().getTime();// 此方法可以提速，用时间来判断是不是新UA，如10月份之后的都是新UA
		if (DateUtils.isSearchById(scheduleDateTime)) {
			assessmentReport = assessmentReportDao.findReportByClassId(onlineClassId);
		} else {
			assessmentReport = assessmentReportDao.findReportByStudentIdAndName(serialNumber, studentId);
		}
		if (null != assessmentReport && StringUtils.isNotEmpty(assessmentReport.getUrl())) {
			return true;
		} else {
			return false;
		}
	}

	private Map<String, Object> getOldUaReportStatus(long onlineClassId, long studentId, String serialNumber) {
		Map<String, Object> result = Maps.newHashMap();
		int oldUaReportStatus = 0;
		AssessmentReport assessmentReport = null;
		long schedDateTime = onlineClassDao.findById(onlineClassId).getScheduledDateTime().getTime();
		if (DateUtils.isSearchById(schedDateTime)) {
			assessmentReport = assessmentReportDao.findReportByClassId(onlineClassId);
		} else {
			assessmentReport = assessmentReportDao.findReportByStudentIdAndName(serialNumber, studentId);
		}
		if (null == assessmentReport || StringUtils.isEmpty(assessmentReport.getUrl())) {
			logger.warn("新UA上线后，老UA的状态不应为empty。");
			oldUaReportStatus = 0;// 表示(empty)
		} else {
			String url = assessmentReport.getUrl();
			result.put("UaReportUrl", url);
			if (assessmentReport.getReaded() == UaReportStatus.RESUBMIT) {
				oldUaReportStatus = 2;// 表示(Resubmit)
			} else {
				oldUaReportStatus = 1;// 表示("")，即已提交
			}
		}
		result.put("oldUaReportStatus", oldUaReportStatus);
		return result;
	}

	private int getNewUaReportStatus(long onlineClassId) {
		OnlineClassVo onlineClassVo = new OnlineClassVo();
		onlineClassVo.getIdList().add(onlineClassId);
		List<StudentUnitAssessment> suaList = assessmentHttpService.findOnlineClassVo(onlineClassVo);
		if (CollectionUtils.isNotEmpty(suaList)) {
			StudentUnitAssessment sua = suaList.get(0);
			if (sua.getSubmitStatus() == 1) {
				return 2;// 表示(submitted)
			} else if (sua.getIsRefillin() == 1) {
				return 0;// 表示(empty)
			} else {
				return 1;// 表示(saved)
			}
		} else {
			return 0;// 表示(empty)
		}
	}

	private int getDemoReportStatus(long studentId, long onlineClassId) {
		DemoReport demoReport = demoReportDao.findByStudentIdAndOnlineClassId(studentId, onlineClassId);

		if (null != demoReport) {
			String lifeCycle = demoReport.getLifeCycle();

			if (StringUtils.isEmpty(lifeCycle))
				lifeCycle = "UNFINISHED";

			switch (lifeCycle) {
			case "UNFINISHED":
				return 0;// 表示(unsubmitted)
			case "SUBMITTED":
				return 1;// 表示(submitted)
			case "CONFIRMED":
				return 2;// 表示(confirmed)
			default:
				return 0;// 表示(unsubmitted)
			}
		}
		return 0;
	}

	private int getNormalReportStatus(long studentId, long onlineClassId) {
		TeacherComment teacherComment = teacherService.findByStudentIdAndOnlineClassId(studentId, onlineClassId);
		if (null == teacherComment || StringUtils.isBlank(teacherComment.getTeacherFeedback())) {
			return 0;// 表示(empty)
		} else {
			return 1;// 表示(submitted)
		}
	}
	
	/**
	 * 
	 * 6种ReportType的枚举
	 *
	 */
	public enum ReportType {
		EmptyReport(0),
		PracticumReport(1),
		NewUAReprot(2),
		DemoReport(3),
		Feedback(4),
		OldUAReport(5);
		
		private int code;
		
		private ReportType(int code ){
			this.setCode(code);
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}
	}
}
