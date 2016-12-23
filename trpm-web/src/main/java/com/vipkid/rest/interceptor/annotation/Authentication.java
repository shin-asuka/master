package com.vipkid.rest.interceptor.annotation;


public class Authentication{
    
    public enum Portal{
        
        ALL,        //所有端
        
        MANAGEMENT, //管理端
        
        STUDENT,    //学生端
        
        TEACHER     //老师端
        
    }
}
