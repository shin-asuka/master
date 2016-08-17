package com.vipkid.rest.config;

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
        
        public static String EMAIL_REG = "^(([a-zA-Z0-9\\'_\\-])\\.?)+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
        
        public static String WD_REG = "^([a-zA-Z0-9])+$";
        
        public static String PASSWORD_REG = "^(?:.*[A-Za-z].*)(?:.*[0-9].*)|(?:.*[0-9].*)(?:.*[A-Za-z].*).{0,}$";
        
    }
    
    public static final String JSON_UTF_8 = "application/json;charset=UTF-8";
}