package com.vipkid.recruitment.practicum.controller;

import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.recruitment.utils.DateTools;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.recruitment.practicum.service.PracticumService;
import com.vipkid.rest.RestfulController;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherApplication;
import com.vipkid.utils.DateUtil;
import com.vipkid.utils.StringUtil;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;
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

    @RequestMapping("/practicum")
    public String practicum(HttpServletRequest request, HttpServletResponse response, Model model) {
        Teacher teacher = null;//SessionUtil.getAttribute(request, ApplicationConstant.SESSION_TR_TEACHER);
        teacher = teacherDao.findById(teacher.getId());
        model.addAttribute("teacher", teacher);
        //查询当前Teacher的当前TeacherApplication
        TeacherApplication application = null;//practicumService.findAppliction(teacher.getId());
        if(application != null){
            if(TeacherApplicationEnum.Status.PRACTICUM.toString().equals(application.getStatus()) && application.getResult() == null){application.setResult(TeacherApplicationEnum.Result.AUDITING.toString());}
            model.addAttribute("application", application);
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
                TeacherApplication app2 = null;//practicumService.findAppByPracticum2(teacher.getId());
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
                    model.addAttribute("practicum2", "PRACTICUM2");
                }

                if (a || b || c || g) { //约第一次实习、需要进行第二次实习、重新约实习、取消实习上课 都进入约课页面
                    int weekOffset = ServletRequestUtils.getIntParameter(request, "curPage", 1);
                    model.addAttribute("curPage", weekOffset);
                    Calendar calendar = Calendar.getInstance();
                    //if(audit==null){audit = new Date();}
                    //calendar.setTime(audit);
                    calendar.add(Calendar.DATE, 7 * (weekOffset-1));
                    audit = calendar.getTime();
                    List<Date> scheduledWeeks = null;
                    if(weekOffset < 2){//第一页从24小时后
                        scheduledWeeks = DateTools.weeksForPracticumStart(audit);
                    }else{//大于第二页从0点开始
                        scheduledWeeks = DateTools.weeksForPracticumNext(audit);
                    }
                    model.addAttribute("scheduledWeeks", scheduledWeeks);
                    model.addAttribute("scheduled", DateTools.getScheduled(scheduledWeeks));

                    Date fromTime = scheduledWeeks.get(0), toTime = scheduledWeeks.get(6);
                    model.addAttribute("fromTime", fromTime);
                    model.addAttribute("toTime", toTime);

                    String timezone = teacher.getTimezone();
                    Map<String, Map<String, Object>> availableScheduled = null;//practicumService.getAvailableScheduled(fromTime, toTime, timezone);
                    model.addAttribute("availableScheduled", availableScheduled);

                    DateTime currDate = DateTime.now(DateTimeZone.forID(timezone));
                    model.addAttribute("serverMills", currDate.getMillis());
                    model.addAttribute("pageStatus", "showSchedule");
                    model.addAttribute("converTimeZone", TimeZone.getTimeZone(timezone));

                    if(c){
                        model.addAttribute("result", "isReapply");
                    }
                }else if(f) { //约好实习，等待上课页面
                    OnlineClass onlineClass = onlineClassDao.findById(application.getOnlineClassId());
                    model.addAttribute("onlineClassId", application.getOnlineClassId());
                    model.addAttribute("teacherId", teacher.getId());
                    model.addAttribute("serverTime", DateUtil.format(new Date(),new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS_DASH)));
                    localoutputFormat.setTimeZone(TimeZone.getTimeZone(teacher.getTimezone()));
                    model.addAttribute("scheduledTime", DateUtil.format(onlineClass.getScheduledDateTime(),new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS_DASH)));
                    String str = localoutputFormat.format(onlineClass.getScheduledDateTime());
                    for(int i = 0; i <= 31 ; i++){
                        str = str.replace(" "+i+",",dateStr[i]);
                    }
                    str = StringUtil.firstUpper(str);
                    model.addAttribute("scheduledDateTime", str);
                    model.addAttribute("pageStatus", "hasBooked");
                    //9.OnlineClass的status为FINISHED 已经完成上课 等待结果
                    boolean finished = (new Date().getTime() - onlineClass.getScheduledDateTime().getTime()) >= 60*60*1000;
                    if(OnlineClassEnum.Status.FINISHED.toString().equals(onlineClass.getStatus()) && finished){
                        model.addAttribute("pageStatus", "isChecking");
                    }
                }else if(d || pass){ //已经实习，通过
                    model.addAttribute("result", "isPass");
                }else if(e){ //已经实习，未通过
                    model.addAttribute("result", "isFial");
                }
            }else{
                model.addAttribute("pageStatus", "hasTimeOut");
            }
        }
        return "recruitment/practicum";

    }
}
