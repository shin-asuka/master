package com.vipkid.trpm.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.TeacherApplication;

@Repository
public class TeacherApplicationDao extends MapperDaoTemplate<TeacherApplication> {

    @Autowired
    public TeacherApplicationDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherApplication.class);
    }

    public enum Status {
        SIGNUP, // 新申请
        BASIC_INFO, // 2015-08-08 添加basic-info 状态，从signup分离
        INTERVIEW, //面试
        SIGN_CONTRACT, //签合同
        TRAINING, // 教师培训
        PRACTICUM,//试讲 
        CANCELED, //已取消
        FINISHED // 已结束
        ;
        public static TeacherApplicationDao.Status prevStatus(Status status) {
            TeacherApplicationDao.Status [] arr = {Status.SIGNUP, Status.BASIC_INFO, Status.INTERVIEW, Status.SIGN_CONTRACT, Status.TRAINING, Status.PRACTICUM};
            for (int n = 1; n<arr.length; n++) {
                //
                if (status == arr[n]) {
                    return arr[n-1];
                }
            }            
            return TeacherApplicationDao.Status.SIGNUP;
        };
    }
    
    public enum Result {
        PASS, // 通过
        FAIL, // 失败
        REAPPLY, //重新申请,继续上PRACTICUM1（由于客观原因没能完成面试）
        PRACTICUM2, //第一次面试没通过，上PRACTICUM2
        TBD_FAIL,
        TBD
    }
    
    public enum AuditStatus {
        ToAudit, // 待审核
        ToSubmit // 带提交
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
     * 查找RESULT = PRACTICUM2 的teacherApplication即为第二阶段
     *
     * @Author:ALong
     * @param teacherId
     * @return 2015年10月22日
     */
    public List<TeacherApplication> findApplictionForPracticum2(long teacherId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("teacherId", teacherId);
        map.put("result", "PRACTICUM2");
        List<TeacherApplication> teacherApplications =
                super.selectList(new TeacherApplication(), map);
        return teacherApplications;
    }

    /**
     * 统计状态次数
     *
     * @param teacherId
     * @param status
     * @return 2015年10月22日
     */
    public List<TeacherApplication> countTeacherId(long teacherId, String status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("teacherId", teacherId);
        map.put("status", status);
        List<TeacherApplication> teacherApplications =super.selectList(new TeacherApplication(), map);
        return teacherApplications;
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
     * @param onlineclassid 通过onlinclass 查teacher application；
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

}
