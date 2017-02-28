package com.vipkid.portal.classroom.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.community.http.client.HttpClientProxy;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.util.Maps;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.OnlineClassEnum.CourseName;
import com.vipkid.portal.classroom.model.ClassRoomVo;
import com.vipkid.portal.classroom.model.SendSysInfoVo;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.dto.InfoRoomDto;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.dao.AuditDao;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.StudentDao;
import com.vipkid.trpm.dao.StudentExamDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Student;
import com.vipkid.trpm.entity.StudentExam;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.entity.teachercomment.TeacherComment;
import com.vipkid.trpm.proxy.OnlineClassProxy;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.util.DateUtils;
import com.vipkid.trpm.util.FilesUtils;
import com.vipkid.trpm.util.IpUtils;
import com.vipkid.trpm.util.LessonSerialNumber;

@Service
public class ClassroomService {

	private static Logger logger = LoggerFactory.getLogger(ClassroomService.class);
	
	@Autowired
	private OnlineClassDao onlineClassDao;
	
	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private LessonDao lessonDao;
	
	@Autowired
	private UserDao userDao;
	
    @Autowired
    private StudentExamDao studentExamDao;
	
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    
    @Autowired
    private AuditDao auditDao;
    
  
	public InfoRoomDto getInfoRoom(ClassRoomVo bean , Teacher teacher){
		
		Student student = this.studentDao.findById(bean.getStudentId());
		//Student Info
		InfoRoomDto resultDto = new InfoRoomDto();
		if(student != null){
			resultDto.setStudentId(student.getId());
			resultDto.setStudentEnglishName(student.getEnglishName());
			User userStudent = userDao.findById(student.getId());
			resultDto.setCreateTime(userStudent.getCreateDateTime());
		}else{
			logger.warn("student is null,onlineClassId:{},studentId:{}",bean.getOnlineClassId(), bean.getStudentId());
		}
		
		//Teacher Info
		OnlineClass onlineClass = this.onlineClassDao.findById(bean.getOnlineClassId());
		if(onlineClass != null){
			resultDto.setOnlineClassId(onlineClass.getId());
			resultDto.setClassroom(onlineClass.getClassroom());
			resultDto.setScheduleTime(onlineClass.getScheduledDateTime());
			resultDto.setSupplierCode(onlineClass.getSupplierCode());
		}else{
			logger.warn("onlineClass is null,onlineClassId:{},studentId:{}",bean.getOnlineClassId(), bean.getStudentId());
		}
		
		//Lesson Info
		Lesson lesson = this.lessonDao.findById(onlineClass.getLessonId());
		if(lesson != null){
			resultDto.setLessonName(lesson.getName());
			resultDto.setSerialNumber(lesson.getSerialNumber());
			resultDto.setObjective(lesson.getObjective());
			resultDto.setVocabularies(lesson.getVocabularies());
			resultDto.setSentencePatterns(lesson.getSentencePatterns());
            resultDto.setPrevip(obtainPrevip(lesson.getSerialNumber()));
            resultDto.setUa(obtainUa(onlineClass.getId()));
            resultDto.setCourseType(OnlineClassEnum.CourseName.obtainCourseName(lesson.getSerialNumber()));
            if(CourseName.PRACTICUM1.show().equals(resultDto.getCourseType()) || CourseName.PRACTICUM2.show().equals(resultDto.getCourseType())){
            	TeacherApplication application = teacherApplicationDao.findApplictionByOlineclassId(bean.getOnlineClassId(), teacher.getId());	
            	if(application != null){
            		resultDto.setTeacherApplicationId(application.getId());
            	}
            }
        }else{
			logger.warn("lesson is null,onlineClassId:{},studentId:{}",bean.getOnlineClassId(), bean.getStudentId());
		}
		
		//Other Info
		int stars = 0;
		TeacherComment comment = teacherService.findByStudentIdAndOnlineClassId(bean.getStudentId(),bean.getOnlineClassId());
		if(comment != null){
			stars = comment.getStars();
		}else{
			logger.warn("课程还没有TeacherComment,onlineClassId:{},studentId:{}",bean.getOnlineClassId(), bean.getStudentId());
		}
		
		resultDto.setTeacherName(teacher.getRealName());
		resultDto.setStars(stars);
		resultDto.setServerTime(new Timestamp(System.currentTimeMillis()));
		resultDto.setIsReplay(!OnlineClassEnum.ClassStatus.isBooked(onlineClass.getStatus()));
		resultDto.setSysInfoUrl(PropertyConfigurer.stringValue("sys.info.url"));
		resultDto.setMicroserviceUrl(PropertyConfigurer.stringValue("microservice.url"));
		return resultDto;
		
	}
	
	/**
	 * 获取学生信息 open
	 * @param studentId
	 * @param serialNum
	 * @return
	 */
	public Map<String,Object> getInfoStudent(long studentId, String serialNum){
		
		Map<String,Object> resultMap = Maps.newHashMap();
		
		Student student = studentDao.findById(studentId);
		Map<String,Object> studentMap = Maps.newHashMap();
		studentMap.put("qq", student.getQq());
		studentMap.put("englishName", student.getEnglishName());
		studentMap.put("knowTheStudent", student.getKnowTheStudent());
        // 查询学生个人信息
		resultMap.put("student",studentMap);

        // 查询学生考试情况
        StudentExam studentExam = studentExamDao.findStudentExamByStudentId(studentId);

        // 处理考试名称
        resultMap.put("studentExam", this.handleExamLevel(studentExam, serialNum));

        // 查询教师评价
        resultMap.put("teacherComments",teacherService.findTCByStudentIdAndGroupByOnlineClassId(String.valueOf(studentId)));
        
        return resultMap;
		
	}
	
	/**
	 * 获取教室的URL
     * @param onlineClassId
     * @param teacher
     * @return
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> getClassRoomUrl(long onlineClassId,Teacher teacher){
    	
    	Map<String,Object> result = Maps.newHashMap();
    	
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);

        //课程没有找到，无法book
        if(onlineClass == null){
            logger.error(" 教室为空 NULL onlineClassId:{}",onlineClassId);
            result.put("info", " online class is null. ");
            return result;
        }

        //判断教室是否创建好
        if(StringUtils.isBlank(onlineClass.getClassroom())){
            logger.error(" 教室为空 NULL onlineClassId:{}",onlineClassId);
            result.put("info", " online class is not have creater. ");
            return result;
        }
        
        //判断该教室是否属于该老师
        if(onlineClass.getTeacherId() != teacher.getId()){
        	logger.error(" You cannot enter this classroom! onlineClassId:{}",onlineClassId);
            result.put("info", " You cannot enter this classroom! ");
            return result;
        }

        Map<String,Object> urlResult = OnlineClassProxy.generateRoomEnterUrl(String.valueOf(teacher.getId()), teacher.getRealName(),
                onlineClass.getClassroom(), OnlineClassProxy.RoomRole.TEACHER, onlineClass.getSupplierCode(),onlineClass.getId(),OnlineClassProxy.ClassType.MAJOR);
        
        if(ReturnMapUtils.isSuccess(urlResult)){
        	logger.info(" 获取教室url 成功 onlineClassId:{},url:{}",onlineClassId, urlResult.get("url"));
        	result.put("link",urlResult.get("url"));
        	return result;
        }else{
        	logger.error(" 获取教室url 失败 onlineClassId:{},info:{}",onlineClassId,urlResult.get("info"));
        	result.put("info", urlResult.get("info"));
        	return result;
        }
    }
    
    
    /**
     * 教室变更切换检查
     * @param onlineClassId
     * @return
     */
    public Map<String, Object> roomChange(String onlineClassId){
	    Map<String,Object> resultMap = Maps.newHashMap();
	    String requestUrl = PropertyConfigurer.stringValue("microservice.url") + "/classroom/onlineClassSupplierCode";
	    Map<String, String> pram = Maps.newHashMap();
	    pram.put("onlineClassId", onlineClassId);
	    String resultBody = HttpClientProxy.get(requestUrl, pram,  Maps.newHashMap());
	    logger.info("请求URL:"+requestUrl+",参数onlineClassId:" + onlineClassId+",请求结果result:"+resultBody);
	    if(resultBody != null){
	       JsonNode jnode = JsonTools.readValue(resultBody);
	       if(jnode != null && jnode.get("supplierCode") != null){ 
	           try{
	               int supplierCode  = jnode.get("supplierCode").asInt();
	               resultMap.put("supplierCode", supplierCode);
	           }catch(Exception e){
	        	   resultMap.put("info","supplierCode:返回参数不是整型,valule="+jnode.get("supplierCode")+",Error-info"+e.getMessage());
	               logger.error(resultMap.get("info")+",Error-info"+e.getMessage(),e);
	           }
	       }else{
	    	   resultMap.put("info","返回内容为空,resultBody=" + resultBody + ",onlineClassId:" + onlineClassId);
	           logger.error(""+resultMap.get("info"));
	       }
	    }else{
	    	resultMap.put("info","返回内容为null,resultBody=" + resultBody + ",onlineClassId:" + onlineClassId);
	        logger.error(""+resultMap.get("info"));
	    }
	    return resultMap;
    }
    
    
    /**
     * 向Appserver发送帮助请求<br/>
     * 上课期间可以发送帮助请求，非上课期间不能发送
     *
     * @Title: sendHelp
     * @param scheduleTime
     * @param onlineClassId
     * @param teacher
     * @return Map<String,Object>
     * @date 2016年1月11日
     */
    public Map<String, Object> sendHelp(String scheduleTime, long onlineClassId, Teacher teacher) {
        Map<String, Object> modelMap = Maps.newHashMap();
        /* 获取服务器时间毫秒 */
        long serverMillis = System.currentTimeMillis();

        /* 计算schedule时间毫秒 */
        Timestamp ldtSchedule = DateUtils.parseFrom(scheduleTime, DateUtils.FMT_YMD_HMS);
        long scheduleMillis = ldtSchedule.getTime();

        /* 判断时间间隔是否在上课时间段之内，如果是则发送帮助请求 */
        long interval = serverMillis - scheduleMillis;

        /* 在30分钟之内可以发送帮助请求 */
        if (0 <= interval && interval <= 30 * 60 * 1000) {
            /* 请求参数 */
            Map<String, String> requestParams = new HashMap<String, String>();
            requestParams.put("onlineClassId", String.valueOf(onlineClassId));
            /* 请求头设置 */
            String t = "TEACHER " + teacher.getId();
            Map<String, String> requestHeader = new HashMap<String, String>();
            requestHeader.put("Authorization", t + " " + DigestUtils.md5Hex(t));

            // Change HTTP POST to Get 2016-11-03
            String content = HttpClientProxy.get(ApplicationConstant.HELP_URL, requestParams, requestHeader);
            logger.info("### Request help return content: {}", content);
            if (StringUtils.isBlank(content)) {
                modelMap.put("info", "Request failed, please contact manager!");
            }
        } else {
            modelMap.put("info", "Sorry, you can only use this function during class time.");
        }
        return modelMap;
    }
	
    
    /**
     * 老师进入教室后 ，通过该方法通知appserver
     *
     * @Title: sendTeacherInClassroom
     * @param requestParams
     * @param teacher
     * @return Map<String,Object>
     * @date 2016年1月11日
     */
    public Map<String, Object> sendTeacherInClassroom(Map<String, String> requestParams, Teacher teacher) {
        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.put("status", false);

        String t = "TEACHER " + teacher.getId();
        Map<String, String> requestHeader = new HashMap<String, String>();
        requestHeader.put("Authorization", t + " " + DigestUtils.md5Hex(t));
        String content = HttpClientProxy.get(ApplicationConstant.TEACHER_IN_CLASSROOM_URL, requestParams,requestHeader);

        logger.info("### Mark that teacher enter classroom: {}", content);
        logger.info("### Sent get request to {} with params {}", ApplicationConstant.TEACHER_IN_CLASSROOM_URL,
                requestParams.get("onlineClassId"));
        if (!StringUtils.isNotEmpty(content)) {
            modelMap.put("status", true);
        } else {
            modelMap.put("msg", "failed to tell the fireman teacher in the classroom!");
        }
        return modelMap;
    }

    /**
     * 老师进入教室后 ，通过该方法发送老师浏览器数据相关信息到 appserver 
     * @param bean
     * @param teacher
     * @param request
     * @return
     */
    public Map<String, Object> sendSysInfo(SendSysInfoVo bean, Teacher teacher,HttpServletRequest request) {
        String t = "TEACHER " + teacher.getId();
        Map<String, String> requestHeader = new HashMap<String, String>();
        requestHeader.put("Authorization", t + " " + DigestUtils.md5Hex(t));
        
        requestHeader.put("User-Agent", request.getHeader("User-Agent"));
        requestHeader.put("x-forwarded-for", IpUtils.getIpAddress(request));
        
        Map<String, String> requestParams = Maps.newHashMap();
        requestParams.put("userId", teacher.getId()+"");
        requestParams.put("classroom", bean.getClassroom());
        requestParams.put("onlineClassId", bean.getOnlineClassId()+"");
        
        String content = HttpClientProxy.get(ApplicationConstant.TEACHER_SYSTEM_INFO_URL, requestParams,requestHeader);

        logger.info("### Mark that teacher enter classroom: {}", content);
        logger.info("### Sent get request to {} with params {}", ApplicationConstant.TEACHER_SYSTEM_INFO_URL, requestParams.get("onlineClassId"));
        
        Map<String, Object> modelMap = Maps.newHashMap();
        if (StringUtils.isBlank(content)) {
        	logger.error("### Failed to tell the fireman teacher browser info Sent get request to {} with params {}", ApplicationConstant.TEACHER_SYSTEM_INFO_URL, requestParams.get("onlineClassId"));
            modelMap.put("info", "failed to tell the fireman teacher browser info!");
        }
        return modelMap;
    }
    
    /**
     * 退出教室，记录日志
     *
     * @Author:ALong
     * @Title: exitclassroom
     * @param onlineClassId
     * @param teacher
     * @return void
     * @date 2016年1月11日
     */
    public void exitclassroom(long onlineClassId, Teacher teacher) {
        /* 退出教室记录日志 */
        Map<String, Object> parmMap = Maps.newHashMap();
        parmMap.put("teacherId", teacher.getId());
        parmMap.put("teacherName", teacher.getRealName());
        parmMap.put("onlineClassId", onlineClassId);
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
        parmMap.put("roomId", onlineClass.getClassroom());
        String content = FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.CLASSROOM_EXIT, parmMap);
        auditDao.saveAudit(ApplicationConstant.AuditCategory.CLASSROOM_EXIT, "INFO", content, teacher.getRealName(),
                teacher, IpUtils.getRemoteIP());
    }
    
    /**
     * 修改onlineClass的状态和完成类型
     *
     * @param onlineClassId
     */
    public void exitOpenclass(long onlineClassId, Teacher teacher) {
    	this.exitclassroom(onlineClassId, teacher);
    	onlineClassDao.updateEntity(new OnlineClass().setId(onlineClassId).setStatus("FINISHED").setFinishType("AS_SCHEDULED"));
    }
    
    
    /**
    *
    * @Title: sendStarlogs
    * @param send
    * @param studentId
    * @param onlineClassId
    * @param teacher
    * @date 2016年1月11日
    */
   public Map<String,Object> sendStarlogs(boolean send, ClassRoomVo bean, Teacher teacher) {
	   Map<String,Object> resultMap = Maps.newHashMap();
       Student student = studentDao.findById(bean.getStudentId());
       OnlineClass onlineClass = onlineClassDao.findById(bean.getOnlineClassId());

       /* 记录操作日志 */
       Map<String, Object> parmMap = Maps.newHashMap();

       parmMap.put("teacherId", teacher.getId());
       parmMap.put("teacherName", teacher.getRealName());

       parmMap.put("studentId", student.getId());
       parmMap.put("studentName", student.getEnglishName());

       parmMap.put("onlineClassId", onlineClass.getId());
       parmMap.put("roomId", onlineClass.getClassroom());

       if (send) {
           String content = FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.STAR_SEND, parmMap);
           auditDao.saveAudit(ApplicationConstant.AuditCategory.STAR_SEND, "INFO", content, teacher.getRealName(),
                   teacher, IpUtils.getRemoteIP());
           logger.info("Teacher: id={},name={} send star, Student: id={},name={}, onlineClassId: id={},room={}",
                   teacher.getId(), teacher.getRealName(), bean.getStudentId(), student.getEnglishName(), bean.getOnlineClassId(),
                   onlineClass.getClassroom());
       } else {
           String content = FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.STAR_REMOVE, parmMap);
           auditDao.saveAudit(ApplicationConstant.AuditCategory.STAR_REMOVE, "INFO", content, teacher.getRealName(),
                   teacher, IpUtils.getRemoteIP());
           logger.info("Teacher: id={},name={} remove star, Student: id={},name={}, onlineClassId: id={},room={}",
        		   teacher.getId(), teacher.getRealName(), bean.getStudentId(), student.getEnglishName(), bean.getOnlineClassId(),
                   onlineClass.getClassroom());
       }
       resultMap.put("status", true);
       return resultMap;
   }
    
	/**
     * 根据serialNum处理 考试的Level名称显示<br/>
     * studentExam 为NULL 则返回一个空对象
     *
     * @Author:ALong
     * @Title: handleExamLevel
     * @param studentExam
     * @param serialNum
     * @return StudentExam
     * @date 2016年1月12日
     */
    private StudentExam handleExamLevel(StudentExam studentExam, String serialNum) {
        logger.info("ReportController: handleExamLevel() 参数为：serialNum={}, studentExam={}", serialNum, JSON.toJSONString(studentExam));
        // studentExam 不为空则进行替换逻辑
        if (studentExam != null) {
            // ExamLevel 不为空则进行替换逻辑
            if (studentExam.getExamLevel() != null) {
                String lowerCase = studentExam.getExamLevel().toLowerCase();
                String examLevel = "No Computer Test result.";
                if ("l1u0".equals(lowerCase)) {
                    studentExam.setExamLevel("Computer Test result  is Level 0 Unit 0");
                }else if(lowerCase.equals("l1u1")){
                    examLevel = "Computer Test result is L1U1(PreVIP).";
                } else if (lowerCase.startsWith("l")) {
                    examLevel= "Computer Test result is " + lowerCase.replaceAll("l", "Level ").replaceAll("u", " Unit ") + ".";
                }
                if (serialNum != null) {
                    switch (serialNum) {
                        case ApplicationConstant.TrailLessonConstants.L0:
                            if(StringUtils.equals(examLevel,"No Computer Test result.")){
                                examLevel = examLevel +" Please use the PreVIP courseware.";
                            }else{
                                examLevel = " Please ignore the Computer Test result and use the PreVIP courseware.";
                            }
                            break;
                        case ApplicationConstant.TrailLessonConstants.L1:
                            if(StringUtils.equals(examLevel,"No Computer Test result.") || StringUtils.equals(examLevel,"Computer Test result is L1U1(PreVIP).")){
                                examLevel = examLevel +" Please use the Level 2 Unit 01 courseware.";
                            }else{
                                examLevel = examLevel + " Please use the "+ lowerCase.replace("l","Level ").replace("u"," Unit ") + " courseware.";
                            }
                            break;
                        case ApplicationConstant.TrailLessonConstants.L2:
                            if(StringUtils.equals(examLevel,"No Computer Test result.")|| StringUtils.equals(examLevel,"Computer Test result is L1U1(PreVIP).")){
                                examLevel = examLevel +" Please use the use Level 2 Unit 04 courseware.";
                            }else{
                                examLevel = examLevel + " Please use the "+ lowerCase.replace("l","Level ").replace("u"," Unit ") + " courseware.";
                            }
                            break;
                        case ApplicationConstant.TrailLessonConstants.L3:
                            if(StringUtils.equals(examLevel,"No Computer Test result.") || StringUtils.equals(examLevel,"Computer Test result is L1U1(PreVIP).")){
                                examLevel = examLevel +" Please use the Level 3 Unit 01 courseware.";
                            }else{
                                examLevel = examLevel + " Please use the "+ lowerCase.replace("l","Level ").replace("u"," Unit ") + " courseware.";
                            }
                            break;
                        case ApplicationConstant.TrailLessonConstants.L4:
                            if(StringUtils.equals(examLevel,"No Computer Test result.") || StringUtils.equals(examLevel,"Computer Test result is L1U1(PreVIP).")){
                                examLevel = examLevel +" Please use the Level 4 Unit 01 courseware.";
                            }else{
                                examLevel = examLevel + " Please use the "+ lowerCase.replace("l","Level ").replace("u"," Unit ") + " courseware.";
                            }
                            break;
                        default:
                            break;
                    }
                }
                studentExam.setExamLevel(examLevel);
            }
        } else {
            // studentExam 为空则返回空对象
            studentExam = new StudentExam();
            // ExamLevel 为空则根据Lession的SerialNum进行处理
            if (serialNum != null) {
                switch (serialNum) {
                    case ApplicationConstant.TrailLessonConstants.L0:
                        studentExam.setExamLevel("No Computer Test result. Please use the PreVIP courseware.");
                        break;
                    case ApplicationConstant.TrailLessonConstants.L1:
                        studentExam.setExamLevel("No Computer Test result. Please use the Level2 Unit1 courseware.");
                        break;
                    case ApplicationConstant.TrailLessonConstants.L2:
                        studentExam.setExamLevel("No Computer Test result. Please use the Level2 Unit4 courseware.");
                        break;
                    case ApplicationConstant.TrailLessonConstants.L3:
                        studentExam.setExamLevel("No Computer Test result. Please use the Level3 Unit1 courseware.");
                        break;
                    case ApplicationConstant.TrailLessonConstants.L4:
                        studentExam.setExamLevel("No Computer Test result. Please use the Level4 Unit1 courseware.");
                        break;
                    default:
                        break;
                }
            }
        }
        return studentExam;
    }

    public boolean obtainPrevip(String lessonSn){
        return LessonSerialNumber.isPreVipkidLesson(lessonSn);
    }

    public boolean obtainUa(Long onlineClassId){
        OnlineClass onlineClass = onlineClassDao.findById(onlineClassId);
        Lesson lesson = lessonDao.findById(onlineClass.getLessonId());
        return lesson.getIsUnitAssessment()==1?true:false;
    }
}