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
        
        public static final String RIGHTANSWER = "{\"QP-1-001\": 2,\"QP-1-002\": 1,\"QP-1-003\": 1,\"QP-1-004\": 4,\"QP-1-005\": 5,\"QP-1-006\": 1,\"QP-1-007\": 3,\"QP-1-008\": 4,\"QP-1-009\": 4,\"QP-1-010\": 3,\"QP-1-011\": 1,\"QP-1-012\": 2,\"QP-1-013\": 3,\"QP-1-014\": 4,\"QP-1-015\": 1,\"QP-1-016\": 2,\"QP-1-017\": 4,\"QP-1-018\": 1,\"QP-1-019\": 4,\"QP-1-020\": 3}";
        
        public static final Map<String,Integer> CORRECTANSWERMAP = JsonTools.readValue(RIGHTANSWER,new TypeReference<HashMap<String,Integer>>(){});
    
    }
}