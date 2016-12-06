package com.vipkid.recruitment.training.controller;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherQuizEnum;
import com.vipkid.recruitment.training.service.TrainingService;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.AdminQuizService;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherQuiz;
import com.vipkid.trpm.entity.User;


@RestController
@RestInterface(lifeCycle={LifeCycle.TRAINING})
@RequestMapping("/recruitment/training")
public class TrainingController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(TrainingController.class);

    @Autowired
    private AdminQuizService adminQuizService;

    @Autowired
    private TrainingService trainingService;

    /**
     * @author zhangzhaojun
     * 查找用户是否存在待考记录
     * @param request
     * @param response
     * @return  result
     */
    @RequestMapping(value = "/findNeedQuiz", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> findNeedQuiz(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String,Object> result = Maps.newHashMap();
            result.put("need",false);
            User user = getUser(request);
            logger.info("用户：{}查询他是否存在待考记录",user.getId());
            this.adminQuizService.updateCheckQuiz(user.getId());
            result.put("need",this.adminQuizService.findNeedQuiz(user.getId()));
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(), this,e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), this, e);
        }
    }

    /**
     * 开始考试，用户的考试记录
     * @param request
     * @param response
     * @return result
     */
    @RequestMapping(value = "/startQuiz", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> startQuiz(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String,Object> result = Maps.newHashMap();
            result.put("quizToken", 0);
            User user = getUser(request);
            logger.info("用户{}开始考试",user.getId());
            result.put("quizToken",this.adminQuizService.startQuiz(user.getId()));
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(), this,e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), this, e);
        }
    }


    /**
     * 进行判题，算出分数，更新考试信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/saveQuizResult", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> saveQuizResult(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String,Object> pramMap){
        try{
            Map<String,Object> result = Maps.newHashMap();
            result.put("result", false);
            Object grade = pramMap.get("grade");
            Object quizToken = pramMap.get("quizToken");
            Teacher teacher = getTeacher(request);
            logger.info("用户{}提交grade:{}",teacher.getId(),String.valueOf(grade));
            result.put("result",this.adminQuizService.saveQuizResult(teacher.getId(), String.valueOf(grade),Long.valueOf(quizToken+"")));
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(), this,e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), this, e);
        }
    }

    /**
     * .查询用户最后一次考试记录
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getLastQuiz", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getLastQuiz(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String,Object> result = Maps.newHashMap();
            User user = getUser(request);
            logger.info("用户{}查询最后一次考试记录",user.getId());
            //查询用户最后一次考试记录
            List<TeacherQuiz> list = adminQuizService.getLastQuiz(user.getId());
            if(CollectionUtils.isNotEmpty(list)){
                logger.info("用户:{}的考试记录：{}",user.getId(),list);
                TeacherQuiz teacherQuiz = list.get(0);
                result.put("isPass",(teacherQuiz.getStatus() == TeacherQuizEnum.Status.PASS.val()));
                result.put("grade",teacherQuiz.getQuizScore());
                result.put("count",list.size());
            }
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(), this,e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), this, e);
        }
    }

    /**
     * 跳转到Practicum改变用户的LifeCycle
     * @param request
     * @param response
     */
    @RequestMapping(value = "/toPracticum", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> toPracticum(HttpServletRequest request, HttpServletResponse response){
        try{
            Teacher teacher = getTeacher(request);
            logger.info("user:{},getReschedule",teacher.getId());
            Map<String,Object> result = this.trainingService.toPracticum(teacher);
            if(ReturnMapUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(), this,e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), this, e);
        }
    }



}
