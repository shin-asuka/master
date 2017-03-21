package com.vipkid.payroll.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;


import com.vipkid.http.utils.JsonUtils;
import com.vipkid.payroll.utils.DateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
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
     * 
     * @param onlineClassId
     * @return
     */
    public Student getFirstStudentByOnlineClass(Long onlineClassId) {
        Student student = null;
        if (onlineClassId != null) {
            List<Student> list = studentDao.findStudentByOnlineClassId(onlineClassId);
            if (CollectionUtils.isNotEmpty(list)) {
                student = list.get(0);
                User user = userDao.findById(student.getId());
                student.setName(user.getName());
                student.setCreateDateTime(user.getCreateDateTime());
            }
        }
        logger.info("通过在线课堂ID获取学生信息  onlineClassId = {} , student = {}", onlineClassId, student);
        return student;
    }

    public Student getById(Long studentId) {
        Student student = null;
        if (studentId != null) {
            student = studentDao.findById(studentId);
            User user = userDao.findById(student.getId());
            student.setName(user.getName());
            student.setCreateDateTime(user.getCreateDateTime());
        }
        return student;
    }

    public List<Map<String, Object>> findPaidByStudentIdAndScheduleDateTime(Long studentId, Date scheduleDateTime) {
        String startDate = DateUtils.getFirstDayOfMonth(scheduleDateTime);
        String endDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        logger.info("查询学生的订单，开始日期 = {}，结束日期 = {}，StudentId = {}", startDate, endDate, studentId);
        List<Map<String, Object>> list = studentDao.findOrderListByStudentIdAndPaidDateTime(studentId, scheduleDateTime);
        logger.info("学生的订单信息为，StudentId = {}，orderList = {}", studentId, JsonUtils.toJSONString(list));
        return list;
    }

	public List<Long> findConfirmedPriceGreaterTan500BeforeThisMonth(Long studentId, Timestamp scheduledDateTime) {
        List<Long> list = studentDao.findConfirmedPriceGreaterTan500BeforeThisMonth(studentId, scheduledDateTime);
        logger.info("学生之前的的订单信息为，StudentId = {}，orderList = {}", studentId, JSON.toJSONString(list));
        return list;
	}
}
