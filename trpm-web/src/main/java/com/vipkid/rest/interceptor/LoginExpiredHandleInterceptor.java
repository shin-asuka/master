package com.vipkid.rest.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Preconditions;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestInterface;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.rest.LoginService;

/**
 * 登陆Token 拦截器，配合 RestInterface 拦截器一起使用
 * 
 * 该拦截器仅仅对包含@RestInterface注解的类或者方法起作用，
 * 
 * 1.主要对登陆Token进行检查
 * 
 * 2.通过用户的LifeCycle判断用户的请求是否有效
 * 
 * @author Along(ZengWeiLong)
 * @ClassName: LoginExpiredHandleInterceptor 
 * @date 2016年9月30日 下午3:11:32 
 *
 */
public class LoginExpiredHandleInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = LoggerFactory.getLogger(LoginExpiredHandleInterceptor.class);

    @Autowired
    private LoginService loginService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
	    
	    logger.info("IP:{},发起请求:{}",request.getRemoteAddr(),request.getRequestURI());

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //没有注解RestInterface，不进行拦截
        RestInterface restInterface = restAnnotation(handlerMethod);
        if(restInterface == null){
	        return true;
	    }
        //有注解RestInterface，则进行拦截下面判断
	    try{
    	    String token = request.getHeader(RestfulController.AUTOKEN);
    	    Preconditions.checkArgument(StringUtils.isNotBlank(token));
            User user = loginService.getUser(request);
            if(user == null){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                logger.warn("用户不存在，token过期");
                return false;
            }
            
            logger.info("IP:{},user:{},发起请求:{},参数:{}",request.getRemoteAddr(),user.getId(),request.getRequestURI(),JsonTools.getJson(request.getParameterMap()));
            
            Teacher teacher = this.loginService.getTeacher(request);
            if(teacher == null){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                logger.warn("用户老师账号不存在");
                return false;
            }
            //权限判断，符合条件的LifeCycle可以访问控制器
            if(ArrayUtils.contains(restInterface.lifeCycle(), teacher.getLifeCycle())){
                response.setStatus(HttpStatus.FORBIDDEN.value());
                logger.warn("没有权限访问的用户:允许状态{},当前状态:{}",restInterface.lifeCycle(),teacher.getLifeCycle());
                return false;
            }
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
	
	
	private RestInterface restAnnotation(HandlerMethod handlerMethod){
        RestInterface restInterface = handlerMethod.getMethodAnnotation(RestInterface.class);
        if (restInterface == null) {
            restInterface = handlerMethod.getBeanType().getAnnotation(RestInterface.class);
        }
        return restInterface;
	}

}
