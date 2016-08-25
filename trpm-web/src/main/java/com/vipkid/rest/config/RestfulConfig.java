package com.vipkid.rest.config;

import java.util.HashMap;
import java.util.Map;

import org.community.tools.JsonTools;

import com.fasterxml.jackson.core.type.TypeReference;

public class RestfulConfig {

    public static final class Port{
        
        public static int TEACHER = 1;
        
        public static int RECRUITMENT = 2;
    }
    
    public static final class HttpStatus{
        
        public static int STATUS_403 = 403;
        
        public static int STATUS_200 = 200;
        
        public static int STATUS_400 = 400;
        
        public static int STATUS_404 = 404;
        
        public static int STATUS_500 = 500;
    }
    
    public static final class Validate{
        
        public static String EMAIL_REG = "^(([a-zA-Z0-9\\\"_\\-])\\.?)+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
        
        public static String WD_REG = "^([a-zA-Z0-9])+$";
        
        public static String PASSWORD_REG = "^(?:.*[A-Za-z].*)(?:.*[0-9].*)|(?:.*[0-9].*)(?:.*[A-Za-z].*).{0,}$";
        
    }
    
    public static final String JSON_UTF_8 = "application/json;charset=UTF-8";
    
    public static final class Quiz{
    
        public static final int QUIZ_PASS_SCORE = 60; 
        
        public static final String RIGHTANSWER = "{\"QP-1-0001\": 2,\"QP-1-0002\": 1,\"QP-1-0003\": 1,\"QP-1-0004\": 4,\"QP-1-0005\": 5,\"QP-1-0006\": 1,\"QP-1-0007\": 3,\"QP-1-0008\": 4,\"QP-1-0009\": 4,\"QP-1-0010\": 3,\"QP-1-0011\": 1,\"QP-1-0012\": 2,\"QP-1-0013\": 3,\"QP-1-0014\": 4,\"QP-1-0015\": 1,\"QP-1-0016\": 2,\"QP-1-0017\": 4,\"QP-1-0018\": 1,\"QP-1-0019\": 4,\"QP-1-0020\": 3}";
        
        public static final Map<String,Integer> CORRECTANSWERMAP = JsonTools.readValue(RIGHTANSWER,new TypeReference<HashMap<String,Integer>>(){});
    
    }
}