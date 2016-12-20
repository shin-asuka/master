package com.vipkid.trpm.interceptor;

import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.vo.StandardJsonObject;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.security.AppContext;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.dao.TeacherTokenDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherToken;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.util.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * 从app端打开h5页面,再从h5页面发起的请求拦截器
 */
public class H5CookieExpiredHandleInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = LoggerFactory.getLogger(H5CookieExpiredHandleInterceptor.class);


	@Autowired
    private LoginService loginService;

	@Autowired
	private TeacherTokenDao teacherTokenDao;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws IOException {
	    logger.info("IP:{},发起请求:{}",IpUtils.getIpAddress(request),request.getRequestURI());

		boolean isFilter = PropertyConfigurer.booleanValue("h5.cookie.check");
		if(!isFilter){
			//自测调试代码
			logger.info("配置[h5.cookie.check]={}不拦截,默认登录老师id是1890513",isFilter);
			Teacher teacher = loginService.findTeacherById(1890513);
			AppContext.setTeacher(teacher);
			User user = loginService.findUserById(teacher.getId());
			AppContext.setUser(user);
			return true;
		}

		String token = request.getHeader(RestfulController.AUTOKEN);
		String ip = IpUtils.getRequestRemoteIP();

		logger.info("preHandleRequest 用户  request token = {} ,ip = {}, url = {} ",token ,ip,request.getRequestURL());

		if(StringUtils.isBlank(token)){
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			setErrorResponse(response);
			logger.info("TOKEN为空 无效");
			return false;
		}
		//String teacherId = redisProxy.get(key);
		TeacherToken teacherToken = teacherTokenDao.findByToken(token);
		if(teacherToken==null || teacherToken.getTeacherId()==null){
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			setErrorResponse(response);
			logger.info("TOKEN{} 无效",token);
			return false;
		}
		Long teacherId = teacherToken.getTeacherId();
		logger.info("preHandleUserInfo teacherId = {} ",teacherId);

		User user = loginService.findUserById(teacherId);
		logger.info("preHandleUserInfo token = {} ,ip,user = {}, url = {} ", token, ip,
			user == null ? null : (user.getId() + "|" + user.getUsername()), request.getRequestURL());
		if (user == null) {
			logger.info("IP:{},用户为NULL。。。", IpUtils.getIpAddress(request));

			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			setErrorResponse(response);

			return false;
		}

		AppContext.setUser(user);

		logger.info("IP:{},user:{},发起请求:{}", IpUtils.getIpAddress(request), user.getId(),
			request.getRequestURI());

		Teacher teacher = loginService.findTeacherById(teacherId);
		if (teacher == null) {
			logger.info("IP:{},Teacher is NULL。。。", request.getRemoteAddr());
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

}
