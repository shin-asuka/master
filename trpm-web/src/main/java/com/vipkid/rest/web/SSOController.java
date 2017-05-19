package com.vipkid.rest.web;

import com.google.api.client.util.Maps;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherQuizEnum;
import com.vipkid.http.utils.HttpClientUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.AdminQuizService;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.service.TeacherPageLoginService;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherQuiz;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.util.CookieUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.HttpClient;
import org.apache.xml.security.utils.XMLUtils;
import org.opensaml.saml2.core.Artifact;
import org.opensaml.saml2.core.ArtifactResolve;
import org.opensaml.saml2.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlAccessOrder;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.logging.XMLFormatter;

@RestController
@RestInterface(lifeCycle=LifeCycle.REGULAR)
@RequestMapping("/sso")
public class SSOController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(SSOController.class);

    
    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getSAML(HttpServletRequest request, HttpServletResponse response){
        String spUrl = "https://vipkiddemo.influitive.com/saml/consume";
        Map<String,String> map = Maps.newHashMap();
        String res = "";
        String token = "";
        map.put("SAMLResponse",res);
        map.put("RelayState",token);
        Response
        String  HttpClientUtils.post(spUrl, map);
    }

    @RequestMapping(value = "/SAMLResponse", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Response createResponse(HttpServletRequest request, HttpServletResponse response)
    {
        SAMLResponse = request.getParameter("SAMLResponse");
        InputSource inputSource = new InputSource(new StringReader(SAMLResponse));
        SAMLReader samlReader = new SAMLReader();
        response2 = org.opensaml.saml2.core.Response)samlReader.readFromFile(inputSource);
        org.opensaml.saml2.core.Response response2 = (org.opensaml.saml2.core.Response)samlReader.readFromFile(inputSource);
        //To fetch the digital signature from the response.
        Signature signature  = response2.getSignature();
        X509Certificate certificate = (X509Certificate) keyStore.getCertificate(domainName);
        //pull out the public key part of the certificate into a KeySpec
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(certificate.getPublicKey().getEncoded());
        //get KeyFactory object that creates key objects, specifying RSA - java.security.KeyFactory
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        //generate public key to validate signatures
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        //we have the public key
        BasicX509Credential publicCredential = new BasicX509Credential();
        //add public key value
        publicCredential.setPublicKey(publicKey);
        //create SignatureValidator
        SignatureValidator signatureValidator = new SignatureValidator(publicCredential);
        //try to validate
        try{
            signatureValidator.validate(signature);
            catch(Exception e){
        //
            }
        }


    }
}
