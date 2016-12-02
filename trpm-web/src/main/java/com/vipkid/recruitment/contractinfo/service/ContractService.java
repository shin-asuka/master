package com.vipkid.recruitment.contractinfo.service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vipkid.recruitment.dao.TeacherContractFileDao;
import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.trpm.dao.TeacherAddressDao;
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
 * Created by zhangzhaojun on 2016/11/14.
 */
@Service
public class ContractService {
    private static Logger logger = LoggerFactory.getLogger(ContractService.class);
    @Autowired
    private TeacherContractFileDao teacherContractFileDao;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    @Autowired
    private TeacherTaxpayerFormDao teacherTaxpayerFormDao;

    @Autowired
    private TeacherAddressDao teacherAddressDao;

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

            TeacherContractFile teacherContractFile;
            List<TeacherContractFile> teacherContractFiles = new ArrayList<TeacherContractFile>();
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
     *
     * @param teacherContractFile
     * @return
     */
    public int save(TeacherContractFile teacherContractFile) {
        return this.teacherContractFileDao.save(teacherContractFile);
    }

    /**
     * 删除teacherOtherDegrees里的文件
     * @param fileId
     * @param teacherId
     * @return
     */
    public Map<String, Object> reomteFile(int fileId, long teacherId) {
        TeacherContractFile teacherContractFile = this.teacherContractFileDao.findById(fileId);
        if(null==teacherContractFile){
            return ResponseUtils.responseFail("You  delete the file failed!", this);
        }
        if (teacherContractFile.getTeacherId() != teacherId) {
            return ResponseUtils.responseFail("You can't delete the file !", this);
        } else {
            if (teacherContractFile.getTeacherApplicationId() == 0) {
                logger.info("Teacher:{} delete teacherContractFile",teacherId);
                this.teacherContractFileDao.delete(teacherContractFile);
                return ResponseUtils.responseSuccess();
            } else {
                return ResponseUtils.responseSuccess();
            }
        }
    }

    /**
     * 查询老师上传过的文件
     *
     * @param teacher
     * @return
     */
    public Map<String, ContractFile> findContract(Teacher teacher) {
        ContractFile contractFile = new ContractFile();

        logger.info("Teacher：{}  find  Teacher Contract Files", teacher.getId());
        List<TeacherContractFile> teacherContractFiles =teacherContractFileDao.findByTeacherIdAndTeacherApplicationId(teacher.getId(),0);
        Map<String, ContractFile> map = Maps.newHashMap();

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

    /**
     * 判断 Contract file 是否Pass
     * @param res
     * @return
     */
    public String isPass(List<String> res) {
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
     * @param teacherId
     * @return
     */
    public List<TeacherContractFile> findTeacherContractFile(long teacherId) {
        logger.info("Teacher:{} 执行 findTeacherContractFile  method for check  ContractFile submit",teacherId);
        return teacherContractFileDao.findByTeacherIdAndTeacherApplicationId(teacherId, 0);
    }
}
