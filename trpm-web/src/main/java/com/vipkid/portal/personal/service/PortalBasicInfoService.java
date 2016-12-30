package com.vipkid.portal.personal.service;

import static com.vipkid.trpm.util.DateUtils.FMT_YMD;

import java.time.LocalDate;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.http.service.FileHttpService;
import com.vipkid.rest.dto.PersonlInfoDto;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherLocationDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.entity.User;

@Service
public class PortalBasicInfoService {

	@Autowired
	private FileHttpService fileHttpService;
	
	@Autowired
	private TeacherAddressDao teacherAddressDao;
	
	@Autowired
	private TeacherLocationDao teacherLocationDao;
	
	@Autowired
	private TeacherDao teacherDao;
	
	@Autowired
	private UserDao userDao;
	
	
	public PersonlInfoDto getBasicInfo(Teacher teacher,User user){
		
		PersonlInfoDto personInfo = new PersonlInfoDto();
		personInfo.setAvatar(fileHttpService.queryTeacherFiles(teacher.getId()).getAvatar());
		personInfo.setTeacherId(user.getId());
		personInfo.setEmail(user.getUsername());
		personInfo.setGender(user.getGender());
		if(teacher.getBirthday() != null){
			String birthday = DateFormatUtils.format(new Date(teacher.getBirthday().getTime()),"yyyy-MM-dd");
			personInfo.setBirthday(birthday);
		}
		personInfo.setNationality(teacher.getCountry());
		
		personInfo.setPhoneType(teacher.getPhoneType());
		personInfo.setPhoneNationCode(teacher.getPhoneNationCode());
		personInfo.setPhoneNationId(teacher.getPhoneNationId());
		personInfo.setMobile(teacher.getMobile());
		
		personInfo.setSkype(teacher.getSkype());
		personInfo.setStreetAddress(teacher.getAddress());
		personInfo.setTimezone(teacher.getTimezone());
		personInfo.setUniversity(teacher.getGraduatedFrom());
		personInfo.setHighestLevelOfEdu(teacher.getHighestLevelOfEdu());
		personInfo.setIntroduction(teacher.getIntroduction());
		personInfo.setEvaluationBio(teacher.getEvaluationBio());
		personInfo.setIsRemindEditBankInfo(StringUtils.isBlank(teacher.getBankCardNumber()));
		
		TeacherAddress currentAddress = teacherAddressDao.getTeacherAddress(teacher.getCurrentAddressId());
        if (currentAddress != null) {
            
        	personInfo.setStreetAddress(currentAddress.getStreetAddress());
        	
        	personInfo.setZipCode(currentAddress.getZipCode());
        	
        	TeacherLocation currentCountry = teacherLocationDao.findById(currentAddress.getCountryId());
            if(currentCountry != null){
            	personInfo.setCountryId(currentCountry.getId());
            	personInfo.setCountryName(currentCountry.getName());
            }
            
            TeacherLocation currentState = teacherLocationDao.findById(currentAddress.getStateId());
            if(currentState != null){
            	personInfo.setStateId(currentState.getId());
            	personInfo.setStateName(currentState.getName());
            }
            
            TeacherLocation currentCity = teacherLocationDao.findById(currentAddress.getCity());
            if(currentCity != null){
            	personInfo.setCityId(currentCity.getId());
            	personInfo.setCityName(currentCity.getName());
            }
        }
		return personInfo;
	}
	
	public boolean updateBasicInfo(Teacher teacher,User user,PersonlInfoDto basicInfo){
		
		user.setGender(basicInfo.getGender());
		
		if (StringUtils.isNotBlank(basicInfo.getBirthday())) {
			LocalDate localDate = LocalDate.parse(basicInfo.getBirthday(), FMT_YMD);
			teacher.setBirthday(java.sql.Date.valueOf(localDate));
		}
		
		teacher.setPhoneNationCode(basicInfo.getPhoneNationCode());
		teacher.setPhoneNationId(basicInfo.getPhoneNationId());
		teacher.setPhoneType(basicInfo.getPhoneType());
		teacher.setMobile(basicInfo.getMobile());
		teacher.setIntroduction(basicInfo.getIntroduction());
		
		teacher.setSkype(basicInfo.getSkype());
		teacher.setTimezone(basicInfo.getTimezone());
		teacher.setGraduatedFrom(basicInfo.getUniversity());
		teacher.setHighestLevelOfEdu(basicInfo.getHighestLevelOfEdu());
		teacher.setEvaluationBio(basicInfo.getEvaluationBio());
		
		TeacherAddress teacherAddress = new TeacherAddress();
		teacherAddress.setId(teacher.getCurrentAddressId());
		teacherAddress.setCountryId(basicInfo.getCountryId());
		teacherAddress.setStateId(basicInfo.getStateId());
		teacherAddress.setCity(basicInfo.getCityId());
		teacherAddress.setStreetAddress(basicInfo.getStreetAddress());
		teacherAddress.setZipCode(basicInfo.getZipCode());
		
		this.teacherAddressDao.updateOrSave(teacherAddress);
		
		teacher.setCurrentAddressId(teacherAddress.getId());
		
		this.teacherDao.update(teacher);
		this.userDao.update(user);
		
		return true;
	}
}
