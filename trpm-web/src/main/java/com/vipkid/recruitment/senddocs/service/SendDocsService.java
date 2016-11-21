package com.vipkid.recruitment.senddocs.service;

import javax.annotation.Resource;

import com.amazonaws.services.iot.model.S3Action;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    private FileService fileService;

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





}
