package com.vipkid.trpm.dao;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.User;

@Repository
public class UserDao extends MapperDaoTemplate<User> {

	@Autowired
	public UserDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, User.class);
	}

	public User findByUsername(String username) {
		return selectOne(new User().setUsername(username));
	}

	public User findById(long id) {
		return selectOne(new User().setId(id));
	}

	@Override
	public int update(User user) {
		return super.update(user);
	}

	@Override
	public int save(User user) {
		return super.save(user);
	}

	/**
	 * 更新新的密码
	 * 
	 * @param user
	 * @param newPassword
	 * @return
	 */
	public int updateWithNewPassword(long userId, String newPassword) {
		return update(new User().setId(userId).setPassword(newPassword), "updatePasswordDao");
	}

	@Override
	public User selectOne(User user) {
		return super.selectOne(user);
	}

	public User findByLogin(String username) {
		return selectOne(new User().setUsername(username), "userLoginDao");
	}

	public User findByToken(String token) {
		return selectOne(new User().setToken(token));
	}

}
