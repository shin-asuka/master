package com.vipkid.rest.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.community.tools.JsonTools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;
import com.vipkid.trpm.constant.ApplicationConstant.TeacherLifeCycle;

public class RestfulConfig {
    
    public static long SYSTEM_USER_ID = 2;

    public static final class Port{
        
        public static int TEACHER = 1;
        
        public static int RECRUITMENT = 2;
        
        public static int NEWRECRUITMENT = 3;
    }
    
    public static final class RoleClass{
        
        public static String PE = "PE";
        
        public static String PES = "PE-Supervisor";
        
        public static String TE = "TE";
        
        public static String TES = "TE-Supervisor";
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
    
    public static final Set<String> NEWRECRUITMENTSET = Sets.newHashSet(TeacherLifeCycle.SIGNUP,TeacherLifeCycle.BASIC_INFO);
    
    public static final Set<String> TEACHERPORTSET = Sets.newHashSet(TeacherLifeCycle.REGULAR);

}