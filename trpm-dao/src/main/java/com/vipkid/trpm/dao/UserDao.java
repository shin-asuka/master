package com.vipkid.trpm.dao;

import org.apache.commons.lang3.StringUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * @param userId
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

	public List<User> findTeachersByRegisterTimes(List<Map> registerTimes) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("registerTimes", registerTimes);
		return listEntity("findTeacherUsersByRegisterTimes", paramsMap);
	}
	public int doLock(long userId) {
		return update(new User().setId(userId), "doLockUser");
	}


	public int findUserShowNumber(String showName){
		if(StringUtils.isBlank(showName)){
			return -1;
		}
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("showName",showName);
		return selectEntity("findUserShowNumber",paramsMap);
	}
}
