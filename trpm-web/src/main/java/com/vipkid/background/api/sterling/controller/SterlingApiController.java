package com.vipkid.background.api.sterling.controller;

import com.amazonaws.util.json.Jackson;
import com.google.common.collect.Maps;
import com.vipkid.background.api.sterling.dto.CandidateInputDto;
import com.vipkid.background.api.sterling.service.SterlingApiUtils;
import com.vipkid.http.utils.JacksonUtils;
import com.vipkid.rest.utils.ApiResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liyang on 2017/3/11.
 */
@RequestMapping("/api/background/sterling")
@Controller
public class SterlingApiController {

    private static final Logger logger = LoggerFactory.getLogger(SterlingApiController.class);


    @RequestMapping("/createCandidates.json")
    public Object createCandidates(CandidateInputDto candidateInputDto){
        logger.info(JacksonUtils.toJSONString(candidateInputDto));

        return ApiResponseUtils.buildSuccessDataResp("success");
    }



    @RequestMapping("/updateCandidates.json")
    public Object updateCandidates(CandidateInputDto candidateInputDto){
        logger.info(JacksonUtils.toJSONString(candidateInputDto));

        return ApiResponseUtils.buildSuccessDataResp("success");
    }


    @RequestMapping("/createScreening.json")
    public Object createScreening(Long teacherId){
        logger.info("teacher:{}",teacherId);
        return ApiResponseUtils.buildSuccessDataResp("success");
    }


    @RequestMapping("/createPreAdverse.json")
    public Object createPreAdverse(Long teacherId){
        logger.info("teacher:{}",teacherId);
        return ApiResponseUtils.buildSuccessDataResp("success");
    }



    @RequestMapping("/callback.json")
    public Object  callback(HttpServletRequest request){
        String type = request.getParameter("type");
        String payload = request.getParameter("payload");
        logger.info("type:{},payload:{}",type,payload);
        return ApiResponseUtils.buildSuccessDataResp("success");
    }




}
