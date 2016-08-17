package com.vipkid.rest.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Maps;
import com.google.common.base.Preconditions;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.rest.LoginService;

@RestController
@RequestMapping("/quiz")
public class AdminQuizController {

    private static Logger logger = LoggerFactory.getLogger(AdminQuizController.class);
    
    @Autowired
    private LoginService loginService;
    
    @RequestMapping(value = "/getLastQuiz", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getLastQuiz(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        try{
            String token = request.getHeader(CookieKey.AUTOKEN);
            Preconditions.checkArgument(StringUtils.isNotBlank(token));
            User user = loginService.getUser(request);
            if(user == null){
                response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
                logger.warn("用户不存在，token过期");
                return result;
            }
            // TODO 查询用户最后一次考试记录
            result.put("isPass",true);
            result.put("grade",61);
            result.put("count",2);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
        }
        return result;
    }
    
    
    @RequestMapping(value = "/findNeedQuiz", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> findNeedQuiz(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        try{
            String token = request.getHeader(CookieKey.AUTOKEN);
            Preconditions.checkArgument(StringUtils.isNotBlank(token));
            User user = loginService.getUser(request);
            if(user == null){
                response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
                logger.warn("用户不存在，token过期");
                return result;
            }
            // TODO 查询用户是否需要考试
            result.put("need",false);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
        }
        return result;
    }
    
    
    @RequestMapping(value = "/saveQuizResult", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> saveQuizResult(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="grade") int grade){
        Map<String,Object> result = Maps.newHashMap();
        try{
            String token = request.getHeader(CookieKey.AUTOKEN);
            Preconditions.checkArgument(StringUtils.isNotBlank(token));
            User user = loginService.getUser(request);
            if(user == null){
                response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
                logger.warn("用户不存在，token过期");
                return result;
            }
            // TODO 保存考试结果,大于60分更新当前数据PASS通过，不插入新的考试
            // 小于60分则更新当前数据，并插入新的考试记录
            result.put("result",true);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
        }
        return result;
    }
    
    
}
