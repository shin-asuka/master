package com.vipkid.rest.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Maps;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherQuizEnum;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.AdminQuizService;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.service.TeacherPageLoginService;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherQuiz;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.util.CookieUtils;

@RestController
@RestInterface(lifeCycle=LifeCycle.REGULAR)
@RequestMapping("/quiz")
public class AdminQuizController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(AdminQuizController.class);
    
    @Autowired
    private LoginService loginService;
        
    @Autowired
    private AdminQuizService adminQuizService;
    
    @Autowired 
    private TeacherPageLoginService teacherPageLoginService;
    
    @RequestMapping(value = "/getLastQuiz", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getLastQuiz(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        result.put("openQuiz",false);
        try{
            User user = getUser(request);
            //查询用户最后一次考试记录
            List<TeacherQuiz> list = adminQuizService.getLastQuiz(user.getId());
            if(CollectionUtils.isNotEmpty(list)){
                TeacherQuiz teacherQuiz = list.get(0);
                result.put("isPass",(teacherQuiz.getStatus() == TeacherQuizEnum.Status.PASS.val()));
                result.put("grade",teacherQuiz.getQuizScore());
                result.put("count",list.size());
            }
            result.put("openQuiz",this.adminQuizService.openQuiz(user.getId()));
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }
    
    
    @RequestMapping(value = "/saveOpenQuiz", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> saveOpenQuiz(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        result.put("result",false);
        try{
            User user = getUser(request);
            result.put("result",this.adminQuizService.saveOpenQuiz(user.getId()));
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }
    
    @RequestMapping(value = "/findNeedQuiz", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> findNeedQuiz(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        result.put("need",false);
        try{
            User user = getUser(request);
            result.put("need",this.adminQuizService.findNeedQuiz(user.getId()));
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }
    
    @RequestMapping(value = "/startQuiz", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> startQuiz(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        result.put("quizToken", 0);
        try{
            logger.info("开始考试");
            User user = getUser(request);
            result.put("quizToken",this.adminQuizService.startQuiz(user.getId()));
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }
    
    @RequestMapping(value = "/saveQuizResult", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> saveQuizResult(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="grade") String grade,@RequestParam(value="quizToken") long quizToken){
        Map<String,Object> result = Maps.newHashMap();
        result.put("result", false);
        try{
            logger.info("提交分数:{}",grade);
            Teacher teacher = getTeacher(request);
            result.put("result",this.adminQuizService.saveQuizResult(teacher.getId(), grade, quizToken));
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }
    
    
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> updatePassword(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="password") String password){
        Map<String,Object> result = Maps.newHashMap();
        result.put("result", false);
        try{
            logger.info("提交密码修改:{}",password);
            User user = getUser(request);
            boolean rest = this.adminQuizService.updatePassword(user.getId(), password);
            CookieUtils.removeCookie(response, CookieKey.TRPM_CHANGE_WINDOW, null, null);
            result.put("result",rest);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }
    
}
