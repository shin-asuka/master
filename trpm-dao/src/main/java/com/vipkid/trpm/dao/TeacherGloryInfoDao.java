package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.TeacherGloryInfo;
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
public class TeacherGloryInfoDao extends MapperDaoTemplate<TeacherGloryInfo> {

    @Autowired
    public TeacherGloryInfoDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherGloryInfo.class);
    }

    public List<TeacherGloryInfo> getAll(){
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        return listEntity("findAll", paramsMap);
    }

}
