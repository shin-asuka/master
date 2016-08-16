/**
 * 
 */
package com.vipkid.payroll.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.trpm.dao.TeacherCommentDao;
import com.vipkid.trpm.entity.TeacherComment;

/**
 * @author zouqinghua
 * @date 2016年5月17日  下午8:42:13
 *
 */
@Service
public class TeacherCommentService {

	private Logger logger = LoggerFactory.getLogger(TeacherCommentService.class);

	@Autowired
	private TeacherCommentDao teacherCommentDao;
	
	/**
	 * 查询学生所上课程是否有评语
	 * 
	 * @param onlineClassId
	 * @param studentId
	 * @return
	 */
	public TeacherComment hasCommentsByOnlineClassIdAndStudentId(Long onlineClassId,Long studentId){
		Boolean hasComment = false;
		TeacherComment teacherComment = null;
		if(onlineClassId!=null){
			teacherComment = teacherCommentDao.findByStudentIdAndOnlineClassId(studentId, onlineClassId);
			if (teacherComment == null 
                    || StringUtils.isBlank(teacherComment.getTeacherFeedback())) {
				hasComment = false;
            }else{
            	hasComment = true;
            }
			if(teacherComment == null){
				teacherComment = new TeacherComment();
				teacherComment.setOnlineClassId(onlineClassId);
			}
			teacherComment.setHasComment(hasComment);
		}
		logger.info("查询学生所上课程是否有评语  onlineClassId = {} , studentId " , onlineClassId ,studentId);
		return teacherComment;
	}
}
