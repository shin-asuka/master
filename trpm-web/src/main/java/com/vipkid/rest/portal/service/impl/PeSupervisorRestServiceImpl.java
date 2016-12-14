package com.vipkid.rest.portal.service.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.community.config.PropertyConfigurer;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.vipkid.rest.portal.model.PeSupervisorData;
import com.vipkid.rest.portal.model.PeSupervisorEachClassInfo;
import com.vipkid.rest.portal.service.PeSupervisorRestService;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherPeDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherPe;

public class PeSupervisorRestServiceImpl implements PeSupervisorRestService{
	private static final int LINE_PER_PAGE = PropertyConfigurer.intValue("page.linePerPage");
	
	@Autowired
	private TeacherDao teacherDao;
	
	@Autowired
	private TeacherPeDao teacherPeDao;
	
	/**
	 * 返回教师端PE Supervisor页面的数据模型
	 * @author zhangbole
	 */
	public PeSupervisorData getPeSupervisorData(long teacherId, int page){
		PeSupervisorData result = new PeSupervisorData();
		Teacher teacher = teacherDao.findById(teacherId);
		if(teacher == null){
			return null;
		}
		
		result.setTeacherId(teacherId);
		
		int totalPage = totalPe(teacherId);
		result.setTotalPage(totalPage);
		
		if(page<=0 || page>totalPage){
			page = 1;//当page参数不合理时，默认为1
		}
		result.setCurPage(page);
			
		List<PeSupervisorEachClassInfo> dataList = getDataList(teacherId, page, LINE_PER_PAGE);
		result.setDataList(dataList);
		
		return result;
	}
	
	private List<PeSupervisorEachClassInfo> getDataList(long teacherId, int page, int linePerPage){
		List<TeacherPe> originalDataList = teacherPeDao.pageByTeacherId(teacherId, page, linePerPage);
		List<PeSupervisorEachClassInfo> dataList = Lists.newArrayList();
		if(null == originalDataList){
			return null;
		}
		
		/*重新包装dataList*/
		for (TeacherPe teacherPe : originalDataList) {
			PeSupervisorEachClassInfo peSupervisorEachClassInfo = new PeSupervisorEachClassInfo();
			
			peSupervisorEachClassInfo.setId(teacherPe.getId());
			peSupervisorEachClassInfo.setOnlineClassId( teacherPe.getOnlineClassId());
			peSupervisorEachClassInfo.setTeacherName(teacherPe.getTeacherName());
			peSupervisorEachClassInfo.setSerialNumber(teacherPe.getSerialNumber());
			peSupervisorEachClassInfo.setLessonName(teacherPe.getLessonName());
			peSupervisorEachClassInfo.setStatus(teacherPe.getStatus());
			
			Timestamp timestamp = teacherPe.getScheduleTime();
			Date date = new Date(timestamp.getTime());
			DateFormat df = new SimpleDateFormat("MMM dd yyyy, hh:mma",Locale.ENGLISH);
			String scheduleTime = df.format(date);
			peSupervisorEachClassInfo.setScheduleTime(scheduleTime);
			
			dataList.add(peSupervisorEachClassInfo);
		}
		return dataList;
	}
	
	/*返回总页数*/
    private int totalPe(long teacherId) {
        int lineNum = teacherPeDao.countByTeacherId(teacherId);
        int linePerPage = LINE_PER_PAGE;
        if(linePerPage <= 0){
        	linePerPage = 1;
        }
        int totalPage = lineNum/linePerPage+1;
        return totalPage;
    }
}
