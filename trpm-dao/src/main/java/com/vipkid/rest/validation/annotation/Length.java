package com.vipkid.rest.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Length {
    
    int maxLength() default 0;
    
    int minLength() default 0;
    
    String message() default "The field length invalid";
}
