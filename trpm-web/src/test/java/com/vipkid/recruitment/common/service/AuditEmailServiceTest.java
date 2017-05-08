package com.vipkid.recruitment.common.service;

import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by liuguowen on 2017/3/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext.xml"})
@WebAppConfiguration
@Transactional
public class AuditEmailServiceTest {

    private static final String MOCK_CLASS_1 = "P1-U1-LC1-L1";

    @Resource
    private AuditEmailService auditEmailService;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    @Autowired
    private LessonDao lessonDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private OnlineClassDao onlineClassDao;

//    @Test
//    public void testSendTBDResultToMonter() {
//        TeacherApplication teacherApplication = teacherApplicationDao.findApplictionById(1213790);
//        OnlineClass onlineClass = onlineClassDao.findById(teacherApplication.getOnlineClassId());
//
//        Teacher peTeacher = teacherDao.findById(onlineClass.getTeacherId());
//        User candidate = userDao.findById(teacherApplication.getTeacherId());
//
//        String mockClass = "Mock Class 2";
//        Lesson lesson = lessonDao.findById(onlineClass.getLessonId());
//        if (StringUtils.equals(MOCK_CLASS_1, lesson.getSerialNumber())) {
//            mockClass = "Mock Class 1";
//        }
//
//        auditEmailService.sendTBDResultToMonter(peTeacher, candidate.getName(), mockClass, teacherApplication,
//                        onlineClass);
//    }

}
