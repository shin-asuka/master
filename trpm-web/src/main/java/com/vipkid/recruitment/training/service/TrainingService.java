package com.vipkid.recruitment.training.service;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Teacher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingService {
    
    private static Logger logger = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    private TeacherDao teacherDao;
    private TeacherApplicationDao teacherApplicationDao;
    /**
     * Next --> 更新步骤<br/>
     * @Author:ALong
     * @param teacher
     * @return 2015年10月14日
     */
    public Teacher toPracticum(Teacher teacher){
        // 如果当前为第4步 则状态变更为第5步骤，否则不做任何变更
        teacher = teacherDao.findById(teacher.getId());
        if(TeacherEnum.LifeCycle.TRAINING.toString().equals(teacher.getLifeCycle())){
            teacher.setLifeCycle(TeacherEnum.LifeCycle.PRACTICUM.toString());
            this.teacherDao.update(teacher);
        }
        return teacher;
    }

    /**
     * 查询  该教师 Current = 1 的步骤记录<br/>
     * @Author:ALong
     * @param teacherId
     * @return 2015年10月13日
     */
    public TeacherApplication findAppliction(long teacherId){
        //List<TeacherApplication> list = teacherApplicationDao.findByTeacherId(teacherId,TeacherApplicationEnum.Status.TRAINING.toString());
        List<TeacherApplication> list = teacherApplicationDao.findCurrentApplication(teacherId);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

}
