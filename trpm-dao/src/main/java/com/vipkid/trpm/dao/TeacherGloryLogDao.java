package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.TeacherGloryLog;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LP-813 on 2017/4/28.
 */
@Repository
public class TeacherGloryLogDao extends MapperDaoTemplate<TeacherGloryLog> {

    @Autowired
    public TeacherGloryLogDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherGloryLog.class);
    }

    public Integer saveLog(TeacherGloryLog teacherGloryLog){
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        return save(teacherGloryLog);
    }

}
