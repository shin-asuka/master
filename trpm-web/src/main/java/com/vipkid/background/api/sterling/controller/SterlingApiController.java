package com.vipkid.background.api.sterling.controller;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.vipkid.background.api.sterling.dto.*;
import com.vipkid.background.api.sterling.service.SterlingApiUtils;
import com.vipkid.background.api.sterling.service.SterlingService;
import com.vipkid.http.utils.JacksonUtils;
import com.vipkid.rest.utils.ApiResponseUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * Created by liyang on 2017/3/11.
 */

@Controller
public class SterlingApiController {

    private static final Logger logger = LoggerFactory.getLogger(SterlingApiController.class);


    @Autowired
    private SterlingService sterlingService;
    @RequestMapping("/background/sterling/createCandidates")
    @ResponseBody
    public Object createCandidates(@RequestBody  CandidateInputDto candidateInputDto){

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

        CandidateOutputDto candidateOutputDto = sterlingService.createCandidate(candidateInputDto);
        if (StringUtils.isNotBlank(candidateOutputDto.getErrorMessage())) {
            return ApiResponseUtils.buildErrorResp(105003, candidateOutputDto.getErrorMessage());
        }

        Map<String,Object> result= Maps.newHashMap();
        result.put("bgSterlingScreeningId",candidateOutputDto.getId());
        return ApiResponseUtils.buildSuccessDataResp(result);
    }



    @RequestMapping("/background/sterling/saveCandidate")
    public Object saveCandidate(@RequestBody CandidateInputDto candidateInputDto){
        logger.info(JacksonUtils.toJSONString(candidateInputDto));

        if(candidateInputDto == null ){
            return ApiResponseUtils.buildErrorResp(105001,"参数不正确");
        }
        if(StringUtils.isBlank(candidateInputDto.getEmail())){
            return ApiResponseUtils.buildErrorResp(105002,"email 为空");
        }
        CandidateOutputDto candidateOutputDto  = sterlingService.saveCandidate(candidateInputDto);
        if(StringUtils.isNotBlank(candidateOutputDto.getErrorMessage())){
            return ApiResponseUtils.buildErrorResp(105003, candidateOutputDto.getErrorMessage());
        }
        Map<String,Object> result= Maps.newHashMap();
        result.put("bgSterlingScreeningId",candidateOutputDto.getId());
        return ApiResponseUtils.buildSuccessDataResp(result);

    }

    @RequestMapping("/background/sterling/updateCandidates")
    public Object updateCandidates(@RequestBody CandidateInputDto candidateInputDto){
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

        CandidateOutputDto candidateOutputDto  = sterlingService.updateCandidate(candidateInputDto);
        if(StringUtils.isNotBlank(candidateOutputDto.getErrorMessage())){
            return ApiResponseUtils.buildErrorResp(105003, candidateOutputDto.getErrorMessage());
        }
        Map<String,Object> result= Maps.newHashMap();
        result.put("bgSterlingScreeningId",candidateOutputDto.getId());
        return ApiResponseUtils.buildSuccessDataResp(result);
    }


    @RequestMapping("/background/sterling/createScreening")
    public Object createScreening(Long teacherId,String documentUrl){
        logger.info("teacher:{}",teacherId);
        ScreeningOutputDto screeningOutputDto = sterlingService.createScreening(teacherId,documentUrl);
        if(StringUtils.isNotBlank(screeningOutputDto.getErrorMessage())){
            return ApiResponseUtils.buildErrorResp(screeningOutputDto.getErrorCode(),screeningOutputDto.getErrorMessage());
        }
        Map<String,Object> result= Maps.newHashMap();
        result.put("bgSterlingScreeningId",screeningOutputDto.getId());
        return ApiResponseUtils.buildSuccessDataResp("success");
    }


    @RequestMapping("/background/sterling/createPreAdverse")
    public Object createPreAdverse(Long teacherId){
        logger.info("teacher:{}",teacherId);
        AdverseOutputDto adverseOutputDto = sterlingService.createPreAdverse(teacherId);
        if(StringUtils.isNotBlank(adverseOutputDto.getErrorMessage())){
            return ApiResponseUtils.buildErrorResp(adverseOutputDto.getErrorCode(),adverseOutputDto.getErrorMessage());
        }
        Map<String,Object> result= Maps.newHashMap();
        result.put("teacherId",adverseOutputDto.getId());
        return ApiResponseUtils.buildSuccessDataResp("success");
    }




    @RequestMapping("/api/background/sterling/callback")
    public Object  callback(@RequestBody SterlingCallBack callBack,HttpServletRequest request){
        logger.warn(JacksonUtils.toJSONString(callBack));

        return ApiResponseUtils.buildSuccessDataResp("success");
    }




//----------下面是测试的
    @Deprecated
    @RequestMapping("/background/sterling/repairDateScreeing")
    public Object repairDataCandidate(Long backgroundSterlingId){
        ScreeningOutputDto screeningOutputDto = sterlingService.repairDateScreeing(backgroundSterlingId);
        return ApiResponseUtils.buildSuccessDataResp(screeningOutputDto);
    }


    @Deprecated
    @RequestMapping("/background/sterling/test")
    public Object  testDate(){
        sterlingService.saveTestDate();
        return ApiResponseUtils.buildSuccessDataResp("success");
    }


}
