package com.vipkid.portal.classroom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.portal.classroom.model.ClassRoomVo;
import com.vipkid.rest.dto.InfoRoomDto;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.StudentDao;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Student;
import com.vipkid.trpm.entity.Teacher;

@Service
public class ClassroomService {

	private static Logger logger = LoggerFactory.getLogger(ClassroomService.class);
	
	@Autowired
	private OnlineClassDao onlineClassDao;
	
	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private LessonDao lessonDao;
	
	public InfoRoomDto getInfoRoom(ClassRoomVo bean , Teacher teacher){
		OnlineClass onlineClass = this.onlineClassDao.findById(bean.getOnlineClassId());
		Student student = this.studentDao.findById(bean.getStudentId());
		Lesson lesson = this.lessonDao.findById(onlineClass.getLessonId());
		
		InfoRoomDto resultDto = new InfoRoomDto();
		resultDto.setOnlineClassId(onlineClass.getId());
		resultDto.setStudentId(student.getId());
		resultDto.setSerialNumber(onlineClass.getSerialNumber());
		resultDto.setLessonName(lesson.getName());
		
		
		return null;
	}
}
