package com.vipkid.recruitment.contractinfo.service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherTaxpayerFormDao;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    @Autowired
    private TeacherTaxpayerFormDao teacherTaxpayerFormDao;

    @Autowired
    private TeacherAddressDao teacherAddressDao;

    /**
     * 更新teacher表
     *
     * @param teacher
     * @return
     */
    public int updateTeacher(Teacher teacher) {
        return teacherDao.update(teacher);
    }

    /**
     * 更新Teacher表中的DiplomaUrl
     *
     * @param teacher
     * @return
     */
    public Map<String, Object> updateDiplomaUrl(Teacher teacher) {
        Teacher t = teacherDao.findById(teacher.getId());
        if (t.getBachelorDiploma().length() < 1) {
            return ResponseUtils.responseFail("Your DiplomaUrl file is Empty . !", this);
        }
        this.teacherDao.update(teacher);
        return ResponseUtils.responseSuccess();
    }

    /**
     * 更新Teacher表中的ContractUrl
     *
     * @param teacher
     * @return
     */
    public Map<String, Object> updateContract(Teacher teacher) {
        Teacher t = teacherDao.findById(teacher.getId());
        if (t.getContract().length() < 1) {
            return ResponseUtils.responseFail("Your ContractUrl file is Empty . !", this);
        }
        this.teacherDao.update(teacher);
        return ResponseUtils.responseSuccess();
    }

    /**
     * 更新Teacher表中的IdentificationUrl
     *
     * @param teacher
     * @return
     */
    public Map<String, Object> updateIdentification(Teacher teacher) {
        Teacher t = teacherDao.findById(teacher.getId());
        if (t.getPassport().length() < 1) {
            return ResponseUtils.responseFail("Your IdentificationUrl file is Empty . !", this);
        }
        this.teacherDao.update(teacher);
        return ResponseUtils.responseSuccess();
    }


    /**
     * 在TeacherApplication中加入一条带审核数据
     *
     * @param teacher
     * @return
     */
    public boolean updateTeacherApplication(Teacher teacher, List<Integer> ids) {
        try{
            TeacherTaxpayerForm teacherTaxpayerForm = teacherTaxpayerFormDao.findByTeacherIdAndType(teacher.getId(), TeacherEnum.FormType.W9.val());
            if (teacherTaxpayerForm == null) {
                if (teacher.getCountry().equals("USA")) {
                    logger.warn("{} teacher's country is USA but W9 file is not uploaded!", teacher.getId());
                    return false;
                } else {
                    //查询教师的Location id
                    TeacherAddress teacherAddress = teacherAddressDao.findById(teacher.getCurrentAddressId());
                    //  2497273 = 老师location 为   United States
                    if (teacherAddress != null && teacherAddress.getCountryId() == 2497273) {
                        logger.warn("{} teacher's address's country id is 2497273 (USA) but W9 file is not uploaded!", teacher.getId());
                        return false;
                    }
                }
            }

            List<TeacherApplication> list = teacherApplicationDao.findCurrentApplication(teacher.getId());
            if (CollectionUtils.isNotEmpty(list)) {
                for (int i = 0; i < list.size(); i++) {
                    TeacherApplication application = list.get(i);
                    application.setCurrent(0);
                    teacherApplicationDao.update(application);
                }
            }

            TeacherApplication application = new TeacherApplication();
            application.setTeacherId(teacher.getId());//  步骤关联的教师
            application.setApplyDateTime(new Timestamp(System.currentTimeMillis()));
            application.setStatus(TeacherApplicationEnum.Status.CONTRACT_INFO.toString());
            application = teacherApplicationDao.initApplicationData(application);
            int ret = teacherApplicationDao.save(application);
            logger.info("update teacherApplication for teacherId:{} with result: {}", teacher.getId(), ret);
            if(ret <= 0) {
                logger.warn("failed to save teacherApplication!");
                return false;
            }

            TeacherOtherDegrees teacherOtherDegrees;
            List<TeacherOtherDegrees> otherDegrees = new ArrayList<TeacherOtherDegrees>();
            for (Integer id : ids) {
                //TODO: change the table name and Dao name
                teacherOtherDegrees = teacherOtherDegreesDao.findById(id);
                teacherOtherDegrees.setTeacherApplicationId(application.getId());
                logger.info("applicationId:{}", application.getId());
                otherDegrees.add(teacherOtherDegrees);
            }

            logger.info("批量更新文件 for teacherId:{} with  teacherApplicationId:{}", teacher.getId(), application.getId());
            teacherOtherDegreesDao.updateBatch(otherDegrees);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }


    /**
     * 插入teacherOtherDegrees里的文件
     *
     * @param teacherOtherDegrees
     * @return
     */
    public int save(TeacherOtherDegrees teacherOtherDegrees) {
        return teacherOtherDegreesDao.save(teacherOtherDegrees);
    }

    /**
     * 删除teacherOtherDegrees里的文件
     *
     * @param teacherOtherDegrees
     * @return
     */
    public int delete(TeacherOtherDegrees teacherOtherDegrees) {
        return teacherOtherDegreesDao.delete(teacherOtherDegrees);
    }

    public Map<String, Object> reomteFile(int fileId, Teacher teacher) {
        TeacherOtherDegrees teacherOtherDegrees = teacherOtherDegreesDao.findById(fileId);
        if (teacherOtherDegrees.getTeacherId() != teacher.getId()) {
            return ResponseUtils.responseFail("You can't delete the file !", this);
        } else {
            if (teacherOtherDegrees.getTeacherApplicationId() == 0) {
                teacherOtherDegreesDao.delete(teacherOtherDegrees);
                return ResponseUtils.responseSuccess();
            } else {
                return ResponseUtils.responseSuccess();
            }
        }
    }

    /**
     * 查询老师上传过的文件
     *
     * @param t
     * @return
     */
    public Map<String, ContractFile> findContract(Teacher t) {
        ContractFile contractFile = new ContractFile();
        Teacher teacher = teacherDao.findById(t.getId());
        logger.info("用户：{}查询上传文件", teacher.getId());

        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        logger.info("用户：{}查询TeacherApplication", teacher.getId());
        List<TeacherOtherDegrees> teacherOtherDegrees = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(listEntity)) {
            TeacherApplication teacherApplication = listEntity.get(0);
            teacherOtherDegrees = teacherOtherDegreesDao.findByTeacherIdAndTeacherApplicationId(teacher.getId(), teacherApplication.getId());
        }
        if (CollectionUtils.isEmpty(teacherOtherDegrees)) {
            logger.info("用户{}查询未提交的文件", teacher.getId());
            teacherOtherDegrees = teacherOtherDegreesDao.findByTeacherId(teacher.getId());
        }
        Map<String, ContractFile> map = Maps.newHashMap();

        if (CollectionUtils.isNotEmpty(teacherOtherDegrees)) {
            List<TeacherOtherDegrees> degrees = Lists.newArrayList();
            List<TeacherOtherDegrees> contract = Lists.newArrayList();
            List<String> res = Lists.newArrayList();
            List<TeacherOtherDegrees> identification = Lists.newArrayList();
            List<TeacherOtherDegrees> diploma = Lists.newArrayList();
            List<TeacherOtherDegrees> tax = Lists.newArrayList();
            List<TeacherOtherDegrees> certification = Lists.newArrayList();
            teacherOtherDegrees.forEach(obj -> {
                if (obj.getFileType() == 1) {
                    degrees.add(obj);
                }
                if (obj.getFileType() == 2) {
                    certification.add(obj);
                }
                if (obj.getFileType() == 3) {
                    obj.setTypeName("identity");
                    identification.add(obj);
                }
                if (obj.getFileType() == 6) {
                    obj.setTypeName("passport");
                    identification.add(obj);

                }
                if (obj.getFileType() == 7) {
                    obj.setTypeName("driver");
                    identification.add(obj);
                }
                if (obj.getFileType() == 4) {
                    diploma.add(obj);
                }
                if (obj.getFileType() == 5) {
                    contract.add(obj);
                }
                if (obj.getFileType() == 8) {
                    tax.add(obj);
                }
                if (obj.getResult() != null && obj.getResult().equals("")) {
                    res.add(obj.getResult());
                }

            });
            if (CollectionUtils.isNotEmpty(tax)) {
                contractFile.setTax(tax.get(tax.size() - 1));
            }

            if (CollectionUtils.isNotEmpty(contract)) {
                contractFile.setContract(contract.get(contract.size() - 1));
            }
            if (CollectionUtils.isNotEmpty(diploma)) {
                contractFile.setDiploma(diploma.get(diploma.size() - 1));
            }
            if (CollectionUtils.isNotEmpty(identification)) {
                contractFile.setIdentification(identification.get(identification.size() - 1));
            }
            String result = isPass(res);
            contractFile.setCertification(certification);
            contractFile.setDegrees(degrees);
            map.put(result, contractFile);
        }
        return map;

    }

    public String isPass(List<String> res) {
        if (CollectionUtils.isEmpty(res)) {
            return String.valueOf(TeacherApplicationEnum.Result.FAIL);
        }

        for (String result : res) {
            if (result.equals("FAIL")) {
                return String.valueOf(TeacherApplicationEnum.Result.FAIL);
            }
        }
        return String.valueOf(TeacherApplicationEnum.Result.PASS);
    }


    public List<TeacherOtherDegrees> findTeacherOtherDegrees(long teacherId) {
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacherId);
        logger.info("用户：{}查询TeacherApplication", teacherId);
        List<TeacherOtherDegrees> teacherOtherDegrees = Lists.newArrayList();
        //TODO zhaojun
        if (CollectionUtils.isNotEmpty(listEntity)) {
            TeacherApplication teacherApplication = listEntity.get(0);
            teacherOtherDegrees = teacherOtherDegreesDao.findByTeacherIdAndTeacherApplicationId(teacherId, teacherApplication.getId());
        }
        if (CollectionUtils.isEmpty(teacherOtherDegrees)) {
            logger.info("用户{}查询未提交的文件", teacherId);
            teacherOtherDegrees = teacherOtherDegreesDao.findByTeacherId(teacherId);
        }
        return teacherOtherDegrees;

    }
}
