package com.vipkid.rest.interceptor.annotation;

import java.lang.annotation.Annotation;

import org.springframework.web.method.HandlerMethod;

public class AnnotaionUtils {

    public static <A extends Annotation> A getAnnotation(HandlerMethod handlerMethod,Class<A> annotationType){
        A restInterface = handlerMethod.getMethodAnnotation(annotationType);
        if (restInterface == null) {
            restInterface = handlerMethod.getBeanType().getAnnotation(annotationType);
        }
        return restInterface;
    }
}
