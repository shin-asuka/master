package com.vipkid.trpm.dao;

import com.google.common.base.Preconditions;
import com.vipkid.trpm.entity.TeacherPeLevels;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liuguowen on 2017/1/16.
 */
@Repository
public class TeacherPeLevelsDao extends MapperDaoTemplate<TeacherPeLevels> {

    @Autowired
    public TeacherPeLevelsDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherPeLevels.class);
    }

    public int deleteTeacherPeLevels(int applicationId) {
        Preconditions.checkArgument(0 != applicationId);
        return super.delete(new TeacherPeLevels().setApplicationId(applicationId), "deleteByApplicationId");
    }

    public void saveTeacherPeLevels(List<TeacherPeLevels> teacherPeLevels){
        super.saveBatch(teacherPeLevels);
    }

    public List<TeacherPeLevels> getTeacherPeLevelsByApplicationId(int applicationId){
        Preconditions.checkArgument(0 != applicationId);
        return super.selectList(new TeacherPeLevels().setApplicationId(applicationId));
    }

}
