package com.vipkid.background.api.sterling.service;

import com.fasterxml.jackson.core.type.TypeReference;

import com.google.api.client.util.Maps;

import com.vipkid.background.api.sterling.dto.*;

import com.vipkid.common.utils.ProtostuffUtils;
import com.vipkid.file.utils.FileUtils;
import com.vipkid.http.utils.HttpClientUtils;
import com.vipkid.http.utils.JacksonUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.Map;

/**
 * Created by liyang on 2017/3/11.
 */

/**
 * 通过HTTP 与Sterling进行交互的工具类
 */
public class SterlingApiUtils {


    private static final Logger logger = LoggerFactory.getLogger(SterlingApiUtils.class);

    private static String sterlingHost = "https://api-int.kennect.com";

    private static String BEARER_FORMATE = "Bearer %s";

    /**
     * 创建一个候选人
     * @param candidateInputDto
     * @return
     */
    public static SterlingCandidate createCandidate(CandidateInputDto candidateInputDto){
        String postUrl = sterlingHost + "/v1/candidates";
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.format(BEARER_FORMATE,getAccessToken()));
        String response = HttpClientUtils.post(postUrl,JacksonUtils.toJSONString(candidateInputDto),headers);

        if(org.apache.commons.lang3.StringUtils.isBlank(response)){
            return null;
        }
        SterlingCandidate sterlingCandidate = JacksonUtils.readJson(response, new TypeReference<SterlingCandidate>() {});
        return sterlingCandidate;
    }


    /**
     * 变更一个候选人，candidateId 不能为空
     * @param candidateInputDto
     * @return
     */
    public static SterlingCandidate updateCandidate(CandidateInputDto candidateInputDto){
        String postUrl = sterlingHost + "/v1/candidates";
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.format(BEARER_FORMATE,getAccessToken()));
        String response = HttpClientUtils.put(postUrl,JacksonUtils.toJSONString(candidateInputDto),headers);
        if(org.apache.commons.lang3.StringUtils.isBlank(response)){
            return null;
        }
        SterlingCandidate sterlingCandidate = JacksonUtils.readJson(response, new TypeReference<SterlingCandidate>() {});
        return sterlingCandidate;
    }


    /**
     * 获取一个候选人的信息
     * @param candidateId
     * @return
     */
    public static SterlingCandidate getCandidate(String candidateId){
        if(StringUtils.isBlank(candidateId)){
            return null;
        }
        String getUrl = sterlingHost + "/v1/candidates/" + candidateId;
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.format(BEARER_FORMATE,getAccessToken()));

        String response = HttpClientUtils.get(getUrl,headers);
        if(StringUtils.isBlank(response)){
            return null;
        }
        SterlingCandidate sterlingCandidate = JacksonUtils.readJson(response, new TypeReference<SterlingCandidate>() {});
        return sterlingCandidate;
    }


    /**
     * 给一个候选人创建一个"筛选"，用于作背景调查
     * @param screeningInputDto
     * @return
     */
    public static SterlingScreening createScreening(ScreeningInputDto screeningInputDto){

        if(null == screeningInputDto){
            return null;
        }
        String postUrl = sterlingHost + "/v1/screenings/" ;
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.format(BEARER_FORMATE,getAccessToken()));

        String response = HttpClientUtils.post(postUrl,JacksonUtils.toJSONString(screeningInputDto),headers);
        if(StringUtils.isBlank(response)){
            return null;
        }
        SterlingScreening sterlingScreening = JacksonUtils.readJson(response, new TypeReference<SterlingScreening>() {});

        return sterlingScreening;
    }


    /**
     * 根据背景调查的ID 获取调查后的report
     * @param screeningId
     * @return
     */
    public static SterlingReportLink createReportLink(String screeningId){
        if(StringUtils.isBlank(screeningId)){
            return null;
        }

        String postUrl = String.format(sterlingHost + "/v1/screenings/%s/report-links",screeningId);
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.format(BEARER_FORMATE,getAccessToken()));
        headers.put(HTTP.CONTENT_TYPE,"application/json");
        String response = HttpClientUtils.post(postUrl,null,headers);
        if(StringUtils.isBlank(response)){
            return null;
        }
        SterlingReportLink sterlingReportLink = JacksonUtils.readJson(response, new TypeReference<SterlingReportLink>() {});
        return sterlingReportLink;
    }


    /**
     * 当背景调查的结果是alert时，可以再来一次preAdverseAction
     * @param screeingId
     * @param reportIdList
     * @return
     */
    public static boolean preAdverseAction(String screeingId,List<String> reportIdList){
        if(StringUtils.isBlank(screeingId) || CollectionUtils.isEmpty(reportIdList)){
            return false;
        }
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Authorization", String.format(BEARER_FORMATE, getAccessToken()));
        Map<String, Object> params = Maps.newHashMap();
        params.put("reportItemIds",reportIdList);
        String postUrl = String.format(sterlingHost+"/v1/screenings/%s/adverse-actions",screeingId);
        String response = HttpClientUtils.post(postUrl,JacksonUtils.toJSONString(params),headers);
        if(StringUtils.isBlank(response)){
            return true;
        }else{
            SterlingScreening sterlingScreening = JacksonUtils.readJson(response, new TypeReference<SterlingScreening>() {});
            if(CollectionUtils.isNotEmpty(sterlingScreening.getErrors())){
                logger.warn(JacksonUtils.toJSONString(sterlingScreening.getErrors()));
            }
        }

        return false;
    }


    /**
     * 用http的方式上传授权文档
     * @param screeingId
     * @param documentLink
     * @return
     */
    public static boolean createScreeningDocument(String screeingId,String documentLink){
        if(StringUtils.isBlank(screeingId) || StringUtils.isBlank(documentLink)){
            return false;
        }
        byte[] fileByteArray = FileUtils.webUrlConvertByteArray(documentLink);

        String post = String.format(sterlingHost +"/v1/screenings/%s/documents",screeingId);
        Map<String,String> headers =Maps.newHashMap();
        headers.put("Authorization", String.format(BEARER_FORMATE, getAccessToken()));
        headers.put(HTTP.CONTENT_TYPE,"text/plain");

        String response = HttpClientUtils.postBinaryFile(post, fileByteArray, headers);
        System.out.println(response);
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
    public static SterlingAccessToken refreshAccessToken(){
        String postUrl=sterlingHost+"/oauth/token";
        Map<String,String> params = Maps.newHashMap();
        params.put("grant_type","client_credentials");
        Map<String,String> headers =Maps.newHashMap();
        headers.put("Authorization",String.format("Basic %s", new sun.misc.BASE64Encoder().encode("APIUser@VIPKID.com:nEtGtGHyqD".getBytes())));
        headers.put(HTTP.CONTENT_TYPE,"application/x-www-form-urlencoded");
        String response = HttpClientUtils.appointHeadersPost(postUrl,params,headers);
        if(org.apache.commons.lang3.StringUtils.isBlank(response)){
            return null;
        }
        SterlingAccessToken sterlingAccessToken = JacksonUtils.readJson(response, new TypeReference<SterlingAccessToken>() {});
        return sterlingAccessToken;
    }


    /**
     * 获取AccessToken
     * @return
     */
    public static String getAccessToken(){
        String result = "{\"error\":null,\"message\":null,\"moreInfo\":null,\"access_token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik56VTFORE13UlRFeU1UaEJOME5FUkVGQlJEbEZOMEZETURCRE5UYzVSVUUwUmtaRE5rVTFPQSJ9.eyJuYW1lIjoiQVBJVXNlckBWSVBLSUQuY29tIiwiZW1haWwiOiJBUElVc2VyQFZJUEtJRC5jb20iLCJyb2xlcyI6WyIvaW50djEvR0VUL2hlYWx0aCIsIi9pbnR2MS9HRVQvc3RhdHMiLCIvaW50djEvUE9TVC9jYW5kaWRhdGVzIiwiL2ludHYxL0dFVC9jYW5kaWRhdGVzIiwiL2ludHYxL0dFVC9jYW5kaWRhdGVzL3thbnl9IiwiL2ludHYxL1BVVC9jYW5kaWRhdGVzL3thbnl9IiwiL2ludHYxL1BPU1QvY2FuZGlkYXRlcy97YW55fS9kb2N1bWVudHMiLCIvaW50djEvR0VUL2NhbmRpZGF0ZXMve2FueX0vZG9jdW1lbnRzIiwiL2ludHYxL1BPU1QvY2FuZGlkYXRlcy97YW55fS90cnVzdCIsIi9pbnR2MS9QT1NUL3NjcmVlbmluZ3MiLCIvaW50djEvR0VUL3NjcmVlbmluZ3MiLCIvaW50djEvR0VUL3NjcmVlbmluZ3Mve2FueX0iLCIvaW50djEvUE9TVC9zY3JlZW5pbmdzL3thbnl9L3JlcG9ydC1saW5rcyIsIi9pbnR2MS9QT1NUL3NjcmVlbmluZ3Mve2FueX0vYWR2ZXJzZS1hY3Rpb25zIiwiL2ludHYxL0dFVC9zY3JlZW5pbmdzL3thbnl9L3JlcG9ydCIsIi9pbnR2MS9QT1NUL3NjcmVlbmluZ3Mve2FueX0vZG9jdW1lbnRzIiwiL2ludHYxL0dFVC9zY3JlZW5pbmdzL3thbnl9L2RvY3VtZW50cyIsIi9pbnR2MS9QT1NUL3N1YnNjcmlwdGlvbnMiLCIvaW50djEvUE9TVC9zdWJzY3JpcHRpb25zL3thbnl9IiwiL2ludHYxL0dFVC9zdWJzY3JpcHRpb25zL3thbnl9IiwiL2ludHYxL0dFVC9zdWJzY3JpcHRpb25zL3thbnl9L2V2ZW50cyIsIi9pbnR2MS9HRVQvc3Vic2NyaXB0aW9ucy97YW55fS9wYWNrYWdlcyIsIi9pbnR2MS9QT1NUL3N1YnNjcmlwdGlvbnMve2FueX0vZW5hYmxlIiwiL2ludHYxL1BPU1Qvc3Vic2NyaXB0aW9ucy97YW55fS9kaXNhYmxlIiwiL2ludHYxL1BPU1Qvc3Vic2NyaXB0aW9ucy97YW55fS9qb2IiLCIvaW50djEvREVMRVRFL3N1YnNjcmlwdGlvbnMve2FueX0iLCIvaW50djEvUE9TVC9pZGVudGl0aWVzIiwiL2ludHYxL0dFVC9pZGVudGl0aWVzL3thbnl9IiwiL2ludHYxL1BPU1QvaWRlbnRpdGllcy97YW55fS9yZXRyeSIsIi9pbnR2MS9HRVQvaWRlbnRpdGllcy97YW55fS92ZXJpZmljYXRpb24iLCIvaW50djEvUFVUL2lkZW50aXRpZXMve2FueX0vdmVyaWZpY2F0aW9uIiwiL2ludHYxL1BBVENIL2lkZW50aXRpZXMve2FueX0vdmVyaWZpY2F0aW9uIiwiL2ludHYxL0RFTEVURS9pZGVudGl0aWVzL3thbnl9IiwiL2ludHYxL1BPU1QvdHJ1c3RlZC11c2VycyIsIi9pbnR2MS9HRVQvdHJ1c3RlZC11c2Vycy97YW55fSIsIi9pbnR2MS9ERUxFVEUvdHJ1c3RlZC11c2Vycy97YW55fSIsIi9pbnR2MS9QT1NUL3RydXN0cyIsIi9pbnR2MS9HRVQvdHJ1c3RzL3thbnl9IiwiL2ludHYxL0RFTEVURS90cnVzdHMve2FueX0iLCIvaW50djEvR0VUL29uZXRpbWUtcmVwb3J0LWxpbmtzL3thbnl9IiwiL2ludHYxL0dFVC9wYWNrYWdlcyIsIi9pbnR2MS9HRVQvYWR2ZXJzZS1hY3Rpb25zIiwiL2ludHYxL0dFVC9nb2R6aWxsYS9tZXRyaWNzIiwiL2ludHYxL0dFVC9nb2R6aWxsYS9zdWJzY3JpcHRpb25zLW1ldHJpY3MiXSwiaXNzIjoiaHR0cHM6Ly9zdGVybGluZ2JhY2tjaGVjay5hdXRoMC5jb20vIiwic3ViIjoiYXV0aDB8MTcxMzEyNDQiLCJhdWQiOiJDYkNidENvVmtjSzdMOUFmR1dndnJNT3l5cVVYN0wyQSIsImV4cCI6MTQ4OTM0NDgwNiwiaWF0IjoxNDg5MzA4ODA2fQ.vi3mMTVdMA1_qe8W-77TJAWeZJhRgHRDU4n8gQMlG-8cR7qpStqB_eI7cDxHj97J6QdPj60zirnEb_t68A-jxJhqNRuI-uPERglZFMvzDYdt9RGmFvQbqlzPlapeN5sbco_kbVP4nCg8KklBVnQsmI__go1Rv9JwPxXDh3phipPmXZRuxQswBG6z2hRnveempXL2InyZqcw2fuvNuf9KixJAROcGAO1ssqE2LJEy7JlpJdEYUxauJWmzBiHGmY9yMDCbVs4u4ouMEuAzxbHizHKkxQ0Yo0yhIXYgb-J9dc7pIsNGfkgUfn1UoZQMTAOx5lJKoBK25bnM2IDn6rO9lg\",\"token_type\":\"bearer\",\"expires_in\":36000}";
        SterlingAccessToken sterlingAccessToken = JacksonUtils.readJson(result, new TypeReference<SterlingAccessToken>() {});
        return sterlingAccessToken.getAccess_token();
    }



    public static void main(String [] arge){


    }


}
