package com.vipkid.portal.classroom.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.rest.interceptor.annotation.RestInterface;

@RestController
@RestInterface(lifeCycle=LifeCycle.REGULAR)
@RequestMapping("/portal/classroom/")
public class ClassroomController {

}
