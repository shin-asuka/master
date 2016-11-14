package com.vipkid.recruitment.training.controller;

import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.rest.RestfulController;
import com.vipkid.trpm.constant.ApplicationConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RestInterface(lifeCycle={ApplicationConstant.TeacherLifeCycle.TRAINING})
@RequestMapping("/recruitment/training")
public class TrainingController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(TrainingController.class);

}
