package com.vipkid.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.enums.TeacherPageLoginEnum.LoginType;
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
    public boolean isType(long teacerId,LoginType loginType){
        return this.teacherPageLoginDao.isType(teacerId, loginType.val());
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
    public boolean saveTeacherPageLogin(long teacherId,LoginType longType){
        TeacherPageLogin teacherPageLogin= new TeacherPageLogin();
        teacherPageLogin.setUserId(teacherId);
        teacherPageLogin.setLoginType(longType.val());
        return this.teacherPageLoginDao.saveTeacherPageLogin(teacherPageLogin) == 1 ? true : false;
    }
}
