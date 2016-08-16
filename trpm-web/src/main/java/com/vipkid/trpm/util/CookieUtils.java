package com.vipkid.trpm.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class CookieUtils {

    /* Cookie过期时间，默认1个月 */
    private static int maxAge = 30 * 24 * 3600;

    private static String path = "/";

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        CookieUtils.path = path;
    }

    public static int getMaxAge() {
        return maxAge;
    }

    public static void setMaxAge(int maxAge) {
        CookieUtils.maxAge = maxAge;
    }

    public static void setCookie(HttpServletResponse response, String name, String value,
            String domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(getPath());
        cookie.setMaxAge(getMaxAge());

        if (!StringUtils.isEmpty(domain)) {
            cookie.setDomain(domain);
        }

        try {
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeCookie(HttpServletResponse response, String name, String value,
            String domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(getPath());
        cookie.setMaxAge(0);

        if (!StringUtils.isEmpty(domain)) {
            cookie.setDomain(domain);
        }

        response.addCookie(cookie);
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = readCookies(request);

        if (cookieMap.containsKey(name)) {
            Cookie cookie = cookieMap.get(name);
            return cookie;
        } else {
            return null;
        }
    }

    public static Cookie getCookie(HttpServletRequest request, Enum<?> enums) {
        return getCookie(request, enums.name());
    }

    public static String getValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        return (null == cookie) ? null : cookie.getValue();
    }

    public static String getValue(HttpServletRequest request, Enum<?> enums) {
        return getValue(request, enums.name());
    }

    public static Map<String, Cookie> readCookies(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();

        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }

        return cookieMap;
    }

}
