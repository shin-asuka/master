package com.vipkid.trpm.interceptor;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.vipkid.http.service.AnnouncementHttpService;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.constant.ApplicationConstant.RedisConstants;
import com.vipkid.trpm.controller.portal.PersonalInfoController;
import com.vipkid.trpm.entity.Staff;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.service.passport.IndexService;
import com.vipkid.trpm.service.portal.LocationService;
import com.vipkid.trpm.service.rest.AdminQuizService;
import com.vipkid.trpm.util.CookieUtils;

public class CookieExpiredHandleInterceptor extends HandlerInterceptorAdapter {

	private static final String IS_DISPLAY_PAYROLL = "isDisplayPayroll";
	private static final String PAYROLL_OPEN_FLAG = "payroll_open_for_teacher";
	private static final String PAYROLL_BLACK_LIST = "payroll_black_list_";
	private static final String PAYROLL_WHITE_LIST = "payroll_white_list_";
	private static final String PAYROLL_EXD = "payroll_exd";
	private static final String PAYROLL_OPEN_VALUE = "1";

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
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws IOException {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);

		if (null == preAuthorize) {
			preAuthorize = handlerMethod.getBeanType().getAnnotation(PreAuthorize.class);
		}

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
		Teacher teacher = indexService.getTeacher(request);
		Staff manager = null;
		if(teacher !=null && teacher.getManager()>0){
			manager = indexService.getStaff(teacher.getManager());
			if(manager!=null){
				String managerName = manager.getEnglishName();
				//request.setAttribute("TRPM_MANAGER", manager);
				request.setAttribute("TRPM_MANAGER_NAME", managerName);
			}
		}
		request.setAttribute("locationService", locationService);
		request.setAttribute("TRPM_TEACHER", teacher);
		request.setAttribute("TRPM_USER", user);
		request.setAttribute("TRPM_COURSE_TYPES", indexService.getCourseType(user.getId()));
		request.setAttribute("recruitmentUrl", PropertyConfigurer.stringValue("recruitment.www"));
		request.setAttribute("isPe", indexService.isPe(user.getId()));
		try {
			// teacher payroll 开关
			String openFlagInConfig = PropertyConfigurer.stringValue(PAYROLL_OPEN_FLAG);
			// redis 中的teacher payroll 开关
			String openFlagInRedis = redisProxy.get(PAYROLL_OPEN_FLAG);
			// redis 中的 payroll 黑名单
			//String blackListRedis = redisProxy.get(PAYROLL_BLACK_LIST + user.getId());
			String whiteListRedis = redisProxy.get(PAYROLL_WHITE_LIST + user.getId());
			if (openFlagInRedis == null) {
				redisProxy.setex(PAYROLL_OPEN_FLAG, RedisConstants.PAYROLL_DISPLAY_MAX_NUM_EXCEED_DAY_SEC,
						openFlagInConfig);
				openFlagInRedis = redisProxy.get(PAYROLL_OPEN_FLAG);
			}
			
			if (PAYROLL_OPEN_VALUE.equals(openFlagInRedis) || PAYROLL_EXD.equals(whiteListRedis)) {
				request.setAttribute(IS_DISPLAY_PAYROLL, true);
			}
		} catch (Exception e) {
			logger.error("捕获payroll redis 异常 ，teacher id是{}",user.getId());
		}
		
        String clazz = handlerMethod.getBeanType().getCanonicalName();
        if (adminQuizService.findNeedQuiz(user.getId())) {
            if(!PersonalInfoController.class.getCanonicalName().equals(clazz)){
                logger.info("当前老师 [{}] 未通过考试", user.getName());
                response.sendRedirect("/training/material");
                return false;
            }
            return true;
        }
		
		if (!checkChangePasswordUri(request) && checkCookie(request)) {
			logger.info("拦截检测到需要修改密码进入页面");
			response.sendRedirect(request.getContextPath() + "/schedule.shtml");
			return false;
		}
		logger.info("通过拦截");
		
		
		//拦截器配置,执行portal公共事件处理
//		logger.info("Get Anouncements");
//		List<Announcement> announcements = announcementHttpService.findAnnouncementList();
//		request.setAttribute("announcements", announcements);
//		logger.info("Get announcements = "+announcements);
		return true;
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
