package com.vipkid.background.service;

import com.alibaba.druid.support.json.JSONUtils;
import com.vipkid.background.enums.TeacherPortalCodeEnum;
import com.vipkid.background.vo.input.BackgroundCheckInput;
import com.vipkid.background.vo.output.BaseOutput;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.recruitment.dao.TeacherContractFileDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.rest.exception.ServiceException;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherLicenseDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherLicense;
import com.vipkid.trpm.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Service("backgroundCheckService")
public class BackgroundCheckService {
    private static Logger logger = LoggerFactory.getLogger(BackgroundCheckService.class);

    private TeacherContractFileDao contractFileDao;

    private TeacherLicenseDao licenseDao;

    private TeacherDao teacherDao;

    private TeacherAddressDao teacherAddressDao;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public BaseOutput saveBackgroundCheckInfo(BackgroundCheckInput input){
        //update teacher
        Teacher teacher = teacherDao.findById(input.getTeacherId());
        updateTeacherBasicIno(teacher, input);

        //insert to teacher_address
        saveLatestAddress(input);

        //insert to teacher_contract_file//
        saveContractFile(input);

        //insert to teacher_license
        saveLicense(input);

        BaseOutput output = new BaseOutput();
        output.setResCode(TeacherPortalCodeEnum.RES_SUCCESS.getCode());
        output.setResMsg(TeacherPortalCodeEnum.RES_SUCCESS.getMsg());
        return output;
    }

    private void updateTeacherBasicIno(Teacher teacher, BackgroundCheckInput input){
        teacher.setMaidenName(input.getMaidenName());
        if(StringUtils.isNotBlank(input.getBirthDay())){
            teacher.setBirthday(DateUtils.parseDate(input.getBirthDay(), DateUtils.DEFAULT_FORMAT_PATTERN));
        }
        int row = teacherDao.update(teacher);
        if(row != 1){
            logger.warn("submit background check information, update table Teacher, return affected row is not one, param="+ JSONUtils.toJSONString(input));
            throw new ServiceException(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
    }

    private void saveLatestAddress(BackgroundCheckInput input){
        TeacherAddress address = new TeacherAddress();
        address.setTeacherId(input.getTeacherId());
        address.setCity(input.getCity());
        address.setCountryId(input.getCountryId());
        address.setStateId(input.getStateId());
        address.setStreetAddress(input.getStreet());
        address.setZipCode(input.getZipCode());
        address.setType(input.getAddressType());
        address.setCreateTime(new Timestamp(new Date().getTime()));

        int row = teacherAddressDao.updateOrSave(address);
        if(row != 1){
            logger.warn("submit background check information, insert to table TeacherAddress, return affected row is not one, param="+ JSONUtils.toJSONString(input));
            throw new ServiceException(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
    }

    private void saveContractFile(BackgroundCheckInput input){
        TeacherContractFile file = new TeacherContractFile();
        file.setTeacherId(input.getTeacherId());
        file.setCreateId(input.getTeacherId());
        file.setCreateTime(new Timestamp(new Date().getTime()));
        file.setFileType(TeacherApplicationEnum.ContractFileType.US_BACKGROUND_CHECK.val());
        file.setUrl(input.getFileUrl());
        file.setTeacherApplicationId(-9);//代表无意义
        int row = contractFileDao.save(file);
        if(row != 1){
            logger.warn("submit background check information, insert to table TeacherContractFile, return affected row is not one, param="+ JSONUtils.toJSONString(input));
            throw new ServiceException(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
    }

    private void saveLicense(BackgroundCheckInput input){
        TeacherLicense license = new TeacherLicense();
        license.setTeacherId(input.getTeacherId());
        license.setDriverLicense(input.getDriverLicenseNumber());
        license.setType(input.getDriverLicenseType());
        license.setIssuingAgency(input.getDriverLicenseAgency());
        license.setSecurityNo(input.getSocialSecurityNumber());
        license.setCreateTime(new Date());
        int row = licenseDao.save(license);
        if(row != 1){;
            logger.warn("submit background check information, insert to table TeacherLicense, return affected row is not one, param="+ JSONUtils.toJSONString(input));
            throw new ServiceException(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
    }
}
