package com.vipkid.trpm.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.TeacherPageLogin;

@Repository
public class TeacherPageLoginDao extends MapperDaoTemplate<TeacherPageLogin> {

    private static Logger logger = LoggerFactory.getLogger(TeacherPageLoginDao.class);
    
	@Autowired
	public TeacherPageLoginDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, TeacherPageLogin.class);
	}

	public TeacherPageLogin findByUserIdAndLoginType(long userId, int loginType) {
		return selectOne(new TeacherPageLogin().setUserId(userId).setLoginType(loginType));
	}
	
	public TeacherPageLogin findByUserId(long userId) {
        return selectOne(new TeacherPageLogin().setUserId(userId));
    }

	public int saveTeacherPageLogin(TeacherPageLogin teacherPageLogin) {
	    List<TeacherPageLogin> list = this.selectList(teacherPageLogin);
	    if(CollectionUtils.isNotEmpty(list)){
	        logger.warn("老师:{},已经点击过loginType:{}",teacherPageLogin.getUserId(),teacherPageLogin.getLoginType());
	        return 1;
	    }
	    return super.save(teacherPageLogin);
	}

}
