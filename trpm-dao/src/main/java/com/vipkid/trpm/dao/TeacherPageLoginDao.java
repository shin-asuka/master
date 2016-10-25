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

	/**
	 * 没有返回true，有返回false 
	 * @Author:ALong (ZengWeiLong)
	 * @param userId
	 * @param loginType
	 * @return    
	 * boolean
	 * @date 2016年10月25日
	 */
	public boolean isType(long userId, int loginType) {
		List<TeacherPageLogin> list = super.selectList(new TeacherPageLogin().setUserId(userId).setLoginType(loginType));
		if(CollectionUtils.isEmpty(list)){
    		return true;
		}
		return false;
	}
	
	/**
	 * 查询所有 
	 * @Author:ALong (ZengWeiLong)
	 * @param userId
	 * @return    
	 * List<TeacherPageLogin>
	 * @date 2016年10月25日
	 */
	public List<TeacherPageLogin> findList(long userId){
	    List<TeacherPageLogin> list = super.selectList(new TeacherPageLogin().setUserId(userId));
	    return list;
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
