package com.vipkid.recruitment.common.service;

import com.google.api.client.util.Maps;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.TeacherApplicationEnum.AuditStatus;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherLockLogEnum.Reason;
import com.vipkid.recruitment.common.CommonConstant;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeacherApplicationLogDao;
import com.vipkid.recruitment.dao.TeacherLockLogDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeacherLockLog;
import com.vipkid.recruitment.interview.InterviewConstant;
import com.vipkid.recruitment.practicum.PracticumConstant;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.dto.TimezoneDto;
import com.vipkid.trpm.constant.ApplicationConstant.FinishType;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherLocationDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class RecruitmentService {
    
    
    private static Logger logger = LoggerFactory.getLogger(RecruitmentService.class);

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    @Autowired
    private TeacherAddressDao teacherAddressDao;

    @Autowired
    private TeacherLocationDao teacherLocationDao;
    
    @Autowired
    private OnlineClassDao onlineClassDao;

    @Autowired
    private TeacherApplicationLogDao teacherApplicationLogDao;

    @Autowired
    private TeacherLockLogDao teacherLockLogDao;

    /**
     * 获取老师当前LifeCycle状态下的流程结果 
     * @Author:ALong (ZengWeiLong)
     * @param teacher
     * @return
     * Map<String,Object>
     * @date 2016年10月19日
     */
    public Map<String,Object> getStatus(Teacher teacher){
        Map<String,Object> resultMap = Maps.newHashMap();
        
        resultMap.put("lifeCycle",teacher.getLifeCycle());

        List<TeacherApplication> list = this.teacherApplicationDao.findCurrentApplication(teacher.getId());

        //【待提交】没有流程则视为
        if(CollectionUtils.isEmpty(list)){
            logger.info("当前没有流程记录,以老师状态【"+teacher.getLifeCycle()+"】为准 teacherId:{} ",teacher.getId());
            resultMap.put("result",AuditStatus.TO_SUBMIT.toString());
            return resultMap;
        }
        
        //【待提交】当前状态与流程状态不一样,以lifeCycle为准
        TeacherApplication teacherApplication = list.get(0);
        if(!StringUtils.equalsIgnoreCase(teacherApplication.getStatus(), teacher.getLifeCycle())){
            logger.info("当前状态与流程状态不一样,以老师状态【"+teacher.getLifeCycle()+"】为准 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
            resultMap.put("result",AuditStatus.TO_SUBMIT.toString());
            return resultMap;
        }
        
        //BASIC_INFO 11.5小时之内如果状态是FAIL 为待审核
        if(StringUtils.equalsIgnoreCase(Status.BASIC_INFO.toString(),teacherApplication.getStatus())){
            logger.info("BasicInfo teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
            resultMap.putAll(getBasicInfoStatus(teacher, teacherApplication));
            return resultMap;
        //INTERVIEW
        }else if(StringUtils.equalsIgnoreCase(Status.INTERVIEW.toString(),teacherApplication.getStatus())){
            logger.info("Interview teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
            resultMap.putAll(getInterviewStatus(teacher, teacherApplication));
            return resultMap;
        //TRAINING
        }else if(StringUtils.equalsIgnoreCase(Status.TRAINING.toString(),teacherApplication.getStatus())){
            logger.info("Training teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
            resultMap.putAll(getTrainingStatus(teacher, teacherApplication));
            return resultMap;
        //Practicum
        }else if(StringUtils.equalsIgnoreCase(Status.PRACTICUM.toString(),teacherApplication.getStatus())){
            logger.info("Practicum teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
            resultMap.putAll(getPracticumStatus(teacher, teacherApplication));
            return resultMap;
        //ContractInfo
        }else if(StringUtils.equalsIgnoreCase(Status.CONTRACT_INFO.toString(),teacherApplication.getStatus())){
            logger.info("ContractInfo teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
            resultMap.putAll(getContractInfoStatus(teacher, teacherApplication));
            return resultMap;
        }else{
            resultMap.put("result",AuditStatus.TO_SUBMIT.toString());
            return resultMap;
        }
    }
    
    private Map<String,Object> getBasicInfoStatus(Teacher teacher,TeacherApplication teacherApplication){
        //其他情况 待经审核
        Map<String,Object> result = Maps.newHashMap();
        //审核结果为空则为待审核
        if(StringUtils.isBlank(teacherApplication.getResult())){
            logger.info("进入"+teacherApplication.getStatus()+"待审核页面 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
            result.put("result",AuditStatus.TO_AUDIT.toString());
            return result;
        //审核结果为其他
        }else{
            //如果是Fail并且在半小时之内为待审核
            boolean _result = StringUtils.equalsIgnoreCase(Result.FAIL.toString(),teacherApplication.getResult());
            long auditTimeMillis = teacherApplication.getApplyDateTime().getTime();
            if (teacherApplication.getAuditDateTime() != null) {
                auditTimeMillis = teacherApplication.getAuditDateTime().getTime();
            }
            boolean _failTimeout = !DateUtils.count11Half(auditTimeMillis);
            if(_result && _failTimeout){
                logger.info("进入"+teacherApplication.getStatus()+"待审核页面 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
                result.put("result",AuditStatus.TO_AUDIT.toString());
                return result;
            //其他则直接返回给前端
            }else{
                logger.info("进入"+teacherApplication.getStatus()+"审核结果 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
                result.put("result",teacherApplication.getResult());
                if(!StringUtils.equalsIgnoreCase(Result.PASS.toString(),teacherApplication.getResult())){
                    //失败原因
                    result.put("failedReason",teacherApplication.getFailedReason());
                    //重来备注
                    result.put("comments",teacherApplication.getComments());
                }
                return result;
            }
        }
    }

    private Map<String,Object> getInterviewPracticumStatusByResultIsBlank(TeacherApplication teacherApplication, int enterClassAllowedMinute){
        Map<String,Object> result = Maps.newHashMap();
        if(teacherApplication.getOnlineClassId() == 0){
            //待约课
            logger.info("进入"+teacherApplication.getStatus()+"待约课页面 teacherId:{} taId:{}",teacherApplication.getTeacherId(),teacherApplication.getId());
            result.put("result",AuditStatus.TO_SUBMIT.toString());
            return result;
        }else{
            logger.info("进入"+teacherApplication.getStatus()+"约课等待页面 teacherId:{} taId:{}",teacherApplication.getTeacherId(),teacherApplication.getId());
            //倒计时
            OnlineClass onlineClass = this.onlineClassDao.findById(teacherApplication.getOnlineClassId());
            //处于book状态的onlineClass 应该处于倒计时页面
            if(OnlineClassEnum.ClassStatus.BOOKED.toString().equals(onlineClass.getStatus())){
                //小于1个小时 可进入onlineClass
                if(!DateUtils.countXMinute(onlineClass.getScheduledDateTime().getTime(), enterClassAllowedMinute)){
                    logger.info("进入"+teacherApplication.getStatus()+"待上课核页面 teacherId:{} taId:{}",teacherApplication.getTeacherId(),teacherApplication.getId());
                    result.put("result",AuditStatus.TO_CLASS.toString());
                    return result;
                    //大于1小时 但 小于54周 处于审核中
                }else if(!DateUtils.count54week(onlineClass.getScheduledDateTime().getTime())){
                    logger.info("进入"+teacherApplication.getStatus()+"待审核页面 teacherId:{} taId:{}",teacherApplication.getTeacherId(),teacherApplication.getId());
                    result.put("result",AuditStatus.TO_AUDIT.toString());
                    return result;
                    //大于54周超时
                }else{
                    logger.info("进入"+teacherApplication.getStatus()+"超时页面 teacherId:{} taId:{}",teacherApplication.getTeacherId(),teacherApplication.getId());
                    result.put("result",AuditStatus.HAS_TIMEOUT.toString());
                    return result;
                }
            }else{
                //这状态属于数据有误，将其处于待审核状态
                logger.info("进入"+teacherApplication.getStatus()+"待审核页面 teacherId:{} taId:{}",teacherApplication.getTeacherId(),teacherApplication.getId());
                result.put("result",AuditStatus.TO_AUDIT.toString());
                return result;
            }
        }
    }

    private Map<String,Object> getInterviewStatus(Teacher teacher,TeacherApplication teacherApplication){
        Map<String,Object> result = Maps.newHashMap();
        if(StringUtils.isBlank(teacherApplication.getResult())){
            result.putAll(getInterviewPracticumStatusByResultIsBlank(teacherApplication, InterviewConstant.ENTER_CLASS_MINUTES));
        }else{
            //如果是Fail并且在11个半小时之内为待审核
            boolean _result = StringUtils.equalsIgnoreCase(Result.FAIL.toString(),teacherApplication.getResult());
            long auditTimeMillis = teacherApplication.getApplyDateTime().getTime();
            if (teacherApplication.getAuditDateTime() != null) {
                auditTimeMillis = teacherApplication.getAuditDateTime().getTime();
            }
            boolean _failTimeout = !DateUtils.count11Half(auditTimeMillis);
            if(_result && _failTimeout){
                logger.info("进入"+teacherApplication.getStatus()+"待审核页面 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
                result.put("result",AuditStatus.TO_AUDIT.toString());
                //其他则直接返回给前端
            }else {
                result.put("result", teacherApplication.getResult());
                logger.info("进入"+teacherApplication.getStatus()+"的"+teacherApplication.getResult()+"页面 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
                if (!StringUtils.equalsIgnoreCase(Result.PASS.toString(), teacherApplication.getResult())) {
                    //失败原因
                    result.put("failedReason", teacherApplication.getFailedReason());
                    //重来备注
                    result.put("comments", teacherApplication.getComments());
                } else {
                    result.put("basePay", teacherApplication.getBasePay());
                }
            }
        }
        return result;
    }

    private Map<String,Object> getTrainingStatus(Teacher teacher,TeacherApplication teacherApplication){
        //其他情况 待经审核
        Map<String,Object> result = Maps.newHashMap();
        //审核结果为空则为待审核
        if(StringUtils.isBlank(teacherApplication.getResult())){
            logger.info("进入"+teacherApplication.getStatus()+"待审核页面 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
            result.put("result",AuditStatus.TO_SUBMIT.toString());
            return result;
        //审核结果为其他
        }else {
            result.put("result",teacherApplication.getResult());
            logger.info("进入"+teacherApplication.getStatus()+"的"+teacherApplication.getResult()+"页面 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
            if(!StringUtils.equalsIgnoreCase(Result.PASS.toString(),teacherApplication.getResult())){
                //失败原因
                result.put("result",AuditStatus.TO_SUBMIT.toString());

                result.put("failedReason",teacherApplication.getFailedReason());
                //重来备注
                result.put("comments",teacherApplication.getComments());
            }
            return result;
        }
    }
    
    private Map<String,Object> getPracticumStatus(Teacher teacher,TeacherApplication teacherApplication){
        Map<String,Object> result = Maps.newHashMap();
        if(StringUtils.isBlank(teacherApplication.getResult())){
            result.putAll(getInterviewPracticumStatusByResultIsBlank(teacherApplication, PracticumConstant.ENTER_CLASS_MINUTES));
        }else{
            //如果是Fail并且在11个半小时之内为待审核
            boolean _result = StringUtils.equalsIgnoreCase(Result.FAIL.toString(),teacherApplication.getResult());
            long auditTimeMillis = teacherApplication.getApplyDateTime().getTime();
            if (teacherApplication.getAuditDateTime() != null) {
                auditTimeMillis = teacherApplication.getAuditDateTime().getTime();
            }
            boolean _failTimeout = !DateUtils.count11Half(auditTimeMillis);
            if((_result && _failTimeout) || Result.TBD.toString().equals(teacherApplication.getResult()) || Result.TBD_FAIL.toString().equals(teacherApplication.getResult())){
                logger.info("进入"+teacherApplication.getStatus()+"待审核页面 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
                result.put("result",AuditStatus.TO_AUDIT.toString());
            }else if(Result.PRACTICUM2.toString().equals(teacherApplication.getResult())){
                logger.info("进入"+teacherApplication.getStatus()+"待约课页面 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
                result.put("result",AuditStatus.TO_SUBMIT.toString());
            }else{
                //其他则直接返回给前端
                result.put("result",teacherApplication.getResult());
                logger.info("进入"+teacherApplication.getStatus()+"的"+teacherApplication.getResult()+"页面 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
                if(!StringUtils.equalsIgnoreCase(Result.PASS.toString(),teacherApplication.getResult())){
                    //失败原因
                    result.put("failedReason",teacherApplication.getFailedReason());
                    //重来备注
                    result.put("comments",teacherApplication.getComments());
                }
            }
        }

        List<TeacherApplication> list = teacherApplicationDao.findApplicationForStatusResult(teacher.getId(), null, Result.PRACTICUM2.name());
        if(CollectionUtils.isNotEmpty(list)){
            result.put("practicumNo", 2);
        }else{
            result.put("practicumNo", 1);
        }

        result.put("trainingPassTime", 0);
        if(AuditStatus.TO_SUBMIT.toString().equals(result.get("result").toString())){
            logger.info("进入"+teacherApplication.getStatus()+"待约课页面 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
            List<TeacherApplication> trainingPassTAList = teacherApplicationDao.findApplicationForStatusResult(teacher.getId(), Status.TRAINING.toString(), Result.PASS.toString());
            if(CollectionUtils.isNotEmpty(trainingPassTAList) && trainingPassTAList.get(0).getAuditDateTime() != null){
                result.put("trainingPassTime", trainingPassTAList.get(0).getAuditDateTime().getTime());
            }
        }

        return result;
    }
    
    private Map<String,Object> getContractInfoStatus(Teacher teacher,TeacherApplication teacherApplication){
        //其他情况 待经审核
        Map<String,Object> result = Maps.newHashMap();
        //审核结果为空则为待审核
        if(StringUtils.isBlank(teacherApplication.getResult())){
            logger.info("进入"+teacherApplication.getStatus()+"待审核页面 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
            result.put("result",AuditStatus.TO_AUDIT.toString());
            return result;
        //审核结果为其他
        }else{
            result.put("result",teacherApplication.getResult());
            logger.info("进入"+teacherApplication.getStatus()+"的"+teacherApplication.getResult()+"页面 teacherId:{} taId:{}",teacher.getId(),teacherApplication.getId());
            if(!StringUtils.equalsIgnoreCase(Result.PASS.toString(),teacherApplication.getResult())){
                //失败原因
                result.put("failedReason",teacherApplication.getFailedReason());
                //重来备注
                result.put("comments",teacherApplication.getComments());
            }
            return result;
        }
    }

    /**
     * 获取地址信息
     * @param teacherAddressId
     * @return
     * boolean
     */
    public TeacherAddress getTeacherAddress(int teacherAddressId){
        if(teacherAddressId == 0){
            return null;
        }
        TeacherAddress ta = teacherAddressDao.findById(teacherAddressId);
        return ta;
    }
    
    /**
     * 获取地址信息
     * @param id
     * @return
     * boolean
     */
    public TeacherLocation getTeacherLocation(Integer id){
        if(null == id||0==id){
            return null;
        }
        TeacherLocation teacherLocation = teacherLocationDao.findById(id);
        return teacherLocation;
    }
    
    /**
     * 获取上课信息
     * @param teacher
     * @return    
     * Map<String,Object>
     */
    public Map<String,Object> getOnlineClassInfo(Teacher teacher){
        Map<String,Object> result = Maps.newHashMap();
        
        List<TeacherApplication> list = this.teacherApplicationDao.findCurrentApplication(teacher.getId());
        
        if(CollectionUtils.isEmpty(list)){
            logger.info(" The teacher only start recruitment process  id = {}",teacher.getId());
            return result;
        }
        
        TeacherApplication bean = list.get(0);
        if(bean.getOnlineClassId() <= 0){
            logger.warn(" OnlineClass id is null,id = {}",bean.getOnlineClassId());
            return result;
        }
        
        if(LifeCycle.INTERVIEW.toString().equalsIgnoreCase(bean.getStatus()) || LifeCycle.PRACTICUM.toString().equalsIgnoreCase(bean.getStatus()) ){
            OnlineClass onlineClass = this.onlineClassDao.findById(bean.getOnlineClassId());
            if(onlineClass == null){
                logger.error(" OnlineClass is null,id = {}",bean.getOnlineClassId());
                return result;
            }
            if(OnlineClassEnum.ClassStatus.BOOKED.toString().equals(onlineClass.getStatus())){
                result.put("serverTime",System.currentTimeMillis());
                result.put("scheduledDateTime",onlineClass.getScheduledDateTime().getTime());
                result.put("onlineClassId", onlineClass.getId());
                result.put("isQuickInterview", onlineClass.getClassType() == OnlineClassEnum.ClassType.QUICK_INTERVIEW.val() ? true : false);
                // mockclass
                TeacherApplication teacherApplication = teacherApplicationDao.findByOlineclassId(onlineClass.getId());
                result.put("applicationId", teacherApplication.getId());
            }
        }
        return result;
    }
    /**
     * 更新timezone
     * @param bean
     * @param teacher
     * @return
     * boolean
     */
    public Map<String,Object> updateTimezone(TimezoneDto bean,Teacher teacher){
        teacher.setTimezone(bean.getTimezone());
        //2.更新Address
        TeacherAddress teacherAddress = this.teacherAddressDao.updateOrSaveCurrentAddressId(teacher, bean.getCountryId(), bean.getStateId(), bean.getCityId(),null,null);
        if(teacherAddress == null || teacherAddress.getId() <= 0){
            return ReturnMapUtils.returnFail("老师:"+teacher.getId()+",地址信息:"+JsonTools.getJson(teacherAddress)+",保存有问题.");
        }
        this.teacherDao.update(teacher);
        return ReturnMapUtils.returnSuccess();
    }

    /**
     * 检查老师是否被Fail，或者当前步骤是否Pass 
     * 2016年12月13日 下午1:29:27
     * @param teacher
     * @return    
     * boolean true:是,false:不是
     */
    public boolean teacherIsApplicationFinished(Teacher teacher){
        //老师已经被fail过，不能继续操作n
        List<TeacherApplication> fail = teacherApplicationDao.findApplicationForStatusResult(teacher.getId(),teacher.getLifeCycle(),Result.FAIL.toString());
        if(CollectionUtils.isNotEmpty(fail)){
            return true;
        }
        //当前步骤已经pass过 不允许操作
        List<TeacherApplication> pass = teacherApplicationDao.findApplicationForStatusResult(teacher.getId(),teacher.getLifeCycle(),Result.PASS.toString());
        if(CollectionUtils.isNotEmpty(pass)){
            return true;
        }
        return false;
    }

    public int getRemainRescheduleTimes(Teacher teacher, String status, String type, boolean isForRescheduleAction){
        //type: cancelNum, CancelNoShow, ITProblem
        int remainTimes = 0;
        int lockedTimes = getLockTimes(teacher.getId(), status, null);
        int lockTimes = getLockTimes(teacher.getId(), status, new Integer(0));
        int reapplyTimesByITProblem = getReapplyTimesByITProblem(teacher.getId(), status);
        int reapplyTimesByCancelNoShow = getReapplyTimesByCancelNoShow(teacher.getId(), status);
        int cancelNum = getCancelNum(teacher.getId(), status);
        if (lockedTimes > 0){
            if (lockTimes > 0) {
                if (isForRescheduleAction) {
                    teacherLockLogDao.unlock(new TeacherLockLog(teacher.getId(), Reason.RESCHEDULE.toString(), status));
                } else {
                    remainTimes = 1;
                }
            }
        } else {
            if (FinishType.STUDENT_CANCELLATION.equals(type) || FinishType.STUDENT_NO_SHOW.equals(type) || Result.CANCEL.toString().equals(type)){
                remainTimes = CommonConstant.CANCEL_MAX_ALLOWED_TIMES - reapplyTimesByCancelNoShow - cancelNum;
            } else if (FinishType.STUDENT_IT_PROBLEM.equals(type)){
                remainTimes = CommonConstant.IT_PRO_MAX_ALLOWED_TIMES - reapplyTimesByITProblem;
            } else {
                remainTimes = 99;
            }
        }
        return remainTimes;
    }

    private int getLockTimes(long teacherId, String lifeCycle, Integer isUnlocked){
        return teacherLockLogDao.count(new TeacherLockLog(teacherId, Reason.RESCHEDULE.toString(), lifeCycle, isUnlocked));
    }

    private int getReapplyTimesByCancelNoShow(long teacherId, String status){
        List<String> finishTypes = Arrays.asList(FinishType.STUDENT_CANCELLATION, FinishType.STUDENT_NO_SHOW);
        return teacherApplicationDao.countByTeacherIdStatusFinishType(teacherId, status, finishTypes);
    }

    private int getReapplyTimesByITProblem(long teacherId, String status){
        List<String> finishTypes = Arrays.asList(FinishType.STUDENT_IT_PROBLEM);
        return teacherApplicationDao.countByTeacherIdStatusFinishType(teacherId, status, finishTypes);
    }

    /**
     * 获取老师对Interview 课程的Cancel 次数
     * @param teacherId
     * @return int
     */
    public int getCancelNum(long teacherId, String status){
        return this.teacherApplicationLogDao.getCancelNum(teacherId, status, Result.CANCEL);
    }


    /**
     * 获取推荐人已经完成了多少节课
     * @param teacher
     * @return
     */
    public Map<String,String> getReferralCompleteNumber(Teacher teacher){
        Map<String, String> pramMap = Maps.newHashMap();
        pramMap.put("referralShow", "none");
        if(teacher != null && StringUtils.isNotBlank(teacher.getReferee())){
            String[] referralInfo = teacher.getReferee().split(",");
            try{
                String teacherId = referralInfo[0];
                pramMap.put("referrerName", referralInfo[1]);
                if(StringUtils.isNumeric(teacherId)){
                    long id = Long.valueOf(teacherId);
                    int numberClasses = this.onlineClassDao.countClassNumByTeacherId(id);
                    pramMap.put("numberClasses",numberClasses+"");
                    pramMap.put("referralShow", "block");
                }
            }catch(Exception e){
                pramMap.put("referralShow", "none");
                logger.warn(" 推荐人{}，上课数据获取失败:{}",teacher.getReferee(),e.getStackTrace());
            }
        }
        return pramMap;
    }
}
