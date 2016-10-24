package com.vipkid.rest.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vipkid.rest.validation.tools.ValidationEnum.Type;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Verify {
    
    Type[] type() default {Type.NOT_NULL};

}
