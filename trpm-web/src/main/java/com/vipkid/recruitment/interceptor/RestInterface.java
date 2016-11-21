package com.vipkid.recruitment.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vipkid.enums.TeacherEnum.LifeCycle;



@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestInterface {

    LifeCycle[] lifeCycle() default {};
}
