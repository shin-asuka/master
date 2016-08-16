package com.vipkid.trpm.interceptor;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.HtmlUtils;

/**
 * HTML编码过滤器
 * @className:HtmlEncodingFilter.java 
 * @version: 1.0 2015-4-1 15:02:13
 */
public class HtmlEncodingFilter extends OncePerRequestFilter {

	/* (non-Javadoc)
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal (
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		boolean ignoreHtmlEncoding = Boolean.parseBoolean(request.getHeader("IgnoreHtmlEncoding"));
		
		if (ignoreHtmlEncoding) {
			// 解决js注入
			filterChain.doFilter(request, response);
		} else {
			HttpServletRequest r = new MyHttpServletRequest(request);
			filterChain.doFilter(r, response);
		}
	}
	
	/**
	 * MyHttpServletRequest请求类.
	 *
	 * @className:MyHttpServletRequest 
	 * @version: 1.0 2015-4-1 15:11:06
	 */
	class MyHttpServletRequest extends HttpServletRequestWrapper {
		
		/**
		 * MyHttpServletRequest类构造方法，初始化方法.
		 *
		 * @param request request
		 */
		public MyHttpServletRequest(HttpServletRequest request) {
			super(request);
		}
		
		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
		 */
		@Override
		public String getParameter(String name) {
			String value=super.getParameter(name);
			
			if (value != null) {
			    value = HtmlUtils.htmlEscape(value);
				return value;
			}
			return super.getParameter(name);
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getParameterValues(java.lang.String)
		 */
		@Override
		public String[] getParameterValues(String name) {
			String[] values = super.getParameterValues(name);
			if (values != null) {
				String[] newValues = new String[values.length];
				for (int i = 0; i < values.length; i++) {
					String value = values[i];
					value = HtmlUtils.htmlEscape(value);
					newValues[i] = value;
				}
				return newValues;
			}
			return values;
		}
	}
}