package com.vipkid.portal.classroom.controller;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by LP-813 on 2017/2/16.
 */
@RestController
@RestInterface(lifeCycle= TeacherEnum.LifeCycle.REGULAR)
@RequestMapping("/portal/comments/")
public class TrialFeedbackController {


}
