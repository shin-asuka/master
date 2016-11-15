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

    @RequestMapping(value = "/init", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> init(HttpServletRequest request, HttpServletResponse response) {
        Map<String,Object> result = Maps.newHashMap();
        Teacher teacher = getTeacher(request);
        teacher = teacherDao.findById(teacher.getId());
        result.put("teacher", teacher);
        //查询当前Teacher的当前TeacherApplication
        TeacherApplication application = practicumService.findAppliction(teacher.getId());
        if(application != null){
            if(TeacherApplicationEnum.Status.PRACTICUM.toString().equals(application.getStatus()) && application.getResult() == null){application.setResult(TeacherApplicationEnum.Result.AUDITING.toString());}
            result.put("application", application);
            Date audit = application.getAuditDateTime();
            long  countTime = 0;
            if(audit != null){
                countTime = (new Date().getTime() - audit.getTime())/1000;
            }
            long  weekTime = 54 * 7 * 24 * 3600;

            //最后一次申请时间离现在小于54周可进行以下展示
            if(countTime <= weekTime){
                //1.当应用状态为TRAINING 约第一次实习，
                boolean a = TeacherApplicationEnum.Status.TRAINING.toString().equals(application.getStatus());
                //2.当应用状态为PRACTICUM并且RESULT为PRACTICUM2 第一次实习不满意，需要约第二次实习
                boolean b = (TeacherApplicationEnum.Status.PRACTICUM.toString().equals(application.getStatus()) && TeacherApplicationEnum.Result.PRACTICUM2.toString().equals(application.getResult()));
                TeacherApplication app2 = practicumService.findAppByPracticum2(teacher.getId());
                boolean newb = app2 == null ? false:true;
                //3.当应用状态为PRACTICUM并且RESULT为REAPPLY 第一次面试由于客观原因没能完成实习，重新约实习
                boolean c = (TeacherApplicationEnum.Status.PRACTICUM.toString().equals(application.getStatus()) && TeacherApplicationEnum.Result.REAPPLY.toString().equals(application.getResult()));
                //4.当应用状态为PRACTICUM并且RESULT为PASS 实习通过
                boolean d = (TeacherApplicationEnum.Status.PRACTICUM.toString().equals(application.getStatus()) && TeacherApplicationEnum.Result.PASS.toString().equals(application.getResult()));
                boolean pass = TeacherApplicationEnum.Status.FINISHED.toString().equals(application.getStatus());
                //5.当应用状态为PRACTICUM并且RESULT为FAIL 实习未通过
                boolean e = (TeacherApplicationEnum.Status.PRACTICUM.toString().equals(application.getStatus()) && TeacherApplicationEnum.Result.FAIL.toString().equals(application.getResult()));
                //6.当应用状态为PRACTICUM并且RESULT为AUDITING已经约好实习，等待实习上课
                boolean f = (TeacherApplicationEnum.Status.PRACTICUM.toString().equals(application.getStatus()) && TeacherApplicationEnum.Result.AUDITING.toString().equals(application.getResult()) && application.getOnlineClassId() != 0 );
                //7.当应用状态为PRACTICUM并且RESULT为AUDITING已经约好实习，但老师已经取消实习上课
                boolean g = (TeacherApplicationEnum.Status.PRACTICUM.toString().equals(application.getStatus()) && TeacherApplicationEnum.Result.AUDITING.toString().equals(application.getResult()) && application.getOnlineClassId() == 0);


                //第二阶段显示
                if(newb){
                    result.put("practicumNo", 2);
                } else {
                    result.put("practicumNo", 1);
                }
                result.put("serverTimeMillis", System.currentTimeMillis());


                if (a || b || c || g) { //约第一次实习、需要进行第二次实习、重新约实习、取消实习上课 都进入约课页面
                    //Map<String, Map<String, Object>> availableScheduled = practicumService.getAvailableScheduled(fromTime, toTime, timezone);
                    result.put("availableTimeSlots", null);
                    result.put("pageStatus", "showSchedule");
                    if(c){
                        result.put("pageStatus", "isReapply");
                    }
                }else if(f) { //约好实习，等待上课页面
                    OnlineClass onlineClass = onlineClassDao.findById(application.getOnlineClassId());
                    result.put("scheduledTimeMillis", onlineClass.getScheduledDateTime().getTime());
                    result.put("pageStatus", "isBooked");
                    //9.OnlineClass的status为FINISHED 已经完成上课 等待结果
                    boolean finished = (new Date().getTime() - onlineClass.getScheduledDateTime().getTime()) >= 60*60*1000;
                    if(OnlineClassEnum.Status.FINISHED.toString().equals(onlineClass.getStatus()) && finished){
                        result.put("pageStatus", "isChecking");
                    }
                }else if(d || pass){ //已经实习，通过
                    result.put("pageStatus", "isPass");
                }else if(e){ //已经实习，未通过
                    result.put("pageStatus", "isFail");
                }
            }else{
                result.put("pageStatus", "isTimeOut");
            }
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
            result.put("status", true);
            result.put("url", "http://www.baidu.com");
            result.put("lessonName", "Recruitment Class");
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
