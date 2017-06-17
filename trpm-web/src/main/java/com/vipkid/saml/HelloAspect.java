package com.vipkid.saml;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by LP-813 on 2017/6/17.
 */
public class HelloAspect implements InvocationHandler {
    private Object delegate;

    public HelloAspect(Object obj){
        this.delegate = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        doBefore();
        Object newObj = method.invoke(delegate, args);
        return newObj;
    }

    private void doBefore(){
        System.out.println("before...");
    }
}
