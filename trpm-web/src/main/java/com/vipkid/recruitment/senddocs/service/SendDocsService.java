package com.vipkid.recruitment.senddocs.service;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import com.amazonaws.services.iot.model.S3Action;
import org.apache.commons.collections.CollectionUtils;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.service.portal.FileService;

/**
 * @author Austin.Cao  Date: 19/11/2016
 */
@Service
public class SendDocsService {

    Logger logger = LoggerFactory.getLogger(SendDocsService.class);

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

    public Map<String,Object> toRegular(Teacher teacher){
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isEmpty(listEntity)){
            return ResponseUtils.responseFail("You have no legal power into the next phase !", this);
        }
        if(TeacherApplicationEnum.Status.PUBLICITY_INFO.toString().equals(listEntity.get(0).getStatus())
                && TeacherApplicationEnum.Result.PASS.toString().equals(listEntity.get(0).getResult())){


            teacher.setLifeCycle(TeacherEnum.LifeCycle.REGULAR.toString());
            this.teacherDao.insertLifeCycleLog(teacher.getId(), TeacherEnum.LifeCycle.PUBLICITY_INFO, TeacherEnum.LifeCycle.REGULAR, teacher.getId());
            this.teacherDao.update(teacher);
            return ResponseUtils.responseSuccess();
        }
        return ResponseUtils.responseFail("You have no legal power into the next phase !",this);
    }



}
