package com.vipkid.saml;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.Maps;
import com.sun.xml.internal.ws.client.sei.ResponseBuilder;
import com.vipkid.http.utils.HttpClientUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;
import org.springframework.web.util.WebUtils;
import org.w3c.dom.Element;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.core.model.SAMLSSOServiceProviderDO;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.Base64;
import java.util.Map;

/**
 * Created by LP-813 on 2017/5/26.
 */
public class BootSaml {
    public static void  main(String[] args){
        HelloImpl helloImpl = new HelloImpl();
        HelloAspect helloAspect = new HelloAspect(helloImpl);
        Hello hello = (Hello) Proxy.newProxyInstance(helloImpl.getClass().getClassLoader(),helloImpl.getClass().getInterfaces(),helloAspect);
        hello.sayHello();
    }
}
