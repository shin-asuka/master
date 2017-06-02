package com.vipkid.saml;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.Maps;
import com.sun.xml.internal.ws.client.sei.ResponseBuilder;
import com.vipkid.http.utils.HttpClientUtils;
import org.apache.commons.httpclient.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.EncryptedAssertion;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.impl.AssertionMarshaller;
import org.opensaml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.web.util.WebUtils;
import org.w3c.dom.Element;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.core.model.SAMLSSOServiceProviderDO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;

/**
 * Created by LP-813 on 2017/5/26.
 */
public class BootSaml {
    public static void  main(String[] args){

        SAMLSSOServiceProviderDO samlssoServiceProviderDO = new SAMLSSOServiceProviderDO();
        samlssoServiceProviderDO.setIssuer("vipkid-teacher-sso");
        samlssoServiceProviderDO.setAssertionConsumerUrl("https://vipkiddemo.influitive.com/saml/consume");
        samlssoServiceProviderDO.setDoEnableEncryptedAssertion(false);
        samlssoServiceProviderDO.setDoSignResponse(true);
        samlssoServiceProviderDO.setCertAlias("small");
        SAMLResponseBuilder samlResponseBuilder = new SAMLResponseBuilder();
        try {
            Response response = samlResponseBuilder.buildSAMLResponse(samlssoServiceProviderDO, "ChaoYang");
            ResponseMarshaller marshaller = new ResponseMarshaller();
            Element plaintextElement = null;
            try {
                plaintextElement = marshaller.marshall(response);
            } catch (MarshallingException e) {
                e.printStackTrace();
            }
            String originalAssertionString = XMLHelper.nodeToString(plaintextElement);
            Map map = Maps.newHashMap();
            Base64.Encoder encoder =Base64.getEncoder();
            byte[] originalAssertionString1 = encoder.encode(originalAssertionString.getBytes());
            String SAMLResponse = new String(originalAssertionString1);
//          String SAMLResponse = originalAssertionString;
            map.put("SAMLResponse",SAMLResponse);
            System.out.println(originalAssertionString);
            System.out.println(SAMLResponse);
            String ret = HttpClientUtils.post("https://vipkiddemo.influitive.com/saml/consume",map);
            System.out.println(ret);

        } catch (IdentityException e) {
            e.printStackTrace();
        }
    }
}
