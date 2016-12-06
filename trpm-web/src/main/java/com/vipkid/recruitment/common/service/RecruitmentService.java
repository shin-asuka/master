package com.vipkid.recruitment.common.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.vipkid.recruitment.common.CommonConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.TeacherApplicationEnum.AuditStatus;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherLockLogEnum.Reason;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeacherApplicationLogDao;
import com.vipkid.recruitment.dao.TeacherLockLogDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeacherLockLog;
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
            resultMap.put("result",AuditStatus.TO_SUBMIT.toString());
            return resultMap;
        }
        TeacherApplication teacherApplication = list.get(0);

        //【待提交】当前状态与流程状态不一样,以lifeCycle为准
        if(!StringUtils.equalsIgnoreCase(teacherApplication.getStatus(), teacher.getLifeCycle())){
            resultMap.put("result",AuditStatus.TO_SUBMIT.toString());
            return resultMap;
        }

        Map<String,Object> _result = null;
        //BASIC_INFO 11.5小时之内如果状态是FAIL 为待审核
        if(StringUtils.equalsIgnoreCase(Status.BASIC_INFO.toString(),teacherApplication.getStatus())){
            _result = getBasicInfoStatus(teacher, teacherApplication);
        //INTERVIEW 待审核 待约课
        }else if(StringUtils.equalsIgnoreCase(Status.INTERVIEW.toString(),teacherApplication.getStatus())){
            _result = getInterviewStatus(teacher, teacherApplication);
        //待审核
        }else if(StringUtils.equalsIgnoreCase(Status.PRACTICUM.toString(),teacherApplication.getStatus())){
            _result = getPracticumStatus(teacher, teacherApplication);
        }
        if(_result != null){
            resultMap.putAll(_result);
            return resultMap;
        }
        //其他情况 待经审核
        if(StringUtils.isBlank(teacherApplication.getResult())){
            resultMap.put("result",AuditStatus.TO_AUDIT.toString());
            return resultMap;
        }
        //已经审核 结果【FAIL,PASS,REPLAY】
        if(StringUtils.isNotBlank(teacherApplication.getResult())){
            resultMap.put("result",teacherApplication.getResult());
            if(StringUtils.equalsIgnoreCase(Result.FAIL.toString(),teacherApplication.getResult())
                    || StringUtils.equalsIgnoreCase(Result.REAPPLY.toString(),teacherApplication.getResult())){
                //失败原因
                resultMap.put("failedReason",teacherApplication.getFailedReason());
                //重来备注
                resultMap.put("comments",teacherApplication.getComments());
            }
            return resultMap;
        }
        return resultMap;
    }


    private Map<String,Object> getBasicInfoStatus(Teacher teacher,TeacherApplication teacherApplication){
        Map<String,Object> result = Maps.newHashMap();
        if(StringUtils.equalsIgnoreCase(Result.FAIL.toString(),teacherApplication.getResult())){
            if(!DateUtils.count11hrlf(teacherApplication.getAuditDateTime().getTime())){
                result.put("result",AuditStatus.TO_AUDIT.toString());
                return result;
            }
        }
        return null;
    }

    private Map<String,Object> getInterviewStatus(Teacher teacher,TeacherApplication teacherApplication){
        Map<String,Object> result = Maps.newHashMap();
        if(StringUtils.isBlank(teacherApplication.getResult())){
            if(teacherApplication.getOnlineClassId() == 0){
                //待约课
                result.put("result",AuditStatus.TO_SUBMIT.toString());
                return result;
            }else{
                //倒计时
                OnlineClass onlineClass = this.onlineClassDao.findById(teacherApplication.getOnlineClassId());
                //处于book状态的onlineClass 应该处于倒计时页面
                if(OnlineClassEnum.ClassStatus.BOOKED.toString().equals(onlineClass.getStatus())){
                    //小于1个小时 可进入onlineClass
                    if(!DateUtils.count1h(onlineClass.getScheduledDateTime().getTime())){
                        result.put("result",AuditStatus.TO_CLASS.toString());
                        return result;
                    //小于54周 处于审核中
                    }else if(!DateUtils.count54week(onlineClass.getScheduledDateTime().getTime())){
                        result.put("result",AuditStatus.TO_AUDIT.toString());
                        return result;
                    //大于54周超时
                    }else{
                        result.put("result",AuditStatus.HAS_TIMEOUT.toString());
                        return result;
                    }
                }
            }
        }else if(StringUtils.equalsIgnoreCase(Result.PASS.toString(),teacherApplication.getResult())){
            result.put("result",teacherApplication.getResult());
            result.put("basePay",teacherApplication.getBasePay());
            return result;
        }
        return null;
    }

    private Map<String,Object> getPracticumStatus(Teacher teacher,TeacherApplication teacherApplication){
        Map<String,Object> result = this.getInterviewStatus(teacher, teacherApplication);
        if (result == null) {
            result = Maps.newHashMap();
        }
        if(Result.PRACTICUM2.toString().equals(teacherApplication.getResult())){
            result.put("result",AuditStatus.TO_SUBMIT.toString());

        }
        if(Result.TBD.toString().equals(teacherApplication.getResult()) || Result.TBD_FAIL.toString().equals(teacherApplication.getResult())){
            result.put("result",AuditStatus.TO_AUDIT.toString());
        }
        List<TeacherApplication> list = teacherApplicationDao.findApplictionForStatusResult(teacher.getId(), null, Result.PRACTICUM2.name());
        if(CollectionUtils.isNotEmpty(list)){
            result.put("practicumNo", 2);
        } else {
            result.put("practicumNo", 1);
        }
        List<TeacherApplication> trainingPassTAList = teacherApplicationDao.findApplictionForStatusResult(teacher.getId(), Status.TRAINING.toString(), Result.PASS.toString());
        if (CollectionUtils.isNotEmpty(trainingPassTAList) && trainingPassTAList.get(0).getAuditDateTime() != null){
            result.put("trainingPassTime", trainingPassTAList.get(0).getAuditDateTime().getTime());
        } else {
            result.put("trainingPassTime", 0);
        }
        return result;
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
    public TeacherLocation getTeacherLocation(int id){
        if(id == 0){
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
            logger.error(" OnlineClass id is null,id = {}",bean.getOnlineClassId());
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


    public int getRemainRescheduleTimes(Teacher teacher, String status, String type){
        //type: cancelNum, CancelNoShow, ITProblem
        int remainTimes = 0;
        int lockTimes = getLockTimes(teacher.getId(), status);
        int reapplyTimesByITProblem = getReapplyTimesByITProblem(teacher.getId(), status);
        int reapplyTimesByCancelNoShow = getReapplyTimesByCancelNoShow(teacher.getId(), status);
        int cancelNum = getCancelNum(teacher.getId(), status);
        if (lockTimes > 0){
            int itOverTimes = reapplyTimesByITProblem - CommonConstant.IT_PRO_MAX_ALLOWED_TIMES;
            itOverTimes = itOverTimes > 0 ? itOverTimes : 0;

            int cancelOverTimes = reapplyTimesByCancelNoShow + cancelNum - CommonConstant.CANCEL_MAX_ALLOWED_TIMES;
            cancelOverTimes = cancelOverTimes > 0 ? cancelOverTimes : 0;

            if(itOverTimes + cancelOverTimes < lockTimes){
                remainTimes = 1;
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

    private int getLockTimes(long teacherId, String lifeCycle){
        return teacherLockLogDao.count(new TeacherLockLog(teacherId, Reason.RESCHEDULE.toString(), lifeCycle));
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
}
