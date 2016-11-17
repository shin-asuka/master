package com.vipkid.recruitment.training.controller;
import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherQuizEnum;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.recruitment.training.service.TrainingService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherQuiz;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.rest.AdminQuizService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RestInterface(lifeCycle={ApplicationConstant.TeacherLifeCycle.TRAINING})
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

    /**
     * 开始考试，用户的考试记录
     * @param request
     * @param response
     * @return result
     */
    @RequestMapping(value = "/startQuiz", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> startQuiz(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        result.put("quizToken", 0);
        try{
            User user = getUser(request);
            logger.info("用户{}开始考试",user.getId());
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


    /**
     * 进行判题，算出分数，更新考试信息
     * @param request
     * @param response
     * @param grade
     * @param quizToken
     * @return
     */
    @RequestMapping(value = "/saveQuizResult", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> saveQuizResult(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="grade") String grade, @RequestParam(value="quizToken") long quizToken){
        Map<String,Object> result = Maps.newHashMap();
        result.put("result", false);
        try{
            User user = getUser(request);
            logger.info("用户{}提交grade:{}",user.getId(),grade);
            result.put("result",this.adminQuizService.saveQuizResult(user.getId(), grade,quizToken));
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

    /**
     * .查询用户最后一次考试记录
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getLastQuiz", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getLastQuiz(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            logger.info("用户{}查询最后一次考试记录",user.getId());
            //查询用户最后一次考试记录
            List<TeacherQuiz> list = adminQuizService.getLastQuiz(user.getId());
            if(CollectionUtils.isNotEmpty(list)){
                TeacherQuiz teacherQuiz = list.get(0);
                result.put("isPass",(teacherQuiz.getStatus() == TeacherQuizEnum.Status.PASS.val()));
                result.put("grade",teacherQuiz.getQuizScore());
                result.put("count",list.size());
            }
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

    /**
     * 跳转到Practicum改变用户的LifeCycle
     * @param request
     * @param response
     */
    @RequestMapping(value = "/toPracticum", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public  Map<String,Object> toPracticum(HttpServletRequest request,HttpServletResponse response){
        Teacher teacher = getTeacher(request);
        teacher = this.trainingService.toPracticum(teacher);
        logger.info("用户{}跳转到Practicum",teacher.getId());
        Map<String,Object> recMap = new HashMap<String,Object>();
        if(TeacherEnum.LifeCycle.PRACTICUM.toString().equals(teacher.getLifeCycle())){
            recMap.put("info", true);
        }else{
            recMap.put("info", false);
        }
        logger.info("toPracticum Status " + teacher.getLifeCycle());
        return recMap;
    }

}
