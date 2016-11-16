package com.vipkid.trpm.interceptor;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vipkid.rest.security.AppContext;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.vipkid.http.service.AnnouncementHttpService;
import com.vipkid.rest.config.RestfulConfig.RoleClass;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.constant.ApplicationConstant.LoginType;
import com.vipkid.trpm.controller.portal.PersonalInfoController;
import com.vipkid.trpm.entity.Staff;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.service.passport.IndexService;
import com.vipkid.trpm.service.portal.LocationService;
import com.vipkid.trpm.service.rest.AdminQuizService;
import com.vipkid.trpm.service.rest.TeacherPageLoginService;
import com.vipkid.trpm.util.CookieUtils;
import com.vipkid.trpm.util.IpUtils;

public class CookieExpiredHandleInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = LoggerFactory.getLogger(CookieExpiredHandleInterceptor.class);

	private static final String AUTHORIZE = "permitAll";

	private static final int COOKIE_EXPIRED_CODE = 606;

	@Autowired
	private RedisProxy redisProxy;

	@Autowired
	private IndexService indexService;

	@Autowired
	private LocationService locationService;

	@Resource
	private AnnouncementHttpService announcementHttpService;
	
	@Autowired
	private AdminQuizService adminQuizService;
	
	@Autowired
	private TeacherPageLoginService teacherPageLoginService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws IOException {
	    logger.info("IP:{},发起请求:{}",IpUtils.getIpAddress(request),request.getRequestURI());
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //1.有注解PreAuthorize，不进行拦截
        PreAuthorize preAuthorize = preAnnotation(handlerMethod);
        if (null == preAuthorize || AUTHORIZE.equals(preAuthorize.value())) {
            return true;
        }

		String token = CookieUtils.getValue(request, CookieKey.TRPM_TOKEN);
		String xRequestedWith = request.getHeader("X-Requested-With");

		if (null == token && StringUtils.contains(xRequestedWith, "XMLHttpRequest")) {
			response.setStatus(COOKIE_EXPIRED_CODE);
			logger.info("COOKIE 无效 ajax");
			return false;
		} else if (null == token && StringUtils.isEmpty(xRequestedWith)) {
			response.sendRedirect(request.getContextPath() + "/index.shtml");
			logger.info("TOKEN 无效");
			return false;
		} else if (null != token && null == redisProxy.get(token)) {
			response.sendRedirect(request.getContextPath() + "/index.shtml");
			logger.info("TOKEN 无效");
			return false;
		}

        User user = indexService.getUser(request);
        if(user == null){
            logger.info("IP:{},用户为NULL。。。",IpUtils.getIpAddress(request));
            return false; 
        }
        AppContext.setUser(user);
        
        logger.info("IP:{},user:{},发起请求:{}",IpUtils.getIpAddress(request),user.getId(),request.getRequestURI());
        
        Teacher teacher = indexService.getTeacher(request);
        if(teacher == null){
            logger.info("IP:{},Teacher is NULL。。。",request.getRemoteAddr());
            return false; 
        }
        
		Staff manager = null;
		if(teacher !=null && teacher.getManager()>0){
			manager = indexService.getStaff(teacher.getManager());
			if(manager!=null){
				String managerName = manager.getEnglishName();
				request.setAttribute("TRPM_MANAGER_NAME", managerName);
			}
            AppContext.setTeacher(teacher);
		}
        
		request.setAttribute("locationService", locationService);
		request.setAttribute("TRPM_TEACHER", teacher);
		request.setAttribute("TRPM_USER", user);
		request.setAttribute("TRPM_COURSE_TYPES", indexService.getCourseType(user.getId()));
		request.setAttribute("recruitmentUrl", PropertyConfigurer.stringValue("recruitment.www"));
		
        Map<String,Object> role = indexService.getAllRole(user.getId());
        request.setAttribute("isPes",role.get(RoleClass.PES));
        request.setAttribute("isTe",role.get(RoleClass.TE));
        request.setAttribute("isTes",role.get(RoleClass.TES));
        request.setAttribute("isEvalClick",teacherPageLoginService.isType(user.getId(), LoginType.EVALUATION_CLICK));

        //是否需要考试
        if (adminQuizService.findNeedQuiz(user.getId())) {
            //请求映射的Class
            String clazz = handlerMethod.getBeanType().getCanonicalName();
            if(PersonalInfoController.class.getCanonicalName().equals(clazz)){
                //需要考试，请求的映射在基本信息修改类
                return true;
            }else{
                //需要考试，请求的映射不再基本信息修改则进行跳转
                logger.info("当前老师 [{}] 未通过考试", user.getName());
                response.sendRedirect("/training/material");
                return false;
            }
        }
		
        //是否需要密码修改
		if (!checkChangePasswordUri(request) && checkCookie(request)) {
			logger.info("拦截检测到需要修改密码进入页面");
			response.sendRedirect(request.getContextPath() + "/schedule.shtml");
			return false;
		}
		logger.info("通过拦截");
		return true;
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
		Cookie cookie = CookieUtils.getCookie(request, ApplicationConstant.CookieKey.TRPM_CHANGE_WINDOW);
		if (cookie != null && CookieKey.TRPM_CHANGE_WINDOW.equals(cookie.getValue())) {
			return true;
		}
		return false;
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
		String regex = "^(?:.*schedule.shtml)|(?:.*changePassword.shtml)|(?:.*disableLayer.json)|(?:.*changePasswordAction.json){0,}$";
		String requestUri = request.getRequestURI();
		return requestUri.matches(regex);
	}

}
