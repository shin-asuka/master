package com.vipkid.trpm.service.portal;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vipkid.dataSource.annotation.Slave;
import com.vipkid.enums.OrderByEnum;
import com.vipkid.http.constant.HttpUrlConstant;
import com.vipkid.http.service.HttpApiClient;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.vo.StandardJsonObject;
import com.vipkid.portal.classroom.model.TeacherCommentVo;
import com.vipkid.portal.personal.model.ReferralTeacherVo;
import com.vipkid.rest.dto.ReferralTeacherDto;
import com.vipkid.rest.security.AppContext;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.*;
import com.vipkid.trpm.entity.teachercomment.*;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.util.DateUtils;
import com.vipkid.trpm.util.LessonSerialNumber;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zouqinghua
 * @date 2016年8月4日  下午3:10:43
 */
@Service
public class TeacherService {

	private Logger logger = LoggerFactory.getLogger(TeacherService.class);

	@Autowired
	private HttpApiClient httpApiClient;

	@Autowired
	private HttpUrlConstant httpUrlConstant;

	private static final String API_TEACHER_COMMENT_QUERY_COMMON = "/api/teacher/comment/findByCommonCondition";

	private static final String API_TEACHER_COMMENT_QUERY_GROUPBY = "/api/teacher/comment/findByStudentIdAndGroupByOnlineClassId";

	private static final String API_TEACHER_COMMENT_QUERY_TEACHERID_MOY = "/api/teacher/comment/findByTeacherIdMonthOfYear";

	private static final String API_TEACHER_COMMENT_QUERY_CLASSIDLIST = "/api/teacher/comment/findByOnlineClassIdList";

	private static final String API_TEACHER_COMMENT_QUERY_SIDLeSn = "/api/teacher/comment/findByStudentIdLessonSnPrefix";


	private static final String API_TEACHER_COMMENT_UPDATE = "/api/teacher/comment/updateById";

	private static final String API_TEACHER_COMMENT_INSERT = "/api/teacher/comment/checkExistOrInsertOne";


	private static final int QUERY_LIMIT_SIZE = 200;

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private TeacherDao teacherDao;

	@Autowired
	private OnlineClassDao onlineClassDao;

	@Autowired
	private LessonDao lessonDao;

	@Autowired
	private StudentDao studentDao;

	@Autowired
	private StudentExamDao studentExamDao;

	@Autowired
	private ReportService reportService;

	@Autowired
	private RedisProxy redisProxy;


	/**
	 * 通过teacherId获取教师信息
	 *
	 * @param id
	 * @return
	 */
	public Teacher get(Long id) {
		Teacher teacher = null;
		if (id != null) {
			teacher = teacherDao.findById(id);
		}
		return teacher;
	}

	public QueryTeacherCommentOutputDto getTeacherComment(String teacherId,String studentId,String onlineClassId) {

		Map<String, String> param = Maps.newHashMap();
		param.put("teacherId", teacherId);
		param.put("studentId", studentId);
		param.put("onlineClassId", onlineClassId);
		param.put("orderBy", OrderByEnum.id.val());
		param.put("limit", "1");
		List<TeacherCommentResult> teacherCommentList = getTeacherCommentResult(param,API_TEACHER_COMMENT_QUERY_COMMON);
		if (CollectionUtils.isEmpty(teacherCommentList)) {
			return null;
		}

		TeacherCommentResult teacherComment = teacherCommentList.get(0);

		QueryTeacherCommentOutputDto result = new QueryTeacherCommentOutputDto();
		result.setCourseType(teacherComment.getCourseType());
		result.setTeacherCommentId(String.valueOf(teacherComment.getId()));
		result.setCreateDate(teacherComment.getCreateTime());
		result.setStars(teacherComment.getStars()==null?0:teacherComment.getStars());

		if (NumberUtils.isNumber(onlineClassId)) {
			OnlineClass onlineClass = onlineClassDao.findById(Long.valueOf(onlineClassId));
			if (onlineClass != null) {
				//获取登录老师(时区),转换前端展示的classTime
				Teacher teacher = AppContext.getTeacher();
				Timestamp classTime = onlineClass.getScheduledDateTime();
				if(classTime!=null && teacher!=null && StringUtils.isNotBlank(teacher.getTimezone())){
					String classTimeFormat = DateUtils.formatTo(classTime.toInstant(),teacher.getTimezone(),DateUtils.FMT_YMD_EMd);
					result.setClassTime(classTimeFormat);
				}

				Lesson lesson = lessonDao.findById(teacherComment.getLessonId());
				if (lesson != null) {
					result.setTopic(lesson.getTopic());
					boolean isPreVipkid = LessonSerialNumber.isPreVipkidLesson(lesson.getSerialNumber());
					result.setPreVip(isPreVipkid);
				}
				result.setClassNumber(teacherComment.getLessonSerialNumber());

				Student student = studentDao.findById(teacherComment.getStudentId());
				if(student!=null&&StringUtils.isNotBlank(student.getEnglishName())){
					result.setStudentName(student.getEnglishName());
				}
			}

		}
		Course course = courseDao.findById(teacherComment.getCourseId());
		if(course!=null){
			result.setCourseDisplayName(course.getName());
		}

		//preVip特有字段
		if(teacherComment.getNeedParentSupport()!=null){
			result.setNeedParentSupport(teacherComment.getNeedParentSupport());
		}
		if(StringUtils.isNotBlank(teacherComment.getVocabularyRetention())){
			result.setVocabularyRetention(teacherComment.getVocabularyRetention());
		}
		if(StringUtils.isNotBlank(teacherComment.getPronunciation())){
			result.setPronunciation(teacherComment.getPronunciation());
		}
		if(StringUtils.isNotBlank(teacherComment.getAlphabetSkills())){
			result.setAlphabetSkills(teacherComment.getAlphabetSkills());
		}
		if(StringUtils.isNotBlank(teacherComment.getPhonologicalAwareness())){
			result.setPhonologicalAwareness(teacherComment.getPhonologicalAwareness());
		}
		if(StringUtils.isNotBlank(teacherComment.getFollowsInstructions())){
			result.setFollowsInstructions(teacherComment.getFollowsInstructions());
		}
		if(StringUtils.isNotBlank(teacherComment.getParticipatesActively())){
			result.setParticipatesActively(teacherComment.getParticipatesActively());
		}
		if(StringUtils.isNotBlank(teacherComment.getSpeaksClearly())){
			result.setSpeaksClearly(teacherComment.getSpeaksClearly());
		}
		if(StringUtils.isNotBlank(teacherComment.getMouseTouchpadActivities())){
			result.setMouseTouchpadActivities(teacherComment.getMouseTouchpadActivities());
		}
		if(StringUtils.isNotBlank(teacherComment.getDegreeCompletion())){
			result.setDegreeCompletion(teacherComment.getDegreeCompletion());
		}



		//返回已填过的字段
		result.setEmpty(teacherComment.getEmpty());

		if (teacherComment.getPerformance() != null && teacherComment.getPerformance() > 0) {
			String lfd = ApplicationConstant.LEVEL_OF_DIFFITULTY.get(teacherComment.getPerformance());
			if (StringUtils.isNotBlank(lfd)) {
				result.setLevelOfdifficulty(lfd);
			}
		}
		result.setTeacherFeedback(teacherComment.getTeacherFeedback());
		result.setSuggestAdjustment(
				(teacherComment.getPerformanceAdjust() != null && teacherComment.getPerformanceAdjust() > 0) ?
						true :
						false);
		result.setTipsForOtherTeachers(teacherComment.getTipsForOtherTeachers());

		String trialLevelResultDisplay = findTrialLevelResult4Display(teacherComment.getTrialLevelResult(),studentId,teacherComment.getLessonSerialNumber());
		result.setTrialLevelResult(trialLevelResultDisplay);

		return result;
	}

	private String findTrialLevelResult4Display(String trialLevelResult, String studentId,
		String lessonSerialNumber) {
		if (StringUtils.isNotBlank(lessonSerialNumber) && lessonSerialNumber.startsWith("T")) {
			String trialLevelResultTmp = reportService.handleTeacherComment(trialLevelResult);
			if (StringUtils.isNotBlank(trialLevelResultTmp)) {
				return trialLevelResultTmp;
			} else {
				StudentExam studentExam =
					studentExamDao.findStudentExamByStudentId(Long.valueOf(studentId));
				StudentExam examLevel = reportService.handleExamLevel(studentExam, lessonSerialNumber);
				if (examLevel != null && StringUtils.isNotBlank(examLevel.getExamLevel())) {
					return examLevel.getExamLevel();
				}
			}
			return "No Level Test result";
		}
		return null;
	}


	public TeacherComment findByStudentIdAndOnlineClassId(long studentId, long onlineClassId){
		Map<String, String> param = Maps.newHashMap();
		param.put("studentId", String.valueOf(studentId));
		param.put("onlineClassId", String.valueOf(onlineClassId));
		param.put("orderBy", OrderByEnum.id.val());
		param.put("limit", "1");
		List<TeacherCommentResult> teacherCommentList = getTeacherCommentResult(param,API_TEACHER_COMMENT_QUERY_COMMON);
		if (CollectionUtils.isEmpty(teacherCommentList)) {
			return null;
		}else{
			return new TeacherComment(teacherCommentList.get(0));
		}
	}
	public List<Map<String, Object>> findTCByStudentIdAndGroupByOnlineClassId(String studentId){
		Map<String, String> param = Maps.newHashMap();
		param.put("studentId", String.valueOf(studentId));
		List<Map<String, Object>> result = getTeacherCommentObject(param,API_TEACHER_COMMENT_QUERY_GROUPBY);
		if(CollectionUtils.isEmpty(result)){
			return null;
		}else{
			return result;
		}
	}

	public List<Map<String, Object>> findByStudentIdLessonSnPrefix(String studentId,String lessonSnPrefix){
		Map<String, String> param = Maps.newHashMap();
		param.put("studentId", String.valueOf(studentId));
		param.put("lessonSnPrefix", lessonSnPrefix);
		List<Map<String, Object>> result = getTeacherCommentObject(param,API_TEACHER_COMMENT_QUERY_SIDLeSn);
		if(CollectionUtils.isEmpty(result)){
			return null;
		}else{
			return result;
		}
	}


	public List<TeacherCommentResult> findTeacherCommentByTeacherIdAndMonthOfYear(long teacherId, String monthOfYear,
			String timezone,Integer curPage,Integer pageSize) {
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("teacherId", String.valueOf(teacherId));
		paramsMap.put("monthOfYear", monthOfYear);
		paramsMap.put("toTZOffset", timezone);

		List<TeacherCommentResult> teacherCommentList = getTeacherCommentResult(paramsMap,
				API_TEACHER_COMMENT_QUERY_TEACHERID_MOY);
		List<TeacherCommentResult> results = Lists.newArrayList();
		if (CollectionUtils.isEmpty(teacherCommentList)) {
			return null;
		} else {
			List<String> onlineClassIds = Lists.newArrayList();
			for (TeacherCommentResult one : teacherCommentList) {
				if (one.getOnlineClassId() != null) {
					onlineClassIds.add(String.valueOf(one.getOnlineClassId()));
				}

			}
			//过滤无效状态的onlineClass
			List<Map<String, Object>> validOnlineClassIds = onlineClassDao
					.batchGetStatusByOnlineClassIds(onlineClassIds);
			if (CollectionUtils.isEmpty(validOnlineClassIds)) {
				return null;
			} else {
				Set<String> validOnlineClassIdsSet = Sets.newHashSet();
				for (Map<String, Object> oneMap : validOnlineClassIds) {
					if (oneMap.get("id") != null) {
						validOnlineClassIdsSet.add((String) oneMap.get("id"));
					}
				}
				for (TeacherCommentResult oneTc : teacherCommentList) {
					if (oneTc.getOnlineClassId() != null && validOnlineClassIdsSet
							.contains(String.valueOf(oneTc.getOnlineClassId()))) {
						results.add(oneTc);
					}
				}
			}
			if (CollectionUtils.isEmpty(results)) {
				return null;
			} else {
				//分页显示
				if(curPage!=null&&pageSize!=null){
					return getByPage(results,curPage,pageSize);
				}else{
					return results;
				}

			}
		}
	}

	private List<TeacherCommentResult> getByPage(List<TeacherCommentResult> results, Integer curPage, Integer linePerPage) {
		//        Integer pageNo = Integer.valueOf(curPage);
		//        Integer pageSize = Integer.valueOf(linePerPage);
		Integer startLine = Integer.valueOf((curPage - 1) * linePerPage);
		Integer limitLine = Integer.valueOf(linePerPage);
		int count = results.size();
		//        int totalPage = count % linePerPage == 0?count / linePerPage:Math.abs(count / linePerPage) + 1;
		//        Integer totalLine = Integer.valueOf(count);

		if (startLine > count - 1) {
			return null;
		}
		if (startLine + limitLine > count - 1) {
			return results.subList(startLine, count);
		} else {
			return results.subList(startLine, startLine + limitLine);
		}

	}

	public TeacherCommentResult findByTeacherCommentId(String teacherCommentId){
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("id",teacherCommentId);
		List<TeacherCommentResult> teacherCommentList = getTeacherCommentResult(paramsMap,
				API_TEACHER_COMMENT_QUERY_COMMON);
		if (CollectionUtils.isEmpty(teacherCommentList)) {
			return null;
		}else{
			return teacherCommentList.get(0);
		}
	}

	public List<TeacherCommentResult> batchGetByOnlineClassIds(List<Long> onlineClassIds){
		List<TeacherCommentResult> teacherCommentList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(onlineClassIds)) {
			//分批查询,一次200个
			List<List<Long>> parts = Lists.partition(onlineClassIds, QUERY_LIMIT_SIZE);

			parts.stream().forEach(subIds -> {
				Map<String, String> paramsMap = Maps.newHashMap();
				paramsMap.put("onlineClassIdList", Joiner.on(',').join(subIds));
				paramsMap.put("needFeedback", "true");
				logger.info("batchGetByOnlineClassIds query one time");

				List<TeacherCommentResult> teacherCommentListPart =
					getTeacherCommentResult(paramsMap, API_TEACHER_COMMENT_QUERY_CLASSIDLIST);
				if(CollectionUtils.isNotEmpty(teacherCommentListPart)){
					teacherCommentList.addAll(teacherCommentListPart);
				}
			});
		}
		return teacherCommentList;

	}

	public boolean updateTeacherComment(TeacherCommentUpdateDto inputDto) {
		Map<String, String> param = Maps.newHashMap();
		//        param.put("tcId", inputDto.getTeacherCommentId());
		//        param.put("feedback", inputDto.getTeacherFeedback());
		//        param.put("tipsForOtherTeachers", inputDto.getTipsForOtherTeachers());
		//        param.put("levelOfdifficulty", inputDto.getLevelOfdifficulty());
		//        param.put("suggestAdjustment", String.valueOf(inputDto.isSuggestAdjustment()));
		//        param.put("trialLevelResult", inputDto.getTrialLevelResult());
		param.put("teacherComment", JsonUtils.toJSONString(inputDto));
		logger.info("updateTeacherComment http request url = {}; param={}",
				httpUrlConstant.getApiHomeworkServerUrl() + API_TEACHER_COMMENT_UPDATE,JsonUtils.toJSONString(inputDto));
		String response = httpApiClient
				.doPost(httpUrlConstant.getApiHomeworkServerUrl() + API_TEACHER_COMMENT_UPDATE, param);
		logger.info("updateTeacherComment http response = {}", response);
		StandardJsonObject standardJsonObject = null;
		try {
			standardJsonObject = JsonUtils.toBean(response, StandardJsonObject.class);
		} catch (Exception e) {
			logger.error("请求CF失败，返回数据格式异常，转换StandardJsonObject失败，请求参数：{}，返回结果：{}", param,
					response, e);
			return false;
		}
		if (standardJsonObject == null || !standardJsonObject.getRet()) {
			logger.error("请求CF返回失败，请求参数：{}，返回结果：{}", param, response);
			return false;
		}

		return true;
	}

	public TeacherCommentResult checkExistOrInsertOne(TeacherCommentUpdateDto inputDto) {
		List<TeacherCommentResult> tcResult = Lists.newArrayList();

			Map<String, String> param = Maps.newHashMap();
			param.put("teacherComment", JsonUtils.toJSONString(inputDto));
		try {
			logger.info("checkExistOrInsertOne http request url = {}; param={}",
				httpUrlConstant.getApiHomeworkServerUrl() + API_TEACHER_COMMENT_INSERT,
				JsonUtils.toJSONString(inputDto));
			String response = httpApiClient
				.doPost(httpUrlConstant.getApiHomeworkServerUrl() + API_TEACHER_COMMENT_INSERT, param);
			logger.info("checkExistOrInsertOne http response = {}", response);

			StandardJsonObject standardJsonObject = null;


			standardJsonObject = JsonUtils.toBean(response, StandardJsonObject.class);

			if (standardJsonObject == null || !standardJsonObject.getRet()) {
				logger.error("请求checkExistOrInsertOne返回失败，请求参数：{}，返回结果：{}", param, response);
				return null;
			}
			if (standardJsonObject.getData() == null
				|| standardJsonObject.getData().get("result") == null) {
				logger.error("请求checkExistOrInsertOne返回数据为空，请求参数：{}，返回结果：{}", param, response);
				return null;
			}
			tcResult = JsonUtils.readJson(JsonUtils.toJSONString(standardJsonObject.getData().get("result")), new TypeReference<List<TeacherCommentResult>>() {});
			if (CollectionUtils.isEmpty(tcResult)) {
				logger.error("请求checkExistOrInsertOne数据返回为空，请求参数：{}，返回结果：{}", param, response);
				return null;
			}
		} catch (Exception e) {
			logger
				.error("请求checkExistOrInsertOne失败，返回数据格式异常，转换StandardJsonObject失败，请求参数：{}", param, e);
			return null;
		}

		return tcResult.get(0);
	}


	private List<TeacherCommentResult> getTeacherCommentResult(Map<String, String> requestParam,String requestUrl) {
		String response = null;
		List<TeacherCommentResult> teacherCommentList = null;
		try {
			logger.info("getTeacherCommentResult http request url = {}; param={}",httpUrlConstant.getApiHomeworkServerUrl() + requestUrl,requestParam);
			response = httpApiClient
					.doPost(httpUrlConstant.getApiHomeworkServerUrl() + requestUrl, requestParam);
			logger.info("getTeacherCommentResult http response = {}",response);

			StandardJsonObject standardJsonObject = null;
			if (StringUtils.isBlank(response)) {
				return null;
			}

			standardJsonObject = JsonUtils.toBean(response, StandardJsonObject.class);

			if (standardJsonObject == null || !standardJsonObject.getRet()) {
				logger.error("请求CF返回失败，请求参数：{}，返回结果：{}", requestParam, response);
				return null;
			}

			teacherCommentList = JsonUtils
					.readJson(JsonUtils.toJSONString(standardJsonObject.getData().get("result")), new TypeReference<List<TeacherCommentResult>>() {});
			if (CollectionUtils.isEmpty(teacherCommentList)) {
				logger.info("请求CF返回业务数据为空，请求参数：{}，返回结果：{}", requestParam,
						response);
				return null;
			}
		} catch (Exception e) {
			logger.error("请求CF失败，返回数据格式异常，转换StandardJsonObject失败，请求参数：{}，返回结果：{}", requestParam,
					response, e);
			return null;
		}
		return teacherCommentList;
	}

	private List<Map<String,Object>> getTeacherCommentObject(Map<String, String> requestParam,String requestUrl) {
		String response = null;
		List<Map<String,Object>> teacherCommentList = Lists.newArrayList();
		try {
			logger.info("getTeacherCommentObject http request url = {}; param={}",httpUrlConstant.getApiHomeworkServerUrl() + requestUrl,requestParam);
			response = httpApiClient
					.doPost(httpUrlConstant.getApiHomeworkServerUrl() + requestUrl, requestParam);
			logger.info("getTeacherCommentObject http response = {}",response);
			StandardJsonObject standardJsonObject = null;
			if (StringUtils.isBlank(response)) {
				return null;
			}

			standardJsonObject = JsonUtils.toBean(response, StandardJsonObject.class);

			if (standardJsonObject == null || !standardJsonObject.getRet()) {
				logger.warn("请求CF返回失败，请求参数：{}，返回结果：{}", requestParam, response);
				return null;
			}

			List<Map<String,Object>> responseList = JsonUtils
					.readJson(JsonUtils.toJSONString(standardJsonObject.getData().get("result")),new TypeReference<List<Map<String,Object>>>(){} );
			if (CollectionUtils.isEmpty(responseList)) {
				logger.warn("请求CF返回业务数据为空，请求参数：{}，返回结果：{}", requestParam,
						response);
				return null;
			}
			for(Map oneRecord : responseList){
				teacherCommentList.add((Map<String, Object>)oneRecord);
			}
		} catch (Exception e) {
			logger.error("请求CF失败，返回数据格式异常，转换StandardJsonObject失败，请求参数：{}，返回结果：{}", requestParam,
					response, e);
			return null;
		}
		return teacherCommentList;
	}


	public String inputCheckPrevipMajorCourseTeacherComment(String serialNumber,SubmitTeacherCommentDto input){
		String errMsg = null;
		boolean isPreVipkid = LessonSerialNumber.isPreVipkidLesson(serialNumber);
		if(isPreVipkid && serialNumber.startsWith("M") && input!=null){
			//previp的Major课才进行check
			if(StringUtils.isBlank(input.getTeacherFeedback())){
				errMsg = "teacherFeedback can not be blank";
				return errMsg;
			}
			if(StringUtils.isBlank(input.getVocabularyRetention())){
				errMsg = "vocabularyRetention can not be blank";
				return errMsg;
			}
			if(StringUtils.isBlank(input.getPronunciation())){
				errMsg = "pronunciation can not be blank";
				return errMsg;
			}
			if(StringUtils.isBlank(input.getAlphabetSkills())
				&& !serialNumber.trim().startsWith("MC-L1-U1")
				&& !serialNumber.trim().startsWith("MC-L1-U2")
				&& !serialNumber.trim().startsWith("MC-L1-U3")){
				//u1-u3的课不需要check此项
				errMsg = "alphabetSkills can not be blank";
				return errMsg;
			}

			if(StringUtils.isBlank(input.getPhonologicalAwareness())){
				errMsg = "phonologicalAwareness can not be blank";
				return errMsg;
			}

			if(StringUtils.isBlank(input.getFollowsInstructions())){
				errMsg = "followsInstructions can not be blank";
				return errMsg;
			}

			if(StringUtils.isBlank(input.getParticipatesActively())){
				errMsg = "participatesActively can not be blank";
				return errMsg;
			}

			if(StringUtils.isBlank(input.getSpeaksClearly())){
				errMsg = "speaksClearly can not be blank";
				return errMsg;
			}

			if(StringUtils.isBlank(input.getMouseTouchpadActivities())){
				errMsg = "mouseTouchpadActivities can not be blank";
				return errMsg;
			}

			if(StringUtils.isBlank(input.getDegreeCompletion())){
				errMsg = "degreeCompletion can not be blank";
				return errMsg;
			}

			if(input.getPerformance() == null || input.getPerformance()==0){
				errMsg = "level of difficulty can not be blank";
				return errMsg;
			}
		}

		return errMsg;
	}

	
	public List<ReferralTeacherDto> findReferralTeachers(ReferralTeacherVo bean){
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("param", bean);
		return this.teacherDao.findReferralTeachers(paramsMap);
	}

	
	public Integer findReferralTeachersCount(ReferralTeacherVo bean){
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("param", bean);
		return this.teacherDao.findReferralTeachersCount(paramsMap);
	}

	/**
	 * 老师激励活动初始化数据1条,最小返回5
	 * @param teacherId 老师ID
	 */
	@Slave
	public Integer incentivesTeacherInit(String teacherId) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("teacherId", teacherId);
		List<String> teacherIds = teacherDao.findRegularIdNoLike(paramMap);
		if (CollectionUtils.isEmpty(teacherIds)) {
			return 5;
		}
		paramMap=assembleParam(paramMap);
		return dealIncentivesData(paramMap,teacherId);

	}

	/**
	 * 老师激励活动初始化数据
	 */
	@Slave
	public void incentivesAllTeacherInit() {
		Map<String, Object> paramMap = Maps.newHashMap();
		List<String> teacherIds = teacherDao.findRegularIdNoLike(paramMap);
		if (CollectionUtils.isEmpty(teacherIds)) {
			return;
		}
		paramMap=assembleParam(paramMap);
		for (String id : teacherIds) {
			dealIncentivesData(paramMap,id);
		}
	}

	/**
	 * 组装查询参数
	 * @param paramMap
	 * @return
	 */
	private Map<String, Object> assembleParam(Map<String, Object> paramMap ){
		paramMap.put("status", "FINISHED");
		paramMap.put("start", "2017-03-01");
		paramMap.put("end", "2017-03-29");
		List<String> finishTypes = Lists.newArrayList();
		finishTypes.add(ApplicationConstant.FinishType.AS_SCHEDULED);
		finishTypes.add(ApplicationConstant.FinishType.STUDENT_NO_SHOW);
		finishTypes.add(ApplicationConstant.FinishType.STUDENT_IT_PROBLEM);
		finishTypes.add(ApplicationConstant.FinishType.SYSTEM_PROBLEM);
		paramMap.put("finishTypes", finishTypes);
		return paramMap;
	}
	/**
	 * i.	设每个老师的计算基数为x
	 * ii.	x=（老师3月1日-28日状态为finished 的课程总和）/4
	 * iii.	如果x≤5， 则显示为5
	 * iv.	如果x>5， 则显示实际数据
	 * v.	x只显示整数，如果商数有余数，则四舍五入到个位，仍然显示为整数。
	 * 最小返回5
	 * @param paramMap
	 * @param id
	 * @return
	 */
	private Integer dealIncentivesData(Map<String, Object> paramMap,String id){
		try {
			paramMap.put("teacherId", id);
			Integer count = onlineClassDao.countScheduledByParam(paramMap);
			if (count == null) return 5;

			BigDecimal initCount = new BigDecimal(count / 4.0 <= 5 ? 5 : count / 4.0)
					.setScale(0, BigDecimal.ROUND_HALF_UP);
			//活动持续一个多月，缓存60天
			redisProxy.set(ApplicationConstant.RedisConstants.INCENTIVE_FOR_APRIL + id
					, initCount.toString(), 5184000);
			logger.info("incentivesTeacherInit put redis  key={},teacherId={},initCount={}"
					, ApplicationConstant.RedisConstants.INCENTIVE_FOR_APRIL + id, id, initCount);

			return initCount.intValue();
		} catch (Exception e) {
			logger.warn("incentivesTeacherInit put redis error :  key={},teacherId={},error={} "
					, ApplicationConstant.RedisConstants.INCENTIVE_FOR_APRIL + id, id,e.getMessage());
			return 5;
		}
	}

}
