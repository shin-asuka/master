/**
 *
 */
package com.vipkid.http.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.payroll.service.StudentService;
import com.vipkid.rest.portal.vo.StudentCommentPageVo;
import com.vipkid.rest.portal.vo.StudentCommentTotalVo;
import com.vipkid.rest.portal.vo.StudentCommentVo;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Student;
import com.vipkid.trpm.service.activity.ActivityService;
import com.vipkid.trpm.service.portal.OnlineClassService;
import com.vipkid.trpm.util.LessonSerialNumber;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author yangchao
 * @date 2017年1月11日  下午17:36:00
 *
 */

public class ManageGatewayService extends HttpBaseService {

	private static final Logger logger = LoggerFactory.getLogger(ManageGatewayService.class);

	private static final Integer PAGE_SIZE = 20;//可调整缓存大小

	private static final String  GATEWAY_STUDENT_COMMENT_BATCH_API = "/service/student_comment/comments/by/classes?ids=%s";

	private static final String  GATEWAY_STUDENT_COMMENT_BY_TEACHER_API = "/service/student_comment/teacher/%s/comments?start=%s&limit=%s&ratings=%s";

	private static final String  GATEWAY_STUDENT_COMMENT_TOTAL_BY_TEACHER_API = "/service/student_comment/teacher/%d/comments/total";

	private static final String  GATEWAY_STUDENT_COMMENT_TRANSLATION_API = "/service/student_comment/comment/%s/translation";

	private static final String  GATEWAY_STUDENT_COMMENT_RATING_AVERAGE = "/service/student_comment/teacher/ratings/average?ids=%s";

	private static final String  GATEWAY_STUDENT_COMMENT_TAGS = "/internal/student_comment/comment/%s";

	@Autowired
	private OnlineClassService onlineClassService;
	@Autowired
	private StudentService studentService;
	@Autowired
	private LessonDao lessonDao;
	@Autowired
	private ActivityService activityService;

	public List<StudentCommentVo> getStudentCommentListByBatch(String idsStr) {

		List<StudentCommentVo> studentCommentApiList = Lists.newArrayList();

		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_BATCH_API ,idsStr));
			if (data!=null) {
				studentCommentApiList = JsonUtils.toBeanList(data, StudentCommentVo.class);
				for(StudentCommentVo studentCommentVo : studentCommentApiList) {
					String result = getTranslation(studentCommentVo.getId().longValue());
					studentCommentVo.setTransaltion(StringUtils.isEmpty(result) ? "" : result);
					Integer classId = studentCommentVo.getClass_id();
					studentCommentVo.setOcToken(activityService.encode(classId));
					Student student = studentService.getById(studentCommentVo.getStudent_id().longValue());
					if(student!=null) {
						studentCommentVo.setStudentAvatar(student.getAvatar());
						studentCommentVo.setStudentName(student.getEnglishName());
					}
				}
			}
		} catch (Exception e) {
			logger.error("【ManageGatewayService.getStudentCommentListByBatch】调用失败，idsStr：{}", e, idsStr);
		}

		return studentCommentApiList;
	}

	public StudentCommentPageVo getStudentCommentListByTeacherId(Integer teacher,Integer start,Integer limit,String ratings){

		StudentCommentPageVo studentCommentPageApi = new StudentCommentPageVo();
		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_BY_TEACHER_API,
															teacher!=null ? String.valueOf(teacher) : "",
															start!=null ? String.valueOf(start) : "",
															limit!=null ? String.valueOf(limit) : "",
															ratings!=null ? String.valueOf(ratings) : ""));
			if (data!=null) {
				studentCommentPageApi = JSONObject.parseObject(data, StudentCommentPageVo.class);
				for(StudentCommentVo stuCommentApi : studentCommentPageApi.getData()){
					OnlineClass onlineClass = onlineClassService.getOnlineClassById(stuCommentApi.getClass_id());
					if(onlineClass!= null) {
						stuCommentApi.setScheduleDateTime(DateFormatUtils.format(onlineClass.getScheduledDateTime(), "yyyy-MM-dd HH:mm"));
						//构造OnlineClassName
						Lesson lesson = lessonDao.findById(onlineClass.getLessonId());
						if(lesson!=null) {
							String lessonSn = lesson.getSerialNumber();
							stuCommentApi.setLessonSn(lessonSn);
							String onlineClassName = LessonSerialNumber.formatToStudentCommentPattern(lessonSn);
							onlineClassName = onlineClassName + lesson.getName();
							stuCommentApi.setOnlineClassName(onlineClassName);
						}
						Student student = studentService.getById(stuCommentApi.getStudent_id().longValue());
						if(student!=null) {
							stuCommentApi.setStudentAvatar(student.getAvatar());
							stuCommentApi.setStudentName(student.getEnglishName());
						}
						Integer classId = stuCommentApi.getClass_id();
						stuCommentApi.setOcToken(activityService.encode(classId));
					}
					String result = getTranslation(stuCommentApi.getId().longValue());
					stuCommentApi.setTransaltion(StringUtils.isEmpty(result)? "":result);
				}
			}else{
				studentCommentPageApi.setTotal(0);
			}
		} catch (Exception e) {
			logger.error("【ManageGatewayService.getStudentCommentListByTeacherId】调用失败，teacherId：{},start:{},limit:{},ratingLevel:{},exception:{}", teacher, start, limit, ratings,e);
		}

		return studentCommentPageApi;
	}

	public StudentCommentTotalVo getStudentCommentTotalByTeacherId(Integer teacher){

		StudentCommentTotalVo studentCommentTotalApi = new StudentCommentTotalVo();

		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_TOTAL_BY_TEACHER_API,teacher));
			if (data!=null) {
				studentCommentTotalApi = JsonUtils.toBean(data, StudentCommentTotalVo.class);
			}
		} catch (Exception e) {
			logger.error("【ManageGatewayService.getStudentCommentTotalByTeacherId】调用失败，teacherId：{},exception:{}", teacher,e);
		}

		return studentCommentTotalApi;
	}

	/**
	 * 批量获取老师三个月内的平均评价分值
	 * @param teacherIds 按逗号分隔的字符串
	 * @return
	 */
	public Map<String,String> getTeacherRatingsAverageByBatch(String teacherIds){
		Map<String,String> map = Maps.newHashMap();
		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_RATING_AVERAGE,teacherIds));
			if (data!=null) {
				ObjectMapper mapper = new ObjectMapper();
				map = mapper.readValue(data, Map.class);
			}
		} catch (Exception e) {
			logger.error("【ManageGatewayService.getStudentCommentTotalByTeacherId】调用失败，teacherId:"+teacherIds,e);
		}
		return map;
	}

	/**
	 * 获取某条评价的标签
	 * @param id
	 * @return
	 */
	public ArrayList getTagsByCommentId(String id){
		Map map = Maps.newHashMap();
		ArrayList tags = Lists.newArrayList();
		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_TAGS,id));
			if (data!=null) {
				ObjectMapper mapper = new ObjectMapper();
				map = mapper.readValue(data, Map.class);
				tags = (ArrayList) map.get("tags");
			}
		} catch (Exception e) {
			logger.error("【ManageGatewayService.getTagsByCommentId】调用失败，id:"+id,e);
		}
		return tags;
	}

	public Boolean saveTranslation(Long id,String text){
		String ret = null;
		try {
			JSONObject input = new JSONObject();
			input.put("translation",text);
			String data = WebUtils.postJSON(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_TRANSLATION_API, id),input);
			if (data!=null) {
				JSONObject jb = JSONObject.parseObject(data);
				if(jb.get("status").equals("OK")){
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("【ManageGatewayService.saveTranslation】调用失败，id：{},text:{},exception:{}", id, text,e);
			throw e;
		}
		return false;
	}

	public String getTranslation(Long id){
		String ret = "";
		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_TRANSLATION_API,id));
			if (data!=null) {
				JSONObject jb = JSONObject.parseObject(data);
				ret = (String)jb.get("translation");
			}
		} catch (Exception e) {
			logger.error("【ManageGatewayService.getTranslation】调用失败，id：{},exception:{}", id,e);
		}
		return ret;
	}
	/**
	 * 计算双向分页
	 * 默认单边的窗口大小为 10
	 * 页数 = 左页数 + 1 + 右页数
	*/

	public Integer calculateAbsolutePosition(StudentCommentPageVo allCommentOfTeacher,Long onlineClassId){
		Integer absolutePosition = 0;
		Integer flag = 0;
		for(Integer i=0;i<allCommentOfTeacher.getTotal();i++){
			if(allCommentOfTeacher.getData().get(i).getClass_id().longValue() == onlineClassId){
				absolutePosition = i;
				flag = 1;
				break;
			}
		}
		if(flag == 1) {
			return absolutePosition;
		}else {
			return -1;
		}
	}
}
