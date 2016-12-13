package com.vipkid.trpm.dao;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.TeacherToken;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/12/13 下午12:32
 */
@Repository
public class TeacherTokenDao extends MapperDaoTemplate<TeacherToken> {

    @Autowired
    public TeacherTokenDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherToken.class);
    }


    /**
     * 通过teacherId查询token
     *
     * @param teacherId
     * @return TeacherToken
     */
    public TeacherToken findByTeacherId(long teacherId) {
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", teacherId);

        return selectOne("findByTeacherId", paramsMap);
    }

    /**
     * 通过token查询teacherId
     *
     * @param token
     * @return TeacherToken
     */
    public TeacherToken findByToken(String token) {
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("token", token);

        return selectOne("findByToken", paramsMap);
    }
}
