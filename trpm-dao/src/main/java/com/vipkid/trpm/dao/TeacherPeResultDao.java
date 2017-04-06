package com.vipkid.trpm.dao;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.TeacherPeResult;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TeacherPeResultDao extends MapperDaoTemplate<TeacherPeResult> {

    @Autowired
    public TeacherPeResultDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherPeResult.class);
    }

    public List<TeacherPeResult> listTeacherPeResult(Integer applicationId) {
        TeacherPeResult teacherPeResult = new TeacherPeResult();
        teacherPeResult.setApplicationId(applicationId);
        return super.selectList(teacherPeResult);
    }

    public int deleteTeacherPeResults(int applicationId) {
        Preconditions.checkArgument(0 != applicationId);
        TeacherPeResult teacherPeResult = new TeacherPeResult();
        teacherPeResult.setApplicationId(applicationId);
        return super.delete(teacherPeResult, "deleteByApplicationId");
    }

    public void saveTeacherPeResults(List<TeacherPeResult> teacherPeResults) {
        super.saveBatch(teacherPeResults);
    }

    public List<Map<String, Object>> getRubricResultTables(Integer rubricId, Long applicationId) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("rubricId", rubricId);
        paramMap.put("applicationId", applicationId);
        return super.listEntity("findRubricResultTables", paramMap);
    }

    public List<Map<String, Object>> getTBDRubricResultTables(Integer rubricId, Long applicationId) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("rubricId", rubricId);
        paramMap.put("applicationId", applicationId);
        return super.listEntity("findTBDRubricResultTables", paramMap);
    }

}
