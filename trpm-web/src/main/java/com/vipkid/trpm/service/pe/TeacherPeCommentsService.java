package com.vipkid.trpm.service.pe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.trpm.dao.TeacherPeCommentsDao;
import com.vipkid.trpm.entity.TeacherPeComments;

/**
 * Created by liuguowen on 2017/1/16.
 */
@Service
public class TeacherPeCommentsService {

    @Autowired
    private TeacherPeCommentsDao teacherPeCommentsDao;

    public void updateTeacherPeComments(int applicationId, TeacherPeComments teacherPeComments){
        teacherPeCommentsDao.deleteTeacherPeComments(applicationId);
        teacherPeCommentsDao.saveTeacherPeComments(teacherPeComments);
    }

}
