package com.vipkid.portal.pesupervisor.service;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.community.config.PropertyConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Lists;
import com.vipkid.portal.pesupervisor.model.PeSupervisorClassDetail;
import com.vipkid.portal.pesupervisor.model.PeSupervisorData;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.dao.TeacherPeDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherPe;
import com.vipkid.trpm.util.DateUtils;

@Service
public class PeSupervisorRestService {
	private static final int LINE_PER_PAGE = PropertyConfigurer.intValue("page.linePerPage");

	@Autowired
	private TeacherPeDao teacherPeDao;

	@Autowired
	private LoginService loginService;

	/**
	 * 返回教师端PE Supervisor页面的数据模型
	 * 
	 * @author zhangbole
	 */
	public PeSupervisorData getPeSupervisorData(Teacher teacher, int page) {
		PeSupervisorData result = new PeSupervisorData();
		Long teacherId = teacher.getId();
		
		result.setTeacherId(teacherId);

		int totalPage = totalPe(teacherId);
		result.setTotalPage(totalPage);

		if (page <= 0 || page > totalPage) {
			page = 1;// 当page参数不合理时，默认为1
		}
		result.setCurPage(page);

		List<PeSupervisorClassDetail> dataList = getDataList(teacherId, page, LINE_PER_PAGE);
		result.setDataList(dataList);

		return result;
	}

	private List<PeSupervisorClassDetail> getDataList(long teacherId, int page, int linePerPage) {
		List<TeacherPe> originalDataList = teacherPeDao.pageByTeacherId(teacherId, page, linePerPage);
		List<PeSupervisorClassDetail> dataList = Lists.newArrayList();
		if (null == originalDataList) {
			return null;
		}

		/* 重新包装dataList */
		for (TeacherPe teacherPe : originalDataList) {
			PeSupervisorClassDetail peSupervisorEachClassInfo = new PeSupervisorClassDetail();

			peSupervisorEachClassInfo.setId(teacherPe.getId());
			peSupervisorEachClassInfo.setOnlineClassId(teacherPe.getOnlineClassId());
			peSupervisorEachClassInfo.setTeacherName(teacherPe.getTeacherName());
			peSupervisorEachClassInfo.setSerialNumber(teacherPe.getSerialNumber());
			peSupervisorEachClassInfo.setLessonName(teacherPe.getLessonName());
			peSupervisorEachClassInfo.setStatus(teacherPe.getStatus());
			peSupervisorEachClassInfo.setTeacehrId(teacherPe.getTeacherId());

			Timestamp timestamp = teacherPe.getScheduleTime();
			DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mma", Locale.ENGLISH);
			String scheduleTime = DateUtils.formatTo(timestamp.toInstant(), df);
			peSupervisorEachClassInfo.setScheduleTime(scheduleTime);

			dataList.add(peSupervisorEachClassInfo);
		}
		return dataList;
	}

	/* 返回总页数 */
	private int totalPe(long teacherId) {
		int lineNum = teacherPeDao.countByTeacherId(teacherId);
		int linePerPage = LINE_PER_PAGE;
		if (linePerPage <= 0) {
			linePerPage = 1;
		}
		int totalPage = (lineNum + linePerPage - 1) / linePerPage;
		return totalPage;
	}
}
