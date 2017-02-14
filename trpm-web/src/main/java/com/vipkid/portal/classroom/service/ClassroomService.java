package com.vipkid.portal.classroom.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.api.client.util.Maps;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.portal.classroom.model.ClassRoomVo;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.dto.InfoRoomDto;
import com.vipkid.trpm.constant.ApplicationConstant;
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
			resultDto.setSerialNumber(onlineClass.getSerialNumber());
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
			resultDto.setObjective(lesson.getObjective());
			resultDto.setVocabularies(lesson.getVocabularies());
			resultDto.setSentencePatterns(lesson.getSentencePatterns());
		}else{
			logger.warn("lesson is null,onlineClassId:{},studentId:{}",bean.getOnlineClassId(), bean.getStudentId());
		}
		
		//Other Info
		int stars = 0;
		TeacherComment comment = teacherService.findByStudentIdAndOnlineClassId(bean.getOnlineClassId(), bean.getStudentId());
		if(comment != null){
			stars = comment.getStars();
		}else{
			logger.warn("课程还没有TeacherComment,onlineClassId:{},studentId:{}",bean.getOnlineClassId(), bean.getStudentId());
		}
		
		resultDto.setTeacherName(teacher.getRealName());
		resultDto.setStars(stars);
		resultDto.setIsReplay(!OnlineClassEnum.ClassStatus.isBooked(onlineClass.getStatus()));
		
		return resultDto;
	}
	
	
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
     * 用户interview进教室
     * 1.课程合法性验证
     * 2.必须是处于Interview的待上课的老师可以获取URL
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
	
    
    
}
