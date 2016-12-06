package com.vipkid.recruitment.interview.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherLockLogEnum.Reason;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.dao.InterviewDao;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeacherApplicationLogDao;
import com.vipkid.recruitment.dao.TeacherLockLogDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeacherLockLog;
import com.vipkid.recruitment.interview.InterviewConstant;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherQuizDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.proxy.OnlineClassProxy;
import com.vipkid.trpm.proxy.OnlineClassProxy.ClassType;
import com.vipkid.trpm.util.DateUtils;

@Service
public class InterviewService {

    @Autowired
    private InterviewDao interviewDao;

    @Autowired
    private OnlineClassDao onlineClassDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    @Autowired
    private TeacherQuizDao teacherQuizDao;

    @Autowired
    private TeacherApplicationLogDao teacherApplicationLogDao;

    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private LessonDao lessonDao;
    
    @Autowired
    private TeacherLockLogDao teacherLockLogDao;

    private static Logger logger = LoggerFactory.getLogger(InterviewService.class);

    /**
     * 可用INTERVIEW课程列表查询
     * TODO
     * 1.处于Interview阶段的待约课老师才能约课
     * @return
     * List&lt;Map&lt;String,Object&gt;&gt;
     */
    public List<Map<String,Object>> findlistByInterview(){
        String fromTime = LocalDateTime.now().plusHours(1).format(DateUtils.FMT_YMD_HMS);
        String toTime = LocalDateTime.now().plusDays(2).withHour(23).withMinute(59).withSecond(59).format(DateUtils.FMT_YMD_HMS);
        logger.info("findlistByInterview parameter fromTime:{}, toTime:{}",fromTime, toTime);
        List<Map<String,Object>> list = interviewDao.findlistByInterview(fromTime, toTime);
        if(CollectionUtils.isNotEmpty(list)){
            Collections.shuffle(list);
        }
        return list;
    }

    /**
     * 用户interview进教室
     * 1.课程合法性验证
     * 2.必须是处于Interview的待上课的老师可以获取URL
     * @param onlineClassId
     * @param teacher
     * @return
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> getClassRoomUrl(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);

        //课程没有找到，无法book
        if(onlineClass == null){
            return ReturnMapUtils.returnFail("The online class not exis:"+onlineClassId);
        }
        //判断教室是否创建好
        if(StringUtils.isBlank(onlineClass.getClassroom())){
            return ReturnMapUtils.returnFail("The classroom is null:"+onlineClassId);
        }
        //课程必须是当前步骤中的数据
        List<TeacherApplication> listEntity = this.teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isEmpty(listEntity)){
            return ReturnMapUtils.returnFail("You cannot enter this classroom!");
        }
        //进教室权限判断    
        if(listEntity.get(0).getOnlineClassId() != onlineClassId){
            return ReturnMapUtils.returnFail("You cannot enter this classroom!");
        }

        Map<String,Object> result = OnlineClassProxy.generateRoomEnterUrl(teacher.getId()+"", teacher.getRealName(),onlineClass.getClassroom(), OnlineClassProxy.RoomRole.TEACHER, onlineClass.getSupplierCode());
        
        Lesson lesson = lessonDao.findById(onlineClass.getLessonId());
        
        result.put("lessonName",lesson.getName());
        
        return result;
    }

    /***
     * BOOK INTERVIEW 
     * 1.onlineClassId 必须是OPEN 课
     * 2.book的课程在开课前1小时之内不允许book
     * 3.约课老师必须是INTERVIEW的待约课老师
     * 4.cancel次数小于3次
     * @param onlineClassId
     * @param teacher
     * @return
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> bookInterviewClass(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);

        //课程没有找到，无法book
        if(onlineClass == null){
            return ReturnMapUtils.returnFail("The online class not exis:"+onlineClassId);
        }

        //onlineClassId 必须是OPEN 课
        if(!OnlineClassEnum.ClassStatus.OPEN.toString().equalsIgnoreCase(onlineClass.getStatus())){
            return ReturnMapUtils.returnFail("This class("+onlineClassId+") is empty or anyone else has been booked !");
        }

        //book的课程在开课前1小时之内不允许book
        if(System.currentTimeMillis() + InterviewConstant.CANCEL_TIME > onlineClass.getScheduledDateTime().getTime()){
            return ReturnMapUtils.returnFail("Class is about to start is not allowed to book !");
        }
        //约课老师必须是INTERVIEW的待约课老师
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isNotEmpty(listEntity)){
            TeacherApplication teacherApplication = listEntity.get(0);
            //存在步骤，但步骤中已经存在待审核的课程 不允许继续book
            if(teacherApplication.getOnlineClassId() != 0 && StringUtils.isBlank(teacherApplication.getResult())){
                return ReturnMapUtils.returnFail("You have booked a class already. Please refresh your page !"+onlineClassId);
            }
        }
        //判断剩余可取消次数
        if(recruitmentService.getRemainRescheduleTimes(teacher, Status.INTERVIEW.toString(), Result.CANCEL.toString()) <= 0){
            return ReturnMapUtils.returnFail("You cancel too many times, can't book the class !");
        }
        //执行BOOK逻辑
        String dateTime = DateFormatUtils.format(onlineClass.getScheduledDateTime(),"yyyy-MM-dd HH:mm:ss");
        Map<String,Object> result = OnlineClassProxy.doBookRecruitment(teacher.getId(), onlineClass.getId(), ClassType.TEACHER_RECRUITMENT,dateTime);
        if(ReturnMapUtils.isFail(result)){
            //一旦失败，抛出异常回滚
            throw new RuntimeException("The a book class result is fail!"+result.get("info"));
        }
        return result;
    }

    /***
     * CANCEL INTERVIEW 
     * 1.课程合法性验证
     * 2.开课前1小时不允许取消课程
     * 3.必须是处于Interview的带上课的老师可以取消课程
     * 4.记录cancel记录
     * @param onlineClassId
     * @param teacher
     * @return
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> cancelInterviewClass(long onlineClassId,Teacher teacher){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);

        //课程没有找到，无法取消
        if(onlineClass == null){
            return ReturnMapUtils.returnFail("The online class doesn't exist:"+onlineClassId);
        }

        //book的课程在开课前1小时之内不允许cancel
        if(System.currentTimeMillis() + InterviewConstant.CANCEL_TIME > onlineClass.getScheduledDateTime().getTime()){
            return ReturnMapUtils.returnFail("The online class will begin,can't rescheduled:"+onlineClassId);
        }

        List<TeacherApplication> listEntity = this.teacherApplicationDao.findCurrentApplication(teacher.getId());
        //如果步骤中无数据则不允许cancel
        if(CollectionUtils.isEmpty(listEntity)){
            return ReturnMapUtils.returnFail("You do not have permission to cancel this course:"+onlineClassId);
        }else{
            TeacherApplication teacherApplication = listEntity.get(0);
            //如果步骤中有数据并且数据不是本次cancel的课程 则不允许cancel
            if(teacherApplication.getOnlineClassId() != onlineClass.getId()){
                return ReturnMapUtils.returnFail("You have already cancelled this class. Please refresh your page !");
            }else{
                //果步骤中有数据并且数据不是本次cancel的课程 但管理端已经审核，不允许cancel
                if(StringUtils.isNotBlank(teacherApplication.getResult())){
                    return ReturnMapUtils.returnFail("This class already audited. Please refresh your page !");
                }
            }
        }

        //保存cancel记录
        this.teacherApplicationLogDao.saveCancel(teacher.getId(), listEntity.get(0).getId(), Status.INTERVIEW, Result.CANCEL, onlineClass);

        int count = recruitmentService.getRemainRescheduleTimes(teacher, Status.INTERVIEW.toString(), Result.CANCEL.toString());

        if(count == 0){
            userDao.doLock(teacher.getId());
            teacherLockLogDao.save(new TeacherLockLog(teacher.getId(), Reason.RESCHEDULE.toString(), LifeCycle.INTERVIEW.toString()));
        }

        //执行Cancel逻辑
        Map<String,Object> result = OnlineClassProxy.doCancelRecruitement(teacher.getId(), onlineClass.getId(), ClassType.TEACHER_RECRUITMENT);
        result.put("count", count);
        if(ReturnMapUtils.isFail(result)){
            //一旦失败，抛出异常回滚
            throw new RuntimeException("The a cancel class result is fail ! "+result.get("info"));
        }
        return result;
    }

    /**
     * 进入下一步骤
     * @param teacher
     * @return
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> toTraining(Teacher teacher){
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isEmpty(listEntity)){
            return ReturnMapUtils.returnFail("You have no legal power into the next phase !");
        }

        //执行逻辑 只有在INTERVIEW的PASS状态才能进入
        if(Status.INTERVIEW.toString().equals(listEntity.get(0).getStatus())
                && Result.PASS.toString().equals(listEntity.get(0).getResult())){
            //按照新流程 该步骤将老师的LifeCycle改变为Interview -to-Training
            teacher.setLifeCycle(LifeCycle.TRAINING.toString());
            this.teacherDao.insertLifeCycleLog(teacher.getId(),LifeCycle.INTERVIEW,LifeCycle.TRAINING, teacher.getId());
            this.teacherDao.update(teacher);
            // 增加quiz的考试记录
            teacherQuizDao.insertQuiz(teacher.getId(),teacher.getId());
            return ReturnMapUtils.returnSuccess();
        }
        return ReturnMapUtils.returnFail("You have no legal power into the next phase !");
    }
}
