//package com.vipkid.rest.web;
//
//import com.google.api.client.util.Maps;
//import com.vipkid.enums.TeacherEnum.LifeCycle;
//import com.vipkid.enums.TeacherQuizEnum;
//import com.vipkid.http.utils.HttpClientUtils;
//import com.vipkid.rest.RestfulController;
//import com.vipkid.rest.config.RestfulConfig;
//import com.vipkid.rest.interceptor.annotation.RestInterface;
//import com.vipkid.rest.service.AdminQuizService;
//import com.vipkid.rest.service.LoginService;
//import com.vipkid.rest.service.TeacherPageLoginService;
//import com.vipkid.saml.BootSaml;
//import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
//import com.vipkid.trpm.entity.Teacher;
//import com.vipkid.trpm.entity.TeacherQuiz;
//import com.vipkid.trpm.entity.User;
//import com.vipkid.trpm.util.CookieUtils;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.http.client.HttpClient;
//import org.apache.xml.security.utils.XMLUtils;
//import org.opensaml.saml2.core.Artifact;
//import org.opensaml.saml2.core.ArtifactResolve;
//import org.opensaml.saml2.core.Response;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//import org.wso2.carbon.identity.core.model.SAMLSSOServiceProviderDO;
//import org.xml.sax.InputSource;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.xml.bind.annotation.XmlAccessOrder;
//import java.io.StringReader;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.XMLFormatter;
//
//@RestController
//@RestInterface(lifeCycle=LifeCycle.REGULAR)
//@RequestMapping("/sso")
//public class SsoController extends RestfulController {
//
//    private static Logger logger = LoggerFactory.getLogger(SsoController.class);
//
//    @Autowired
//    private BootSaml bootSaml;
//
//    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
//    public Map<String,Object> getSAML(HttpServletRequest request, HttpServletResponse response){
//        String spUrl = "https://vipkiddemo.influitive.com/saml/consume";
//        Map<String,String> map = Maps.newHashMap();
//        Response res = null;
//        String token = "";
//        SAMLSSOServiceProviderDO samlssoServiceProviderDO = new SAMLSSOServiceProviderDO();
//        res = bootSaml.getSAMLResponse(samlssoServiceProviderDO,"Chao Yang");
//        map.put("SAMLResponse",res.getInResponseTo());
//        map.put("RelayState",token);
//        String ret = HttpClientUtils.post(spUrl, map);
//        return null;
//    }
//
//
//}
