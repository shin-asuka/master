package com.vipkid.recruitment.practicum.service;

import com.google.api.client.util.Maps;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.recruitment.dao.PracticumDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherApplicationDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherApplication;
import com.vipkid.trpm.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PracticumService {
    
    private static Logger logger = LoggerFactory.getLogger(PracticumService.class);
    @Autowired
    private PracticumDao practicumDao;
    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private OnlineClassDao onlineClassDao;

    public Map<String,Object> getStatus(long teacherId){
        Map<String,Object> resultMap = Maps.newHashMap();
        Teacher teacher = this.teacherDao.findById(teacherId);
        resultMap.put("lifeCycle",teacher.getLifeCycle());
        resultMap.put("result",TeacherApplicationDao.AuditStatus.ToSubmit.toString());
        List<TeacherApplication> list = this.teacherApplicationDao.findCurrentApplication(teacher.getId());

        //没有流程则视为待提交
        if(CollectionUtils.isEmpty(list)){
            resultMap.put("result",TeacherApplicationDao.AuditStatus.ToSubmit.toString());
            return resultMap;
        }
        TeacherApplication teacherApplication = list.get(0);

        //当前状态与流程状态不一样,以lifeCycle为准，为待提交
        if(!StringUtils.equalsIgnoreCase(teacherApplication.getStatus(), teacher.getLifeCycle())){
            resultMap.put("result",TeacherApplicationDao.AuditStatus.ToSubmit.toString());
            return resultMap;
        }

        //BASIC_INFO 11.5小时之内如果状态是FAIL 为待审核
        if(StringUtils.equalsIgnoreCase(TeacherApplicationEnum.Status.BASIC_INFO.toString(),teacherApplication.getStatus())){
            if(StringUtils.equalsIgnoreCase(TeacherApplicationEnum.Result.FAIL.toString(),teacherApplication.getResult())){
                Date auditDate = teacherApplication.getAuditDateTime();
                if(!DateUtils.count11hrlf(auditDate.getTime())){
                    resultMap.put("result",TeacherApplicationDao.AuditStatus.ToAudit.toString());
                    return resultMap;
                }
            }
        }

        //待审核
        if(StringUtils.isBlank(teacherApplication.getResult())){
            resultMap.put("result",TeacherApplicationDao.AuditStatus.ToAudit.toString());
            return resultMap;
        }
        //已经审核
        if(StringUtils.isNotBlank(teacherApplication.getResult())){
            resultMap.put("result",teacherApplication.getResult());
            return resultMap;
        }

        return resultMap;
    }


    public TeacherApplication getPracticumTeacherApplication(long teacherId) {
        List<TeacherApplication> list = teacherApplicationDao.findApplictionForStatus(teacherId, TeacherApplicationEnum.Status.TRAINING.name());
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    /**
     * 查询  该教师 Current = 1 的步骤记录<br/>
     * @Author:ALong
     * @param teacherId
     * @return 2015年10月13日
     */
    public TeacherApplication findAppliction(long teacherId){
        List<TeacherApplication> list = teacherApplicationDao.findCurrentApplication(teacherId);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据result状态来判断practicum的状态
     * @Author:ALong
     * @return
     * TeacherApplication
     * @date 2015年12月28日
     */
    public TeacherApplication findAppByPracticum2(long teacherId){
        List<TeacherApplication> list = teacherApplicationDao.findApplictionForStatusResult(teacherId, null, TeacherApplicationEnum.Result.PRACTICUM2.name());
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    public List<Map<String,Object>> findListByPracticum(){
        String fromTime = LocalDateTime.now().plusHours(1).format(DateUtils.FMT_YMD_HMS);
        String toTime = LocalDateTime.now().plusDays(2).withHour(23).withMinute(59).withSecond(59).format(DateUtils.FMT_YMD_HMS);
        logger.info("findListByPracticum parameter fromTime:{}, toTime:{}",fromTime, toTime);
        return practicumDao.findlistByPracticum(fromTime, toTime);
    }
}
