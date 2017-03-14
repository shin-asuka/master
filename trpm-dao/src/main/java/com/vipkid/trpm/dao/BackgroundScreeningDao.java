package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.BackgroundScreening;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;

/**
 * Created by luning on 2017/3/11.
 */
@Repository
public class BackgroundScreeningDao extends MapperDaoTemplate<BackgroundScreening> {
    @Autowired
    public BackgroundScreeningDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, BackgroundScreening.class);
    }

    public BackgroundScreening findByTeacherId(Long teacherId) {
        if (teacherId == 0) {
            return null;
        }
        BackgroundScreening backgroundScreening = new BackgroundScreening();
        backgroundScreening.setTeacherId(teacherId);

        return selectOne(backgroundScreening,"findByTeacherId");
    }

}
