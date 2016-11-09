package com.vipkid.rest.security;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.rest.utils.CookieUtils;

import java.util.Map;

/**
 * @author zouqinghua
 * @date 2016年10月20日 下午8:50:48
 *
 */
public class AppContext {

    private static Logger logger = LoggerFactory.getLogger(AppContext.class);
    private static final String HTTP_HEADER_TOKEN = "token";
    public static final String USER_INFO_NAME = "user";
    public static final String TEACHER_INFO_NAME = "teacher";

    public static final String HTTP_AUTHENTICATION = "Authorization";

    private static ThreadLocal<Map<String, Object>> context = new ThreadLocal<Map<String, Object>>();

    public static Map<String, Object> getContext() {
        if (context == null) {
            context = new ThreadLocal<>();
        }
        if (context.get() == null) {
            context.set(Maps.newHashMap());
        }
        return context.get();
    }

    public static void setContext(Map<String, Object> context) {
        AppContext.context.set(context);
    }

    public static void releaseContext() {
        try {
            context.remove();
        } catch (Exception e) {
        }
    }

    public static void setUser(User user) {
        put(USER_INFO_NAME, user);
    }

    public static User getUser() {
        User user = (User) get(USER_INFO_NAME);
        return user;
    }

    public static void setTeacher(Teacher teacher) {
        put(TEACHER_INFO_NAME, teacher);
    }

    public static Teacher getTeacher() {
        Teacher teacher = (Teacher) get(TEACHER_INFO_NAME);
        return teacher;
    }

    public static void put(String key, Object value) {
        getContext().put(key, value);
    }

    public static Object get(String key) {
        return getContext().get(key);
    }

    public static String getToken(HttpServletRequest request) {
        String token = "";
        try {
            String authorization = getAuthorization(request);
            if (StringUtils.isNoneBlank(authorization)) {
                String[] authorizations = authorization.split(" ");
                if (authorizations.length == 1) { // authorization = token
                    token = authorizations[0];
                } else if (authorizations.length == 3) { // authorization = server userId token
                    token = authorizations[2];
                }
            } else {
                token = request.getHeader(HTTP_HEADER_TOKEN);
            }
        } catch (Exception e) {
            logger.error("获取token异常", e);
        }

        return token;
    }

    public static String getAuthorization(HttpServletRequest request) {
        String authorization = null;
        if (request != null) {
            authorization = request.getHeader(HTTP_AUTHENTICATION);
            if (authorization == null) {
                authorization = CookieUtils.getCookie(request, HTTP_AUTHENTICATION);
            }
        }
        return authorization;
    }

}
