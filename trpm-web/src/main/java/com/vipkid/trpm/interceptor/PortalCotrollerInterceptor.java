package com.vipkid.trpm.interceptor;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.vipkid.http.service.AnnouncementHttpService;
import com.vipkid.http.vo.Announcement;

/**
 * @author zouqinghua
 * @date 2016年7月18日  下午1:18:37
 *
 */
public class PortalCotrollerInterceptor implements HandlerInterceptor{

	private static final Logger logger = LoggerFactory.getLogger(PortalCotrollerInterceptor.class);
	
	@Resource
	private AnnouncementHttpService announcementHttpService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(modelAndView!=null){
			logger.info("Get Anouncements");
			List<Announcement> announcements = announcementHttpService.findAnnouncementList();
			logger.info("Get announcements = "+announcements);
			//System.out.println("announcements : "+announcements.size());
			modelAndView.addObject("announcements", announcements);
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

}
