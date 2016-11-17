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
    /**
     * Next --> 更新步骤<br/>
     * @param teacher
     * @return
     */
    public Teacher toPracticum(Teacher teacher){
        // 如果当前为第4步 则状态变更为第5步骤，否则不做任何变更
        teacher = teacherDao.findById(teacher.getId());
        if(TeacherEnum.LifeCycle.TRAINING.toString().equals(teacher.getLifeCycle())){
            logger.info("用户{}转变到Practicum",teacher.getId());
            teacher.setLifeCycle(TeacherEnum.LifeCycle.PRACTICUM.toString());
            this.teacherDao.update(teacher);
        }
        return teacher;
    }

}
