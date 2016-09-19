package com.vipkid.rest.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Maps;
import com.google.common.base.Preconditions;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.rest.AdminQuizService;
import com.vipkid.trpm.service.rest.LoginService;

@RestController
@RequestMapping("/tag")
public class TagsController {
    
    private Logger logger = LoggerFactory.getLogger(PersonalInfoRestController.class);
    
    @Autowired
    private LoginService loginService;
    
    @Autowired
    private AdminQuizService adminQuizService;
    
    @RequestMapping(value = "/getTags", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getTags(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        result.put("status", false);
        try{
            String token = request.getHeader(CookieKey.AUTOKEN);
            Preconditions.checkArgument(StringUtils.isNotBlank(token));
            User user = loginService.getUser(request);
            if(user == null){
                response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
                logger.warn("用户不存在，token过期");
                return result;
            }
            result = adminQuizService.findTags();
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
        }        
        return result;
    }
}
