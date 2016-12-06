package com.vipkid.recruitment.contractinfo.service;

import java.util.List;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Teacher;

/**
 * @author Austin.Cao  Date: 19/11/2016
 */
@Service
public class ContractInfoService {

    Logger logger = LoggerFactory.getLogger(ContractInfoService.class);

    @Resource
    private TeacherDao teacherDao;

    @Resource
    private TeacherApplicationDao teacherApplicationDao;

    public boolean updateTeacher(Teacher teacher) {

        if(teacher == null || teacher.getId() <= 0) {
            logger.error("Failed to update teacher {}", teacher);
            return false;
        }

        int effected = teacherDao.update(teacher);
        if(effected > 0) {
            logger.error("Successful to update teacher {}", teacher);
            return true;
        }

        return false;
    }

    public boolean toRegular(Teacher teacher){
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isEmpty(listEntity)){
            logger.error("teacherApplication list is empty, can NOT get into REGULAR !");
            return false;
        }
        if(TeacherApplicationEnum.Status.CONTRACT_INFO.toString().equals(listEntity.get(0).getStatus())
                && TeacherApplicationEnum.Result.PASS.toString().equals(listEntity.get(0).getResult())){


            teacher.setLifeCycle(TeacherEnum.LifeCycle.REGULAR.toString());
            this.teacherDao.insertLifeCycleLog(teacher.getId(), TeacherEnum.LifeCycle.CONTRACT_INFO, TeacherEnum.LifeCycle.REGULAR, teacher.getId());
            this.teacherDao.update(teacher);
            return true;
        }
        logger.error("current teacherApplication is not CONTRACT_INFO or not PASS, can NOT get into REGULAR !");
        return false;
    }


}
