package com.vipkid.rest.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.rest.utils.CookieUtils;


/**
 * @author zouqinghua
 * @date 2016年10月20日  下午8:50:48
 *
 */
public class AppContext {

	private static Logger logger = LoggerFactory.getLogger( AppContext.class);
	private static final String HTTP_HEADER_TOKEN = "token";

    public static final String HTTP_AUTHENTICATION = "Authorization";
    
    
    public static String getToken(HttpServletRequest request){
    	String token = "";
    	try {
    		String authorization = getAuthorization(request);
        	if (StringUtils.isNoneBlank(authorization)) {
                String[] authorizations = authorization.split(" ");
                if(authorizations.length ==1){ //authorization = token
                	token = authorizations[0];
                }else if(authorizations.length ==3){  //authorization = server userId token
                	token = authorizations[2];
                }
            }else{
            	token = request.getHeader(HTTP_HEADER_TOKEN);
            }
		} catch (Exception e) {
			logger.error("获取token异常", e);
		}
    	
    	return token;
    }
    
	public static String getAuthorization(HttpServletRequest request){
    	String authorization = null;
    	if(request!=null){
    		authorization = request.getHeader(HTTP_AUTHENTICATION);
            if(authorization == null){
            	authorization = CookieUtils.getCookie(request, HTTP_AUTHENTICATION);
            }
    	}
    	return authorization;
    }

}
