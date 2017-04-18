package com.vipkid.portal.bookings.controller;

import com.vipkid.http.service.ManageGatewayService;
import com.vipkid.rest.portal.vo.StudentCommentPageVo;
import com.vipkid.teacher.tools.utils.conversion.JsonUtils;
import com.vipkid.trpm.controller.h5.StudentCommentController;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by pankui on 2017-04-18.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext.xml"})
@WebAppConfiguration
public class StudentCommentRestControllerTest {


    @Autowired
    ManageGatewayService manageGatewayService;

    @Autowired
    StudentCommentController studentCommentController;

    @Test
    public  void testGetStudentCommentListByTeacherId(){
        int teacherId = 1;
        int start = 1;
        int limit = 10;
        String ratings = "1";
        StudentCommentPageVo studentCommentPageVo = manageGatewayService.getStudentCommentListByTeacherId(teacherId, start, limit, ratings);

        System.out.println(JsonUtils.toJson(studentCommentPageVo));
    }


    @Test
    public void testGetStudentCommentByPage(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletWebRequest)RequestContextHolder.getRequestAttributes()).getResponse();

        Map<String, Object> map =  studentCommentController.getStudentCommentByPage(request,response,1,10,"1",3094608);

        System.out.println(JsonUtils.toJson(map));
    }
}
