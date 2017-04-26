package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.User;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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


	public int findUserCountByShowName(String showName){
		if(StringUtils.isBlank(showName)){
			return -1;
		}
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("showName",showName);
		return selectEntity("findUserCountByShowName",paramsMap);
	}

	public List<User> findUserShowNameAndIdList(int offset){

		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("offset",offset);
		return listEntity("findUserShowNameAndIdList",paramsMap);
	}

	public List<User> findUserNameListByIdList(List<String> userIds){
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("userIds",userIds);
		return listEntity("findUserNameListByIdList",paramsMap);
	}

	/* 查询 User 的总数量 */
	public int getCount(){
		return super.selectCount(new User());
	}

	public List<User> findAllShowNameDuplicateUsers(String lifeCycle, int startLine, int limitLine){
		if(null != lifeCycle){
			return super.selectLimit(new User(), "findAllShowNameDuplicateRegularUsers", startLine, limitLine);
		} else {
			return super.selectLimit(new User(), "findAllShowNameDuplicateOtherUsers", startLine, limitLine);
		}
	}

	public void updateBatch(List<User> userList){
		super.updateBatch(userList);
	}

	public List<User> findFullNameEqualsShowNameUsers(String lifeCycle, int startLine, int limitLine){
	 	if(null != lifeCycle){
			return super.selectLimit(new User(), "findFullNameEqualsShowNameRegularUsers", startLine, limitLine);
		} else {
		 	return super.selectLimit(new User(), "findFullNameEqualsShowNameOtherUsers", startLine, limitLine);
	 	}
	}


	public User findUserByOnlineClassId(long onlineClassId){
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("onlineClassId", onlineClassId);
		return super.selectOne("findUserByOnlineClassId", paramsMap);
	}

}
