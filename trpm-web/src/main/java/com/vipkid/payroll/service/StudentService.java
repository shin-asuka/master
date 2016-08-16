package com.vipkid.payroll.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.trpm.dao.StudentDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Student;
import com.vipkid.trpm.entity.User;

@Service
public class StudentService {

	private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private StudentDao studentDao;
	
	/**
	 * 查询在线教室第一个学生
	 * @param onlineClassId
	 * @return
	 */
	public Student getFirstStudentByOnlineClass(Long onlineClassId){
		Student student = null;
		if(onlineClassId!=null){
			List<Student> list = studentDao.findStudentByOnlineClassId(onlineClassId);
			if(CollectionUtils.isNotEmpty(list)){
				student = list.get(0);
				User user = userDao.findById(student.getId());
				student.setName(user.getName());
				student.setCreateDateTime(user.getCreateDateTime());
			}
		}
		logger.info("通过在线课堂ID获取学生信息  onlineClassId = {} , student = {}",onlineClassId,student);
		return student;
	}
	
	public Student getById(Long studentId){
		Student student = null;
		if(studentId!=null){
			student = studentDao.findById(studentId);
			User user = userDao.findById(student.getId());
			student.setName(user.getName());
			student.setCreateDateTime(user.getCreateDateTime());
		}
		return student;
	}
	
	public Boolean findIsPaidByStudentIdAndPayDate(Long studentId,String paidDateTime){
		
		Boolean isPaidForTrial = false;
		List<Map<String, Object>> list = studentDao.findOrderListByStudentIdAndPaidDateTime(studentId, paidDateTime);
		if(CollectionUtils.isNotEmpty(list)){
			isPaidForTrial = true;
		}
		if(isPaidForTrial == false){ //如果新版订单不存在，查询旧版订单
			list = studentDao.findOldOrderListByStudentIdAndPaidDateTime(studentId, paidDateTime);
			if(CollectionUtils.isNotEmpty(list)){
				isPaidForTrial = true;
			}
		}
		return isPaidForTrial;
	}
}
