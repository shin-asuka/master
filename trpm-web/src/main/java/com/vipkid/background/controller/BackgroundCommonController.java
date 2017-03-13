package com.vipkid.background.controller;

import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RestInterface
@RequestMapping("/background")
public class BackgroundCommonController extends RestfulController{
    
    private static Logger logger = LoggerFactory.getLogger(BackgroundCommonController.class);
    

}
