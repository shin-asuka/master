package com.vipkid.rest.interceptor;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.vipkid.enums.AppEnum;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.UserEnum;
import com.vipkid.recruitment.utils.RequestUtils;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.AnnotaionUtils;
import com.vipkid.rest.interceptor.annotation.Authentication.Portal;
import com.vipkid.rest.interceptor.annotation.RemoteInterface;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.utils.UserUtils;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.util.CookieUtils;
import com.vipkid.trpm.util.IpUtils;
/**
 * 登陆Token认证, API认证 拦截器，配合 RestInterface,RemoteInterface 拦截器一起使用
 * 
 * 该拦截器仅仅对包含@RestInterface 和@RemoteInterface 注解的类或者方法起作用，
 * 当@RestInterface 和@RemoteInterface共存时RemoteInterface优先级高于RestInterface
 * 
 * 1.主要对登陆Token进行检查 或者 对调用方进行认证
 * 
 * 2.通过用户的LifeCycle判断用户的请求是否有效
 * 
 * @author Along(ZengWeiLong)
 * @ClassName: LoginExpiredHandleInterceptor 
 * @date 2016年9月30日 下午3:11:32 
 *
 */
public class LoginExpiredHandleInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(LoginExpiredHandleInterceptor.class);
    @Autowired
    private LoginService loginService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        
        try{
            
            String uri = request.getRequestURI();
            
            if(StringUtils.endsWith(uri, "user/login")){
                logger.info("IP:{},发起请求:{}",IpUtils.getIpAddress(request),request.getRequestURI());
            }else{
                logger.info("IP:{},发起请求:{},请求参数:{}",IpUtils.getIpAddress(request),request.getRequestURI(),RequestUtils.readRequestBody(request));
            }
            
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            
            //如果有RemoteInterface 注解 表示该接口暴露给其他系统调用需要进行以下拦截认证
            RemoteInterface remoteInterface = AnnotaionUtils.getAnnotation(handlerMethod,RemoteInterface.class);
            if(remoteInterface != null && remoteInterface.portal().length > 0){
                return this.remoteHandle(remoteInterface, request, response);
            }
            
            //如果有RestInterface，表示该接口为前后端分离接口需要进行以下拦截认证
            RestInterface restInterface = AnnotaionUtils.getAnnotation(handlerMethod,RestInterface.class);
            if(restInterface != null && restInterface.lifeCycle().length > 0){
                return this.restHandle(restInterface, request, response, uri);
            }       
        
            return true;
            
        }catch(IllegalArgumentException e){
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(RestfulConfig.JSON_UTF_8);
            responseToJson(e.getMessage(),e,response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseToJson(e.getMessage(),e,response);
        }
        
        return false;
    }
    
    private void responseToJson(String Jsonbody,HttpServletResponse response){
        this.responseToJson(Jsonbody, null, response);
    }
    
    private void responseToJson(String Jsonbody,Throwable t, HttpServletResponse response){
        try{
            response.setContentType(RestfulConfig.JSON_UTF_8);
            response.getWriter().print(JsonTools.getJson(ReturnMapUtils.returnFail(Jsonbody,t)));
            response.getWriter().close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }        
    
    /**
     * API接口认证
     * @param remoteInterface
     * @return    
     * boolean
     */
    public boolean remoteHandle(RemoteInterface remoteInterface, HttpServletRequest request, HttpServletResponse response){
        //有注解RestInterface，则进行拦截下面判断
        boolean result = false;
        
        if(ArrayUtils.contains(remoteInterface.portal(), Portal.ALL)){
            remoteInterface = new RemoteInterface() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return RemoteInterface.class;
                }
                @Override
                public Portal[] portal() {
                    // TODO Auto-generated method stub
                    return new Portal[]{Portal.MANAGEMENT,Portal.STUDENT,Portal.TEACHER};
                }
            };
        }
        
        if(!result && ArrayUtils.contains(remoteInterface.portal(), Portal.MANAGEMENT)){

            result = true;
            
            User user = UserUtils.getUser(request);
            if(user.getId() == 0){
              result = false;
              logger.warn("management api user is null");
            }
            
            if(UserEnum.Status.isLocked(user.getStatus())){
                result = false;
                logger.warn("management api user is locked:id = {}, name = {} ",user.getId(),user.getUsername());
            }
            
            logger.info("managemane api 被访问 验证结果{}, info:user id = {} username = {}",result,user.getId(),user.getUsername());
        }
        
        if(!result && ArrayUtils.contains(remoteInterface.portal(), Portal.STUDENT)){
            result = true;
            logger.info("student api 被访问,验证结果:{}",result);
        }
        
        if(!result){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            logger.warn("用户身份认证失败!{}",Arrays.toString(remoteInterface.portal()));
            responseToJson("You identity permission is don't match:"+Arrays.toString(remoteInterface.portal()),response);
        }
        return result;
    }
    
    /**
     * 前后端分离登陆认证
     * @param restInterface
     * @param request
     * @param response
     * @param uri
     * @return    
     * boolean
     */
    public boolean restHandle(RestInterface restInterface,HttpServletRequest request,HttpServletResponse response,String uri){
        
        //有注解RestInterface，则进行拦截下面判断
        String token = request.getHeader(RestfulController.AUTOKEN);
        if(StringUtils.isBlank(token)){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            logger.warn("Token无效:{}",token);
            responseToJson("Token is invalid",response);
            return false;
        }
        //user
        User user = loginService.getUser();
        if(user == null){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            logger.warn("User 不存在...或者已经过期.");
            responseToJson("The a user is  not exist",response);
            return false;
        }
        //判断当前用户所在地区的ip是否变化，如果变化。则返回空用户，用户重新登陆
        Boolean isIpChange = IpUtils.checkUserIpChange(user);
        if(isIpChange){
            String ip = IpUtils.getRequestRemoteIP();
            String redisIp = user.getIp();
            logger.info("用户IP地址发生变化  getUser userIPChange token = {},uri={},user = {}, redisIp = {}, currentIp = {}",token,uri,user.getId()+"|"+user.getUsername(),redisIp,ip);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            CookieUtils.removeCookie(response, CookieKey.TRPM_TOKEN, null, null);
            return false;
        }
        
        if(StringUtils.endsWith(uri, "user/login")){
            logger.info("IP:{},user:{},发起请求:{}",IpUtils.getIpAddress(request),user.getId(),request.getRequestURI());
        }else{
            logger.info("IP:{},user:{},发起请求:{},参数:{}",IpUtils.getIpAddress(request),user.getId(),request.getRequestURI(),RequestUtils.readRequestBody(request));
        }
        //teacher
        Teacher teacher = this.loginService.getTeacher();
        if(teacher == null){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            logger.warn(user.getUsername()+",Teacher 不存在.");
            responseToJson("The a teacher is  not exist",response);
            return false;
        }
        //权限判断，符合条件的LifeCycle可以访问控制器
        if(ArrayUtils.contains(restInterface.lifeCycle(),AppEnum.getByName(LifeCycle.class,teacher.getLifeCycle())) 
                || ArrayUtils.contains(restInterface.lifeCycle(),LifeCycle.ALL)){
            //user 常规拦截
            if(UserEnum.Status.isLocked(user.getStatus())){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                logger.warn(user.getUsername()+",账户被锁.");
                responseToJson("You has been locked!",response);
                return false; 
            }
            if(!UserEnum.Dtype.TEACHER.toString().equalsIgnoreCase(user.getDtype())){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                logger.warn(user.getUsername()+",账户Dtype不合法.");
                responseToJson("You are not an effective teacher!",response);
                return false; 
            }
            if(TeacherEnum.LifeCycle.QUIT.toString().equals(teacher.getLifeCycle())){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                logger.warn(user.getUsername()+",账户被Quit.");
                responseToJson("You has been quit!",response);
                return false; 
            }
            if(TeacherEnum.LifeCycle.FAIL.toString().equals(teacher.getLifeCycle())){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                logger.warn(user.getUsername()+",账户status被Fail.");
                responseToJson("You has been fail!",response);
                return false; 
            }
            //常规拦截结束
            request.setAttribute(RestfulController.AUTOKEN, user);
            request.setAttribute(RestfulController.TEACHER, teacher);
            return true;
        }
        response.setStatus(HttpStatus.FORBIDDEN.value());
        logger.warn("没有权限访问的用户:允许状态{},当前状态:{}",restInterface.lifeCycle(),teacher.getLifeCycle());
        responseToJson("You lifeCycle permission is don't match:"+Arrays.toString(restInterface.lifeCycle())+",your state:"+teacher.getLifeCycle(),response);
        return false;
    }
}
