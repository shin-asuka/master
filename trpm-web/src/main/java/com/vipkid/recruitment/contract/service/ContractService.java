package com.vipkid.recruitment.contract.service;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.recruitment.dao.TeacherOtherDegreesDao;
import com.vipkid.recruitment.entity.ContractFile;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.recruitment.entity.TeacherOtherDegrees;

/**
 * Created by zhangzhaojun on 2016/11/14.
 */
@Service
public class ContractService {
    private static Logger logger = LoggerFactory.getLogger(ContractService.class);
    @Autowired
    private TeacherOtherDegreesDao teacherOtherDegreesDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    /**
     * 1.更新教师信息<br/>
     * 2.更新TeacherApplication信息<br/>
     * 3.新增一条审核信息TeacherApplication并审核
     * @param teacher
     */
    @Transactional
    public int  updateTeacher(Teacher teacher){
        return this.teacherDao.update(teacher);



        //  1.更新教师
        /*Teacher pageTeacher = teacherDao.findById(teacherId);
        logger.info("根据用户：{}的ID查询用户的详细信息",teacherId);
        pageTeacher.setHighestLevelOfEdu(contractFile.getDiploma());
        pageTeacher.setCertificates(contractFile.getCertification());
        pageTeacher.setContract(contractFile.getContract());
        pageTeacher.setPassport(contractFile.getIdentification());
        logger.info("更新用户：{}合同等文件URL",teacherId);
        this.teacherDao.update(pageTeacher);


        TeacherOtherDegrees teacherOtherDegrees  = new TeacherOtherDegrees();
        logger.info("保存用户：{}上传其他文件URL",teacherId);
        teacherOtherDegrees.setDegrees(contractFile.getDegrees());
        teacherOtherDegrees.setTeacherId(teacherId);
        teacherOtherDegreesDao.save(teacherOtherDegrees);

        //  2.更新TeacherApplication表Current = 0,  新增TeacherApplication表 数据Current = 1
        List<TeacherApplication> list = teacherApplicationDao.findCurrentApplication(teacherId);
        for (int i = 0; i < list.size(); i++) {
            TeacherApplication application = list.get(i);
            System.out.println(application.getId());
            application.setCurrent(0);
            this.teacherApplicationDao.update(application);
        }
        TeacherApplication application = new TeacherApplication();
        application.setTeacherId(teacherId);//  步骤关联的教师
        application.setApplyDateTime(new Timestamp(System.currentTimeMillis()));
        application.setStatus(TeacherApplicationEnum.Status.SIGN_CONTRACT.toString());
        application = teacherApplicationDao.initApplicationData(application);
        this.teacherApplicationDao.save(application);
        logger.info("用户：{}，update table TeacherApplication Column Current = 0,  add table TeacherApplication row Current = 1",teacherId);*/


      //  return pageTeacher;
    }


    public Teacher toSendDocs(Teacher teacher){
        // 如果当前为第4步 则状态变更为第5步骤，否则不做任何变更
        teacher = teacherDao.findById(teacher.getId());
        if(TeacherEnum.LifeCycle.CONTRACT.toString().equals(teacher.getLifeCycle())){
            logger.info("用户{}转变到SENT_DOCS",teacher.getId());
            teacher.setLifeCycle(TeacherEnum.LifeCycle.SENT_DOCS.toString());
            //SENT_DOCS or SEND_DOCS ???
            //this.teacherDao.insertLifeCycleLog(teacher.getId(), TeacherLifeCycle.CONTRACT, TeacherLifeCycle.SENT_DOCS, teacher.getId());
            this.teacherDao.update(teacher);
        }
        return teacher;
    }


}
