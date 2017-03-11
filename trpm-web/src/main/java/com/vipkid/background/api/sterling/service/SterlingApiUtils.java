package com.vipkid.background.api.sterling.service;

import com.google.api.client.util.Maps;
import com.sun.deploy.util.Base64Wrapper;
import com.sun.xml.internal.messaging.saaj.util.Base64;
import com.vipkid.background.api.sterling.dto.*;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.http.vo.HttpResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.util.List;
import java.util.Map;

/**
 * Created by liyang on 2017/3/11.
 */

/**
 * 通过HTTP 与Sterling进行交互的工具类
 */
public class SterlingApiUtils {


    private static String sterlingHost="https://api-int.kennect.com";

    /**
     * 创建一个候选人
     * @param candidateInputDto
     * @return
     */
    public static SterlingCandidate createCandidate(CandidateInputDto candidateInputDto){

        return null;
    }


    /**
     * 变更一个候选人，candidateId 不能为空
     * @param candidateInputDto
     * @return
     */
    public static SterlingCandidate updateCandidate(CandidateInputDto candidateInputDto){

        return null;
    }


    /**
     * 获取一个候选人的信息
     * @param candidateId
     * @return
     */
    public static SterlingCandidate getCandidate(String candidateId){
        return null;
    }


    /**
     * 给一个候选人创建一个"筛选"，用于作背景调查
     * @param screeningInputDto
     * @return
     */
    public static SterlingScreening createScreening(ScreeningInputDto screeningInputDto){
        return null;
    }


    /**
     * 根据背景调查的ID 获取调查后的report
     * @param screeningId
     * @return
     */
    public static SterlingReportLink createReportLink(String screeningId){
        return null;
    }


    /**
     * 当背景调查的结果是alert时，可以再来一次preAdverseAction
     * @param screeingId
     * @param reportId
     * @return
     */
    public static boolean preAdverseAction(String screeingId,List<String> reportId){
        return false;
    }


    /**
     * 用http的方式上传授权文档
     * @param screeingId
     * @param documentLink
     * @return
     */
    public static boolean createScreeningDocument(String screeingId,String documentLink){
        return false;
    }


    /**
     * 接收到callback 后对其进行组装
     * @param response
     * @return
     */
    public static SterlingCallBack buildCallBack(String response){
        return null;
    }


    /**
     * 重新获取一次accessToken
     * @return
     */
    public static String refreshAccessToken(){
        String postUrl=sterlingHost+"/oauth/token";
        Map<String,String> params = Maps.newHashMap();
        Map<String,String> headers =Maps.newHashMap();
        headers.put("Authorization",String.format("Basic %s", new sun.misc.BASE64Encoder().encode("APIUser@VIPKID.com:nEtGtGHyqD".getBytes())));
        headers.put("Content-Type","application/x-www-form-urlencoded");
        HttpResult result = WebUtils.post(postUrl,params,headers);
        if(result.getStatus() == 200){

        }
        return StringUtils.EMPTY;
    }


    /**
     * 获取AccessToken
     * @return
     */
    public static String getAccessToken(){
        return StringUtils.EMPTY;
    }



    public static void main(String [] arge){
        System.out.println(SterlingApiUtils.refreshAccessToken());
    }


}
