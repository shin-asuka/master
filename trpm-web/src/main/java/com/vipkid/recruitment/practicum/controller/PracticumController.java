package com.vipkid.recruitment.practicum.controller;

import com.alibaba.druid.util.StringUtils;
import com.google.api.client.util.Maps;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.recruitment.practicum.service.PracticumService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherApplication;
import com.vipkid.trpm.entity.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.community.http.client.HttpClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RestInterface(lifeCycle={ApplicationConstant.TeacherLifeCycle.PRACTICUM})
@RequestMapping("/recruitment/practicum")
public class PracticumController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(PracticumController.class);

    @Autowired
    private PracticumService practicumService;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private OnlineClassDao onlineClassDao;


    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> list(HttpServletRequest request, HttpServletResponse response) {
        Map<String,Object> result = Maps.newHashMap();
        try{
            //"showSchedule"
            User user = getUser(request);
            logger.info("user:{},list",user.getId());
            result.put("list", this.practicumService.findListByPracticum());
            result.put("status", true);
            return result;
        } catch (IllegalArgumentException e) {
            result.clear();
            result.put("status", false);
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            result.clear();
            result.put("status", false);
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> get(HttpServletRequest request, HttpServletResponse response) {
        //"isBooked"

        Map<String,Object> result = Maps.newHashMap();
        try{
            Teacher teacher = getTeacher(request);
            TeacherApplication application = practicumService.findAppliction(teacher.getId());
            OnlineClass onlineClass = onlineClassDao.findById(application.getOnlineClassId());
            result.put("scheduledTimeMillis", onlineClass.getScheduledDateTime().getTime());
            result.put("status", true);
            return result;
        } catch (IllegalArgumentException e) {
            result.clear();
            result.put("status", false);
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            result.clear();
            result.put("status", false);
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    @RequestMapping(value = "/bookClass", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> bookClass(HttpServletRequest request, HttpServletResponse response,long onlineClassId){
        Map<String,Object> result = Maps.newHashMap();
        result.put("status", false);
        try{
            User user = getUser(request);
            logger.info("user:{},bookClass",user.getId());

            //TeacherSession判断
            Teacher teacher = getTeacher(request);

            if(teacher == null){
                //model.addAttribute("message", "Too long time no operation,Please login again!");
                //return jsonView(response,model);
            }

            teacher = teacherDao.findById(teacher.getId());

            //online是否存在判断
            OnlineClass onlineclass = onlineClassDao.findById(onlineClassId);
            if(onlineclass == null || OnlineClassEnum.Status.REMOVED.toString().equals(onlineclass.getStatus())){
                //model.addAttribute("message", "The online-class does not exist. Please refresh your page.");
                //return jsonView(response,model);
            }

            //上课时间
            long stime = onlineclass.getScheduledDateTime().getTime();
            long ctime = System.currentTimeMillis();
            long count = stime - ctime;
            if(count < 3600000){
                //model.addAttribute("message", "The online-class time-out(1h). Please refresh your page.");
                //return jsonView(response,model);
            }

            //teacherApplication判断，是否已经booked
            TeacherApplication teacherApplication = practicumService.findAppliction(teacher.getId());
            if(teacherApplication.getOnlineClassId() > 0 && StringUtils.isEmpty(teacherApplication.getResult())){
                //model.addAttribute("message", "You have booked a class already. Please refresh your page.");
                //return jsonView(response, model);
            }

String msg=null;
            ////////////////////////

            logger.error("BOOK CLASS MESSAGE : " + msg);
            if (StringUtils.isEmpty(msg)) {
                //model.addAttribute("message", "Request failed, please try again later !");
            } else if (msg.indexOf("Success") > 0) {
                //model.addAttribute("status", true);

                    //return "recruitment/practicum";

            } else {
                //model.addAttribute("message", "Request failed, Please try again later!");
            }


        } catch (IllegalArgumentException e) {
            result.clear();
            result.put("status", false);
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            result.clear();
            result.put("status", false);
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    @RequestMapping(value = "/reschedule", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> reschedule(HttpServletRequest request, HttpServletResponse response,long onlineClassId){
        Map<String,Object> result = Maps.newHashMap();
        result.put("status", false);
        try{
            User user = getUser(request);
            logger.info("user:{},reschedule",user.getId());



            //Teacher session 判断
            Teacher teacher = getTeacher(request);
            if(teacher == null){
                //model.addAttribute("message", "Too long time no operation, Please login again!");
                //return jsonView(response,model);
            }

            //teacherApplication 判断
            TeacherApplication teacherApplication = practicumService.findAppliction(teacher.getId());

                if(onlineClassId != teacherApplication.getOnlineClassId()){
                    //model.addAttribute("message", "You have already cancelled this class. Please refresh your page.");
                    //return jsonView(response, model);
                }




            ////////////////////
String msg = null;
            logger.error("CANCEL CLASS MESSAGE : " + msg);
            if (StringUtils.isEmpty(msg)) {
                //model.addAttribute("message", "Request failed, Please try again later !");
            } else if (msg.indexOf("Success") > 0) {
                //model.addAttribute("status", true);

                    //return "recruitment/practicum";

            } else if (msg.indexOf("628") > 0) {
                //model.addAttribute("message", "Sorry, you can't cancel again within 5 minutes. Try again later!");
            } else {
                //model.addAttribute("message", "Request failed, Please try again later!");
            }


        } catch (IllegalArgumentException e) {
            result.clear();
            result.put("status", false);
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            result.clear();
            result.put("status", false);
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    @RequestMapping(value = "/getClassRoomUrl", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getClassRoomUrl(HttpServletRequest request, HttpServletResponse response,long onlineClassId){
        Map<String,Object> result = Maps.newHashMap();
        try{
            User user = getUser(request);
            logger.info("user:{},getClassRoomUrl",user.getId());
            //result = this.interviewService.getClassRoomUrl(onlineClassId, getTeacher(request));
            if(!MapUtils.getBooleanValue(result, "status")){
                result.put("info", "The class room url not exis.");
                response.setStatus(HttpStatus.FORBIDDEN.value()); ;
            }
            return result;
        } catch (IllegalArgumentException e) {
            result.clear();
            result.put("status", false);
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            result.clear();
            result.put("status", false);
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }
}
