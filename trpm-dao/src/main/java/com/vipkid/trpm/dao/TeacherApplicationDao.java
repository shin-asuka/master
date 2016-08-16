package com.vipkid.trpm.dao;

import java.sql.Timestamp;
import java.util.*;

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
        List<TeacherApplication> teacherApplications =
                super.selectList(new TeacherApplication(), map);
        return teacherApplications;
    }

    /**
     * 查找current 为 1 的 （当前步骤）
     *
     * @Author
     * @param teacherId
     * @return 2015年10月22日
     */
    public List<TeacherApplication> findApplictionNew(long teacherId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("teacherId", teacherId);
        map.put("current", 1);
        List<TeacherApplication> teacherApplications =
                super.selectList(new TeacherApplication(), map);
        return teacherApplications;
    }

    /**
     * @param teacher
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
