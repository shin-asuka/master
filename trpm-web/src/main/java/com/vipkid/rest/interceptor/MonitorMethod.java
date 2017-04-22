package com.vipkid.rest.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vipkid.http.utils.JsonUtils;
import com.vipkid.rest.exception.ServiceException;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by ZengWeiLong on 2017/1/10.
 */
@Component
@Aspect
public class MonitorMethod {

	private Logger logger = LoggerFactory.getLogger(MonitorMethod.class);

    @Pointcut("execution(* com.vipkid.rest.web..*Controller.*(..))")
    public void restControler(){}
    @Around("restControler()")
    public Object doRestControlerAround(ProceedingJoinPoint pjp) {
    	return doLogger(pjp);
    }
    
    @Pointcut("execution(* com.vipkid.rest.service..*Service.*(..))")
    public void restService(){}
    @Around("restService()")
    public Object doRestServiceAround(ProceedingJoinPoint pjp) {
    	return doLogger(pjp);
    }
	
    @Pointcut("execution(* com.vipkid.recruitment.*.service..*Service.*(..))")
    public void recruitmentService(){}
    @Around("recruitmentService()")
    public Object doRecruitmentServiceAround(ProceedingJoinPoint pjp) {
    	return doLogger(pjp);
    }
    
    @Pointcut("execution(* com.vipkid.recruitment.*.controller..*Controller.*(..))")
    public void recruitmentController(){}
    @Around("recruitmentController()")
    public Object doRecruitmentControllerAround(ProceedingJoinPoint pjp) {
    	return doLogger(pjp);
    }
	
    @Pointcut("execution(* com.vipkid.portal.*.service.*Service..*(..))")
    public void portalService(){}
    @Around("portalService()")
    public Object doPortalServiceAround(ProceedingJoinPoint pjp) {
    	return doLogger(pjp);
    }
    
    // 配置切入点,该方法无方法体,主要为方便同类中其他方法使用此处配置的切入点
    @Pointcut("execution(* com.vipkid.portal.*.controller..*Controller.*(..))")
    public void portalController() {}
    @Around("portalController()")
    public Object doPortalControllerAround(ProceedingJoinPoint pjp) {
    	return doLogger(pjp);
    }

    private Object doLogger(ProceedingJoinPoint pjp){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String reslult = "";
        StringBuffer argString = new StringBuffer("");
        try{
        	//参数获取
        	Object[] objects = pjp.getArgs();
        	if(objects != null){
        		for (Object o:objects) {
        			if(o instanceof HttpServletRequest){
        				argString.append("request,");
        			}else if(o instanceof HttpServletResponse){
        				argString.append("response,");
        			}else if(o instanceof MultipartFile){
                        logger.info("上传文件");
                    }else{
                        argString.append(JsonUtils.toJSONString(o)+",");
                    }
    			}
        	}
        	//执行结果
            Object obj = pjp.proceed();  
            //结果序列化
            if(obj != null){
            	reslult = JsonUtils.toJSONString(obj);
            }
            return obj;
        }catch (IllegalArgumentException e) {
            logger.warn("MonitorMethodInterceptor IllegalArgumentException, errorMessage=" + e.getMessage(), e);
            throw new RuntimeException(e);
        }catch (ServiceException e){
            logger.warn("MonitorMethodInterceptor ServiceException, errorMessage=" ,e);
            throw new RuntimeException(e);
        }catch (Throwable e) {
            logger.error("MonitorMethodInterceptor error !",e);
            throw new RuntimeException(e);
        }finally{
        	stopWatch.stop();
            String logString = reslult;
            if(logString != null && logString.length() > 100) logString = logString.substring(0,100);
            logger.info("\nInvocation Time : Execution use time【" + (stopWatch.getNanoTime()/(1000*1000)) + " 毫秒】, MonitorMethod:【" +pjp + "】,参数:【"+argString+"】, 结果集(前100位):" + logString);
        }
    }
}
