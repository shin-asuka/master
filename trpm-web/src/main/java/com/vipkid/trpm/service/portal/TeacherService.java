package com.vipkid.trpm.service.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;

/**
 * @author zouqinghua
 * @date 2016年8月4日  下午3:10:43
 *
 */
@Service
public class TeacherService {

	@Autowired
    private UserDao userDao;

    @Autowired
    private TeacherDao teacherDao;
    
    /**
     * 通过teacherId获取教师信息
     * @param id
     * @return
     */
	public Teacher get(Long id){
		Teacher teacher = null;
		if(id!=null){
			teacher = teacherDao.findById(id);
		}
		return teacher;
	}
	
	
	
}
