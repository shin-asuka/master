package com.vipkid.background.api.sterling.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vipkid.background.api.sterling.dto.CandidateInputDto;
import com.vipkid.background.api.sterling.dto.SterlingAccessToken;
import com.vipkid.background.api.sterling.dto.SterlingCandidate;
import com.vipkid.common.utils.ProtostuffUtils;
import com.vipkid.file.utils.FileUtils;
import com.vipkid.http.utils.HttpClientUtils;
import com.vipkid.http.utils.JacksonUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liyang on 2017/3/12.
 */
public class SterlingApiUtilsTest {

    @Test
    public void  refreshAccessTokenTest(){
        SterlingAccessToken accessToken = SterlingApiUtils.refreshAccessToken();
        byte[] bytes =  ProtostuffUtils.serializer(accessToken);
        if(ArrayUtils.isEmpty(bytes)){
            System.out.println("这是一个空的");
        }else{
            System.out.println(bytes.length);
            System.out.println(bytes);
        }
        SterlingAccessToken accessTokenDeser = ProtostuffUtils.deserializer(bytes,SterlingAccessToken.class);
        System.out.println(JacksonUtils.toJSONString(accessTokenDeser));
    }


    @Test
    public void createCandidateTest(){
        String json = "{\n" +
                "  \"clientReferenceId\": \"vipkid2\",\n" +
                "  \"email\": \"wwww@example.com\",\n" +
                "  \"givenName\": \"www\",\n" +
                "  \"familyName\": \"xx\",\n" +
                "  \"confirmedNoMiddleName\": true,\n" +
                "  \"dob\": \"1998-07-18\",\n" +
                "  \"ssn\": \"123456798\",\n" +
                "  \"phone\": \"+14041231233\",\n" +
                "  \"address\": {\n" +
                "    \"addressLine\": \"123 Main Street\",\n" +
                "    \"municipality\": \"Orlando\",\n" +
                "    \"regionCode\": \"US-FL\",\n" +
                "    \"postalCode\": \"12345\",\n" +
                "    \"countryCode\": \"US\"\n" +
                "  }\n" +
                "}";
        CandidateInputDto candidateInputDto = JacksonUtils.readJson(json, new TypeReference<CandidateInputDto>() {});
        System.out.println(candidateInputDto.getAddress().getAddressLine());
        SterlingCandidate sterlingCandidate =  SterlingApiUtils.createCandidate(candidateInputDto);
        System.out.println(JacksonUtils.toJSONString(sterlingCandidate));

    }


    @Test
    public void updateCandidateTest(){
        String json = "{\n" +
                "  \"id\":\"1234\",\n" +
                "  \"clientReferenceId\": \"vipkid1\",\n" +
                "  \"email\": \"john@example.com\",\n" +
                "  \"givenName\": \"www\",\n" +
                "  \"familyName\": \"xx\",\n" +
                "  \"confirmedNoMiddleName\": true,\n" +
                "  \"dob\": \"1998-07-18\",\n" +
                "  \"ssn\": \"123456798\",\n" +
                "  \"phone\": \"+14041231233\",\n" +
                "  \"address\": {\n" +
                "    \"addressLine\": \"123 Main Street\",\n" +
                "    \"municipality\": \"Orlando\",\n" +
                "    \"regionCode\": \"US-FL\",\n" +
                "    \"postalCode\": \"12345\",\n" +
                "    \"countryCode\": \"US\"\n" +
                "  }\n" +
                "}";
        CandidateInputDto candidateInputDto = JacksonUtils.readJson(json, new TypeReference<CandidateInputDto>() {});
        SterlingCandidate sterlingCandidate =  SterlingApiUtils.updateCandidate(candidateInputDto);
        System.out.println(JacksonUtils.toJSONString(sterlingCandidate));
    }


    @Test
    public void createScreeningDocumentTest(){
        String fileUrl = "https://teacher-data.vipkid.com.cn/teacher/identification/3094294/aa2a3c550b0849ac929c04fcaa6340f7/3094294-20161222-011004.jpg";

        SterlingApiUtils.createScreeningDocument("",fileUrl);


    }
}
