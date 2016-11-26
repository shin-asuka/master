package com.vipkid.trpm.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Lists;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.vipkid.http.service.GeoIPService;
import com.vipkid.rest.utils.SpringContextHolder;
import com.vipkid.trpm.entity.User;

public class IpUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(IpUtils.class);
	private static GeoIPService geoIPService = SpringContextHolder.getBean(GeoIPService.class);
	
	/**
	 * 泰国 iso_code = "TH"  geoname_id = 1605651, namae = Thailand 
	 */
	public final static String THAILAND = "TH"; //泰国 iso_code 1605651 Thailand
	public final static List<String> CHECK_CONTRYS = Lists.newArrayList(THAILAND); 
	public final static String COUNTRY_NAME_ZHCN = "zh-CN"; //语言
	
    /**
     * 客户端IP
     * 
     * @Author:ALong
     * @return String
     * @date 2016年1月6日
     */
    public static String getRemoteIP() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }
    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * <p>
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？ 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * <p>
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130, 192.168.1.100
     * <p>
     * 用户真实IP为： 192.168.1.110
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");

        if (StringUtils.isNotEmpty(ip)) {
            ip = ip.replaceAll(" ", "").replaceAll("unknown", "");
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if(ip!=null && ip.equals("0:0:0:0:0:0:0:1")){
        	ip =  "127.0.0.1";
        }
        if (ip != null && isIP(ip)) {
            return ip;
        }
        return null;
    }
    public static boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        return ipAddress;
    }
    
    /**
     * 获取用户真实IP地址
     * 
     * @return
     */
    public static String getRequestRemoteIP(){
		String ip = "";
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			ip = IpUtils.getIpAddress(request );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}
    
    /**
	 * 检查用户IP是否变化
	 * @param user
	 * @return
	 */
	public static Boolean checkUserIpChange(User user){
		if(user == null){
			return false; //跳过ip检查
		}
		
		if(!PropertyConfigurer.booleanValue("user.checkIP")){
			return false; //跳过ip检查
		}
		
		//针对退出在线教室进行校验
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String uri = request.getRequestURI();
		Boolean isNeedCheckedUrl = isNeedCheckUrl(uri);
		logger.info("RequestUserUri isNeedCheckedUrl = {}, uri = {}, user = {}",
				isNeedCheckedUrl,uri,user.getId()+"|"+user.getUsername());
		if(!isNeedCheckedUrl){
			return false;
		}
		
		Boolean isChange = false;
		String redisIp = user.getIp();
		if(StringUtils.isNotBlank(redisIp)){
			
			String ip = IpUtils.getRequestRemoteIP();
			Country country = geoIPService.getCountryName(ip);
			
			//校验ip所在国家，对需要检查国家进行校验
			String countryName = country==null ?null:country.getNames().get(IpUtils.COUNTRY_NAME_ZHCN);
			City city = geoIPService.getCity(ip);
        	String cityName = city==null?null:city.getNames().get(IpUtils.COUNTRY_NAME_ZHCN);
        	String countryIsoCode = country==null ?null : country.getIsoCode();
			Boolean isCheckedCountry = isNeedCheckCountry(countryIsoCode);
			
			logger.info("RequestUserCountry isNeedCheckCountry = {},user = {}, currentIp = {},IsoCode = {},countryName = {} , cityName = {}",
					isCheckedCountry,user.getId()+"|"+user.getUsername(),ip,countryIsoCode,countryName,cityName);
			if(!isCheckedCountry){
				return false; //国家 不在检测范围类， 跳过ip检查
			}
			
			//验证IP地址是否发生改变
	        if((StringUtils.isNotBlank(ip) && ip.equals(redisIp))){
	        	isChange = true;
	        }
	        
	        logger.info("检测用户IP地址 RequestUserIP isChange={} user = {}, redisIp = {}, currentIp = {}, uri = {}",isChange,user.getId()+"|"+user.getUsername(),redisIp,ip,uri);
	        if(isChange){
	        	Country countryOld = geoIPService.getCountryName(redisIp);
	        	String countryOldName = countryOld==null ?null:countryOld.getNames().get(IpUtils.COUNTRY_NAME_ZHCN);
	        	City cityOld = geoIPService.getCity(redisIp);
	        	String cityOldName = cityOld==null ?null:cityOld.getNames().get(IpUtils.COUNTRY_NAME_ZHCN);
	        	
	        	logger.info("RequestUser 用户IP地址发生变化, userIPChange user = {}, redisIp = {}, currentIp = {},countryName = {},cityName = {},countryOldName = {},cityName = {}, cityOldName = {}",
	        			user.getId()+"|"+user.getUsername(),redisIp,ip,countryName,cityName,countryOldName,cityOldName);
	        }
		}
		return isChange;
	}
	
	public static Boolean isNeedCheckCountry(String countryIsoCode){
		Boolean isChecked = false;
		if(StringUtils.isNotBlank(countryIsoCode)){
			try {
				String checkCountry = PropertyConfigurer.stringValue("user.checkCountrys");
				checkCountry = checkCountry.toUpperCase();
				List<String> checkCountrys = Lists.newArrayList(checkCountry.split(","));
				if( CollectionUtils.isNotEmpty(checkCountrys) 
						&& checkCountrys.contains(countryIsoCode.toUpperCase())){
					isChecked = true; //url 在检测范围类， 进行ip检查
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isChecked;
	}
	
	public static Boolean isNeedCheckUrl(String uri){
		Boolean isChecked = false;
		if(StringUtils.isNotBlank(uri)){
			try {
				String checkUrl = PropertyConfigurer.stringValue("user.checkUrls");
				List<String> checkUrls = Lists.newArrayList(checkUrl.split(","));
				String uriReq = uri;
				/*String ext = "";
				if(uri.lastIndexOf(".")>-1){
					uriReq = uri.substring(0,uri.lastIndexOf("."));
					ext = uri.substring(uri.lastIndexOf("."));
				}*/
				if(CollectionUtils.isNotEmpty(checkUrls) && checkUrls.contains(uriReq)){
					for (String url : checkUrls) {
						if(uriReq.equalsIgnoreCase(url)){
							isChecked = true; //url 在检测范围类， 进行ip检查
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isChecked;
	}
    
}
