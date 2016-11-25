package com.vipkid.recruitment.common.service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.vipkid.trpm.constant.ApplicationConstant.FinishType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.enums.OnlineClassEnum;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherApplicationEnum.AuditStatus;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.rest.dto.TimezoneDto;
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

    /**
     * 获取老师当前LifeCycle状态下的流程结果 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @return
     * Map<String,Object>
     * @date 2016年10月19日
     */
    public Map<String,Object> getStatus(long teacherId){
        Map<String,Object> resultMap = Maps.newHashMap();
        Teacher teacher = this.teacherDao.findById(teacherId);
        resultMap.put("lifeCycle",teacher.getLifeCycle());

        List<TeacherApplication> list = this.teacherApplicationDao.findCurrentApplication(teacher.getId());

        //【待提交】没有流程则视为
        if(CollectionUtils.isEmpty(list)){
            resultMap.put("result",AuditStatus.ToSubmit.toString());
            return resultMap;
        }
        TeacherApplication teacherApplication = list.get(0);

        //【待提交】当前状态与流程状态不一样,以lifeCycle为准
        if(!StringUtils.equalsIgnoreCase(teacherApplication.getStatus(), teacher.getLifeCycle())){
            resultMap.put("result",AuditStatus.ToSubmit.toString());
            return resultMap;
        }

        Map<String,Object> _result = null;
        //BASIC_INFO 11.5小时之内如果状态是FAIL 为待审核
        if(StringUtils.equalsIgnoreCase(TeacherApplicationEnum.Status.BASIC_INFO.toString(),teacherApplication.getStatus())){
            _result = getBasicInfoStatus(teacher, teacherApplication);
        //INTERVIEW 待审核 待约课
        }else if(StringUtils.equalsIgnoreCase(TeacherApplicationEnum.Status.INTERVIEW.toString(),teacherApplication.getStatus())){
            _result = getInterviewStatus(teacher, teacherApplication);
        //待审核
        }else if(StringUtils.equalsIgnoreCase(TeacherApplicationEnum.Status.PRACTICUM.toString(),teacherApplication.getStatus())){
            _result = getPracticumStatus(teacher, teacherApplication);
        }
        if(_result != null){
            resultMap.putAll(_result);
            return resultMap;
        }
        //其他情况 待经审核
        if(StringUtils.isBlank(teacherApplication.getResult())){
            resultMap.put("result",AuditStatus.ToAudit.toString());
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
        if(StringUtils.equalsIgnoreCase(TeacherApplicationEnum.Result.FAIL.toString(),teacherApplication.getResult())){
            if(!DateUtils.count11hrlf(teacherApplication.getAuditDateTime().getTime())){
                result.put("result",AuditStatus.ToAudit.toString());
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
                result.put("result",AuditStatus.ToSubmit.toString());
                return result;
            }else{
                //倒计时
                OnlineClass onlineClass = this.onlineClassDao.findById(teacherApplication.getOnlineClassId());
                //处于book状态的onlineClass 应该处于倒计时页面
                if(OnlineClassEnum.ClassStatus.BOOKED.toString().equals(onlineClass.getStatus())){
                    result.put("serverTime",System.currentTimeMillis());
                    result.put("scheduledDateTime",onlineClass.getScheduledDateTime().getTime());
                    result.put("onlineClassId", onlineClass.getId());
                    //小于1个小时 可进入onlineClass
                    if(!DateUtils.count1h(onlineClass.getScheduledDateTime().getTime())){
                        result.put("result",AuditStatus.GoToClass.toString());
                        return result;
                    //小于54周 处于审核中
                    }else if(!DateUtils.count54week(onlineClass.getScheduledDateTime().getTime())){
                        result.put("result",AuditStatus.ToAudit.toString());
                        return result;
                    //大于54周超时
                    }else{
                        result.put("result",AuditStatus.HasTimeOut.toString());
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
        Map<String,Object> result = this.getInterviewStatus( teacher, teacherApplication);
        if(TeacherApplicationEnum.Result.PRACTICUM2.toString().equals(teacherApplication.getResult())){
            result.put("result",AuditStatus.ToSubmit.toString());
        }
        List<TeacherApplication> list = teacherApplicationDao.findApplictionForStatusResult(teacher.getId(), null, TeacherApplicationEnum.Result.PRACTICUM2.name());
        if(CollectionUtils.isNotEmpty(list)){
            result.put("practicumNo", 2);
        } else {
            result.put("practicumNo", 1);
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
     * @param teacherAddressId
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
     * 更新timezone
     * @param bean
     * @param teacher
     * @return
     * boolean
     */
    public boolean updateTimezone(TimezoneDto bean,Teacher teacher){
        teacher.setTimezone(bean.getTimezone());
        //2.更新Address
        TeacherAddress teacherAddress = this.teacherAddressDao.updateOrSaveCurrentAddressId(teacher, bean.getCountryId(), bean.getStateId(), bean.getCityId(),null,null);
        if(teacherAddress == null || teacherAddress.getId() <= 0){
            logger.error("老师:{},地址信息:{},保存有问题.",teacher.getId(),JsonTools.getJson(teacherAddress));
            return false;
        }
        this.teacherDao.update(teacher);
        return true;
    }


    public int getInterviewRemainRescheduleTimes(int teacherId, String type){
        //type: cancelNum, CancelNoShow, ITProblem
        int remainTimes = 0;
        int lockTimes = getInterviewLockTimes(teacherId);
        int reapplyTimesByITProblem = getInterviewReapplyTimesByITProblem(teacherId);
        int reapplyTimesByCancelNoShow = getInterviewReapplyTimesByCancelNoShow(teacherId);
        int cancelNum = 0;//getCancelNum(teacherId);
        if (lockTimes > 0){
            if(reapplyTimesByITProblem + reapplyTimesByCancelNoShow + cancelNum <= 5 + lockTimes){
                remainTimes = 1;
            }
        } else {
            if (FinishType.STUDENT_CANCELLATION.equals(type) || FinishType.STUDENT_NO_SHOW.equals(type) || Result.CANCEL.toString().equals(type)){
                remainTimes = 3 - reapplyTimesByCancelNoShow - cancelNum;
            } else if (FinishType.STUDENT_IT_PROBLEM.equals(type)){
                remainTimes = 2 - reapplyTimesByITProblem;
            }
        }
        return remainTimes;
    }

    private int getInterviewLockTimes(int teacherId){
        return 0;
    }

    private int getInterviewReapplyTimesByCancelNoShow(int teacherId){
        List<String> finishTypes = Arrays.asList(FinishType.STUDENT_CANCELLATION, FinishType.STUDENT_NO_SHOW);
        return teacherApplicationDao.countByTeacherIdStatusFinishType(teacherId, Status.INTERVIEW.toString(), finishTypes);
    }

    private int getInterviewReapplyTimesByITProblem(int teacherId){
        List<String> finishTypes = Arrays.asList(FinishType.STUDENT_IT_PROBLEM);
        return teacherApplicationDao.countByTeacherIdStatusFinishType(teacherId, Status.INTERVIEW.toString(), finishTypes);
    }


    public int getPracticumRemainRescheduleTimes(int teacherId, String type){
        //type: cancelNum, CancelNoShow, ITProblem
        int remainTimes = 0;
        int lockTimes = getPracticumLockTimes(teacherId);
        int reapplyTimesByITProblem = getPracticumReapplyTimesByITProblem(teacherId);
        int reapplyTimesByCancelNoShow = getPracticumReapplyTimesByCancelNoShow(teacherId);
        int cancelNum = 0;//getCancelNum(teacherId);
        if (lockTimes > 0){
            if(reapplyTimesByITProblem + reapplyTimesByCancelNoShow + cancelNum <= 5 + lockTimes){
                remainTimes = 1;
            }
        } else {
            if (FinishType.STUDENT_CANCELLATION.equals(type) || FinishType.STUDENT_NO_SHOW.equals(type) || Result.CANCEL.toString().equals(type)){
                remainTimes = 3 - reapplyTimesByCancelNoShow - cancelNum;
            } else if (FinishType.STUDENT_IT_PROBLEM.equals(type)){
                remainTimes = 2 - reapplyTimesByITProblem;
            }
        }
        return remainTimes;
    }

    private int getPracticumLockTimes(int teacherId){
        return 0;
    }

    private int getPracticumReapplyTimesByCancelNoShow(int teacherId){
        List<String> finishTypes = Arrays.asList(FinishType.STUDENT_CANCELLATION, FinishType.STUDENT_NO_SHOW);
        return teacherApplicationDao.countByTeacherIdStatusFinishType(teacherId, Status.PRACTICUM.toString(), finishTypes);
    }

    private int getPracticumReapplyTimesByITProblem(int teacherId){
        List<String> finishTypes = Arrays.asList(FinishType.STUDENT_IT_PROBLEM);
        return teacherApplicationDao.countByTeacherIdStatusFinishType(teacherId, Status.PRACTICUM.toString(), finishTypes);
    }
}
