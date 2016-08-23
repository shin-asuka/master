package com.vipkid.trpm.restful;

import java.io.IOException;
import java.util.Map;

import org.community.tools.JsonTools;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.Maps;

public class AppRestfulControllerTest extends BaseTestCase{

    private static Logger logger = LoggerFactory.getLogger(AppRestfulControllerTest.class);

    //@Test
    public void login() throws IOException {
        String url = URL_PREFIX + "/api/app/login";
        Map<String, String> data = Maps.newHashMap();
        data.put("email", "chenpeng1@vipkid.com.cn");
        data.put("password", "vipkid1");
        try {
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.POST).execute();
            String json = response.body();
            try{
               String token = JsonTools.readValue(json).get("token").asText();
               logger.info(token + " login 登陆OK");
            }catch(Exception e){
                logger.info("login ==> " + json);
            }
       } catch (HttpStatusException e) {
           logger.info(" login 登陆Fail " + e.getStatusCode());
       }
       /*
       data = Maps.newHashMap();
       data.put("email", " ");
       data.put("password", " ");
       try {
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.POST).execute();
            String json = response.body();
            try{
               String token = JsonTools.readValue(json).get("token").asText();
               logger.info(token + " login 登陆Fail");
            }catch(Exception e){
                logger.info("login ==> " + json);
            }
       } catch (HttpStatusException e) {
            logger.info(" login 登陆Fail " + e.getStatusCode());
       }
       */
    }
    
    //@Test
    public void authByToken() throws IOException{
        String url = URL_PREFIX + "/api/app/authByToken";
        Map<String, String> data = Maps.newHashMap();
        data.put("token", "4c444e13-1a88-482a-96ca-4fa780bb54c8");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            try{
                logger.info(json);
                String id = JsonTools.readValue(json).get("data").get("id").asText();
                logger.info("authByToken OK ==> " + id);
            }catch(Exception e){
                logger.info("authByToken ==>" + json);
            }
        } catch (HttpStatusException e) {
            logger.info(" authByToken Fail " + e.getStatusCode());
        }
        /*
        data = Maps.newHashMap();
        data.put("token", token+123);
        response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
        json = response.body();
        id = JsonTools.readValue(json).get("data").get("id").asText();
        logger.info("authByToken ERRROR ==> " + id);
        
        data = Maps.newHashMap();
        data.put("token", "");
        response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
        json = response.body();
        id = JsonTools.readValue(json).get("data").get("id").asText();
        logger.info("authByToken NULL ==> " + id);
        */
    }
    
    //@Test
    public void authById() throws IOException{
        String url = URL_PREFIX + "/api/app/authById";
        Map<String,String> data = Maps.newHashMap();
        data.put("teacherId", "11535832");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            try{
                String id = JsonTools.readValue(json).get("data").get("id").asText();
                logger.info("authById OK ==> " + id);
            }catch(Exception e){
                logger.info("authById ==>" + json);
            }
        } catch (HttpStatusException e) {
            logger.info(" authById Fail " + e.getStatusCode());
        }
        /*
        data = Maps.newHashMap();
        data.put("teacherId", "1153500083");
        response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
        json = response.body();
        id = JsonTools.readValue(json).get("data").get("id").asText();
        logger.info("authById ERROR ==> " + id);
        
        data = Maps.newHashMap();
        data.put("teacherId", "");
        response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
        json = response.body();
        id = JsonTools.readValue(json).get("data").get("id").asText();
        logger.info("authById null ==> " + id);
        */
    }
    
    //@Test
    public void forgetPassword() throws IOException{
        String url = URL_PREFIX + "/api/app/forgetPassword";
        Map<String, String> data = Maps.newHashMap();
        data.put("email", "zwlzwl376@126.com");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.POST).execute();
            String json = response.body();
            logger.info("forgetPassword OK == > " + json);
        } catch (HttpStatusException e) {
            logger.info(" forgetPassword Fail " + e.getStatusCode());
        }
        /*
        try{
            data = Maps.newHashMap();
            data.put("email", "zwlzwl376222@126.com");
            response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.POST).execute();
            json = response.body();
            logger.info("forgetPassword ERROR == > " + json);
        } catch (HttpStatusException e) {
            logger.info(" forgetPassword Fail " + e.getStatusCode());
        }
        
        try{
            data = Maps.newHashMap();
            data.put("email", "");
            response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.POST).execute();
            json = response.body();
            logger.info("forgetPassword ERROR == > " + json);
        } catch (HttpStatusException e) {
            logger.info(" forgetPassword Fail " + e.getStatusCode());
        }
        */
    }
    
    @Test
    public void classCount() throws IOException{
        String url = URL_PREFIX + "/api/app/classCount";
        Map<String, String> data = Maps.newHashMap();
        data.put("teacherId", "1778710");
        data.put("classStatuses", "1");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            logger.info("classCount OK 1 ==> " + json);
        } catch (HttpStatusException e) {
            logger.info(" classCount Fail " + e.getStatusCode());
        }
        /*
        data = Maps.newHashMap();
        data.put("teacherId", "1153583");
        data.put("classStatuses", "1,2");
        data.put("courseTypes", "1,2,3,4,5,6,7");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            logger.info("classCount OK 2 ==> " + json);
        } catch (HttpStatusException e) {
            logger.info(" classCount Fail " + e.getStatusCode());
        }
        /*
        data = Maps.newHashMap();
        data.put("teacherId", "1153577783");
        data.put("classStatuses", "1,2");
        data.put("courseTypes", "1,2");
        response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
        json = response.body();
        logger.info("classCount ERROR ==> " + json);
       
        data = Maps.newHashMap();
        data.put("teacherId", "1153583");
        data.put("classStatuses", "1,2");
        data.put("courseTypes", "");
        response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
        json = response.body();
        logger.info("classCount null 1 ==> " + json);
        
        data = Maps.newHashMap();
        data.put("teacherId", "1153583");
        data.put("classStatuses", "");
        data.put("courseTypes", "");
        response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
        json = response.body();
        logger.info("classCount null 2 ==> " + json);
        */
    } 
    
    
    //@Test
    public void classList() throws IOException{
        String url = URL_PREFIX + "/api/app/classList";
        Map<String, String> data = Maps.newHashMap();
        data.put("teacherId", "7681");
        data.put("startTime", "1430409600000");
        data.put("endTime", "1431014400000");
        data.put("classStatuses", "1,2");
        data.put("courseTypes", "7,3,17,10,14,15,16,2,3,17,10,14,15,16,2");
        data.put("order", "0");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            logger.info("classList OK 1 ==> " + json);
        } catch (HttpStatusException e) {
            logger.info(" classList Fail " + e.getStatusCode());
        }
        
        data = Maps.newHashMap();
        data.put("teacherId", "7681");
        data.put("startTime", "1430409600000");
        data.put("endTime", "1431014400000");
        data.put("classStatuses", "1,2");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            logger.info("classList OK 2 ==> " + json);
        } catch (HttpStatusException e) {
            logger.info(" classList Fail " + e.getStatusCode());
        }
        
        data = Maps.newHashMap();
        data.put("teacherId", "7681");
        data.put("startTime", "1430409600000");
        data.put("endTime", "1431014400000");
        data.put("classStatuses", "1,2");
        data.put("courseTypes", "");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            logger.info("classList null 1==> " + json);
        } catch (HttpStatusException e) {
            logger.info(" classList Fail " + e.getStatusCode());
        }
        
        data = Maps.newHashMap();
        data.put("teacherId", "7681");
        data.put("startTime", "1430409600000");
        data.put("endTime", "1431014400000");
        data.put("classStatuses", "1");
        data.put("courseTypes", "");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            logger.info("classList null 2==> " + json);
        } catch (HttpStatusException e) {
            logger.info(" classList Fail " + e.getStatusCode());
        }
        
        data = Maps.newHashMap();
        data.put("teacherId", "76810101");
        data.put("startTime", "1430409600000");
        data.put("endTime", "1431014400000");
        data.put("classStatuses", "2");
        data.put("courseTypes", "");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            logger.info("classList ERROR ==> " + json);
        } catch (HttpStatusException e) {
            logger.info(" classList Fail " + e.getStatusCode());
        }
        data = Maps.newHashMap();
        data.put("teacherId", "76810101");
        data.put("startTime", "1430409600000");
        data.put("endTime", "1431014400000");
        data.put("classStatuses", "1");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            logger.info("classList ERROR null ==> " + json);
        } catch (HttpStatusException e) {
            logger.info(" classList Fail " + e.getStatusCode());
        }
    } 
    
    
    //@Test
    public void classListPage() throws IOException{
        String url = URL_PREFIX + "/api/app/classListPage";
        Map<String, String> data = Maps.newHashMap();
        data.put("teacherId", "1153583");
        data.put("classStatus", "0");
        data.put("order", "1");
        data.put("start", "1");
        data.put("limit", "20");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            logger.info("classListPage OK 0:next ==> " + json);
        } catch (HttpStatusException e) {
            logger.info(" classListPage Fail " + e.getStatusCode());
        }
        data = Maps.newHashMap();
        data.put("teacherId", "1153583");
        data.put("classStatus", "1");
        data.put("order", "2");
        data.put("start", "1");
        data.put("limit", "20");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            logger.info("classListPage OK 1:inclass ==> " + json);
        } catch (HttpStatusException e) {
            logger.info(" classListPage Fail " + e.getStatusCode());
        }
        
        data = Maps.newHashMap();
        data.put("teacherId", "1153583");
        data.put("classStatus", "2");
        data.put("order", "2");
        data.put("start", "1");
        data.put("limit", "20");
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
            String json = response.body();
            logger.info("classListPage OK 2:closed ==> " + json);
        } catch (HttpStatusException e) {
            logger.info(" classListPage Fail " + e.getStatusCode());
        }
        /*
        data = Maps.newHashMap();
        data.put("teacherId", "1153583333");
        data.put("classStatus", "4");
        data.put("order", "2");
        data.put("start", "1");
        data.put("limit", "20");
        response = Jsoup.connect(url).ignoreContentType(true).data(data).method(Method.GET).execute();
        json = response.body();
        logger.info("classListPage ERROR ==> " + json);
        */
    } 

    //@Test
    public void feedback() throws IOException {
        String url = URL_PREFIX + "/api/app/feedback";
        Map<String, String> requestParam = Maps.newHashMap();
        requestParam.put("studentId", "329");
        requestParam.put("onlineClassId", "401");
        requestParam.put("teacherId", "359");
        Response response = Jsoup.connect(url).ignoreContentType(true).data(requestParam).method(Method.GET).execute();
        String json = response.body();
        logger.info("feedback ==>" + json);
        Assert.assertEquals(JsonTools.readValue(json).get("teacherId").asInt(), 359);

        requestParam.put("studentId", "0");
        requestParam.put("onlineClassId", "0");
        requestParam.put("teacherId", "0");
        try {
            response = Jsoup.connect(url).ignoreContentType(true).data(requestParam).method(Method.GET).execute();
        } catch (HttpStatusException e) {
            Assert.assertEquals(e.getStatusCode(), 400);
        }

        requestParam.put("studentId", "329");
        requestParam.put("onlineClassId", "401");
        requestParam.put("teacherId", "350");
        try {
            response = Jsoup.connect(url).ignoreContentType(true).data(requestParam).method(Method.GET).execute();
        } catch (HttpStatusException e) {
            Assert.assertEquals(e.getStatusCode(), 404);
        }
    }

    //@Test
    public void studentList() throws IOException {
        String url = URL_PREFIX + "/api/app/studentList";
        Map<String, String> requestParam = Maps.newHashMap();
        requestParam.put("studentIds", "329,661");
        Response response = Jsoup.connect(url).ignoreContentType(true).data(requestParam).method(Method.GET).execute();
        String json = response.body();
        logger.info("studentList ==>" + json);
        Assert.assertEquals(JsonTools.readValue(json).get("data").size(), 2);

        requestParam.put("studentIds", "");
        try {
            response = Jsoup.connect(url).ignoreContentType(true).data(requestParam).method(Method.GET).execute();
        } catch (HttpStatusException e) {
            Assert.assertEquals(e.getStatusCode(), 400);
        }

        requestParam.put("studentIds", "0,3");
        try {
            response = Jsoup.connect(url).ignoreContentType(true).data(requestParam).method(Method.GET).execute();
        } catch (HttpStatusException e) {
            Assert.assertEquals(e.getStatusCode(), 404);
        }
    }
}
