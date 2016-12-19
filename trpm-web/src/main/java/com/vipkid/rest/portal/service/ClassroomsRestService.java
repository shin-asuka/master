package com.vipkid.rest.portal.service;

import java.util.Map;

import com.vipkid.rest.portal.model.ClassroomsData;

public interface ClassroomsRestService {
	public Map<String, Object> getClassroomsData(long teacherId, int offsetOfMonth, String courseType, int page);

	public Map<String, Object> getClassroomsMaterialByLessonId(long lessonId);
}