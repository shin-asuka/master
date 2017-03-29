package com.vipkid.recruitment.interview.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

import com.google.api.client.util.Maps;
import com.vipkid.dataSource.annotation.Slave;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherLockLogEnum.Reason;
import com.vipkid.enums.TeacherQuizEnum.Version;
import com.vipkid.enums.UserEnum;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.dao.InterviewDao;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeacherApplicationLogDao;
import com.vipkid.recruitment.dao.TeacherLockLogDao;
import com.vipkid.recruitment.entity.InterviewerClassCount;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeacherLockLog;
import com.vipkid.recruitment.interview.InterviewConstant;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherLocationDao;
import com.vipkid.trpm.dao.TeacherQuizDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.entity.User;
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
    private TeacherAddressDao teacherAddressDao;

    @Autowired
    private TeacherApplicationLogDao teacherApplicationLogDao;

    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private LessonDao lessonDao;
    
    @Autowired
    private TeacherLockLogDao teacherLockLogDao;

    @Autowired
    private TeacherLocationDao teacherLocationDao;

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
        String toTime = LocalDateTime.now().plusDays(InterviewConstant.SHOW_DAYS_EXCLUDE_TODAY + 1).withHour(23).withMinute(59).withSecond(59).format(DateUtils.FMT_YMD_HMS);
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
     * @param user 
     * @return
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> getClassRoomUrl(long onlineClassId,Teacher teacher, User user){
    	
    	Map<String,Object> result = Maps.newHashMap();
    	
		if (teacher == null || teacher.getId() == 0 || user == null || StringUtils.isBlank(teacher.getRealName())
				|| StringUtils.isBlank(user.getName())) {
			return ReturnMapUtils.returnFail("This account doesn't exist");
		}
        
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);

        //课程没有找到，无法book
        if(onlineClass == null){
            return ReturnMapUtils.returnFail("The online class doesn't exist: "+onlineClassId);
        }
        
        Lesson lesson = lessonDao.findById(onlineClass.getLessonId());
        
        if(lesson != null){
        	result.put("lessonName",lesson.getName());
            result.put("lessonSN",lesson.getSerialNumber());
        }
        
        String logpix = "onlineclassId:"+onlineClassId+";teacherId:"+teacher.getId();
        
        //课程必须是当前步骤中的数据
        List<TeacherApplication> listEntity = this.teacherApplicationDao.findCurrentApplication(teacher.getId());
        
        if(CollectionUtils.isEmpty(listEntity)){
            result.putAll(ReturnMapUtils.returnFail("You cannot enter this classroom!",logpix));
            return result;
        }
        
        //进教室权限判断    
        if(listEntity.get(0).getOnlineClassId() != onlineClassId){
        	result.putAll(ReturnMapUtils.returnFail("You cannot enter this classroom!",logpix));
            return result; 
        }

        //判断教室是否创建好
        if(StringUtils.isBlank(onlineClass.getClassroom())){
        	result.putAll(ReturnMapUtils.returnFail("The classroom without creating",logpix));
        	return result;
        }

        Map<String,Object> urlResult = OnlineClassProxy.generateRoomEnterUrl(teacher.getId()+"", user.getName(),onlineClass.getClassroom(), OnlineClassProxy.RoomRole.TEACHER, onlineClass.getSupplierCode(),onlineClassId,OnlineClassProxy.ClassType.TEACHER_RECRUITMENT);

        result.putAll(urlResult);
        
        return result;
    }


    /*加入interviewer scheduler逻辑以后, book 逻辑变动较大, 从接收onlineclassId改为接受前端时间戳*/
    @Slave
    public String getOnlineClassIdRandomised(long timestamp) {

        logger.info("Timestamp to book the interview:" + timestamp);

        Timestamp ts = new Timestamp(timestamp);
        String schduledDateTime = ts.toLocalDateTime().format(DateUtils.FMT_YMD_HMS);
        LocalDateTime schduledDT = ts.toLocalDateTime();
        logger.info("schduledDateTime to book the interview:" + schduledDateTime);

        String fromTime_curDate = schduledDT.with(LocalTime.of(0, 0, 0)).format(DateUtils.FMT_YMD_HMS);
        String toTime_curDate = schduledDT.with(LocalTime.of(23, 59, 59)).format(DateUtils.FMT_YMD_HMS);
        logger.info("bookInterviewClass get least booked teacher fromTime:{}, toTime:{}", fromTime_curDate, toTime_curDate);

        //取出候选课程对应老师当天BOOK或FINISHED课程数。(online class id , scheduledTime, teacher id, count booked)
        List<InterviewerClassCount> listInterviewers = interviewDao.findlistByBookedCount(schduledDateTime, fromTime_curDate, toTime_curDate);
        StringBuilder log =new StringBuilder();
        for (InterviewerClassCount member : listInterviewers){
            log.append("{id:"+member.getOnlinClassId()+",teacherId:"+member.getTeacherId()+",bookedCount:"+member.getBookedCount()+"} ");
        }
        logger.info("listBookedCount: {}", log);

        if (CollectionUtils.isEmpty(listInterviewers)){
            return "";
        }else if (listInterviewers.size()==1){
            return  Integer.toString(listInterviewers.get(0).getOnlinClassId());
        }else{

            List<InterviewerClassCount> targeList=new ArrayList<InterviewerClassCount>();
            int prevCount=listInterviewers.get(0).getBookedCount();
            int curCount=prevCount;
            for (int i=0;i< listInterviewers.size(); i++) {

                curCount = listInterviewers.get(i).getBookedCount();
                if (curCount!= prevCount){
                   break;
                }

                targeList.add(listInterviewers.get(i));
                prevCount=curCount;
            }

            //Shuffle the new list
            if(CollectionUtils.isNotEmpty(targeList)){
                Collections.shuffle(targeList);
            }

            logger.info("Top 1 Class Id to be booked: {}", listInterviewers.get(0).getOnlinClassId());
            return Integer.toString(listInterviewers.get(0).getOnlinClassId());
        }
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
        
        if(teacher == null || teacher.getId() == 0 || StringUtils.isBlank(teacher.getRealName())){
            return ReturnMapUtils.returnFail("This account does not exist.");
        }
        
        String logpix = "onlineclassId:"+onlineClassId+";teacherId:"+teacher.getId();
        
        if(recruitmentService.teacherIsApplicationFinished(teacher)){
            return ReturnMapUtils.returnFail("Your recruitment process is over already, Please refresh your page !","INTERVIEW:"+teacher.getId());
        }
        
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
        
        //课程没有找到，无法book
        if(onlineClass == null){
            return ReturnMapUtils.returnFail("This online class does not exist.",logpix);
        }

        //onlineClassId 必须是OPEN 课
        if(!OnlineClassEnum.ClassStatus.OPEN.toString().equalsIgnoreCase(onlineClass.getStatus())){
            return ReturnMapUtils.returnFail("Oops, someone else just booked this time slot. Please select another.",logpix);
        }

        //book的课程在开课前1小时之内不允许book
        if((System.currentTimeMillis() + InterviewConstant.BOOK_TIME) > onlineClass.getScheduledDateTime().getTime()){
            return ReturnMapUtils.returnFail("Oops, someone else just booked this time slot. Please select another.",logpix);
        }
        //约课老师必须是INTERVIEW的待约课老师
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isNotEmpty(listEntity)){
            TeacherApplication teacherApplication = listEntity.get(0);
            //存在步骤，但步骤中已经存在待审核的课程 不允许继续book
            if(teacherApplication.getOnlineClassId() != 0 && StringUtils.isBlank(teacherApplication.getResult())){
                return ReturnMapUtils.returnFail("You have booked a class already. Please refresh your page !",logpix);
            }
        }
        //判断剩余可取消次数
        if(recruitmentService.getRemainRescheduleTimes(teacher, Status.INTERVIEW.toString(), Result.CANCEL.toString(), false) <= 0){
            userDao.doLock(teacher.getId());
            teacherLockLogDao.save(new TeacherLockLog(teacher.getId(), Reason.RESCHEDULE.toString(), LifeCycle.INTERVIEW.toString()));
            return ReturnMapUtils.returnFail("There are no more cancellations allowed for your account. Contact us at teachvip@vipkid.com.cn for more information.",logpix);
        }
        //执行BOOK逻辑
        String dateTime = DateFormatUtils.format(onlineClass.getScheduledDateTime(),"yyyy-MM-dd HH:mm:ss");
        Map<String,Object> result = OnlineClassProxy.doBookRecruitment(teacher.getId(), onlineClass.getId(), ClassType.TEACHER_RECRUITMENT,dateTime);
        if(ReturnMapUtils.isFail(result)){
            //一旦失败，抛出异常回滚
            throw new RuntimeException(result.get("info")+"");
        }
        if(ReturnMapUtils.isSuccess(result)){
            result.put("onlineClassId",onlineClass.getId());
            logger.info("teacher:{} book Interview success send email.  onlineClassId:{}", teacher.getId(), onlineClass.getId());
            TeacherAddress teacherAddress = null;
            Integer teacherNumber = 0;
            String cityName = "";
            if(teacher.getCurrentAddressId() != 0){
            	teacherAddress = teacherAddressDao.findById(teacher.getCurrentAddressId());
            	int cityId = teacherAddress.getCity();
            	//城市老师数量统计
            	teacherNumber = teacherDao.findRegulareTeacherByCity(cityId, teacherAddress.getStateId());
            	if(cityId == 0){
            		cityId = teacherAddress.getStateId();
            	}
            	//城市名称获取
            	if(cityId != 0){
            		TeacherLocation tl = this.teacherLocationDao.findById(cityId);
            		cityName = tl.getName();
            	}
            }
            EmailUtils.sendEmail4InterviewBook(teacher,onlineClass,cityName,teacherNumber);
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

        if(teacher == null || teacher.getId() == 0 || StringUtils.isBlank(teacher.getRealName())){
            return ReturnMapUtils.returnFail("This account does not exist.");
        }
        
        String logpix = "onlineclassId:"+onlineClassId+";teacherId:"+teacher.getId();

        //class already start, can't cancel error
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);
        if(onlineClass == null){
            return ReturnMapUtils.returnFail("This online class does not exist.",logpix);
        }
        
        if(System.currentTimeMillis() > onlineClass.getScheduledDateTime().getTime()){
            return ReturnMapUtils.returnFail("Sorry, you can't cancel after the start time has passed.",logpix);
        }

        List<TeacherApplication> listEntity = this.teacherApplicationDao.findCurrentApplication(teacher.getId());
        //如果步骤中无数据则不允许cancel
        if(CollectionUtils.isEmpty(listEntity)){
            return ReturnMapUtils.returnFail("You do not have permission to cancel this course",logpix);
        }else{
            TeacherApplication teacherApplication = listEntity.get(0);
            //如果步骤中有数据并且数据不是本次cancel的课程 则不允许cancel
            if(teacherApplication.getOnlineClassId() != onlineClass.getId()){
                return ReturnMapUtils.returnFail("You have already cancelled this class. Please refresh your page !",logpix);
            }else{
                //果步骤中有数据并且数据不是本次cancel的课程 但管理端已经审核，不允许cancel
                if(StringUtils.isNotBlank(teacherApplication.getResult())){
                    return ReturnMapUtils.returnFail("This class already audited. Please refresh your page !",logpix);
                }
            }
        }

        //保存cancel记录
        this.teacherApplicationLogDao.saveCancel(teacher.getId(), listEntity.get(0).getId(), Status.INTERVIEW, Result.CANCEL, onlineClass);

        if (!UserEnum.Status.isLocked(userDao.findById(teacher.getId()).getStatus())) {
            int count = recruitmentService.getRemainRescheduleTimes(teacher, Status.INTERVIEW.toString(), Result.CANCEL.toString(), true);
            if (count <= 0) {
                userDao.doLock(teacher.getId());
                teacherLockLogDao.save(new TeacherLockLog(teacher.getId(), Reason.RESCHEDULE.toString(), LifeCycle.INTERVIEW.toString()));
            }
        }

        //执行Cancel逻辑
        Map<String,Object> result = OnlineClassProxy.doCancelRecruitement(teacher.getId(), onlineClass.getId(), ClassType.TEACHER_RECRUITMENT);
        if(ReturnMapUtils.isFail(result)){
            //一旦失败，抛出异常回滚
            throw new RuntimeException(""+result.get("info"));
        }
        return result;
    }

    /**
     * 进入下一步骤
     * @param teacher
     * @return
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> updateToTraining(Teacher teacher){
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isEmpty(listEntity)){
            return ReturnMapUtils.returnFail("You have no legal power into the next phase !","teacherId;"+teacher.getId());
        }

        //执行逻辑 只有在INTERVIEW的PASS状态才能进入
        if(Status.INTERVIEW.toString().equals(listEntity.get(0).getStatus())
                && Result.PASS.toString().equals(listEntity.get(0).getResult())){
            
            //按照新流程 该步骤将老师的LifeCycle改变为Interview -to-Training
            teacher.setLifeCycle(LifeCycle.TRAINING.toString());
            this.teacherDao.insertLifeCycleLog(teacher.getId(),LifeCycle.INTERVIEW,LifeCycle.TRAINING, teacher.getId());
            this.teacherDao.update(teacher);
            
            // 增加quiz的考试记录
            teacherQuizDao.insertQuiz(teacher.getId(),teacher.getId(),Version.TRAINING_QUIZ);
            
            // 新加Training 申请记录
            List<TeacherApplication> list = teacherApplicationDao.findCurrentApplication(teacher.getId());
            if(CollectionUtils.isNotEmpty(list)){
               list.stream().forEach(application -> {application.setCurrent(0);teacherApplicationDao.update(application);});
            }
            
            // 保存申请时间
            TeacherApplication teacherApplication = new TeacherApplication();
            teacherApplication = teacherApplicationDao.initApplicationData(teacherApplication);
            teacherApplication.setTeacherId(teacher.getId());
            teacherApplication.setApplyDateTime(new Timestamp(System.currentTimeMillis()));
            teacherApplication.setCurrent(1);
            teacherApplication.setStatus(Status.TRAINING.toString());
            this.teacherApplicationDao.save(teacherApplication);
            
            return ReturnMapUtils.returnSuccess();
        }
        return ReturnMapUtils.returnFail("You have no legal power into the next phase !","teacherId;"+teacher.getId());
    }
}
