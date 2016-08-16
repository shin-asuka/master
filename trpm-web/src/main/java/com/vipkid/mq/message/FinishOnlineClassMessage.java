/**
 * 
 */
package com.vipkid.mq.message;

import java.io.Serializable;

import com.vipkid.trpm.entity.*;


/**
 * 结束课程消息
 * 
 * @author zouqinghua
 * @date 2016年5月6日 下午3:51:35
 *
 */
public class FinishOnlineClassMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 操作类型
	 * 
	 * @author zouqinghua
	 * @date 2016年5月9日 下午4:27:40
	 *
	 */
	public enum OperatorType {
		FINISH_ONLINECLASS, // 结束课程
		TRIAL_CHANGE_STUDENT, // 列表页试用课换学生
		CANCEL_ONLINECLASS, // 列表页取消课程
		CHANGE_TEACHER, // 列表页换老师
		UNDO_FINISH_ONLINECLASS ,// 列表页重置课程结束状态预约状态
		ADD_TEACHER_COMMENTS , //增加教师评论
		ADD_UNIT_ASSESSMENT  //增加上传UA报告，发送消息
	}

	private String producer = "teacher_portal";
	private OperatorType operatorType; // 操作类型
	private OnlineClassMessage onlineClassMessage; // 在线课程
	private TeacherMessage teacherMessage;// 教师
	private CourseMessage courseMessage; // 教学方案
	private StudentMessage studentMessage; // 学生
	private LessonMessage lessonMessage; // 课程

	public FinishOnlineClassMessage() {
	}

	public void setOnlineClass(OnlineClass onlineClass) {
		if (onlineClass != null && onlineClass.getId() > 0) {
			onlineClassMessage = new OnlineClassMessage(onlineClass.getId());
			onlineClassMessage.setScheduledDateTime(onlineClass.getScheduledDateTime().getTime());
			onlineClassMessage.setCreateDateTime(onlineClass.getBookDateTime()==null?0:onlineClass.getBookDateTime().getTime());
			onlineClassMessage.setFinishType(onlineClass.getFinishType());
			
			onlineClassMessage.setShortNotice(onlineClass.getShortNotice()==0?false:true); //是否是紧急备用课程
		}
	}

	public void setTeacher(Teacher teacher) {
		if (teacher != null && teacher.getId() > 0) {
			teacherMessage = new TeacherMessage(teacher.getId());
			teacherMessage.setSerialNumber(teacher.getSerialNumber());
			teacherMessage.setRealName(teacher.getRealName());
			teacherMessage.setEmail(teacher.getEmail());
			teacherMessage.setContractType(teacher.getContractType());
			Integer basePay = Float.valueOf((teacher.getExtraClassSalary()+8)*100).intValue();
			teacherMessage.setBasePay(basePay);
		}
	}

	public void setStudent(Student student) {
		if (student != null) {
			studentMessage = new StudentMessage(student.getId(), student.getName());
			studentMessage.setCreateDateTime(student.getCreateDateTime()==null?0:student.getCreateDateTime().getTime());
		}
	}

	public void setLesson(Lesson lesson) {
		if (lesson != null) {
			lessonMessage = new LessonMessage(lesson.getId());
			lessonMessage.setName(lesson.getName());
			lessonMessage.setSerialNumber(lesson.getSerialNumber());
			lessonMessage.setNumber(lesson.getNumber());
		}
	}

	public void setCourse(Course course) {
		if (course != null) {
			courseMessage = new CourseMessage(course.getId());
			courseMessage.setSerialNumber(course.getSerialNumber());
			courseMessage.setName(course.getName());
			courseMessage.setType(course.getType());
		}
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public OperatorType getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(OperatorType operatorType) {
		this.operatorType = operatorType;
	}

	public OnlineClassMessage getOnlineClassMessage() {
		return onlineClassMessage;
	}

	public void setOnlineClassMessage(OnlineClassMessage onlineClassMessage) {
		this.onlineClassMessage = onlineClassMessage;
	}

	public TeacherMessage getTeacherMessage() {
		return teacherMessage;
	}

	public void setTeacherMessage(TeacherMessage teacherMessage) {
		this.teacherMessage = teacherMessage;
	}

	public CourseMessage getCourseMessage() {
		return courseMessage;
	}

	public void setCourseMessage(CourseMessage courseMessage) {
		this.courseMessage = courseMessage;
	}

	public StudentMessage getStudentMessage() {
		return studentMessage;
	}

	public void setStudentMessage(StudentMessage studentMessage) {
		this.studentMessage = studentMessage;
	}

	public LessonMessage getLessonMessage() {
		return lessonMessage;
	}

	public void setLessonMessage(LessonMessage lessonMessage) {
		this.lessonMessage = lessonMessage;
	}

}
