package com.vipkid.touch.referral.service;

import com.google.common.collect.Maps;
import com.vipkid.dataSource.annotation.Slave;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.portal.personal.model.ReferralTeacherVo;
import com.vipkid.rest.dto.ReferralTeacherDto;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Page;
import com.vipkid.trpm.entity.Teacher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/***
 * 老师推荐service
 */
@Service
public class TeacherReferralService {

	private Logger logger = LoggerFactory.getLogger(TeacherReferralService.class);

	@Autowired
	private TeacherDao teacherDao;

	@Slave
	public Page<ReferralTeacherDto> findReferralSucceedTeachersPage(ReferralTeacherVo bean){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("param", bean);

		Integer count = teacherDao.countReferralSucceedTeachers(paramsMap);
        Page<ReferralTeacherDto> page  = new Page<ReferralTeacherDto>();
        page.setCount(count);
        if(count.intValue() <= 0){
            return page;
        }
        List<ReferralTeacherDto> list = teacherDao.listReferralSucceedTeachers(paramsMap);
        if(CollectionUtils.isEmpty(list)){
            return page;
        }
        for(ReferralTeacherDto teacher : list){
            //当前状态显示上第一节课的时间
            String currentStatus = teacher.getScheduledDateTime();
            teacher.setStatus(currentStatus);
        }
        page.setList(list);
        return page;
	}

	@Slave
	public Integer countReferralSucceedTeachers(ReferralTeacherVo bean){
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("param", bean);
        return teacherDao.countReferralSucceedTeachers(paramsMap);
	}

	@Slave
	public Page<ReferralTeacherDto> findReferralProcessingTeachersPage(ReferralTeacherVo bean){
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("param", bean);

        Integer count = teacherDao.countReferralProcessingTeachers(paramsMap);
        Page<ReferralTeacherDto> page  = new Page<ReferralTeacherDto>();
        page.setCount(count);
        if(count.intValue() <= 0){
            return page;
        }
        List<ReferralTeacherDto> list = teacherDao.listReferralProcessingTeachers(paramsMap);
        if(CollectionUtils.isEmpty(list)){
            return page;
        }
        for(ReferralTeacherDto teacher : list){
            String nextStep = getNextStep(teacher.getStatus(), teacher.getLifeCycle());
            teacher.setNextStep(nextStep);
            teacher.setStatus(teacher.getStatus() + ", " + teacher.getResult());
        }
        page.setList(list);
		return page;
	}

	@Slave
	public Integer countReferralProcessingTeachers(ReferralTeacherVo bean) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("param", bean);
		return teacherDao.countReferralProcessingTeachers(paramsMap);
	}

	@Slave
	public Page<ReferralTeacherDto> findReferralFailedTeachersPage(ReferralTeacherVo bean){
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("param", bean);

        Integer count = teacherDao.countReferralFailedTeachers(paramsMap);
        Page<ReferralTeacherDto> page  = new Page<ReferralTeacherDto>();
        page.setCount(count);
        if(count.intValue() <= 0){
            return page;
        }
        List<ReferralTeacherDto> list = teacherDao.listReferralFailedTeachers(paramsMap);
        if(CollectionUtils.isEmpty(list)){
            return page;
        }
        for(ReferralTeacherDto teacher : list){
            String currentStatus = null;
            if(StringUtils.isBlank(currentStatus) && StringUtils.equals(teacher.getLifeCycle(), TeacherEnum.LifeCycle.SIGNUP.name())){
                currentStatus = TeacherEnum.LifeCycle.SIGNUP.getVal();
                teacher.setStatus(currentStatus);
            }else{
                teacher.setStatus(teacher.getStatus() + ", " + teacher.getResult());
            }
        }
        page.setList(list);
		return page;
	}

	@Slave
	public Integer countReferralFailedTeachers(ReferralTeacherVo bean) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("param", bean);
		return teacherDao.countReferralFailedTeachers(paramsMap);
	}

    @Slave
    public ReferralTeacherDto getReferralDetail(ReferralTeacherVo bean){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("param", bean);
        ReferralTeacherDto teacherDto = teacherDao.getReferralTeacher(paramsMap);
        return teacherDto;
    }


	private String getNextStep(String currentStatus, String lifeCycle){
	    String nextStep = "";
	    if(StringUtils.isBlank(currentStatus) && StringUtils.equals(lifeCycle, TeacherEnum.LifeCycle.SIGNUP.name())){
            nextStep = TeacherEnum.LifeCycle.BASIC_INFO.getVal();
            return nextStep;
        }
        /***
         * Basic_info - Interview
         Interview - Teaching Prep
         Teaching Prep - Mock Class
         Mock Class - Contract Info
         Contract Info - Teach 1st Class
         */
		switch (currentStatus){
            case "BASIC_INFO":
                nextStep = "Interview";
                break;
            case "INTERVIEW":
                nextStep = "Teaching Prep";
                break;
            case "TRAINING":
                nextStep = "Mock Class";
                break;
            case "PRACTICUM":
                nextStep = "Contract Info";
                break;
            case "CONTRACT_INFO":
                nextStep = "Teach 1st Class";
                break;
        }
        return nextStep;
	}
}
