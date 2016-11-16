package com.vipkid.rest.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vipkid.rest.validation.annotation.EnumList.Annotaions;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {
    
    Annotaions[] type() default Annotaions.ALL; 
    
}
