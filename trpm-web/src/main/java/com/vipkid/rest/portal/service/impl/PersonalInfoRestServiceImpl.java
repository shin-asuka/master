package com.vipkid.rest.portal.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.rest.portal.model.TeachingInfoData;
import com.vipkid.rest.portal.service.PersonalInfoRestService;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.security.SHA256PasswordEncoder;
import com.vipkid.trpm.service.passport.RemberService;
import com.vipkid.trpm.service.portal.PersonalInfoService;
import com.vipkid.trpm.service.rest.LoginService;
import com.vipkid.trpm.util.CookieUtils;
@Service
public class PersonalInfoRestServiceImpl implements PersonalInfoRestService {
	private static final Logger logger = LoggerFactory.getLogger(PersonalInfoRestServiceImpl.class);
	
	public static final int USERNAME_MIN_SIZE = 6;
	public static final String USERNAME_PATTERN = "^\\w+$";
	
	@Autowired
	private PersonalInfoService personalInfoService;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private TeacherDao teacherDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
    private SHA256PasswordEncoder mSHA256PasswordEncoder;
	
	@Autowired
	private RemberService remberService;
	
	public Map<String, Object> getTeachingInfoData(long teacherId){
//		Teacher teacher = loginService.getTeacher();
//		if(teacherId != teacher.getId()){
//			return ApiResponseUtils.buildErrorResp(1001, "教师id非法");
//		}
		Teacher teacher = teacherDao.findById(teacherId);
		User user = userDao.findById(teacherId);
		if(null == teacher || null == user){
			return ApiResponseUtils.buildErrorResp(1001, "教师id非法");
		}
		TeachingInfoData data = new TeachingInfoData();
		Map<String, Object> map = personalInfoService.personalTeaching(teacherId);//复用以前代码获取teacherCertificatedCourseName数据
		
		data.setTeacherSerialNum(teacher.getSerialNumber());
		data.setTeacherType(teacher.getType());
		data.setLifeCycle(teacher.getLifeCycle());
		data.setStatus(user.getStatus());
		data.setTeachingExperience(teacher.getTeachingExperience());
		data.setCertificates(teacher.getCertificateFiles());
		data.setTrainingProfile((String) map.get("teacherCertificatedCourseName"));
		
		Date contractStartDate = teacher.getContractStartDate();
		Date contractEndDate = teacher.getContractEndDate();
		if(null != contractStartDate && null != contractEndDate){
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			data.setContractStartDate(df.format(contractStartDate));
			data.setContractEndDate(df.format(contractEndDate));
		}
		
		data.setCurrency(teacher.getCurrency());
		
		return ApiResponseUtils.buildSuccessDataResp(data);
	}
	

	public Map<String, Object> changePassword(long teacherId, String currentPassword, String newPassword, HttpServletRequest request,HttpServletResponse response){
//		Teacher teacher = loginService.getTeacher();
//		User user = loginService.getUser();
//		if(teacherId != teacher.getId()){
//			return ApiResponseUtils.buildErrorResp(1001, "教师id非法");
//		}
		User user = userDao.findById(teacherId);
		Teacher teacher = teacherDao.findById(teacherId);
		
		if(null == user || null == teacher){
			return ApiResponseUtils.buildErrorResp(1001, "教师id非法");
		}
		
		// 验证密码格式
		if (null == newPassword || newPassword.length() < USERNAME_MIN_SIZE || !newPassword.matches(USERNAME_PATTERN)) {
			logger.warn("老师越过前端限制修改密码，输入非法的密码格式。teacherId = {}, newPassword = {}", teacherId, newPassword);
			return ApiResponseUtils.buildErrorResp(1001, "非法请求，密码格式不对");
		}
		
		Map<String, Object> data = Maps.newHashMap();
		
		 // 验证用户密码
        String encodedPassword = mSHA256PasswordEncoder.encode(currentPassword);
        
		boolean a = false;
		a = encodedPassword.equals(user.getPassword());
       
        if (!a) {
        	data.put("isSuccess", false);
        	data.put("errCode", 1);
            return ApiResponseUtils.buildSuccessDataResp(data);//原密码输入错误
        }
       
        // 执行密码修改操作
        String encodedNewPassword = mSHA256PasswordEncoder.encode(newPassword);
        Map<String, Object> map = personalInfoService.doChangePassword(teacherId, encodedNewPassword);
        if((Boolean) map.get("action")){
        	data.put("isSuccess", true);
        	data.put("errCode", 0);
        	remberService.delkeys(request, response);
	        CookieUtils.removeCookie(response, CookieKey.TRPM_CHANGE_WINDOW, null, null);
    		return ApiResponseUtils.buildSuccessDataResp(data);//修改成功
        }else{
        	logger.error("教师修改密码失败。teacherId = {}, newPassword = {}", teacherId, newPassword);
        	return ApiResponseUtils.buildErrorResp(1001, "服务器端错误，导致教师修改密码错误");
        }
        
		
	}
}
