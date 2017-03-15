package com.vipkid.rest.portal.service;

import java.util.Map;

public interface ClassroomsRestService {
	Map<String, Object> getClassroomsData(long teacherId, int offsetOfMonth, String courseType, int page);

	Map<String, Object> getClassroomsMaterialByLessonId(long lessonId);

	int[] getPaidTrailPaymentYearMonth(Long studentId, Long onlineClassId, String scheduledTime);
}