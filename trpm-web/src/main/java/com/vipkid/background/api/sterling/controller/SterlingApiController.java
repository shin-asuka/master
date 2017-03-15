package com.vipkid.background.api.sterling.controller;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.vipkid.background.api.sterling.dto.CandidateInputDto;
import com.vipkid.background.api.sterling.dto.SterlingCallBack;
import com.vipkid.background.api.sterling.service.SterlingApiUtils;
import com.vipkid.background.api.sterling.service.SterlingService;
import com.vipkid.http.utils.JacksonUtils;
import com.vipkid.rest.utils.ApiResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by liyang on 2017/3/11.
 */
@RequestMapping("/api/background/sterling")
@Controller
public class SterlingApiController {

    private static final Logger logger = LoggerFactory.getLogger(SterlingApiController.class);


    @Autowired
    private SterlingService sterlingService;
    @RequestMapping("/createCandidates")
    public Object createCandidates(@RequestParam CandidateInputDto candidateInputDto){
        logger.info(JacksonUtils.toJSONString(candidateInputDto));

        if(candidateInputDto == null ){
            return ApiResponseUtils.buildErrorResp(105001,"参数不正确");
        }
        if(StringUtils.isBlank(candidateInputDto.getEmail())){
            return ApiResponseUtils.buildErrorResp(105002,"email 为空");
        }
        if(null == candidateInputDto.getTeacherId()){
            return ApiResponseUtils.buildErrorResp(105002,"email 为空");
        }

        Long id = sterlingService.createCandidate(candidateInputDto);
        Map<String,Object> result= Maps.newHashMap();
        result.put("bgSterlingScreeningId",id);
        return ApiResponseUtils.buildSuccessDataResp(result);
    }



    @RequestMapping("/updateCandidates")
    public Object updateCandidates(@RequestParam CandidateInputDto candidateInputDto){
        logger.info(JacksonUtils.toJSONString(candidateInputDto));

        if(candidateInputDto == null ){
            return ApiResponseUtils.buildErrorResp(105001,"参数不正确");
        }
        if(StringUtils.isBlank(candidateInputDto.getEmail())){
            return ApiResponseUtils.buildErrorResp(105002,"email 为空");
        }
        if(StringUtils.isBlank(candidateInputDto.getCandidateId())){
            return ApiResponseUtils.buildErrorResp(105002,"候选人ID不能为空");
        }

        int row  = sterlingService.updateCandidate(candidateInputDto);
        Map<String,Object> result= Maps.newHashMap();
//        result.put("bgSterlingScreeningId",id);
        return ApiResponseUtils.buildSuccessDataResp(result);
    }


    @RequestMapping("/createScreening")
    public Object createScreening(@RequestParam Long teacherId){
        logger.info("teacher:{}",teacherId);
        Long id = sterlingService.createScreening(teacherId);
        Map<String,Object> result= Maps.newHashMap();
        result.put("bgSterlingScreeningId",id);
        return ApiResponseUtils.buildSuccessDataResp("success");
    }


    @RequestMapping("/createPreAdverse")
    public Object createPreAdverse(@RequestParam Long teacherId){
        logger.info("teacher:{}",teacherId);
        int row = sterlingService.createPreAdverse(teacherId);
        Map<String,Object> result= Maps.newHashMap();
//        result.put("bgSterlingScreeningId",id);
        return ApiResponseUtils.buildSuccessDataResp("success");
    }



    @RequestMapping("/callback")
    public Object  callback(HttpServletRequest request){
        String type = request.getParameter("type");
        String payload = request.getParameter("payload");
        logger.info("type:{},payload:{}",type,payload);
        SterlingCallBack.Payload  payloadObject= JacksonUtils.readJson(payload, new TypeReference<SterlingCallBack.Payload>() {});
        return ApiResponseUtils.buildSuccessDataResp("success");
    }




}
