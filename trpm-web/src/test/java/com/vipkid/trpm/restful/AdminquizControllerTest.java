package com.vipkid.trpm.restful;

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
            Response response = Jsoup.connect(url).ignoreContentType(true).header(CookieKey.AUTOKEN, "12345").method(Method.GET).execute();
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
            Response response = Jsoup.connect(url).ignoreContentType(true).header(CookieKey.AUTOKEN, "12345").method(Method.GET).execute();
            String json = response.body();
            logger.info(" get LastQuiz ==> " + json);
        }catch(HttpStatusException e){
            logger.info(" get LastQuiz  status ==> " + e.getStatusCode());
        }
    }
    
    @Test
    public void saveQuizResult() throws IOException{
        String url = URL_PREFIX + "/api/quiz/saveQuizResult";
        try{
            Map<String, String> data = Maps.newHashMap();
            data.put("grade", "65");
            Response response = Jsoup.connect(url).ignoreContentType(true).header(CookieKey.AUTOKEN, "12345").data(data).method(Method.POST).execute();
            String json = response.body();
            logger.info(" get LastQuiz ==> " + json);
        }catch(HttpStatusException e){
            logger.info(" get LastQuiz  status ==> " + e.getStatusCode());
        }
    }
}
