package com.vipkid.trpm.service.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.trpm.dao.TeacherPageLoginDao;
import com.vipkid.trpm.entity.TeacherPageLogin;

@Service
public class TeacherPageLoginService {

    @Autowired
    private TeacherPageLoginDao teacherPageLoginDao;
    
    /**
     * 没有返回true，有返回false
     * @Author:ALong (ZengWeiLong)
     * @param teacerId
     * @return    
     * TeacherPageLogin
     * @date 2016年8月25日
     */
    public boolean isType(long teacerId,int loginType){
        return this.teacherPageLoginDao.isType(teacerId, loginType);
    }
    
    /**
     * saveTeacherPageLogin
     *  
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @param longType
     * @return int
     * @date 2016年10月26日
     */
    public boolean saveTeacherPageLogin(long teacherId,int longType){
        TeacherPageLogin teacherPageLogin= new TeacherPageLogin();
        teacherPageLogin.setUserId(teacherId);
        teacherPageLogin.setLoginType(longType);
        return this.teacherPageLoginDao.saveTeacherPageLogin(teacherPageLogin) == 1 ? true : false;
    }
}
