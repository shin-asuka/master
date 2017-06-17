package com.vipkid.saml;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by LP-813 on 2017/6/17.
 */
public class HelloImpl implements Hello{
    @Override
    public void sayHello() {
        System.out.println("hello world！");
    }
}
