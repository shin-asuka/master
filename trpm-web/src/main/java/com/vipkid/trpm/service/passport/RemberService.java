package com.vipkid.trpm.service.passport;

import java.util.Base64;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.security.SHA256PasswordEncoder;
import com.vipkid.trpm.util.CacheUtils;
import com.vipkid.trpm.util.CookieUtils;
import com.vipkid.trpm.util.IpUtils;

@Service
public class RemberService {
    

    private static Logger logger = LoggerFactory.getLogger(RemberService.class);
    
    @Autowired
    private RedisProxy redisProxy;
    
    public static String cache = "-subkey-";
    
    /**
     * 
     * 更新Cookie和Redis里面相应的key
     * 
     * */
    public void replaceKeys(HttpServletRequest request,HttpServletResponse response){
        String _remberme = CookieUtils.getValue(request,CookieKey.TRPM_PASSPORT);
        if(StringUtils.isNotBlank(_remberme)){
            String ip = this.getRemortIP();
            String tokenId = CookieKey.TRPM_LOGIN_TOKEN_ID+ip;
            String _cache_token = CookieUtils.getValue(request,tokenId);
            if(StringUtils.isNotBlank(_cache_token)){
                //取得redis值并删除
                String _redis_token_value = 
                redisProxy.get("TRPM"+cache+_cache_token); 
                redisProxy.del("TRPM"+cache+_cache_token);
                if(StringUtils.isNotBlank(_redis_token_value)){
                    logger.info("IP:为"+ip+"的用户对服务发起登陆，申请登陆Token");
                    //重新更换值
                    String key = CacheUtils.getTokenId();
                    CookieUtils.setCookie(response, tokenId,key, null);
                    CookieUtils.setCookie(response, CookieKey.TRPM_LOGIN_TOKEN_ID,ip, null);
                    redisProxy.set("TRPM"+cache+key, _redis_token_value);
                }
            }            
        }
    }
    
    /**
     * 首次记住密码放置Cookie和redis 
     * @Author:ALong (ZengWeiLong)
     * @param request
     * @param response
     * @param user    
     * void
     * @date 2016年4月29日
     */
    public void addKeys(HttpServletRequest request,HttpServletResponse response,User user){
        Map<String, String> rememberMap = Maps.newHashMap();
        rememberMap.put("email", new String(Base64.getEncoder().encode(user.getUsername().getBytes())));
        String value = new String(Base64.getEncoder().encode(JsonTools.getJson(rememberMap).getBytes()));
        CookieUtils.setCookie(response, CookieKey.TRPM_PASSPORT, value, null);
        
        String ip = this.getRemortIP();
        String tokenId = CookieKey.TRPM_LOGIN_TOKEN_ID+ip;
        String _token = CacheUtils.getTokenId();
        CookieUtils.setCookie(response, CookieKey.TRPM_LOGIN_TOKEN_ID, ip, null);
        CookieUtils.setCookie(response, tokenId, _token, null);
        Map<String,String> map = Maps.newHashMap();
        map.put("pemail", user.getUsername());
        map.put("ptoken", user.getPassword());
        redisProxy.set("TRPM"+cache+_token,JsonTools.getJson(map),CookieUtils.getMaxAge());
    }
    
    /**
     * 清空redis和cookie里面的值
     * @Author:ALong (ZengWeiLong)
     * @param request
     * @param response    
     * void
     * @date 2016年4月29日
     */
    public void delkeys(HttpServletRequest request,HttpServletResponse response){
        String ip = this.getRemortIP();
        String tokenId = CookieKey.TRPM_LOGIN_TOKEN_ID+ip;
        String _cache_token = CookieUtils.getValue(request,tokenId);
        if(_cache_token != null){
            redisProxy.del("TRPM"+cache+_cache_token);
        }
        CookieUtils.removeCookie(response, tokenId, null, null);
        CookieUtils.removeCookie(response, CookieKey.TRPM_LOGIN_TOKEN_ID, null, null);
        CookieUtils.removeCookie(response, CookieKey.TRPM_PASSPORT, null, null);
    }
    
    /**
     * 密码判断逻辑
     * @Author:ALong (ZengWeiLong)
     * @param request
     * @param _remberme
     * @param comperPwd
     * @param user
     * @return    
     * boolean
     * @date 2016年4月29日
     */
    public boolean checkNotPassword(HttpServletRequest request,String _remberme,String _strPwd,User user){
        //对于已经存在记住密码的,只要判断Cookie中的key值从redis中取到密码判断是否与登陆账户密码相等即可
        SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
        String comperPwd = encoder.encode(_strPwd);
        if(StringUtils.isNotBlank(_remberme)){
            String ip = this.getRemortIP();
            String tokenId = CookieKey.TRPM_LOGIN_TOKEN_ID+ip;
            String _cache_token = CookieUtils.getValue(request,tokenId);
            String json = redisProxy.get("TRPM"+cache+_cache_token);
            if(json == null){
                return true;
            }
            Map<String,String> map = JsonTools.readValue(json,new TypeReference<Map<String,String>>() {});
            if(!user.getUsername().equals(map.get("pemail"))){
                return true;
            }
            comperPwd = map.get("ptoken");
        }        
        if (!comperPwd.equals(user.getPassword())) {
            return true;
        }        
        return false;
    }
    
    /**记住密码逻辑
     * @Author:ALong (ZengWeiLong)
     * @param remember
     * @param _remberme
     * @param request
     * @param response
     * @param user    
     * void
     * @date 2016年4月29日
     */
    public void remberMe(boolean remember,String _remberme,HttpServletRequest request,HttpServletResponse response,User user){
        //记住密码逻辑判断
        try{
            if(remember || StringUtils.isNotBlank(_remberme)){
                this.delkeys(request, response);
                this.addKeys(request, response, user);
            } else {
                this.delkeys(request, response);
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            throw e;
        }
    }
        
    /**
     * 客户端IP
     * 
     * @Author:ALong
     * @param request
     * @return String
     * @date 2016年1月6日
     */
    public String getRemortIP() {
        String ip = IpUtils.getRemoteIP();
        return ip = ip.replace(",","-").replace(" ","").replace(".","-");
    }
}
