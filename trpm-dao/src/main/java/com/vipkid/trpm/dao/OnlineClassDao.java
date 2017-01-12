package com.vipkid.trpm.dao;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.OnlineClass;
import org.apache.commons.collections.CollectionUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Repository
public class OnlineClassDao extends MapperDaoTemplate<OnlineClass> {

	@Autowired
	public OnlineClassDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, OnlineClass.class);
	}

	/**
	 * 通过老师id及时区，开始时间，结束时间查找
	 *
	 * @param teacherId
	 * @param fromTime
	 * @param toTime
	 * @param timezone
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> findByTeacherIdWithFromAndToTime(long teacherId, Date fromTime, Date toTime,
			String timezone) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("toTZOffset", timezone);

		paramsMap.put("fromTime", fromTime);
		paramsMap.put("toTime", toTime);

		return listEntity("findByTeacherIdWithFromAndToTime", paramsMap);
	}

	/**
	 * 通过老师id及时区，开始时间，结束时间查找 TimeSlot 数量
	 *
	 * @param teacherId
	 * @param fromTime
	 * @param toTime
	 * @param timezone
	 * @return TimeSlot 数量
	 */
	public int countByTeacherIdWithFromAndToTime(long teacherId, Date fromTime, Date toTime, String timezone) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("toTZOffset", timezone);

		paramsMap.put("fromTime", fromTime);
		paramsMap.put("toTime", toTime);

		return selectCount("countByTeacherIdWithFromAndToTime", paramsMap);
	}

	/**
	 * 通过onlineClassId查询上课的学生数量
	 *
	 * @param onlineClassId
	 * @return int
	 */
	public int countStudentByOnlineClassId(long onlineClassId) {
		return selectCount(new OnlineClass().setId(onlineClassId), "countStudentDaoByOnlineClassId");
	}

    /**
     * 随机获取公开课预约的学生
     * @param onlineClassId
     * @return
     */
    public int getRandomStudentFromOpenCourse(long onlineClassId) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("onlineClassId", onlineClassId);
        List<Map<String,Long>> studentList =  listEntity("getRandomStudentFromOpenCourse",paramsMap);
        if (CollectionUtils.isNotEmpty(studentList)) {
            Long studentId = studentList.get(0).get("studentId");
            if (null != studentId) {
                return studentId.intValue();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

	public int updateEntity(OnlineClass onlineClass) {
		return super.update(onlineClass);
	}

	/**
	 * 通过TeacherId，ScheduleDateTime查询课程列表
	 *
	 * @param teacherId
	 * @param scheduleDateTime
	 * @return List<OnlineClass>
	 */
	public List<OnlineClass> findByTeacherIdAndScheduleDateTime(long teacherId, Timestamp scheduleDateTime) {
		OnlineClass onlineClass = new OnlineClass();
		onlineClass.setTeacherId(teacherId);
		onlineClass.setScheduledDateTime(scheduleDateTime);

		return selectList(onlineClass);
	}

	/**
	 * 通过onlineClassId查询课程
	 *
	 * @param onlineClassId
	 * @return OnlineClass
	 */
	public OnlineClass findById(long onlineClassId) {
		OnlineClass onlineClass = new OnlineClass();
		onlineClass.setId(onlineClassId);

		return selectOne(onlineClass);
	}

	/**
	 * 更新OnlineClass课程状态
	 *
	 * @param onlineClassId
	 * @param status
	 */
	public void updateStatus(long onlineClassId, String status) {
		checkArgument(0 != onlineClassId, "Argument onlineClassId equals 0");

		update(new OnlineClass().setId(onlineClassId).setStatus(status));
	}

	/**
	 * 更新教师进入教室时间
	 *
	 * @param onlineClassId
	 * @param enterTime
	 */
	public void updateEnterClassTime(long onlineClassId, Timestamp enterTime) {
		checkArgument(0 != onlineClassId, "Argument onlineClassId equals 0");
		update(new OnlineClass().setId(onlineClassId).setTeacherEnterClassroomDateTime(enterTime));
	}

	/**
	 * 保存OnlineClass
	 */
	@Override
	public int save(OnlineClass onlineClass) {
		return super.save(onlineClass);
	}

	/**
	 * 通过OnlineClassId更新lessonId
	 *
	 * @param onlineClassId
	 * @param lessonId
	 */
	public void updateLesson(long onlineClassId, long lessonId) {
		checkArgument(0 != onlineClassId, "Argument onlineClassId equals 0");

		update(new OnlineClass().setId(onlineClassId).setLessonId(lessonId), "updateLessonByOnlineClassId");
	}

	/**
	 * 查询Major的classrooms的分页总行数
	 *
	 * @param teacherId
	 * @param timezone
	 * @param monthOfYear
	 * @return int
	 */
	public int countMajorBy(long teacherId, String timezone, String monthOfYear) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("monthOfYear", monthOfYear);
		paramsMap.put("toTZOffset", timezone);
		paramsMap.put("likeString", "NOT LIKE 'P%'");

		return selectCount("countByTeacherIdAndTimezoneAndMonthOfYear", paramsMap);
	}

	/**
	 * 分页查询Major的classrooms列表
	 *
	 * @param teacherId
	 * @param timezone
	 * @param monthOfYear
	 * @param curPage
	 * @param linePerPage
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> findMajorBy(long teacherId, String timezone, String monthOfYear, int curPage,
			int linePerPage) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("monthOfYear", monthOfYear);
		paramsMap.put("toTZOffset", timezone);
		paramsMap.put("likeString", "NOT LIKE 'P%'");

		return pageEntity("findByTeacherIdAndTimezoneAndMonthOfYear", "countByTeacherIdAndTimezoneAndMonthOfYear",
				curPage, linePerPage, paramsMap);
	}

	/**
	 * 查询Major从当前日期开始的总行数
	 *
	 * @param teacherId
	 * @param timezone
	 * @param monthOfYear
	 * @return int
	 */
	public int countMajorFromNowBy(long teacherId, String timezone, String monthOfYear) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("monthOfYear", monthOfYear);
		paramsMap.put("toTZOffset", timezone);
		paramsMap.put("likeString", "NOT LIKE 'P%'");

		return selectCount("countByTeacherIdAndTimezoneAndMonthOfYearAndFromNow", paramsMap);
	}

	/**
	 * 查询统计Major的FinishType数据
	 *
	 * @param teacherId
	 * @param timezone
	 * @param monthOfYear
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> findStatMajorFinishTypeBy(long teacherId, String timezone, String monthOfYear) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("monthOfYear", monthOfYear);
		paramsMap.put("toTZOffset", timezone);
		paramsMap.put("likeString", "NOT LIKE 'P%'");

		return listEntity("findStatFinishTypeByTeacherIdAndTimezoneAndMonthOfYear", paramsMap);
	}

	/**
	 * 查询Practicum的classrooms的分页总行数
	 *
	 * @param teacherId
	 * @param timezone
	 * @param monthOfYear
	 * @return int
	 */
	public int countPracticumBy(long teacherId, String timezone, String monthOfYear) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("monthOfYear", monthOfYear);
		paramsMap.put("toTZOffset", timezone);
		paramsMap.put("likeString", "LIKE 'P%'");

		return selectCount("countByTeacherIdAndTimezoneAndMonthOfYear", paramsMap);
	}

	/**
	 * 分页查询Practicum的classrooms列表
	 *
	 * @param teacherId
	 * @param timezone
	 * @param monthOfYear
	 * @param curPage
	 * @param linePerPage
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> findPracticumBy(long teacherId, String timezone, String monthOfYear, int curPage,
			int linePerPage) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("monthOfYear", monthOfYear);
		paramsMap.put("toTZOffset", timezone);
		paramsMap.put("likeString", "LIKE 'P%'");

		return pageEntity("findByTeacherIdAndTimezoneAndMonthOfYear", "countByTeacherIdAndTimezoneAndMonthOfYear",
				curPage, linePerPage, paramsMap);
	}

	/**
	 * 查询Practicum从当前日期开始的总行数
	 *
	 * @param teacherId
	 * @param timezone
	 * @param monthOfYear
	 * @return int
	 */
	public int countPracticumFromNowBy(long teacherId, String timezone, String monthOfYear) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("monthOfYear", monthOfYear);
		paramsMap.put("toTZOffset", timezone);
		paramsMap.put("likeString", "LIKE 'P%'");

		return selectCount("countByTeacherIdAndTimezoneAndMonthOfYearAndFromNow", paramsMap);
	}

	/**
	 * 查询统计Practicum的FinishType数据
	 *
	 * @param teacherId
	 * @param timezone
	 * @param monthOfYear
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> findStatPracticumFinishTypeBy(long teacherId, String timezone, String monthOfYear) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("monthOfYear", monthOfYear);
		paramsMap.put("toTZOffset", timezone);
		paramsMap.put("likeString", "LIKE 'P%'");

		return listEntity("findStatFinishTypeByTeacherIdAndTimezoneAndMonthOfYear", paramsMap);
	}

	/**
	 * 根据开始和结束时间查询需要显示的INVALID课程列表
	 *
	 * @author John
	 *
	 * @param teacherId
	 * @param fromTime
	 * @param toTime
	 * @param timezone
	 * @return 需要显示的INVALID课程列表
	 */
	public List<Map<String, Object>> findInvalidBy(long teacherId, Date fromTime, Date toTime, String timezone) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("toTZOffset", timezone);

		paramsMap.put("fromTime", fromTime);
		paramsMap.put("toTime", toTime);

		return listEntity("findInvalidByTeacherIdAndTimezoneAndFromToTime", paramsMap);
	}

	/**
	 * 查询指定时间内所有booked的课
	 *
	 * @Author:ALong (ZengWeiLong)
	 * @param startTime
	 * @param endTime
	 * @return List<Map<String,Object>>
	 * @date 2016年5月12日
	 */
	public List<Map<String, Object>> findTomorrowAllBook(String startTime, String endTime) {
		return findTomorrowAllBook(startTime, endTime, 0);
	}

	/**
	 * 查询指定时间内所有booed的课
	 *
	 * @Author:ALong (ZengWeiLong)
	 * @param startTime
	 * @param endTime
	 * @param teacherId
	 *            老师ID
	 * @return List<Map<String,Object>>
	 * @date 2016年5月12日
	 */
	public List<Map<String, Object>> findTomorrowAllBook(String startTime, String endTime, long teacherId) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("startTime", startTime);
		paramsMap.put("endTime", endTime);
		paramsMap.put("teacherId", teacherId);
		return listEntity("findTomorrowAllBook", paramsMap);
	}

	public List<Map<String, Object>> findOnlineClassIdAndStudentId(long onlineClassId, long studentId) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("onlineClassId", onlineClassId);
		paramsMap.put("studentId", studentId);
		return listEntity("findOnlineClassIdAndStudentId", paramsMap);
	}

	/**
	 * 查询指定时间内所有的课
	 *
	 * @param startTime
	 * @param endTime
	 * @param teacherId
	 * @return
	 */
	public List<Map<String, Object>> findMajorCourseListByStartTimeAndEndTime(String startTime, String endTime, Long teacherId) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("startTime", startTime);
		paramsMap.put("endTime", endTime);
		paramsMap.put("teacherId", teacherId);
		return listEntity("findMajorCourseListByStartTimeAndEndTime", paramsMap);
	}


	public List<Map<String, Object>> findMajorCourseListByCond(HashMap<String,Object> onlineClassVoCond) {
		return listEntity("findMajorCourseListByCond", onlineClassVoCond);
	}

	/**
	 * 查询指定时间内所有需要填写feedback的课
	 *
	 * @param startTime
	 * @param endTime
	 * @param teacherId
	 * @return
	 */
	public List<Map<String, Object>> findOnlineClassList4CheckTeacherComment(String startTime, String endTime, Long teacherId) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("startTime", startTime);
		paramsMap.put("endTime", endTime);
		paramsMap.put("teacherId", teacherId);
		return listEntity("findOnlineClassList4CheckTeacherComment", paramsMap);
	}

	/**
	 * 过滤有效的onlineClassId
	 *
	 * @param onlineClassIds
	 * @return
	 */
	public List<Map<String, Object>> batchGetStatusByOnlineClassIds(List<String> onlineClassIds) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("onlineClassIds", onlineClassIds);
		return listEntity("batchGetStatusByOnlineClassIds", paramsMap);
	}

	/**
	 * 根据 id 列表批量查询
	 * @param onlineClassIds
	 * @return
     */
	public List<OnlineClass> findOnlineClasses(List<Long> onlineClassIds){
		Map<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("onlineClassIds", onlineClassIds);
		return selectList("findOnlineClassByIds", paramsMap);
	}

}
