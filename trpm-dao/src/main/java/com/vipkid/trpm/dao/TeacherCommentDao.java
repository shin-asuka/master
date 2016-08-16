package com.vipkid.trpm.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.TeacherComment;

@Repository
public class TeacherCommentDao extends MapperDaoTemplate<TeacherComment> {

	@Autowired
	public TeacherCommentDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, TeacherComment.class);
	}

	/**
	 * 通过studentId和onlineClassId查询
	 * 
	 * @param studentId
	 * @param onlineClassId
	 * @return TeacherComment
	 */
	public TeacherComment findByStudentIdAndOnlineClassId(long studentId, long onlineClassId) {
		return selectOne(new TeacherComment().setStudentId(studentId).setOnlineClassId(onlineClassId));
	}

	/**
	 * 更新TeacherComment
	 * 
	 * @Author:ALong
	 * @param entity
	 * @return
	 * @date 2015年12月16日
	 */
	@Override
	public int update(TeacherComment entity) {
		
		//更新数据更新时间
		entity.setLastDateTime(new Timestamp(System.currentTimeMillis()));
		return super.update(entity);
	}

	/**
	 * 通过StudentId查询
	 * 
	 * @param studentId
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> findCommentByStudentId(long studentId) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("studentId", studentId);

		return listEntity("findCommentsByStudentId", paramsMap);
	}

	/**
	 * 通过TeacherId，MonthOfYear查询Serial Number不为Assertment的记录总数
	 * 
	 * @param teacherId
	 * @param monthOfYear
	 * @param timezone
	 * @return int
	 */
	public int countByTeacherIdAndMonthOfYear(long teacherId, String monthOfYear, String timezone) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("monthOfYear", monthOfYear);
		paramsMap.put("toTZOffset", timezone);

		return selectCount("countByTeacherIdAndMonthOfYearAndNotLike", paramsMap);
	}

	/**
	 * 通过TeacherId，MonthOfYear查询Serial Number不为Assertment的记录列表
	 * 
	 * @param teacherId
	 * @param monthOfYear
	 * @param timezone
	 * @param curPage
	 * @param linePerPage
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> findByTeacherIdAndMonthOfYear(long teacherId, String monthOfYear, String timezone,
			int curPage, int linePerPage) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("monthOfYear", monthOfYear);
		paramsMap.put("toTZOffset", timezone);

		return pageEntity("findByTeacherIdAndMonthOfYearAndNotLike", "countByTeacherIdAndMonthOfYearAndNotLike",
				curPage, linePerPage, paramsMap);
	}

	/**
	 * 根据ID查找 TeacherComment
	 * 
	 * @Author:ALong
	 * @param id
	 * @return TeacherComment
	 * @date 2015年12月22日
	 */
	public TeacherComment findTeacherCommentById(long id) {
		return super.selectOne(new TeacherComment().setId(id));
	}
	
	public int insertTeacherComment(TeacherComment comment){

		//插入数据设置创建时间和更新时间
		comment.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
		comment.setLastDateTime(comment.getCreateDateTime());
	    return super.save(comment);
	}

    public TeacherComment findTeacherCommentBy(long studentId, long onlineClassId, long teacherId) {
        return super.selectOne(new TeacherComment().setStudentId(studentId).setOnlineClassId(onlineClassId)
                .setTeacherId(teacherId));
    }
}
