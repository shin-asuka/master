package com.vipkid.rest.interceptor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vipkid.rest.interceptor.annotation.Authentication.Portal;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteInterface {
    
    Portal[] portal() default {};
    
}
