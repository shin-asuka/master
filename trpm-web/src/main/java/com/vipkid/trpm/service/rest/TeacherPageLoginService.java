package com.vipkid.trpm.service.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.trpm.dao.TeacherPageLoginDao;
import com.vipkid.trpm.entity.TeacherPageLogin;

@Service
public class TeacherPageLoginService {

    @Autowired
    private TeacherPageLoginDao teacherPageLoginDao;
    
    /**
     * is first quiz 
     * @Author:ALong (ZengWeiLong)
     * @param teacerId
     * @return    
     * TeacherPageLogin
     * @date 2016年8月25日
     */
    public boolean isType(long teacerId,int loginType){
        return this.teacherPageLoginDao.isType(teacerId, loginType);
    }
    
    
    public List<TeacherPageLogin> findList(long teacherId){
        return this.teacherPageLoginDao.findList(teacherId);
    }
}
