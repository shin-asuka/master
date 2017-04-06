package com.vipkid.background.api.sterling.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.vipkid.background.api.sterling.dto.*;
import com.vipkid.common.utils.RedisCacheUtils;
import com.vipkid.file.utils.FileUtils;
import com.vipkid.http.utils.HttpClientUtils;
import com.vipkid.http.utils.JacksonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.HTTP;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

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

    private static final String sterlingHost = PropertyConfigurer.stringValue("background.sterling.host");

    private static final String sterlingAuth = PropertyConfigurer.stringValue("background.sterling.auth");

    private static String BEARER_FORMAT = "Bearer %s";

    private static final  String MESSAGE_UNAUTHORIZED="Unauthorized";

    private static final String  MESSAGE_AUTHORIZATION = "Authorization";

    private static final String STERLING_ACCESS_TOKEN="sterling_access_token";

    public static final short MAX_RETRY=3;

    /**
     * 创建一个候选人
     * @param candidateInputDto
     * @return
     */
    public static SterlingCandidate createCandidate(CandidateInputDto candidateInputDto,short retryTimes){
        if(retryTimes == 0){
            logger.info("retry == 0  params:{}",JacksonUtils.toJSONString(candidateInputDto));
            return null;
        }

        String postUrl = sterlingHost + "/v1/candidates";
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.format(BEARER_FORMAT,getAccessToken()));


        HttpClientUtils.Response response = HttpClientUtils.timeoutRetryPost(postUrl,JacksonUtils.toJSONString(candidateInputDto),headers,MAX_RETRY);

        logger.info("createCandidate  url:{},header:{},param:{},response:{}",postUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(response));

        if(response == null){
            logger.info("Response is null  url:{},header:{},params:{}",postUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(candidateInputDto));
            return null;
        }
        if(StringUtils.isBlank(response.getContent())){
            logger.info("Response.Content is null  url:{},header:{},params:{}",postUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(candidateInputDto));
            return null;
        }

        SterlingCandidate sterlingCandidate = JacksonUtils.readJson(response.getContent(), new TypeReference<SterlingCandidate>() {});
        if(StringUtils.contains(sterlingCandidate.getMessage(),MESSAGE_AUTHORIZATION)){
            RedisCacheUtils.del(STERLING_ACCESS_TOKEN);
            SterlingAccessToken sterlingAccessToken = refreshAccessToken();
            if(StringUtils.isBlank(sterlingAccessToken.getAccess_token())){
                logger.error("Sterling refresh access token error");
            }else{
                sterlingCandidate = createCandidate(candidateInputDto,--retryTimes);
            }
        }
        return sterlingCandidate;
    }


    /**
     * 变更一个候选人，candidateId 不能为空
     * @param candidateInputDto
     * @return
     */
    public static SterlingCandidate updateCandidate(CandidateInputDto candidateInputDto,short retryTimes){
        if(retryTimes == 0){
            logger.info("retry == 0  params:{}",JacksonUtils.toJSONString(candidateInputDto));
            return null;
        }

        String postUrl = String.format(sterlingHost + "/v1/candidates/%s",candidateInputDto.getCandidateId());
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.format(BEARER_FORMAT,getAccessToken()));


        HttpClientUtils.Response response = HttpClientUtils.timeoutRetryPut(postUrl,JacksonUtils.toJSONString(candidateInputDto),headers,MAX_RETRY);

        logger.info("updateCandidate  url:{},header:{},param:{},response:{}",postUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(response));

        if(response == null){
            logger.info("Response is null  url:{},header:{},params:{}",postUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(candidateInputDto));
            return null;
        }
        if(StringUtils.isBlank(response.getContent())){
            logger.error("Response.Content is null  url:{},header:{},params:{}",postUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(candidateInputDto));
            return null;
        }
        SterlingCandidate sterlingCandidate = JacksonUtils.readJson(response.getContent(), new TypeReference<SterlingCandidate>() {});
        if(StringUtils.contains(sterlingCandidate.getMessage(),MESSAGE_AUTHORIZATION)){
            RedisCacheUtils.del(STERLING_ACCESS_TOKEN);
            SterlingAccessToken sterlingAccessToken = refreshAccessToken();
            if(StringUtils.isBlank(sterlingAccessToken.getAccess_token())){
                logger.error("sterling  refresh access token wrong");
            }else{
                sterlingCandidate = updateCandidate(candidateInputDto,--retryTimes);
            }
        }
        return sterlingCandidate;
    }


    /**
     * 获取一个候选人的信息
     * @param candidateId
     * @return
     */
    public static SterlingCandidate getCandidate(String candidateId,short retryTimes){
        if(StringUtils.isBlank(candidateId)){
            return null;
        }

        if(retryTimes == 0){
            logger.info("retry == 0  params:{}",candidateId);
            return null;
        }

        String getUrl = sterlingHost + "/v1/candidates/" + candidateId;
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.format(BEARER_FORMAT,getAccessToken()));


        HttpClientUtils.Response response = HttpClientUtils.timeoutRetryGet(getUrl,headers,MAX_RETRY);

        logger.info("getCandidate  url:{},header:{},response:{}",getUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(response));

        if(response == null){
            logger.info("Response is null  url:{},header:{},params:{}",getUrl,JacksonUtils.toJSONString(headers),candidateId);
            return null;
        }
        if(StringUtils.isBlank(response.getContent())){
            logger.error("Response.Content is null  url:{},header:{},params:{}",getUrl,JacksonUtils.toJSONString(headers),candidateId);
            return null;
        }


        SterlingCandidate sterlingCandidate = JacksonUtils.readJson(response.getContent(), new TypeReference<SterlingCandidate>() {});

        if(StringUtils.equals(sterlingCandidate.getMessage(),MESSAGE_UNAUTHORIZED)){
            SterlingAccessToken sterlingAccessToken = refreshAccessToken();
            if(StringUtils.isBlank(sterlingAccessToken.getAccess_token())){
                logger.warn("sterling  refresh access token wrong");
            }else{
                sterlingCandidate = getCandidate(candidateId,--retryTimes);
            }
        }
        return sterlingCandidate;
    }


    public static List<SterlingCandidate> getCandidateList(CandidateFilterDto candidateFilterDto){

        String getUrl = sterlingHost+"/v1/candidates";

        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.format(BEARER_FORMAT,getAccessToken()));

        Map<String,Object> params = Maps.newHashMap();

        if(StringUtils.isNotBlank(candidateFilterDto.getClientReferenceId())){
            params.put("clientReferenceId",candidateFilterDto.getClientReferenceId());
        }
        if(StringUtils.isNotBlank(candidateFilterDto.getEmail())){
            params.put("email",candidateFilterDto.getEmail());
        }
        if(StringUtils.isNotBlank(candidateFilterDto.getFamilyName())){
            params.put("familyName",candidateFilterDto.getFamilyName());
        }
        if(StringUtils.isNotBlank(candidateFilterDto.getGivenName())){
            params.put("givenName",candidateFilterDto.getGivenName());
        }
        if(null != candidateFilterDto.getLimit()){
            params.put("limit",candidateFilterDto.getLimit());
        }
        if(null != candidateFilterDto.getOffset()){
            params.put("offset",candidateFilterDto.getOffset());
        }

        if(org.springframework.util.CollectionUtils.isEmpty(params)){
            return Lists.newArrayList();
        }
        StringBuilder getUrlBuilder = new StringBuilder(getUrl+"?");
        for (Map.Entry<String, Object> entry: params.entrySet()){
            getUrlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String requestUrl = getUrlBuilder.substring(0,getUrlBuilder.length()-1);
        HttpClientUtils.Response response = HttpClientUtils.timeoutRetryGet(requestUrl,headers,MAX_RETRY);

        logger.info("getCandidateList  url:{},header:{},param:{},response:{}",requestUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(params),JacksonUtils.toJSONString(response));

        if(response == null){
            logger.error("Response is null  url:{},header:{},params:{}",getUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(candidateFilterDto));
            return Lists.newArrayList();
        }
        if(StringUtils.isBlank(response.getContent())){
            logger.error("Response.Content is null  url:{},header:{},params:{}",getUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(candidateFilterDto));
            return Lists.newArrayList();
        }

        List<SterlingCandidate> sterlingCandidateList = JacksonUtils.readJson(response.getContent(), new TypeReference<List<SterlingCandidate>>() {});

        return sterlingCandidateList;
    }

    /**
     * 给一个候选人创建一个"筛选"，用于作背景调查
     * @param screeningInputDto
     * @return
     */
    public static SterlingScreening createScreening(ScreeningInputDto screeningInputDto,short retryTimes){

        if(null == screeningInputDto){
            return null;
        }


        if(retryTimes == 0){
            logger.info("retry == 0  params:{}",JacksonUtils.toJSONString(screeningInputDto));
            return null;
        }

        String postUrl = sterlingHost + "/v1/screenings" ;
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.format(BEARER_FORMAT,getAccessToken()));

        HttpClientUtils.Response response = HttpClientUtils.timeoutRetryPost(postUrl,JacksonUtils.toJSONString(screeningInputDto),headers,MAX_RETRY);

        logger.info("createScreening  url:{},header:{},param:{},response:{}",postUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(screeningInputDto),JacksonUtils.toJSONString(response));

        if(response == null){
            logger.error("Response is null  url:{},header:{},params:{}",postUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(screeningInputDto));
            return null;
        }
        if(StringUtils.isBlank(response.getContent())){
            logger.error("Response.Content is null  url:{},header:{},params:{}",postUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(screeningInputDto));
            return null;
        }

        SterlingScreening sterlingScreening = JacksonUtils.readJson(response.getContent(), new TypeReference<SterlingScreening>() {});
        if(StringUtils.contains(sterlingScreening.getMessage(),MESSAGE_AUTHORIZATION)){
            RedisCacheUtils.del(STERLING_ACCESS_TOKEN);
            SterlingAccessToken sterlingAccessToken = refreshAccessToken();
            if(StringUtils.isBlank(sterlingAccessToken.getAccess_token())){
                logger.error("sterling  refresh access token wrong");
            }else{
                sterlingScreening = createScreening(screeningInputDto,--retryTimes);
            }
        }
        return sterlingScreening;
    }


    public static SterlingScreening getScreening(String screeningId){
        if(StringUtils.isBlank(screeningId)){
            return null;
        }
        String getUrl = String.format(sterlingHost + "/v1/screenings/%s",screeningId) ;
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.format(BEARER_FORMAT,getAccessToken()));
        headers.put(HTTP.CONTENT_TYPE,"application/json");
        HttpClientUtils.Response response = HttpClientUtils.timeoutRetryGet(getUrl,headers,MAX_RETRY);

        logger.info("getScreening  url:{},header:{},response:{}",getUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(response));

        if(response == null){
            logger.error("Response is null  url:{},header:{},params:{}",getUrl,JacksonUtils.toJSONString(headers),screeningId);
            return null;
        }
        if(StringUtils.isBlank(response.getContent())){
            logger.error("Response.Content is null  url:{},header:{},params:{}",getUrl,JacksonUtils.toJSONString(headers),screeningId);
            return null;
        }


        SterlingScreening sterlingScreening = JacksonUtils.readJson(response.getContent(), new TypeReference<SterlingScreening>() {});
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
        headers.put("Authorization",String.format(BEARER_FORMAT,getAccessToken()));
        headers.put(HTTP.CONTENT_TYPE,"application/json");
        HttpClientUtils.Response response = HttpClientUtils.timeoutRetryPost(postUrl,null,headers,MAX_RETRY);
        if(response == null){
            logger.error("Response is null  url:{},header:{},params:{}",postUrl,JacksonUtils.toJSONString(headers),screeningId);
            return null;
        }
        if(StringUtils.isBlank(response.getContent())){
            logger.error("Response.Content is null  url:{},header:{},params:{}",postUrl,JacksonUtils.toJSONString(headers),screeningId);
            return null;
        }

        SterlingReportLink sterlingReportLink = JacksonUtils.readJson(response.getContent(), new TypeReference<SterlingReportLink>() {});
        return sterlingReportLink;
    }


    /**
     * 当背景调查的结果是alert时，可以再来一次preAdverseAction
     * @param screeningId
     * @param reportIdList
     * @return
     */
    public static boolean preAdverseAction(String screeningId,List<String> reportIdList){
        if(StringUtils.isBlank(screeningId) || CollectionUtils.isEmpty(reportIdList)){
            return false;
        }
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Authorization", String.format(BEARER_FORMAT, getAccessToken()));
        Map<String, Object> params = Maps.newHashMap();
        params.put("reportItemIds",reportIdList);
        String postUrl = String.format(sterlingHost+"/v1/screenings/%s/adverse-actions",screeningId);
        HttpClientUtils.Response response = HttpClientUtils.timeoutRetryPost(postUrl,JacksonUtils.toJSONString(params),headers,MAX_RETRY);

        logger.info("preAdverseAction  url:{},header:{},param:{},response:{}",postUrl,JacksonUtils.toJSONString(headers),JacksonUtils.toJSONString(params),JacksonUtils.toJSONString(response));

        if(response == null){
            logger.info("Response is null  url:{},header:{},params:{}",postUrl,JacksonUtils.toJSONString(headers),screeningId);
            return false;
        }

        if(response.getStatusCode() == 201){
            return true;
        }else if(StringUtils.isNotBlank(response.getContent())){
            SterlingScreening sterlingScreening = JacksonUtils.readJson(response.getContent(), new TypeReference<SterlingScreening>() {});
            if(CollectionUtils.isNotEmpty(sterlingScreening.getErrors())){
                logger.error("Sterling Pre Adverse Action screeningId:{},return:{}",screeningId,JacksonUtils.toJSONString(sterlingScreening.getErrors()));
            }
            return false;
        }else{
            logger.warn("return statusCode:{}",response.getStatusCode());
            return false;
        }
    }


    /**
     * 用http的方式上传授权文档
     * @param screeningId
     * @param documentLink
     * @return
     */
    public static boolean createScreeningDocument(String screeningId,String documentLink){
        if(StringUtils.isBlank(screeningId) || StringUtils.isBlank(documentLink)){
            return false;
        }
        byte[] fileByteArray = FileUtils.webUrlConvertByteArray(documentLink);
        String post = String.format(sterlingHost +"/v1/screenings/%s/documents?party=candidate&documentType=end-user-agreement",screeningId);

        org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        httpHeaders.add("Authorization", String.format(BEARER_FORMAT, getAccessToken()));
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        String response = HttpClientUtils.postBinaryFile(post,fileByteArray,httpHeaders);
        logger.info("createScreeningDocument  url:{},header:{},response:{}",post,JacksonUtils.toJSONString(httpHeaders),JacksonUtils.toJSONString(response));
        if(StringUtils.isNotBlank(response)){
            return true;
        }
        return false;
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
        headers.put("Authorization",String.format("Basic %s", new sun.misc.BASE64Encoder().encode(sterlingAuth.getBytes())));
        headers.put(HTTP.CONTENT_TYPE,"application/x-www-form-urlencoded");
        String response = HttpClientUtils.appointHeadersPost(postUrl,params,headers);
        if(org.apache.commons.lang3.StringUtils.isBlank(response)){
            return null;
        }
        SterlingAccessToken sterlingAccessToken = JacksonUtils.readJson(response, new TypeReference<SterlingAccessToken>() {});
        RedisCacheUtils.set(STERLING_ACCESS_TOKEN,sterlingAccessToken,sterlingAccessToken.getExpires_in());
        return sterlingAccessToken;
    }


    /**
     * 获取AccessToken
     * @return
     */
    public static String getAccessToken(){
        SterlingAccessToken sterlingAccessToken = RedisCacheUtils.get(STERLING_ACCESS_TOKEN,SterlingAccessToken.class);
        if(null == sterlingAccessToken){
            sterlingAccessToken = refreshAccessToken();
        }
        return sterlingAccessToken.getAccess_token();
    }
}
