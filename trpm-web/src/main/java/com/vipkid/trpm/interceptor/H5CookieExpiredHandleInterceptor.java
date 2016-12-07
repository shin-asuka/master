package com.vipkid.trpm.interceptor;

import com.vipkid.http.service.AnnouncementHttpService;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.vo.StandardJsonObject;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig.RoleClass;
import com.vipkid.rest.security.AppContext;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.constant.ApplicationConstant.LoginType;
import com.vipkid.trpm.controller.portal.PersonalInfoController;
import com.vipkid.trpm.entity.Staff;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.service.portal.LocationService;
import com.vipkid.trpm.service.rest.AdminQuizService;
import com.vipkid.trpm.service.rest.LoginService;
import com.vipkid.trpm.service.rest.TeacherPageLoginService;
import com.vipkid.trpm.util.CacheUtils;
import com.vipkid.trpm.util.CookieUtils;
import com.vipkid.trpm.util.IpUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * 从app端打开h5页面,再从h5页面发起的请求拦截器
 */
public class H5CookieExpiredHandleInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = LoggerFactory.getLogger(H5CookieExpiredHandleInterceptor.class);

	private static final String AUTHORIZE = "permitAll";

	private static final int COOKIE_EXPIRED_CODE = 606;

	@Autowired
	private RedisProxy redisProxy;

	/*@Autowired
	private IndexService indexService;*/

	@Autowired
    private LoginService loginService;

	@Autowired
	private LocationService locationService;

	@Autowired
	private AdminQuizService adminQuizService;

	@Autowired
	private TeacherPageLoginService teacherPageLoginService;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws IOException {
	    logger.info("IP:{},发起请求:{}",IpUtils.getIpAddress(request),request.getRequestURI());
//        HandlerMethod handlerMethod = (HandlerMethod) handler;
//        //1.有注解PreAuthorize，不进行拦截
//        PreAuthorize preAuthorize = preAnnotation(handlerMethod);
//        if (null == preAuthorize || AUTHORIZE.equals(preAuthorize.value())) {
//            return true;
//        }

		boolean isFilter = PropertyConfigurer.booleanValue("h5.cookie.check");
		if(!isFilter){
			logger.info("配置[h5.cookie.check]={}不拦截,默认登录老师id是359",isFilter);
			Teacher teacher = loginService.findTeacherById(359);
			AppContext.setTeacher(teacher);
			return true;
		}

		//String token = CookieUtils.getValue(request, CookieKey.TRPM_TOKEN);
		String token = request.getHeader(RestfulController.AUTOKEN);
		String key = CacheUtils.getUserTokenKeyFromApp(token);
		String ip = IpUtils.getRequestRemoteIP();

		logger.info("preHandleRequest 用户  request token = {} ,ip = {}, url = {} ",token ,ip,request.getRequestURL());

//		if (null == token && StringUtils.contains(xRequestedWith, "XMLHttpRequest")) {
//			response.setStatus(COOKIE_EXPIRED_CODE);
//			logger.info("COOKIE 无效 ajax");
//			return false;
//		} else if (null == token && StringUtils.isEmpty(xRequestedWith)) {
//			response.sendRedirect(request.getContextPath() + "/index.shtml");
//			logger.info("TOKEN 无效");
//			return false;
//		} else if (null != token && null == redisProxy.get(key)) {
//			response.sendRedirect(request.getContextPath() + "/index.shtml");
//			logger.info("TOKEN 无效");
//			return false;
//		}
		if(key == null){
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			setErrorResponse(response);
			logger.info("TOKEN 无效");
			return false;
		}
		String teacherId = redisProxy.get(key);
		logger.info("preHandleUserInfo teacherId = {} ",teacherId);
		if(!NumberUtils.isNumber(teacherId)){
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			setErrorResponse(response);
			logger.info("TOKEN 无效");
			return false;
		}

        User user = loginService.findUserById(Long.valueOf(teacherId));
        logger.info("preHandleUserInfo token = {} ,ip,user = {}, url = {} ",token ,ip,user==null?null:(user.getId()+"|"+user.getUsername()),request.getRequestURL());
        if(user == null){
            logger.info("IP:{},用户为NULL。。。",IpUtils.getIpAddress(request));
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			setErrorResponse(response);

            return false;
        }

        AppContext.setUser(user);

        logger.info("IP:{},user:{},发起请求:{}",IpUtils.getIpAddress(request),user.getId(),request.getRequestURI());

        Teacher teacher = loginService.findTeacherById(Long.valueOf(teacherId));
        if(teacher == null){
            logger.info("IP:{},Teacher is NULL。。。",request.getRemoteAddr());
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			setErrorResponse(response);
            return false;
        }

		AppContext.setTeacher(teacher);


		logger.info("通过拦截");
		return true;
	}

	private void setErrorResponse(HttpServletResponse response) {
		try {
			String data = null;

			StandardJsonObject errorJsonObject = StandardJsonObject.newErrorJsonObject("no right to access");
			data = JsonUtils.toJSONString(errorJsonObject);

			response.setContentType("application/json;charset=UTF-8");
			Writer writer = response.getWriter();
			writer.write(data);
			writer.close();
		} catch (IOException e) {
			logger.error("ERROR ## write message happened error, the trace ", e);
		}
	}

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        AppContext.releaseContext();
        super.postHandle(request, response, handler, modelAndView);
    }

	private PreAuthorize preAnnotation(HandlerMethod handlerMethod){
       PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);
       //是否公开LINK
       if (null == preAuthorize) {
           preAuthorize = handlerMethod.getBeanType().getAnnotation(PreAuthorize.class);
       }
        return preAuthorize;
    }

	/**
	 * 检查修改密码的Cookie是否存在，存在:true 不存在 false
	 *
	 * @Author:ALong (ZengWeiLong)
	 * @param request
	 * @return boolean
	 * @date 2016年4月19日
	 */
	private boolean checkCookie(HttpServletRequest request) {
		Cookie cookie = CookieUtils.getCookie(request, CookieKey.TRPM_CHANGE_WINDOW);
		return cookie != null && CookieKey.TRPM_CHANGE_WINDOW.equals(cookie.getValue());
	}

	/**
	 * 检查是否拦截uri请求 不拦截：true 拦截:false
	 * 
	 * @Author:ALong (ZengWeiLong)
	 * @param request
	 * @return boolean
	 * @date 2016年4月19日
	 */
	private boolean checkChangePasswordUri(HttpServletRequest request) {
		String regex = "^(?:.*bookings.shtml)|(?:.*changePassword.shtml)|(?:.*disableLayer.json)|(?:.*changePasswordAction.json){0,}$";
		String requestUri = request.getRequestURI();
		return requestUri.matches(regex);
	}

}
