package com.vipkid.trpm.dao;

import com.google.common.base.Preconditions;
import com.vipkid.trpm.entity.TeacherPeResult;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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

}
