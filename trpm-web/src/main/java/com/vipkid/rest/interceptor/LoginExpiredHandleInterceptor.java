package com.vipkid.rest.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Preconditions;
import com.vipkid.rest.RestfulController;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.rest.LoginService;

public class LoginExpiredHandleInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = LoggerFactory.getLogger(LoginExpiredHandleInterceptor.class);

    @Autowired
    private LoginService loginService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
	    
	    logger.info("IP:{},发起请求:{}",request.getRemoteAddr(),request.getRequestURI());
	    
	    HandlerMethod handlerMethod = (HandlerMethod) handler;
	    RestInterface restInterface = handlerMethod.getMethodAnnotation(RestInterface.class);
        if (restInterface == null) {
            restInterface = handlerMethod.getBeanType().getAnnotation(RestInterface.class);
        }
        if(restInterface == null || !restInterface.value()){
            return true;
        }
	    try{
    	    String token = request.getHeader(RestfulController.AUTOKEN);
    	    Preconditions.checkArgument(StringUtils.isNotBlank(token));
            User user = loginService.getUser(request);
            if(user == null){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                logger.warn("用户不存在，token过期");
                return false;
            }
            logger.info("IP:{},user:{},发起请求:{}",request.getRemoteAddr(),user.getId(),request.getRequestURI());
    	    request.setAttribute(RestfulController.AUTOKEN, user);
    		return true;
	    }catch(IllegalArgumentException e){
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
	    } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
	    return false;
	}

}
