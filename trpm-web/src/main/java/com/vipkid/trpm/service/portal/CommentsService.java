package com.vipkid.trpm.service.portal;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.vipkid.trpm.dao.TeacherCommentDao;
import com.vipkid.trpm.entity.TeacherComment;

@Service
public class CommentsService {

	@Autowired
	private TeacherCommentDao teacherCommentDao;

	/**
	 * 处理comments首页面逻辑
	 * 
	 * @param teacherId
	 * @param monthOfYear
	 * @return Map<String, Object>
	 */
	public Map<String, Object> doComments(long teacherId, String monthOfYear, String timezone) {
		Map<String, Object> modelMap = Maps.newHashMap();

		modelMap.put("totalLine",
				teacherCommentDao.countByTeacherIdAndMonthOfYear(teacherId, monthOfYear, timezone));
		return modelMap;
	}

	/**
	 * 处理comments分页查询逻辑
	 * 
	 * @param teacherId
	 * @param monthOfYear
	 * @param curPage
	 * @param linePerPage
	 * @return Map<String, Object>
	 */
	public Map<String, Object> doCommentsList(long teacherId, String monthOfYear, String timezone,
			int curPage, int linePerPage) {
		Map<String, Object> modelMap = Maps.newHashMap();

		modelMap.put("dataList", teacherCommentDao.findByTeacherIdAndMonthOfYear(teacherId,
				monthOfYear, timezone, curPage, linePerPage));

		return modelMap;
	}

    public TeacherComment getTeacherComment(long studentId, long onlineClassId, long teacherId) {
        return teacherCommentDao.findTeacherCommentBy(studentId, onlineClassId, teacherId);
    }
}
