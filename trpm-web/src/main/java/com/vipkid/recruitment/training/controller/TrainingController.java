package com.vipkid.recruitment.training.controller;

import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.rest.RestfulController;
import com.vipkid.trpm.constant.ApplicationConstant;

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

import static com.vipkid.rest.utils.UserUtils.getUser;

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
            logger.info("提交分数:{}",grade);
            User user = getUser(request);
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
     * 3.下一步将执行的操作<br/>
     * a.前台自动请求该方法，然后获取状态自动跳转<br/>
     * b.该步骤以后将由管理端执行。
     * @Author:ALong
     * @param request
     * @param response
     * @return 2015年10月16日
     */
    @RequestMapping(value = "/toPracticum", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public  Map<String,Object> toPracticum(HttpServletRequest request,HttpServletResponse response){
        Teacher teacher = getUser(request);
        teacher = this.trainingService.toPracticum(teacher);

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
