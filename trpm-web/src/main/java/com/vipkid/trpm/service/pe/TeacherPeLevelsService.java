package com.vipkid.trpm.service.pe;

import com.vipkid.trpm.dao.TeacherPeLevelsDao;
import com.vipkid.trpm.entity.TeacherPeLevels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liuguowen on 2017/1/16.
 */
@Service
public class TeacherPeLevelsService {

    @Autowired
    private TeacherPeLevelsDao teacherPeLevelsDao;

    public void updateTeacherPeLevels(int applicationId, List<TeacherPeLevels> teacherPeLevels) {
        teacherPeLevelsDao.deleteTeacherPeLevels(applicationId);
        teacherPeLevelsDao.saveTeacherPeLevels(teacherPeLevels);
    }

}
