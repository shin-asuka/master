package com.vipkid.recruitment.contractinfo.service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vipkid.recruitment.dao.TeacherContractFileDao;
import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherTaxpayerFormDao;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.ContractFile;
import com.vipkid.trpm.entity.Teacher;

/**
 * @author by zhangzhaojun on 2016/11/14.
 */
@Service
public class ContractInfoService {
    private static Logger logger = LoggerFactory.getLogger(ContractInfoService.class);
    @Autowired
    private TeacherContractFileDao teacherContractFileDao;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    @Autowired
    private TeacherTaxpayerFormDao teacherTaxpayerFormDao;

    @Autowired
    private TeacherAddressDao teacherAddressDao;

    @Autowired
    private TeacherDao teacherDao;

    private static int TEACHERAPPLICATION_ID=0;
    /**
     * 在TeacherApplication中加入一条带审核数据
     * @return boolean
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

            String failReason= "";
            List<TeacherApplication> list = teacherApplicationDao.findCurrentApplication(teacher.getId());
            if (CollectionUtils.isNotEmpty(list)) {
                    TeacherApplication application = list.get(0);
                    application.setCurrent(0);
                    teacherApplicationDao.update(application);
                //将文件的FileReason 的reslut Fail  的置为空，重新为管理端审核
                    failReason = application.getFailedReason().replaceAll("\"result\":\"FAIL\"","\"result\":\"\"");

            }
            TeacherApplication application = new TeacherApplication();
            if(StringUtils.isNotEmpty(failReason)){
                application.setFailedReason(failReason);
            }
            application.setTeacherId(teacher.getId());//  步骤关联的教师
            application.setApplyDateTime(new Timestamp(System.currentTimeMillis()));

            application.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
            application.setAuditorId(RestfulConfig.SYSTEM_USER_ID);

            application.setStatus(TeacherApplicationEnum.Status.CONTRACT_INFO.toString());
            application = teacherApplicationDao.initApplicationData(application);
            int ret = teacherApplicationDao.save(application);
            logger.info("update teacherApplication for teacherId:{} with result: {}", teacher.getId(), ret);
            if(ret <= 0) {
                logger.warn("failed to save teacherApplication!");
                return false;
            }

            TeacherContractFile teacherContractFile;
            List<TeacherContractFile> teacherContractFiles = new ArrayList<>();
            for (Integer id : ids) {
                teacherContractFile = this.teacherContractFileDao.findById(id);
                if(teacherContractFile.getResult().equals("FAIL")){
                    teacherContractFile.setResult(null);
                }
                teacherContractFile.setTeacherApplicationId(application.getId());
                logger.info("applicationId:{}", application.getId());
                teacherContractFiles.add(teacherContractFile);
            }

            logger.info("批量更新文件 for teacherId:{} with  teacherApplicationId:{}", teacher.getId(), application.getId());
            this.teacherContractFileDao.updateBatch(teacherContractFiles);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }


    /**
     * 插入teacherOtherDegrees里的文件
     */
    public int save(TeacherContractFile teacherContractFile) {
        return this.teacherContractFileDao.save(teacherContractFile);
    }

    /**
     * 删除teacherOtherDegrees里的文件
     */
    public Map<String, Object> remoteFile(int fileId, long teacherId) {
        TeacherContractFile teacherContractFile = this.teacherContractFileDao.findById(fileId);
        if(null==teacherContractFile){
            return ReturnMapUtils.returnFail("You  delete the file failed!");
        }
        if (teacherContractFile.getTeacherId() != teacherId) {
            return ReturnMapUtils.returnFail("You can't delete the file !");
        } else {
            if (teacherContractFile.getTeacherApplicationId() == TEACHERAPPLICATION_ID) {
                logger.info("Teacher:{} delete teacherContractFile",teacherId);
                this.teacherContractFileDao.delete(teacherContractFile);
                return ReturnMapUtils.returnSuccess();
            } else {
                return ReturnMapUtils.returnSuccess();
            }
        }
    }

    /**
     * 查询老师上传过的文件
     *
     */
    public Map<String, Object> findContract(Teacher teacher) {
        ContractFile contractFile = new ContractFile();

        logger.info("Teacher：{}  find  Teacher Contract Files", teacher.getId());
        List<TeacherContractFile> teacherContractFiles =teacherContractFileDao.findByTeacherIdAndTeacherApplicationId(teacher.getId(),TEACHERAPPLICATION_ID);
        Map<String, Object> map = Maps.newHashMap();

        if (CollectionUtils.isNotEmpty(teacherContractFiles)) {
            List<TeacherContractFile> degrees = Lists.newArrayList();
            List<TeacherContractFile> contract = Lists.newArrayList();
            List<String> res = Lists.newArrayList();
            List<TeacherContractFile> identification = Lists.newArrayList();
            List<TeacherContractFile> diploma = Lists.newArrayList();
            List<TeacherContractFile> tax = Lists.newArrayList();
            List<TeacherContractFile> certification = Lists.newArrayList();
            teacherContractFiles.forEach(obj -> {
                if (obj.getFileType() == TeacherApplicationEnum.ContractFileType.OTHER_DEGREES.val()) {
                    degrees.add(obj);
                }
                if (obj.getFileType() == TeacherApplicationEnum.ContractFileType.CERTIFICATIONFILES.val()) {
                    certification.add(obj);
                }
                if (obj.getFileType() == TeacherApplicationEnum.ContractFileType.IDENTIFICATION.val()) {
                    obj.setTypeName("identity");
                    identification.add(obj);
                }
                if (obj.getFileType() == TeacherApplicationEnum.ContractFileType.PASSPORT.val()) {
                    obj.setTypeName("passport");
                    identification.add(obj);

                }
                if (obj.getFileType() == TeacherApplicationEnum.ContractFileType.DRIVER.val()) {
                    obj.setTypeName("driver");
                    identification.add(obj);
                }
                if (obj.getFileType() == TeacherApplicationEnum.ContractFileType.DIPLOMA.val()) {
                    diploma.add(obj);
                }
                if (obj.getFileType() == TeacherApplicationEnum.ContractFileType.CONTRACT.val()) {
                    contract.add(obj);
                }
                if (obj.getFileType() == TeacherApplicationEnum.ContractFileType.CONTRACT_W9.val()) {
                    tax.add(obj);
                }
                if (StringUtils.isNotBlank(obj.getResult())) {
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
            map.put("contractFile", contractFile);
            map.put("result",result);
        }
        return map;

    }

    /**
     * 判断 Contract file 是否Pass
     */
     private String isPass(List<String> res) {
        if (CollectionUtils.isEmpty(res)) {
            return String.valueOf(TeacherApplicationEnum.Result.FAIL);
        }

        for (String result : res) {
            if(StringUtils.isEmpty(result)){
                logger.error("Teacher Contract File result is Null");
            }
            if (result.equals("FAIL")) {
                return String.valueOf(TeacherApplicationEnum.Result.FAIL);
            }
        }
        return String.valueOf(TeacherApplicationEnum.Result.PASS);
     }

    /**
     * submit 时查询Contract 的 文件 是否合格
     */
    public List<TeacherContractFile> findTeacherContractFile(long teacherId) {
        logger.info("Teacher:{} 执行 findTeacherContractFile  method for check  ContractFile submit",teacherId);
        return teacherContractFileDao.findByTeacherIdAndTeacherApplicationId(teacherId, TEACHERAPPLICATION_ID);
    }

    public List<TeacherApplication> findTeacherApplication(long teacherId){
        logger.info("Teacher:{} 执行 findTeacherApplication  method for check  ContractFile Many submit",teacherId);
       return teacherApplicationDao.findCurrentApplication(teacherId);
    }


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
