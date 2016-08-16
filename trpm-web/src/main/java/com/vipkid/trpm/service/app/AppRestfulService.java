package com.vipkid.trpm.service.app;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Lists;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.*;
import com.vipkid.trpm.entity.app.AppEnum;
import com.vipkid.trpm.entity.app.AppEnum.LifeCycle;
import com.vipkid.trpm.entity.app.AppTeacher;
import com.vipkid.trpm.util.AppUtils;
import com.vipkid.trpm.util.DateUtils;

@Service
public class AppRestfulService {

	private static Logger logger = LoggerFactory.getLogger(AppRestfulService.class);

	@Autowired
	private TeacherDao teacherDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private TeacherAddressDao teacherAddressDao;

	@Autowired
	private TeacherLocationDao teacherLocationDao;

	@Autowired
	private TeacherCertificatedCourseDao teacherCertificatedCourseDao;

	@Autowired
	private StudentDao studentDao;

	@Autowired
	private AppRestfulDao appRestfulDao;
	
	@Autowired
	private AssessmentReportDao assessmentReportDao;
	
	/**
	 * token -- userId
	 * 
	 * @Author:ALong (ZengWeiLong)
	 * @param user
	 * @return String
	 * @date 2016年6月7日
	 */
	public String saveUpdateToken(User user) {
		if (user == null){
		    logger.error("getToken fail,user is null,value:" + user);
			return null;
		}
		Map<String,Object> tokenMap = this.appRestfulDao.findAppTokenByTeacherId(user.getId());
		if(tokenMap == null || tokenMap.isEmpty()){
		    String appToken = UUID.randomUUID().toString();
		    this.appRestfulDao.saveAppToken(user.getId(), appToken);
		    logger.info("getToken by save:" + appToken + ",value:" + user.getId());
		    return appToken;
		}
		String token = tokenMap.get("appToken")+"";
		if(StringUtils.isBlank(token)){
		    logger.error("getToken fail,token is blank,userid: " +user.getId()+ " value:" + token);
		    return null;
		}
		return token;
	}

	/**
	 * 通过Token 获取 userId
	 * 
	 * @Author:ALong (ZengWeiLong)
	 * @param token
	 * @return String
	 * @date 2016年6月7日
	 */
	public String getUserIdByToken(String token) {
		if (StringUtils.isBlank(token)){
		    logger.error("getId fail,token is blank,token: " + token);
			return null;
		}
		Map<String,Object> tokenMap = this.appRestfulDao.findIdByAppToken(token);
		if(tokenMap == null || tokenMap.isEmpty()){
		    logger.error("getId fail,database is null " + tokenMap);
		    return null;
		}
	    String userId = tokenMap.get("teacherId")+"";
        if(StringUtils.isBlank(userId)){
            logger.error("getId fail,token is blank,userid: " +userId+ " value:" + token);
            return null;
        }
		return userId;
	}

	public AppTeacher findByTeacherId(Teacher teacher) {
		if (teacher == null)
			return null;
		User user = userDao.findById(teacher.getId());
		if (user == null)
			return null;
		AppTeacher appTeacher = new AppTeacher();
		appTeacher.setId(teacher.getId());
		appTeacher.setShortName(user.getName());
		appTeacher.setFullName(teacher.getRealName());
		appTeacher.setEmail(teacher.getEmail());
		appTeacher.setMobile(teacher.getPhoneNationCode() + " " + teacher.getMobile());
		appTeacher.setSkype(teacher.getSkype());
		appTeacher.setAvatar(teacher.getAvatar());
		// 性别组装
		try {
			appTeacher.setGender(AppEnum.Gender.valueOf(user.getGender()).val());
		} catch (Exception e) {
			logger.error("User的Gender字段为空，主动设置默认值,teacherId:" + teacher.getId());
			appTeacher.setGender(AppEnum.Gender.MALE.val());
		}
		appTeacher.setCountry(teacher.getCountry());
		// 地址组装
		if (teacher.getCurrentAddressId() > 0) {
			TeacherAddress teacherAddress = teacherAddressDao.findById(teacher.getCurrentAddressId());
			if(teacherAddress != null){
    			StringBuilder address = new StringBuilder("");
    			if (teacherAddress.getCountryId() > 0) {
    				TeacherLocation teacherLocation = teacherLocationDao.findById(teacherAddress.getCountryId());
    				address.append(teacherLocation.getName() + " ");
    			}
    			if (teacherAddress.getStateId() > 0) {
    				TeacherLocation teacherLocation = teacherLocationDao.findById(teacherAddress.getStateId());
    				address.append(teacherLocation.getName() + " ");
    			}
    			if (teacherAddress.getCity() > 0) {
    				TeacherLocation teacherLocation = teacherLocationDao.findById(teacherAddress.getCity());
    				address.append(teacherLocation.getName() + " ");
    			}
    			address.append(teacherAddress.getStreetAddress() + " ").append(teacherAddress.getZipCode() + " ");
			}else{
			    appTeacher.setAddress(teacher.getAddress());
			}
		} else {
			appTeacher.setAddress(teacher.getAddress());
		}
		appTeacher.setTimeZone(teacher.getTimezone());
		appTeacher.setCertificates(this.teaching(teacher.getId()));
		appTeacher.setLifeCycle(LifeCycle.valueOf(teacher.getLifeCycle().toUpperCase()).val());
        String contractDuration = "";
		Date contractStart = teacher.getContractStartDate();
        Date contractEnd = teacher.getContractEndDate();
        if(contractStart != null){
            contractDuration += contractStart.getTime();    
        }
        if(contractEnd != null){
            contractDuration += ":"+contractEnd.getTime();   
        }
        appTeacher.setContractDuration(contractDuration);
        appTeacher.setIntroduction(teacher.getIntroduction());
		return appTeacher;
	}

	public String teaching(long teacherId) {
		List<Map<String, Object>> certificatedCourses = teacherCertificatedCourseDao.findCertificatedCourseNameByTeacherId(teacherId);
		if(certificatedCourses == null || certificatedCourses.isEmpty()){
		    return "";
		}
		StringBuffer teacherCertificatedCourseName = new StringBuffer("");
		for (Map<String, Object> map:certificatedCourses) {
		    try{
		        teacherCertificatedCourseName.append(AppEnum.CourseType.valueOf((map.get("courseType") + "").toUpperCase()).val()+ ",");
		    }catch(Exception e){
		        logger.info("枚举无法匹配的课程类型："+ map.get("courseType"));
		    }
        }
		String result = teacherCertificatedCourseName.toString();
		if(result.indexOf(",") > 0) result = result.substring(0, result.length()-1);
		return result;
	}
	
	/**一段时间内老师的课以天为单位的分布情况1*/
    public List<Map<String, Object>> getCountOnlineClass(long teacherId,String classStatus,String classType){
        Teacher teacher = this.teacherDao.findById(teacherId);
        if(teacher == null){
            logger.error("Teacher 不存在:id= " + teacherId);
            return Lists.newArrayList();
        }
        String timezone = teacher.getTimezone();
        long lstartTime = System.currentTimeMillis();
        long lendTime =  lstartTime + 7*24*3600*1000;
        return getCountOnlineClass(teacherId, timezone, lstartTime, lendTime, classStatus, classType);
    }
	
	/**一段时间内老师的课以天为单位的分布情况2*/
	public List<Map<String, Object>> getCountOnlineClass(long teacherId,String timezone,long lstartTime,long lendTime,String classStatus,String classType){
	    String startTime = AppUtils.converTimezone(lstartTime, timezone);
	    String endTime = AppUtils.converTimezone(lendTime, timezone);
	    String[] classStatuses =  AppUtils.coverClassStatus(classStatus);
	    String[] classTypes = AppUtils.coverClassType(classType);
	    List<Map<String, Object>> list = this.appRestfulDao.appRestfulCountOnlineClass(teacherId, timezone, startTime, endTime,classStatuses,classTypes);
	    try {
            list = AppUtils.converPushList(startTime,endTime,timezone,list);
        } catch (ParseException e) {
            logger.error(e.getMessage(),e);
        }
	    return list;
	}
	
	
	/**获取一段时间内的课列表*/
    public List<AppOnlineClass> getClassList(long teacherId,long startTime,long endTime,Integer order,String classStatus,String classType){
        String[] classStatuses =  AppUtils.coverClassStatus(classStatus);
        String[] classTypes = AppUtils.coverClassType(classType);
        List<AppOnlineClass> list = this.appRestfulDao.appRestfulListOnlineClass(teacherId, startTime, endTime,order,classStatuses,classTypes);
        list = converPushClass(list);
        return list;
    }
    
    /**老师的某种状态的课列表*/
    public List<AppOnlineClass> getClassListPage(long teacherId,long start,long limit,long order,int classStatus,String classType){
        String[] classTypes = AppUtils.coverClassType(classType);
        List<AppOnlineClass> list = this.appRestfulDao.appRestfulListForPage(teacherId,start,limit,order,classStatus,classTypes);
        list = converPushClass(list);
        return list;
    }
    
    public Map<String,Object> getClassListCount(long teacherId,long classStatus, String classType){
        String[] classTypes = AppUtils.coverClassType(classType);
        return this.appRestfulDao.appRestfulListForCount(teacherId, classStatus, classTypes);   
    }
    
    /**
     * 查询AppOnlineClasslist中feedback和uaReport
     * @Author:ALong (ZengWeiLong)
     * @param list
     * @return 
     * List<AppOnlineClass>
     * @date 2016年6月12日
     */
    public List<AppOnlineClass> converPushClass(List<AppOnlineClass> list){
        long startcount = System.currentTimeMillis();
        if(list != null && !list.isEmpty()){
            List<Long>  onlineClassIds = Lists.newArrayList();
            list.stream().map(obj -> obj.getId()).forEach(onlineClassIds::add);
            //查询feedback
            List<Map<String,Object>> objectList = this.appRestfulDao.selectFeedback(onlineClassIds);
            for (int i = 0; i < list.size();i++) {
                AppOnlineClass aoc = list.get(i);
                if(aoc.getSerialNumber().startsWith("C") && ((aoc.getSerialNumber().endsWith("6") || aoc.getSerialNumber().endsWith("12")))){
                    aoc.setUnitAssessmentStatus(1);
                    AssessmentReport amr = null;
                    if(DateUtils.isSearchById(aoc.getStartTime())){
                        amr = assessmentReportDao.findReportByClassId(aoc.getId());
                    }else{
                        amr = this.assessmentReportDao.findReportByStudentIdAndName(aoc.getSerialNumber(), aoc.getStudentId());
                    }
                    if(amr != null && StringUtils.isNotBlank(amr.getUrl())){
                        aoc.setUnitAssessmentStatus(2);
                    }
                }
                for (int j = 0; j < objectList.size(); j++) {
                    Map<String,Object> tmpMap = objectList.get(j);
                    if((long)tmpMap.get("onlineClassId") == aoc.getId() && tmpMap.get("teacherFeedback") != null && StringUtils.isNotEmpty(tmpMap.get("teacherFeedback").toString())){
                        aoc.setHasFeedback(1);
                        objectList.remove(j);
                    }
                }
                aoc.setStatus(AppEnum.ClassStatus.valueOf(aoc.getStatus().toUpperCase()).val().toString());
                if(StringUtils.isNotBlank(aoc.getStatusInfo())){
                    aoc.setStatusInfo(AppEnum.StatusInfo.valueOf(aoc.getStatusInfo().toUpperCase()).val().toString());
                }
                aoc.setCourseType(AppEnum.CourseType.valueOf(aoc.getCourseType().toUpperCase()).val().toString());
                list.set(i, aoc);
            }            
        }
        long endcount = System.currentTimeMillis();
        logger.info(" converPushClass 算法用时:" + (endcount - startcount) + " 毫秒");
        return list;
    }	

	public List<Map<String, Object>> getStudents(String[] ids) {
		return studentDao.findStudentsBy(ids);
	}

}
