package com.vipkid.background.api.sterling.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vipkid.background.api.sterling.dto.CandidateInputDto;
import com.vipkid.background.api.sterling.dto.ScreeningInputDto;
import com.vipkid.background.api.sterling.dto.SterlingAccessToken;
import com.vipkid.background.api.sterling.dto.SterlingCandidate;
import com.vipkid.common.utils.ProtostuffUtils;
import com.vipkid.file.utils.FileUtils;
import com.vipkid.http.utils.HttpClientUtils;
import com.vipkid.http.utils.JacksonUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    public void getCandidateTest(){
        SterlingCandidate sterlingCandidate = SterlingApiUtils.getCandidate("3180193");
        System.out.println(JacksonUtils.toJSONString(sterlingCandidate));
    }


    @Test
    public void createScreeningTest(){
        ScreeningInputDto screeningInputDto = new ScreeningInputDto();
        ScreeningInputDto.CallBack callBack =new ScreeningInputDto.CallBack();
        callBack.setUri("https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json");
        screeningInputDto.setCallback(callBack);
        screeningInputDto.setPackageId("182951");
        screeningInputDto.setCandidateId("3180193");

        SterlingApiUtils.createScreening(screeningInputDto);
    }

    @Test
    public void createScreeningDocumentTest() throws IOException {
        String fileUrl = "https://teacher-data.vipkid.com.cn/teacher/identification/3094294/aa2a3c550b0849ac929c04fcaa6340f7/3094294-20161222-011004.jpg";
        SterlingApiUtils.createScreeningDocument("001000062942500",fileUrl);
        //SterlingApiUtils.createScreeningDocument("001000062940459",fileUrl);
    }

    @Test
    public void test(){
        String time ="2017-03-15T09:50:52Z";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date d = null;//注意是空格+UTC
        try {
            d = format.parse(time.replace("Z", " UTC"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(d);
        Calendar lastTime = Calendar.getInstance();
        Calendar currentTime = Calendar.getInstance();
        lastTime.add(Calendar.YEAR,2);
        System.out.println(String.format("%s|%s",lastTime.getTime(),currentTime.getTime()));

    }

    @Test
    public void creatTestDate(){
        String jsonTemplet ="{\"teacherId\":4969,\"email\":\"tp.test.%s@example.com\",\"givenName\":\"aa\",\"familyName\":\"aa\",\"confirmedNoMiddleName\":true,\"dob\":\"1998-07-18\",\"ssn\":\"123456%s\",\"phone\":\"+14041231234\",\"address\":{\"addressLine\":\"123MainStreet\",\"municipality\":\"Orlando\",\"regionCode\":\"US-FL\",\"postalCode\":\"12345\",\"countryCode\":\"US\"}}";

        for(int i=100;i<200;i++){
            String json =String.format(jsonTemplet,i,i);
        CandidateInputDto candidateInputDto = JacksonUtils.readJson(json, new TypeReference<CandidateInputDto>() {});
        SterlingCandidate sterlingCandidate =  SterlingApiUtils.createCandidate(candidateInputDto);
        System.out.println();
        }
    }
}


