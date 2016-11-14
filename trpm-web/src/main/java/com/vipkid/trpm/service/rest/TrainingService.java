package com.vipkid.trpm.service.rest;

import com.vipkid.trpm.dao.TeacherApplicationDao;
import com.vipkid.trpm.entity.TeacherApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhangzhaojun on 2016/11/14.
 */
@Service
public class TrainingService {

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    /**
     * 查询  该教师 Current = 1 的步骤记录<br/>
     * @Author:ALong
     * @param teacherId
     * @return 2015年10月13日
     */
    public TeacherApplication findAppliction(long teacherId){
        //List<TeacherApplication> list = teacherApplicationDao.findByTeacherId(teacherId,TeacherApplicationEnum.Status.TRAINING.toString());
        List<TeacherApplication> list = teacherApplicationDao.findApplictionNew(teacherId);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }
}
