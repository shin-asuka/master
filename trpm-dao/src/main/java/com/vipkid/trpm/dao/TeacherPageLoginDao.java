package com.vipkid.trpm.dao;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.TeacherPageLogin;

@Repository
public class TeacherPageLoginDao extends MapperDaoTemplate<TeacherPageLogin> {

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
		return super.save(teacherPageLogin);
	}

}
