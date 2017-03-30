package com.vipkid.background.api.sterling.controller;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vipkid.background.api.sterling.dto.*;
import com.vipkid.background.api.sterling.service.SterlingService;
import com.vipkid.http.utils.JacksonUtils;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.background.BackgroundScreeningDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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


    @Resource
    private BackgroundScreeningDao backgroundScreeningDao;


    @RequestMapping("/background/sterling/saveCandidate")
    public Object saveCandidate(@RequestBody CandidateInputDto candidateInputDto){

        if(candidateInputDto == null ){

            return ApiResponseUtils.buildErrorResp(105001,"参数不正确");
        }
        if(StringUtils.isBlank(candidateInputDto.getEmail())){
            return ApiResponseUtils.buildErrorResp(105002,"email 为空");
        }
        CandidateOutputDto candidateOutputDto  = sterlingService.saveCandidate(candidateInputDto);
        if(StringUtils.isNotBlank(candidateOutputDto.getErrorMessage())){
            logger.warn(JacksonUtils.toJSONString(candidateOutputDto));
            return ApiResponseUtils.buildErrorResp(105003, candidateOutputDto.getErrorMessage());
        }

        //Map<String, Long> result= Maps.newHashMap();
        //result.put("bgSterlingScreeningId", candidateOutputDto.getId());

        return ApiResponseUtils.buildSuccessDataResp(candidateOutputDto.getId());

    }




    @RequestMapping("/background/sterling/createScreening")
    public Object createScreening(Long teacherId,String documentUrl){

        ScreeningOutputDto screeningOutputDto = sterlingService.createScreening(teacherId,documentUrl);

        if(StringUtils.isNotBlank(screeningOutputDto.getErrorMessage())){
            logger.warn("teacherId:{},documentUrl:{},return:{}",teacherId,documentUrl,JacksonUtils.toJSONString(screeningOutputDto));
            return ApiResponseUtils.buildErrorResp(screeningOutputDto.getErrorCode(),screeningOutputDto.getErrorMessage());
        }
        Map<String,Object> result= Maps.newHashMap();
        result.put("bgSterlingScreeningId",screeningOutputDto.getId());
        return ApiResponseUtils.buildSuccessDataResp(result);
    }


    @RequestMapping("/background/sterling/createPreAdverse")
    public Object createPreAdverse(Long teacherId){

        AdverseOutputDto adverseOutputDto = sterlingService.createPreAdverse(teacherId);
        if(StringUtils.isNotBlank(adverseOutputDto.getErrorMessage())){
            logger.warn("teacher:{}",teacherId);
            return ApiResponseUtils.buildErrorResp(adverseOutputDto.getErrorCode(),adverseOutputDto.getErrorMessage());
        }
        Map<String,Object> result= Maps.newHashMap();
        result.put("teacherId",adverseOutputDto.getId());
        return ApiResponseUtils.buildSuccessDataResp(result);
    }




    @RequestMapping(value = "/background/sterling/callback",method = RequestMethod.POST)
    public Object  callback(@RequestBody  SterlingCallBack callBack,HttpServletRequest request){
        logger.warn(JacksonUtils.toJSONString(callBack));
        if(null != callBack){
            sterlingService.updateBackgroundScreening(callBack.getPayload());
        }
        return ApiResponseUtils.buildSuccessDataResp("success");
    }




    @RequestMapping(value = "/background/sterling/repairDateScreening",method = RequestMethod.GET)
    public Object repairDateScreening(Long screeningTableId){
        if(null == screeningTableId){
            return ApiResponseUtils.buildErrorResp("TP10000","参数为空");
        }
        ScreeningOutputDto screeningOutputDto = sterlingService.repairDateScreening(screeningTableId);
        if(null != screeningOutputDto.getErrorCode()){
            return ApiResponseUtils.buildErrorResp(screeningOutputDto.getErrorCode(),screeningOutputDto.getErrorMessage());
        }

        return ApiResponseUtils.buildSuccessDataResp("success");
    }


}
