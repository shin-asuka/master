package com.vipkid.trpm.rest;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.Maps;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;

public class AdminquizControllerTest extends BaseTestCase{

    private static Logger logger = LoggerFactory.getLogger(AdminquizControllerTest.class);
    
    @Test
    public void getLastQuiz() throws IOException{
        String url = URL_PREFIX + "/api/quiz/getLastQuiz";
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).timeout(60000).header(CookieKey.AUTOKEN, token).method(Method.GET).execute();
            String json = response.body();
            logger.info(" get LastQuiz ==> " + json);
        }catch(HttpStatusException e){
            logger.info(" get LastQuiz  status ==> " + e.getStatusCode());
        }
    }
    
    @Test
    public void findNeedQuiz() throws IOException{
        String url = URL_PREFIX + "/api/quiz/findNeedQuiz";
        try{
            Response response = Jsoup.connect(url).ignoreContentType(true).timeout(60000).header(CookieKey.AUTOKEN, token).method(Method.GET).execute();
            String json = response.body();
            logger.info(" get findNeedQuiz ==> " + json);
        }catch(HttpStatusException e){
            logger.info(" get findNeedQuiz  status ==> " + e.getStatusCode());
        }
    }
    
    @Test
    public void saveQuizResult() throws IOException{
        String url = URL_PREFIX + "/api/quiz/saveQuizResult";
        try{
            Map<String, String> data = Maps.newHashMap();
            data.put("grade","[{\"sn\":\"QP-1-0001\",\"teacherAnswer\":2},{\"sn\":\"QP-1-0002\",\"teacherAnswer\":1},{\"sn\":\"QP-1-0003\",\"teacherAnswer\":2}]");
            Response response = Jsoup.connect(url).ignoreContentType(true).timeout(60000).header(CookieKey.AUTOKEN, token).data(data).method(Method.POST).execute();
            String json = response.body();
            logger.info(" get saveQuizResult ==> " + json);
        }catch(HttpStatusException e){
            logger.info(" get saveQuizResult  status ==> " + e.getStatusCode());
        }
    }
}
