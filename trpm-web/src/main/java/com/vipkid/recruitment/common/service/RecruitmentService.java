package com.vipkid.recruitment.common.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.trpm.dao.TeacherApplicationDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherApplication;
import com.vipkid.trpm.util.DateUtils;

@Service
public class RecruitmentService {
    
    @Autowired
    private TeacherDao teacherDao;
    
    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

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
}
