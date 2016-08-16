package com.vipkid.trpm.restful;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.Maps;

public class LoginControllerTest {
    
    private static Logger logger = LoggerFactory.getLogger(LoginControllerTest.class);

    private static final String URL_PREFIX = "http://weilong.ngrok.cc/trpm-web";
    
    
    @Test
    public void login() throws IOException{
        String url = URL_PREFIX + "/api/user/login";
        Map<String, String> data = Maps.newHashMap();
        data.put("email", "baoyuxiao1@vipkid.com.cn");
        data.put("password", "vipkid2");
        Response response = Jsoup.connect(url).data(data).method(Method.POST).execute();
        String json = response.body();
        logger.info(" login ERROR ==> " + json);
        
        data = Maps.newHashMap();
        data.put("email", "baoyuxiao1@vipkid.com.cn");
        data.put("password", "vipkid1");
        response = Jsoup.connect(url).data(data).method(Method.POST).execute();
        json = response.body();
        logger.info(" login OK ==> " + json);
    }
    
    @Test
    public void register() throws IOException{
        String url = URL_PREFIX + "/api/user/register";
        Map<String, String> data = Maps.newHashMap();
        data.put("email", "vipkid123@vipkid.com.cn");
        data.put("password", new String(Base64.getEncoder().encode("123456789".getBytes())));
        Response response = Jsoup.connect(url).data(data).method(Method.POST).execute();
        String json = response.body();
        logger.info(" register OK ==> " + json);
    }
    
    @Test
    public void resetPasswordRequest() throws IOException{
        String url = URL_PREFIX + "/api/user/resetPasswordRequest";
        Map<String, String> data = Maps.newHashMap();
        data.put("email", "zwlzwl376@126.com");
        Response response = Jsoup.connect(url).data(data).method(Method.POST).execute();
        String json = response.body();
        logger.info(" resetPasswordRequest OK ==> " + json);
        
        data = Maps.newHashMap();
        data.put("email", "zwlzwl3762222@126.com");
        response = Jsoup.connect(url).data(data).method(Method.POST).execute();
        json = response.body();
        logger.info(" resetPasswordRequest ERROR ==> " + json);
    }
    
    
    @Test
    public void resetPassword() throws IOException{
        String url = URL_PREFIX + "/api/user/resetPassword";
        Map<String, String> data = Maps.newHashMap();
        data.put("token", "2345try");
        data.put("password","123456789");
        Response response = Jsoup.connect(url).data(data).method(Method.POST).execute();
        String json = response.body();
        logger.info(" resetPassword ERROR ==> " + json);
    }
    
    @Test
    public void auth() throws IOException{
        String url = URL_PREFIX + "/api/user/auth";
        Response response = Jsoup.connect(url).header("token", "wertyuidjfkjshfldskjfl").method(Method.POST).execute();
        String json = response.body();
        logger.info(" auth OK ==> " + json);
        
        response = Jsoup.connect(url).method(Method.POST).execute();
        json = response.body();
        logger.info(" auth ERROR ==> " + json);
    }
}
