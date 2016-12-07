package com.vipkid.rest.portal.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.vipkid.http.service.AssessmentHttpService;
import com.vipkid.http.vo.OnlineClassVo;
import com.vipkid.http.vo.StudentUnitAssessment;
import com.vipkid.trpm.constant.ApplicationConstant.CourseType;
import com.vipkid.trpm.constant.ApplicationConstant.UaReportStatus;
import com.vipkid.trpm.dao.AssessmentReportDao;
import com.vipkid.trpm.dao.CourseDao;
import com.vipkid.trpm.dao.DemoReportDao;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherApplicationDao;
import com.vipkid.trpm.dao.TeacherCommentDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.AssessmentReport;
import com.vipkid.trpm.entity.Course;
import com.vipkid.trpm.entity.DemoReport;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherComment;
import com.vipkid.trpm.service.portal.ClassroomsService;
import com.vipkid.trpm.service.rest.LoginService;
import com.vipkid.trpm.util.DateUtils;

@Service
public class ClassroomsRestService {
	private static final int LINE_PER_PAGE = PropertyConfigurer.intValue("page.linePerPage");

	private static Logger logger = LoggerFactory.getLogger(ClassroomsRestService.class);

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
	private TeacherCommentDao teacherCommentDao;

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

	public Map<String, Object> getClassroomsData(long teacherId, int offsetOfMonth, String courseType, int page) {
		if (!CourseType.isPracticum(courseType)) {// 只要不是"PRACTICUM"，就赋值"MAJOR"
			courseType = "MAJOR";
		}

		Map<String, Object> result = Maps.newHashMap();

		Teacher teacher = teacherdao.findById(teacherId);
		if (teacher == null)
			return null;

		result.put("teacherId", teacher.getId());

		/* 生成monthOfYear */
		result.put("monthOfYear", DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_MMM_YYYY_US));

		/* 生成totalPage, curPage与stateList三个参数 */
		String monthOfYear = DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_YM);
		Map<String, Object> classroomsDate = doClassrooms(teacher, monthOfYear, courseType,
				LINE_PER_PAGE, page);
		int curPage = (int) classroomsDate.get("curPage");
		result.putAll(classroomsDate);// 添加totalPage, curPage与stateList三个参数

		/* 生成dataList */
		List<Map<String, Object>> dataList = getDataList(courseType, teacher, monthOfYear, curPage);
		result.put("dataList", dataList);

		/* 生成tagList */
		List<Map<String, Object>> tagList = getTagList(courseType, teacher.getId());
		result.put("tagList", tagList);
		
		return result;
	}
	
	public Map<String, Object> getClassroomsMaterialByLessonId(long lessonId){
		Map<String, Object> result = Maps.newHashMap();

		Long preLessonId = null;
		Long nextLessonId = null;
		String preLessonSerialNum = null;
		String nextLessonSerialNum = null;
		
		Course course = courseDao.findByLessonId(lessonId);
		List<Lesson> lessonList = null;
        if (null != course) {
            lessonList =  lessonDao.findByCourseId(course.getId());
            int lessonListSize = lessonList.size();
            
            int index = 0;
            for (index = 0; index < lessonListSize; index++) {
				Lesson eachLesson = lessonList.get(index);
				if(eachLesson.getId() == lessonId){
					break;
				}
			}
            if(index < lessonListSize){//如果上面的循环break退出，即lessonList里面有当前的lessonId对应的课程
            	 if(index<lessonListSize-1){//如果不是最后一个
            		 Lesson nextLesson = lessonList.get(index + 1);
     				nextLessonId = nextLesson.getId();
     				nextLessonSerialNum = nextLesson.getSerialNumber();
     			}else{
     				Lesson nextLesson = lessonList.get(0);//把第一个当成下一个
     	            nextLessonId = nextLesson.getId();
     	            nextLessonSerialNum = nextLesson.getSerialNumber();
     			}
     			if(index>0){
     				Lesson preLesson = lessonList.get(index-1);
     				preLessonId= preLesson.getId();
     				preLessonSerialNum = preLesson.getSerialNumber();
     			}else{
     				Lesson preLesson = lessonList.get(lessonListSize - 1);
     				 preLessonId = preLesson.getId();//把最后一个当成前一个
     				 preLessonSerialNum = preLesson.getSerialNumber();
     			}
            }
            else{
            	logger.error("lessonId为{}的课程，所属的courseId为{}，但此course下的所有lesson不包括此lesson", lessonId,course);
            }
        }
        
		result.put("preLessonId", preLessonId);
		result.put("nextLessonId", nextLessonId);
		result.put("preLessonSerialNum", preLessonSerialNum);
		result.put("nextLessonSerialNum", nextLessonSerialNum);
		
		Lesson  lesson = lessonDao.findById(lessonId);
		
		result.put("lessonId", lesson.getId());
		
		String lessonName = lesson.getName();
		if(null != lessonName){
			lessonName = lessonName.replace("\n", "").replace("\t", "");
		}
		result.put("lessonName", lessonName);
		
		result.put("lessonSerialNumber", lesson.getSerialNumber());
		
		String lessonObjective = lesson.getObjective();
		if(null != lessonObjective){
			lessonObjective = lessonObjective.replace("/t", "").replace("/n", "");
		}
		result.put("lessonObjective", lesson.getObjective());
		
		String lessonVocabularies = lesson.getVocabularies();
		if(null != lessonVocabularies){
			lessonVocabularies = lessonVocabularies.replace("\n", "").replace("\t", "");
		}
		result.put("lessonVocabularies", lessonVocabularies);
		
		String sentencePatterns = lesson.getSentencePatterns();
		String[] sentencePatternsGroup = null;
		if(null != sentencePatterns){
			sentencePatternsGroup = sentencePatterns.replace("\n", "").replace("\t", "").replace("<br>", "<br/>").split("<br/>");
		}
		result.put("lessonSentencePatterns", sentencePatternsGroup);
		
		return result;
	}
	
	public Map<String, Object> getClassroomsMaterials(long lessonId){
		Map<String, Object> result = Maps.newHashMap();
		Course course = courseDao.findByLessonId(lessonId);
		List<Lesson> lessonList = null;
        if (null != course) {
            lessonList =  lessonDao.findByCourseId(course.getId());
        }else{
        	return null;
        }
        List<Map<String, Object>> dataList = Lists.newArrayList();
        for (Lesson lesson : lessonList) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("lessonId", lesson.getId());
			
			String lessonName = lesson.getName();
			if(null != lessonName){
				lessonName = lessonName.replace("\n", "").replace("\t", "");
			}
			map.put("lessonName", lessonName);
			
			map.put("lessonSerialNumber", lesson.getSerialNumber());

			String lessonObjective = lesson.getObjective();
			if(null != lessonObjective){
				lessonObjective = lessonObjective.replace("/t", "").replace("/n", "");
			}
			result.put("lessonObjective", lesson.getObjective());
			
			String lessonVocabularies = lesson.getVocabularies();
			if(null != lessonVocabularies){
				lessonVocabularies = lessonVocabularies.replace("\n", "").replace("\t", "");
			}
			map.put("lessonVocabularies", lessonVocabularies);
			
			String sentencePatterns = lesson.getSentencePatterns();
			String[] sentencePatternsGroup = null;
			if(null != sentencePatterns){
				sentencePatternsGroup = sentencePatterns.replace("\n", "").replace("\t", "").replace("<br>", "<br/>").split("<br/>");
			}
			map.put("lessonSentencePatterns", sentencePatternsGroup);
			
			dataList.add(map);
		}
        result.put("dataList", dataList);
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
    public Map<String, Object> doClassrooms(Teacher teacher, String monthOfYear, String courseType,
            int linePerPage, int page) {
        Map<String, Object> modelMap = Maps.newHashMap();

        if (CourseType.isPracticum(courseType)) {
            Map<String, Object> totalLineMap = classroomsService.practicumTotal(teacher, monthOfYear);
            int totalLine = (int) totalLineMap.get("totalLine");
            int totalPage = 1;
            if(linePerPage != 0){
            	totalPage = totalLine/linePerPage+1;
            }
            modelMap.put("totalPage", totalPage);

            if(page<0 || page>totalPage){
            	page = 0;
            }
            if(page == 0){
            	/* 如果page为默认值0，就设page为当前日期对应的page */
            	page = classroomsService.practicumInPage(teacher, monthOfYear, linePerPage);//
            }
            modelMap.put("curPage", page);

            /* 查询统计数据 */
            modelMap.put("stateList", onlineClassDao.findStatPracticumFinishTypeBy(
                    teacher.getId(), teacher.getTimezone(), monthOfYear));
        } else {
            Map<String, Object> totalLineMap = classroomsService.majorTotal(teacher, monthOfYear);
            int totalLine = (int) totalLineMap.get("totalLine");
            int totalPage = 1;
            if(linePerPage != 0){
            	totalPage = totalLine/linePerPage+1;
            }
            modelMap.put("totalPage", totalPage);
            
            if(page == 0){
            	/* 如果page为默认值0，就设page为当前日期对应的page */
            	page = classroomsService.majorInPage(teacher, monthOfYear, linePerPage);//
            }
            modelMap.put("curPage", page);

            /* 查询统计数据 */
            modelMap.put("stateList", onlineClassDao.findStatMajorFinishTypeBy(
                    teacher.getId(), teacher.getTimezone(), monthOfYear));
        }

        return modelMap;
    } 
	
	/**
	 * 生成并重新包装classrooms接口的dataList属性
	 * 
	 * @author zhangbole
	 */
	private List<Map<String, Object>> getDataList(String courseType, Teacher teacher, String monthOfYear, int curPage) {
		List<Map<String, Object>> dataList = Lists.newArrayList();
		Map<String, Object> dataListMap = null;
		if (CourseType.isPracticum(courseType)) {
			dataListMap = classroomsService.practicumList(teacher, monthOfYear, curPage, LINE_PER_PAGE);
		} else {
			dataListMap = classroomsService.majorList(teacher, monthOfYear, curPage, LINE_PER_PAGE);
		}
		dataList = (List<Map<String, Object>>) dataListMap.get("dataList");//复用以前代码产生的dataList，后面对其重新包装
		if (dataList == null)
			return null;
		for (Map<String, Object> eachMap : dataList) {
			String studentName = (String) eachMap.get("englishName");
			eachMap.put("studentName", studentName);
			eachMap.remove("englishName");

			String lessonSerialNumber = (String) eachMap.get("serialNumber");
			eachMap.put("lessonSerialNumber", lessonSerialNumber);
			eachMap.remove("serialNumber");

			long onlineClassId = (long) eachMap.get("id");
			eachMap.put("onlineClassId", onlineClassId);
			eachMap.remove("id");

			Timestamp timeStamp = (Timestamp) eachMap.get("scheduledDateTime");
			Date date = new Date(timeStamp.getTime());
			DateFormat df = new SimpleDateFormat("MMM dd yyyy, hh:mma",Locale.ENGLISH);
			//DateFormat df = new SimpleDateFormat();
			String scheduledDateTime = df.format(date);
			eachMap.put("scheduledDateTime", scheduledDateTime);

			eachMap.remove("isUnitAssessment");
			eachMap.remove("classroom");
			eachMap.remove("classType");

			addReportTypeAndStatus(eachMap, date);
		}
		return dataList;
	}
	
	/**
	 * 生成classrooms接口的tagList属性
	 * 
	 */
	private List<Map<String, Object>> getTagList(String courseType, long teacherId){
		List<Map<String, Object>> tagList = Lists.newArrayList();
		Map<String, Object> coursesTagMap = Maps.newHashMap();
		Map<String, Object> practicumTagMap = Maps.newHashMap();
		coursesTagMap.put("tagName", "Courses");
		practicumTagMap.put("tagName", "Practicum");
		if (CourseType.isPracticum(courseType)) {
			coursesTagMap.put("currently", new Boolean(false));
			practicumTagMap.put("currently", new Boolean(true));
		} else {
			coursesTagMap.put("currently", new Boolean(true));
			practicumTagMap.put("currently", new Boolean(false));
		}
		tagList.add(coursesTagMap);
		if (loginService.enabledPracticum(teacherId)) {
			tagList.add(practicumTagMap);
		}
		return tagList;
	}

	private void addReportTypeAndStatus(Map<String, Object> data, Date scheduledDateTime) {
		int reportType = 0;
		int reportStatus = 0;

		long onlineClassId = (long) data.get("onlineClassId");
		String lessonSerialNumber = (String) data.get("lessonSerialNumber");
		long studentId = (long) data.get("studentId");

		boolean isClassStarted = new Date().after(scheduledDateTime);

		if (lessonSerialNumber.startsWith("P")) {// 如果是PracticumReport
			reportType = 1;// 代表是PracticumReport
			reportStatus = getPracticumReportStatus(onlineClassId);
		} else if (classroomsService.isUaReport(lessonSerialNumber)) {// 如果是UaReport
			if(isOldUaReport(onlineClassId, studentId, lessonSerialNumber)){
				reportType = 5;//代表是旧UaReport
				Map<String, Object> map = getOldUaReportStatus(onlineClassId, studentId, lessonSerialNumber);
				reportStatus = (int) map.get("oldUaReportStatus");
				data.put("UaReportUrl", map.get("UaReportUrl"));//如果是老UA，会多一个UaReportUrl参数
			}else{
				reportType = 2;// 代表是新UaReport
				reportStatus = getNewUaReportStatus(onlineClassId);
			}
		} else if (classroomsService.isDemoReport(lessonSerialNumber) && isClassStarted) {// 如果是demoReport
			reportType = 3;// 代表是demoReport
			reportStatus = getDemoReportStatus(studentId, onlineClassId);

		} else if (isClassStarted) {
			reportType = 4;// 代表是normalReport
			reportStatus = getNormalReportStatus(studentId, onlineClassId);

		} else {
			reportType = 0;// 代表是空报告，不显示
			reportStatus = 0;
		}

		data.put("reportType", reportType);
		data.put("reportStatus", reportStatus);

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
	        long schedDateTime =
	                onlineClassDao.findById(onlineClassId).getScheduledDateTime().getTime();//此方法可以提速，用时间来判断是不是新UA，如10月份之后的都是新UA
	        if (DateUtils.isSearchById(schedDateTime)) {
	            assessmentReport = assessmentReportDao.findReportByClassId(onlineClassId);
	        } else {
	            assessmentReport = assessmentReportDao.findReportByStudentIdAndName(serialNumber, studentId);
	        }
	        if (null != assessmentReport && StringUtils.isNotEmpty(assessmentReport.getUrl())){
	        	return true;
	        }else{
	        	return false;
	        }
	}

	private Map<String, Object> getOldUaReportStatus(long onlineClassId, long studentId, String serialNumber) {
		Map<String, Object> result = Maps.newHashMap();
		int oldUaReportStatus = 0;
		 AssessmentReport assessmentReport = null;
	        long schedDateTime =
	                onlineClassDao.findById(onlineClassId).getScheduledDateTime().getTime();
	        if (DateUtils.isSearchById(schedDateTime)) {
	            assessmentReport = assessmentReportDao.findReportByClassId(onlineClassId);
	        } else {
	            assessmentReport = assessmentReportDao.findReportByStudentIdAndName(serialNumber, studentId);
	        }
		if (null == assessmentReport || StringUtils.isEmpty(assessmentReport.getUrl())) {
			logger.warn("新UA上线后，老UA的状态不应为empty。");
			oldUaReportStatus = 0;//表示(empty)
        } else{
        	String url = assessmentReport.getUrl();
        	result.put("UaReportUrl", url);
        	if (assessmentReport.getReaded() == UaReportStatus.RESUBMIT) {
            	oldUaReportStatus = 2;//表示(Resubmit)
            }else{
            	oldUaReportStatus = 1;//表示("")，即已提交
            }
        }
		result.put("oldUaReportStatus", oldUaReportStatus);
		return result;
	}

	private int getNewUaReportStatus(long onlineClassId) {
		OnlineClassVo onlineClassVo = new OnlineClassVo();
    	onlineClassVo.getIdList().add(onlineClassId);
		List<StudentUnitAssessment> suaList = assessmentHttpService.findOnlineClassVo(onlineClassVo);
		if(CollectionUtils.isNotEmpty(suaList)){
			StudentUnitAssessment sua = suaList.get(0);
            if(sua.getSubmitStatus() == 1){
            	return 2;//表示(submitted)
			}else if(sua.getIsRefillin()==1){
				return 0;//表示(empty)
            }else{
            	return 1;//表示(saved)
			}
		}else{
			return 0;//表示(empty)
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
		TeacherComment teacherComment = teacherCommentDao.findByStudentIdAndOnlineClassId(studentId, onlineClassId);
		if (null == teacherComment || StringUtils.isBlank(teacherComment.getTeacherFeedback())) {
			return 0;// 表示(empty)
		} else {
			return 1;// 表示(submitted)
		}
	}
}
