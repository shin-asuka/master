package com.vipkid.trpm.interceptor;


import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.vipkid.http.service.AnnouncementHttpService;
import com.vipkid.http.service.FileHttpService;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.vo.TeacherFile;
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

public class CookieExpiredHandleInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = LoggerFactory.getLogger(CookieExpiredHandleInterceptor.class);

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

	@Resource
	private AnnouncementHttpService announcementHttpService;
	
	@Autowired
	private AdminQuizService adminQuizService;
	
	@Autowired
	private TeacherPageLoginService teacherPageLoginService;
	
	@Autowired
    private FileHttpService fileHttpService;
	
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

        String token = loginService.getToken();
		//String token = CookieUtils.getValue(request, CookieKey.TRPM_TOKEN);
		String key = CacheUtils.getUserTokenKey(token);
		String ip = IpUtils.getRequestRemoteIP();
		
		String xRequestedWith = request.getHeader("X-Requested-With");
		logger.info("preHandleRequest 用户  request token = {} ,ip = {}, url = {} ",token ,ip,request.getRequestURL());
		
		if (null == token && StringUtils.contains(xRequestedWith, "XMLHttpRequest")) {
			response.setStatus(COOKIE_EXPIRED_CODE);
			logger.info("COOKIE 无效 ajax");
			return false;
		} else if (null == token && StringUtils.isEmpty(xRequestedWith)) {
			response.sendRedirect(request.getContextPath() + "/index.shtml");
			logger.info("TOKEN 无效");
			return false;
		} else if (null != token && null == redisProxy.get(key)) {
			response.sendRedirect(request.getContextPath() + "/index.shtml");
			logger.info("TOKEN 无效");
			return false;
		}

        User user = loginService.getUser();
        logger.info("preHandleUserInfo token = {} ,ip,user = {}, url = {} ",token ,ip,user==null?null:(user.getId()+"|"+user.getUsername()),request.getRequestURL());
        if(user == null){
            logger.info("IP:{},用户为NULL。。。",IpUtils.getIpAddress(request));
            //response.sendRedirect(request.getContextPath() + "/index.shtml");
            return false; 
        }
        
        //判断当前用户所在地区的ip是否变化，如果变化。则返回空用户，用户重新登陆
        Boolean isIpChange = IpUtils.checkUserIpChange(user);
        //isIpChange = true;
        if(isIpChange){
        	String currentIp = IpUtils.getRequestRemoteIP();
        	String uri = request.getRequestURI();
        	String redisIp = user.getIp();
        	logger.info("用户IP地址发生变化  getUser userIPChange token = {},uri={},user = {}, redisIp = {}, currentIp = {}",token,uri,user.getId()+"|"+user.getUsername(),redisIp,currentIp);
        	CookieUtils.removeCookie(response, CookieKey.TRPM_TOKEN, null, null);
        	if( StringUtils.contains(xRequestedWith, "XMLHttpRequest")){
        		response.setStatus(HttpStatus.UNAUTHORIZED.value());
        	}else{
        		response.sendRedirect(request.getContextPath() + "/");
        	}
        	
        	return false;
        }
        
        AppContext.setUser(user);
        
        logger.info("IP:{},user:{},发起请求:{}",IpUtils.getIpAddress(request),user.getId(),request.getRequestURI());
        
        Teacher teacher = loginService.getTeacher();
        if(teacher == null){
            logger.info("IP:{},Teacher is NULL。。。",request.getRemoteAddr());
            return false; 
        }
        
		Staff manager = null;
		if(teacher !=null && teacher.getManager()>0){
			manager = loginService.getStaff(teacher.getManager());
			if(manager!=null){
				String managerName = manager.getEnglishName();
				request.setAttribute("TRPM_MANAGER_NAME", managerName);
			}
            AppContext.setTeacher(teacher);
		}
        
		Long teacherId = teacher.getId();
		
		logger.info("获取老师文件信息 queryTeacherFiles teacherId = {}",teacherId );
		TeacherFile teacherFile = fileHttpService.queryTeacherFiles(teacherId);
		
		logger.info("老师文件信息 teacherFile = {}",JsonUtils.toJSONString(teacherFile));
		request.setAttribute("teacherFile", teacherFile);
				
		request.setAttribute("locationService", locationService);
		request.setAttribute("TRPM_TEACHER", teacher);
		request.setAttribute("TRPM_USER", user);
		request.setAttribute("TRPM_COURSE_TYPES", loginService.getCourseType(user.getId()));
		request.setAttribute("recruitmentUrl", PropertyConfigurer.stringValue("recruitment.www"));
		
        Map<String,Object> role = loginService.getAllRole(user.getId());
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
                response.sendRedirect("/teaching/material");
                return false;
            }
        }
		
        //是否需要密码修改
		if (!checkChangePasswordUri(request) && checkCookie(request)) {
			logger.info("拦截检测到需要修改密码进入页面");
			response.sendRedirect(request.getContextPath() + "/bookings.shtml");
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
