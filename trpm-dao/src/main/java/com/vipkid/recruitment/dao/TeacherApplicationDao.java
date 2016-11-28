package com.vipkid.recruitment.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.recruitment.entity.TeacherApplication;

@Repository
public class TeacherApplicationDao extends MapperDaoTemplate<TeacherApplication> {

    @Autowired
    public TeacherApplicationDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherApplication.class);
    }
    
    /**
     * TeacherApplication 默认值设置<br/>
     * @Author:VIPKID-ZengWeiLong
     * @param application
     * @return 2015年10月12日
     */
    public TeacherApplication initApplicationData(TeacherApplication application){
        //  默认值设置
        application.setGrade6TeachingExperience(-1);
        application.setHighSchoolTeachingExperience(-1);
        application.setOnlineTeachingExperience(-1);
        application.setKidTeachingExperience(-1);
        application.setTeachingCertificate(-1);
        application.setAbroadTeachingExperience(-1);
        application.setHomeCountryTeachingExperience(-1);
        application.setKidUnder12TeachingExperience(-1);
        application.setTeenagerTeachingExperience(-1);
        application.setTeflOrToselCertificate(-1);
        application.setInteractionRapportScore(-1);
        application.setTeachingMethodScore(-1);
        application.setStudentOutputScore(-1);
        application.setPreparationPlanningScore(-1);
        application.setLessonObjectivesScore(-1);
        application.setTimeManagementScore(-1);
        application.setAppearanceScore(-1);
        application.setEnglishLanguageScore(-1);
        //  TODO
        application.setBasePay(0);
        application.setAccent(0);
        application.setPhonics(0);
        application.setPositive(0);
        application.setEngaged(0);
        application.setAppearance(0);
        application.setDelayDays(0);
        //  当前步骤标识
        application.setCurrent(1);
        return application;
    }
    
    /**
     * 查找状态为status 和current=1的数据记录
     *
     * @param teacherId
     * @param status
     * @return 2015年10月22日
     */
    public List<TeacherApplication> findByTeacherId(long teacherId, String status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("teacherId", teacherId);
        map.put("status", status);
        map.put("current", 1);
        List<TeacherApplication> teacherApplications =
                super.selectList(new TeacherApplication(), map);
        return teacherApplications;
    }

    /**
     * 根据状态查询 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @param status
     * @return    
     * List<TeacherApplication>
     * @date 2016年10月20日
     */
    public List<TeacherApplication> findApplictionForStatus(long teacherId,String status) {
       return findApplictionForStatusResult(teacherId, status, null);
    }
    
    /**
     * 根据状态结果查询
     * @Author:ALong
     * @param teacherId
     * @return 2015年10月22日
     */
    public List<TeacherApplication> findApplictionForStatusResult(long teacherId,String status,String result) {
        
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put("teacherId", teacherId);
        
        if(StringUtils.isNotBlank(status)){
            map.put("status", status);
        }
        if(StringUtils.isNotBlank(result)){
            map.put("result", result);
        }
        List<TeacherApplication> list = super.selectList(new TeacherApplication(), map);
        return list;
    }

    /**
     * 查找current 为 1 的 （当前步骤）
     *
     * @Author
     * @param teacherId
     * @return 2015年10月22日
     */
    public List<TeacherApplication> findCurrentApplication(long teacherId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("teacherId", teacherId);
        map.put("current", 1);
        List<TeacherApplication> teacherApplications = super.selectList(new TeacherApplication(), map);
        return teacherApplications;
    }

    /**
     * @param teacherApplication
     */
    @Override
    public int update(TeacherApplication teacherApplication) {
        teacherApplication.setAuditDateTime(new Timestamp(new Date().getTime()));
        return super.update(teacherApplication);
    }

    /**
     * @param auditorId 通过onlinclass 查teacher application；
     */
    public TeacherApplication findApplictionByOlineclassId(long onlineClassId, long auditorId) {
        TeacherApplication teacherApplication = new TeacherApplication();
        teacherApplication.setOrderString("id DESC");
        List<TeacherApplication> teacherApplications =
                super.selectList(teacherApplication.setOnlineClassId(onlineClassId));

        Optional<TeacherApplication> optional = teacherApplications.stream()
                .filter(ta -> ta.getAuditorId() == auditorId).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return teacherApplications.stream().findFirst().get();
        }
    }

    @Override
    public int save(TeacherApplication teacherApplication) {
        return super.save(teacherApplication);
    }

    public int countApplicationByOlineclassId(long onlineClassId) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("onlineClassId", onlineClassId);
        return selectCount("countApplicationByOlineclassId", paramsMap);
    }

    public TeacherApplication findApplictionById(long id) {
        return selectOne(new TeacherApplication().setId(id));
    }

    public List<Map<String, String>> findFailTeachersByAuditTime(String startTime, String endTime) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("startTime", startTime);
        paramsMap.put("endTime", endTime);
        return listEntity("findFailTeachersByAuditTime", paramsMap);
    }

    public List<TeacherApplication> findByAuditTimesStatusResult(List<Map> auditTimes, String status, String result) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("auditTimes", auditTimes);
        paramsMap.put("status", status);
        paramsMap.put("result", result);
        return listEntity("findTAByAuditTimesStatusResult", paramsMap);
    }

    public List<TeacherApplication> findByAuditTimesCurrentStatusResult(List<Map> auditTimes, String status, String result) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("auditTimes", auditTimes);
        paramsMap.put("status", status);
        paramsMap.put("result", result);
        return listEntity("findTAByAuditTimeCurrentStatusResult", paramsMap);
    }


    public List<TeacherApplication> findByTeacherIdsStatusNeResult(List<Long> teacherIds, String status, String result) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherIds", teacherIds);
        paramsMap.put("status", status);
        paramsMap.put("result", result);
        return listEntity("findTAByTeacherIdsStatusNeResult", paramsMap);
    }

    public int countByTeacherIdStatusFinishType(long teacherId, String status, List<String> finishTypes) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherId", teacherId);
        paramsMap.put("status", status);
        paramsMap.put("finishTypes", finishTypes);
        return selectCount("countReapplyTAByTeacherIdStatusFinishType", paramsMap);
    }
}
