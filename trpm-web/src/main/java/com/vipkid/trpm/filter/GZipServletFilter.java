package com.vipkid.trpm.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GZipServletFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(GZipServletFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		if (acceptsGZipEncoding(httpServletRequest)) {
			httpServletResponse.addHeader("Content-Encoding", "gzip");

			GZipServletResponseWrapper gzipResponse = new GZipServletResponseWrapper(
					httpServletResponse);
			filterChain.doFilter(request, gzipResponse);

			if (logger.isDebugEnabled()) {
				logger.debug("Request url: {} was gzip compressed.",
						httpServletRequest.getRequestURL());
			}

			gzipResponse.close();
		} else {
			filterChain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	private boolean acceptsGZipEncoding(HttpServletRequest httpRequest) {
		String acceptEncoding = httpRequest.getHeader("Accept-Encoding");

		return (!StringUtils.isEmpty(acceptEncoding) && StringUtils
				.contains(acceptEncoding, "gzip"));
	}

}
