package com.vipkid.background.service;

import com.vipkid.background.api.sterling.dto.CandidateInputDto;
import com.vipkid.background.api.sterling.dto.CandidateOutputDto;
import com.vipkid.background.api.sterling.service.SterlingService;
import com.vipkid.background.dto.input.BackgroundCheckInputDto;
import com.vipkid.background.dto.output.BaseOutputDto;
import com.vipkid.background.enums.TeacherPortalCodeEnum;
import com.vipkid.background.util.WorkThreadPool;
import com.vipkid.background.vo.BackgroundCheckVo;
import com.vipkid.enums.TeacherAddressEnum;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.recruitment.dao.TeacherContractFileDao;
import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.rest.exception.ServiceException;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherLicenseDao;
import com.vipkid.trpm.dao.TeacherLocationDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherLicense;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service("backgroundCheckService")
public class BackgroundCheckService {
    private static Logger logger = LoggerFactory.getLogger(BackgroundCheckService.class);

    @Autowired
    private TeacherContractFileDao contractFileDao;

    @Autowired
    private TeacherLicenseDao licenseDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private TeacherAddressDao teacherAddressDao;

    @Autowired
    private TeacherLocationDao locationDao;

    @Autowired
    private SterlingService sterlingService;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public BaseOutputDto saveBackgroundCheckInfo(BackgroundCheckInputDto input, String operateType){
        //update teacher
        Teacher teacher = teacherDao.findById(input.getTeacherId());
        updateTeacherBasicIno(teacher, input);


        //save teacher_address
        saveLatestAddress(input);

        if(StringUtils.isNotBlank(input.getFileUrl())){
            //save teacher_contract_file//
            saveContractFile(input.getTeacherId(), TeacherApplicationEnum.ContractFileType.US_BACKGROUND_CHECK.val(), input.getFileUrl(), operateType);
        }

        //save teacher_license
        saveLicense(input);

        if(StringUtils.equals(operateType, "submit")){
            createCandidateAsync(input, teacher);
        }
        BaseOutputDto output = new BaseOutputDto();
        output.setResCode(TeacherPortalCodeEnum.RES_SUCCESS.getCode());
        output.setResMsg(TeacherPortalCodeEnum.RES_SUCCESS.getMsg());
        return output;
    }


    public void saveContractFile(Long teacherId, Integer fileType, String url, String operateType) throws ServiceException{
        //update
        int row = updateUrlAndScreeningId(teacherId, fileType, url, operateType);
        if(row == 1){
            logger.info("save background check information, BackgroundCheckService.saveContractFile, update success, teacherId="+teacherId+", fileType="+fileType+", fileUrl="+url);
            return;
        }
        if(row > 1){
            logger.error("save background check information, BackgroundCheckService.saveContractFile, update failed. return affected row is not one. teacherId="+teacherId+", fileType="+fileType+", fileUrl="+url);
            throw new ServiceException(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
        //insert
        logger.info("save background check information, BackgroundCheckService.saveContractFile, the file does not exist, begin insert. teacherId="+teacherId+", fileType="+fileType+", fileUrl="+url);
        int saveRow = insertContractFile(teacherId, fileType, url, operateType);
        if(saveRow != 1){
            logger.error("save background check information, BackgroundCheckService.saveContractFile, insert success, return affected row is not one, teacherId="+teacherId+", fileType="+fileType+", fileUrl="+url);
            throw new ServiceException(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
        logger.info("save background check information, BackgroundCheckService.saveContractFile, insert success. teacherId="+teacherId+", fileType="+fileType+", fileUrl="+url);

    }

    public BackgroundCheckVo getInfoForUs(Long teacherId){
        Teacher teacher = teacherDao.findById(teacherId);
        if(null == teacher){
            return null;
        }
        BackgroundCheckVo checkInfo = new BackgroundCheckVo();
        checkInfo.setMaidenName(teacher.getMaidenName());
        checkInfo.setFirstName(teacher.getFirstName());
        checkInfo.setMiddleName(teacher.getMiddleName());
        checkInfo.setLastName(teacher.getLastName());
        checkInfo.setBirthDay(DateUtils.formatDate(teacher.getBirthday()));
        checkInfo.setEmail(teacher.getEmail());
        TeacherContractFile file = contractFileDao.findAllowEditOne(teacherId, TeacherApplicationEnum.ContractFileType.US_BACKGROUND_CHECK.val());
        if(file != null){
            checkInfo.setFileUrl(file.getUrl());
            checkInfo.setResult(file.getResult());
            checkInfo.setFailReason(file.getFailReason());
        }
        List<TeacherAddress> list = teacherAddressDao.findListByTeacherId(teacherId);
        for(TeacherAddress address : list){
            if(address.getType().equals(TeacherAddressEnum.AddressType.LATEST.val())){
                checkInfo.setLatestCity(address.getCity());
                checkInfo.setLatestCountryId(address.getCountryId());
                checkInfo.setLatestStateId(address.getStateId());
                checkInfo.setLatestStreet(address.getStreetAddress());
                checkInfo.setLatestZipCode(address.getZipCode());
            }
            if(address.getType().equals(TeacherAddressEnum.AddressType.NORMAL.val())){
                checkInfo.setCurrentCity(address.getCity());
                checkInfo.setCurrentCountryId(address.getCountryId());
                checkInfo.setCurrentStateId(address.getStateId());
                checkInfo.setCurrentStreet(address.getStreetAddress());
                checkInfo.setCurrentZipCode(address.getZipCode());

                //timezone
                TeacherLocation location = locationDao.findById(address.getCity());
                if(location != null){
                    checkInfo.setTimezone(location.getTimezone());
                }
            }
        }

        TeacherLicense license = licenseDao.findByTeacherId(teacherId);
        if(license != null){
            checkInfo.setDriverLicenseNumber(license.getDriverLicense());
            checkInfo.setDriverLicenseType(license.getDriverLicenseType());
            checkInfo.setDriverLicenseAgency(license.getDriverLicenseIssuingAgency());
            if(StringUtils.isNotBlank(license.getSocialNo())){
                String socialNo = license.getSocialNo().substring(0,2) + "******";
                checkInfo.setSocialSecurityNumber(socialNo);
            }
        }
        return checkInfo;
    }

    public List<TeacherContractFile> getInfoForCa(Long teacherId){
        String types = TeacherApplicationEnum.ContractFileType.CANADA_BACKGROUND_CHECK_CPIC_FORM.val() +","+TeacherApplicationEnum.ContractFileType.CANADA_BACKGROUND_CHECK_ID2.val();
        List<TeacherContractFile> list = contractFileDao.findListByTypes(teacherId, types);
        return list;
    }



    private void createCandidateAsync(BackgroundCheckInputDto checkInput, Teacher teacher){
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                CandidateOutputDto output = new  CandidateOutputDto(TeacherPortalCodeEnum.SYS_FAIL);
                CandidateInputDto candidateInputDto = new CandidateInputDto();
                candidateInputDto.setTeacherId(teacher.getId());
                candidateInputDto.setEmail(teacher.getEmail());
                candidateInputDto.setDob(checkInput.getBirthDay());
                if(StringUtils.isBlank(checkInput.getMiddleName())){
                    candidateInputDto.setConfirmedNoMiddleName(true);
                }
                candidateInputDto.setGivenName(teacher.getFirstName());
                candidateInputDto.setMiddleName(checkInput.getMiddleName());
                candidateInputDto.setFamilyName(teacher.getLastName());
                candidateInputDto.setPhone(teacher.getPhoneNationCode());
                candidateInputDto.setSsn(checkInput.getSocialSecurityNumber());
                CandidateInputDto.Address address = new CandidateInputDto.Address();
                address.setAddressLine(checkInput.getLatestStreet());
                address.setCountryCode("US");
                try{
                    TeacherLocation location = locationDao.findById(checkInput.getLatestCity());
                    if(null != location){
                        address.setMunicipality(location.getName());
                    }
                    address.setPostalCode(checkInput.getLatestZipCode());
                    candidateInputDto.setAddress(address);

                    CandidateInputDto.DriversLicense license = new CandidateInputDto.DriversLicense();
                    license.setIssuingAgency(checkInput.getDriverLicenseAgency());
                    license.setLicenseNumber(checkInput.getDriverLicenseNumber());
                    license.setType(checkInput.getDriverLicenseType());
                    candidateInputDto.setDriversLicense(license);

                    logger.info("submit background check information, begin invoke sterlingService.saveCandidate by syn, teacherId="+teacher.getId());

                    output = sterlingService.saveCandidate(candidateInputDto);
                    logger.info("submit background check information, invoke sterlingService.saveCandidate,teacherId="+teacher.getId()+", return resCode="+output.getResCode().getCode()+", resMsg="+output.getResCode().getMsg()+", id="+output.getId());
                }catch(Exception e){
                    logger.error("submit background check information occur exception, BackgroundCheckService.createCandidate failed, teacherId="+teacher.getId());
                }
            }
        };
        WorkThreadPool.fixedThreadPool.submit(thread);
    }
    private int updateUrlAndScreeningId(Long teacherId, Integer fileType, String url, String operateType){
        TeacherContractFile teacherContractFile = new TeacherContractFile();
        teacherContractFile.setTeacherId(teacherId);
        teacherContractFile.setFileType(fileType);
        teacherContractFile.setUrl(url);
        if(StringUtils.equals(operateType, "submit")){
            teacherContractFile.setScreeningId(0L);
        }
        int row = contractFileDao.updateUrlAndScreeningId(teacherContractFile);
        return row;
    }

    private int insertContractFile(Long teacherId, Integer fileType, String url, String operateType){
        TeacherContractFile file = new TeacherContractFile();
        file.setTeacherId(teacherId);
        file.setCreateId(teacherId);
        file.setCreateTime(new Timestamp(new Date().getTime()));
        file.setFileType(fileType);
        file.setUrl(url);
        if(StringUtils.equals(operateType, "submit")){
            file.setScreeningId(0L);
        }
        file.setTeacherApplicationId(-9);//代表无意义
        int row = contractFileDao.save(file);
        return row;
    }


    private void updateTeacherBasicIno(Teacher teacher, BackgroundCheckInputDto input){
        teacher.setMaidenName(input.getMaidenName());
        teacher.setMiddleName(input.getMiddleName());
        if(StringUtils.isNotBlank(input.getBirthDay())){
            teacher.setBirthday(DateUtils.parseDate(input.getBirthDay(), DateUtils.DEFAULT_FORMAT_PATTERN));
        }
        int row = teacherDao.update(teacher);
        if(row != 1){
            logger.error("save background check information, update table Teacher, return affected row is not one, teacherId="+input.getTeacherId());
            throw new ServiceException(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
    }

    private void saveLatestAddress(BackgroundCheckInputDto input){
        String currentStreet = input.getCurrentStreet();
        String currentZipCode = input.getCurrentZipCode();

        //if currentStreet not null or currentZipCode not null, then update it.
        if(StringUtils.isNotBlank(currentStreet) || StringUtils.isNotBlank(currentZipCode)){
            TeacherAddress address = new TeacherAddress();
            address.setTeacherId(input.getTeacherId());
            address.setStreetAddress(input.getCurrentStreet());
            address.setZipCode(input.getCurrentZipCode());
            address.setType(TeacherAddressEnum.AddressType.NORMAL.val());
            teacherAddressDao.updateByTeacherIdAndType(address);
        }

        TeacherAddress address = new TeacherAddress();
        address.setTeacherId(input.getTeacherId());
        address.setCity(input.getLatestCity());
        address.setCountryId(input.getLatestCountryId());
        address.setStateId(input.getLatestStateId());
        address.setStreetAddress(input.getLatestStreet());
        address.setZipCode(input.getLatestZipCode());
        address.setType(input.getAddressType());

        int row = teacherAddressDao.updateByTeacherIdAndType(address);
        if(row == 1){
            logger.info("save background check information, BackgroundCheckService.saveLatestAddress, update success, teacherId="+address.getTeacherId());
            return;
        }
        if(row > 1){
            logger.error("save background check information for US, BackgroundCheckService.saveLatestAddress, update failed. return affected row is not one. teacherId="+address.getTeacherId());
            throw new ServiceException(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
        logger.info("save background check information, BackgroundCheckService.saveLatestAddress, the address does not exist, begin insert.teacherId="+address.getTeacherId());
        address.setCreateTime(new Timestamp(new Date().getTime()));
        //insert
        int insertRow = teacherAddressDao.updateOrSave(address);
        if(insertRow != 1){
            logger.error("save background check information, BackgroundCheckService.saveLatestAddress,insert failed, return affected row is not one, teacherId="+address.getTeacherId());
            throw new ServiceException(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
        logger.info("save background check information, BackgroundCheckService.saveLatestAddress, insert success.teacherId="+address.getTeacherId());
    }

    private void saveLicense(BackgroundCheckInputDto input){
        TeacherLicense license = new TeacherLicense();
        license.setTeacherId(input.getTeacherId());
        license.setDriverLicense(input.getDriverLicenseNumber());
        license.setDriverLicenseType(input.getDriverLicenseType());
        license.setDriverLicenseIssuingAgency(input.getDriverLicenseAgency());
        if(input.getSocialSecurityNumber().indexOf("*") == -1){
            license.setSocialNo(input.getSocialSecurityNumber());
        }
        license.setUpdateId(input.getTeacherId());

        int updateRow = licenseDao.updateByTeacherId(license);
        if(updateRow == 1){
            logger.info("save background check information, BackgroundCheck.saveLicense, insert failed, update success, teacherId="+input.getTeacherId());
            return;
        }
        if(updateRow > 1){
            logger.error("save background check information, BackgroundCheck.saveLicense, update failed. return affected row is not one. teacherId="+input.getTeacherId());
            throw new ServiceException(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
        logger.info("save background check information, BackgroundCheck.saveLicense, the license does not exist, begin insert.teacherId="+input.getTeacherId());
        license.setCreateTime(new Date());
        license.setCreateId(input.getTeacherId());
        int row = licenseDao.insert(license);
        if(row != 1){
            logger.error("save background check information, BackgroundCheck.saveLicense, insert failed, return affected row is not one, teacherId="+input.getTeacherId());
            throw new ServiceException(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
        logger.info("save background check information, BackgroundCheckService.saveLicense, insert success.teacherId="+input.getTeacherId());

    }

}
