package com.vipkid.trpm.rest;

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
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;

public class LoginControllerTest extends BaseTestCase{
    
    private static Logger logger = LoggerFactory.getLogger(LoginControllerTest.class);

    @Test
    public void login() throws IOException{
        String url = URL_PREFIX + "/api/user/login";
        Map<String, String> data = Maps.newHashMap();
        data.put("email", "zengweilong@gmail.com");
        data.put("password", "vipkid1");
        Response response = Jsoup.connect(url).ignoreContentType(true).timeout(60000).data(data).method(Method.POST).execute();
        String json = response.body();
        logger.info(" login OK ==> " + json);
        
        /*        
        data = Maps.newHashMap();
        data.put("email", "baoyuxiao1@vipkid.com.cn");
        data.put("password", "vipkid1");
        response = Jsoup.connect(url).ignoreContentType(true).timeout(60000).data(data).method(Method.POST).execute();
        json = response.body();
        logger.info(" login OK ==> " + json);
        */
    }
    
    //@Test
    public void register() throws IOException{
        String url = URL_PREFIX + "/api/user/register";
        Map<String, String> data = Maps.newHashMap();
        data.put("email", "vipkid123@vipkid.com.cn");
        data.put("key", "1111");
        data.put("imageCode", "1111");
        data.put("password", new String(Base64.getEncoder().encode("123456789".getBytes())));
        Response response = Jsoup.connect(url).ignoreContentType(true).timeout(60000).data(data).method(Method.POST).execute();
        String json = response.body();
        logger.info(" register OK ==> " + json);
    }
    
    //@Test
    public void resetPasswordRequest() throws IOException{
        String url = URL_PREFIX + "/api/user/resetPasswordRequest";
        Map<String, String> data = Maps.newHashMap();
        data.put("email", "zwlzwl376@126.com");
        Response response = Jsoup.connect(url).ignoreContentType(true).timeout(60000).data(data).method(Method.POST).execute();
        String json = response.body();
        logger.info(" resetPasswordRequest OK ==> " + json);
        
        data = Maps.newHashMap();
        data.put("email", "zwlzwl3762222@126.com");
        response = Jsoup.connect(url).data(data).ignoreContentType(true).timeout(60000).method(Method.POST).execute();
        json = response.body();
        logger.info(" resetPasswordRequest ERROR ==> " + json);
    }
    
    
    //@Test
    public void resetPassword() throws IOException{
        String url = URL_PREFIX + "/api/user/resetPassword";
        Map<String, String> data = Maps.newHashMap();
        data.put("token", "2345try");
        data.put("password","123456789");
        Response response = Jsoup.connect(url).ignoreContentType(true).timeout(60000).data(data).method(Method.POST).execute();
        String json = response.body();
        logger.info(" resetPassword ERROR ==> " + json);
    }
    
    //@Test
    public void auth() throws IOException{
        String url = URL_PREFIX + "/api/user/auth";
        Response response = Jsoup.connect(url).ignoreContentType(true).timeout(60000).header(CookieKey.AUTOKEN, TOKEN).method(Method.POST).execute();
        String json = response.body();
        logger.info(" auth OK ==> " + json);
        
        response = Jsoup.connect(url).ignoreContentType(true).timeout(60000).method(Method.POST).execute();
        json = response.body();
        logger.info(" auth ERROR ==> " + json);
    }
}
