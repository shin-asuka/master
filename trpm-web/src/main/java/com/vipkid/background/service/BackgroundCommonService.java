package com.vipkid.background.service;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.vipkid.background.api.sterling.dto.BackgroundFileStatusDto;
import com.vipkid.background.api.sterling.dto.BackgroundStatusDto;
import com.vipkid.enums.BackgroundCheckEnum;
import com.vipkid.enums.BackgroundCheckEnum.BackgroundResult;
import com.vipkid.enums.BackgroundCheckEnum.DisputeStatus;
import com.vipkid.enums.BackgroundCheckEnum.BackgroundPhase;
import com.vipkid.enums.BackgroundCheckEnum.FileStatus;
import com.vipkid.enums.BackgroundCheckEnum.FileResult;
import com.vipkid.enums.TeacherApplicationEnum.ContractFileType;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.recruitment.dao.TeacherContractFileDao;
import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.trpm.dao.BackgroundAdverseDao;
import com.vipkid.trpm.dao.BackgroundScreeningDao;
import com.vipkid.trpm.dao.CanadaBackgroundScreeningDao;
import com.vipkid.trpm.entity.BackgroundScreening;
import com.vipkid.trpm.entity.CanadaBackgroundScreening;
import com.vipkid.trpm.entity.Teacher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class BackgroundCommonService {

    @Autowired
    private BackgroundScreeningDao backgroundScreeningDao;

    @Autowired
    private TeacherContractFileDao teacherContractFileDao;

    @Autowired
    private BackgroundAdverseDao backgroundAdverseDao;

    @Autowired
    private CanadaBackgroundScreeningDao canadaBackgroundScreeningDao;

    private static Logger logger = LoggerFactory.getLogger(BackgroundCommonService.class);


    public BackgroundStatusDto getUsaBackgroundStatus(Teacher teacher){
        BackgroundStatusDto backgroundStatusDto = new BackgroundStatusDto();
        Calendar current = Calendar.getInstance();
        BackgroundScreening backgroundScreening = backgroundScreeningDao.findByTeacherIdTopOne(teacher.getId());
        Date  contractEndDate = teacher.getContractEndDate();
        Calendar  remindTime = Calendar.getInstance();
        remindTime.setTime(contractEndDate);
        //合同即将到期需进行背调,提前一个月进行弹窗提示
        remindTime.add(Calendar.MONTH,-1);
        if (remindTime.getTime().before(current.getTime()) ) {
            //没有背调结果，即第一次开始背调
            if (null == backgroundScreening) {
                backgroundStatusDto.setNeedBackgroundCheck(true);
                backgroundStatusDto.setPhase(BackgroundPhase.START.getVal());
                backgroundStatusDto.setResult("");
                return backgroundStatusDto;
            } else {
                //boolean in5Days = false;
                long screeningId = backgroundScreening.getId();
                Date adverseTime = backgroundAdverseDao.findUpdateTimeByScreeningIdTopOne(screeningId);

                /*current.add(Calendar.DATE, 5);
                if (null != adverseTime && adverseTime.before(current.getTime())) {
                    in5Days = true;
                }
                current.add(Calendar.DATE, -5);*/

                current.add(Calendar.YEAR, -2);

                //上次背调超过两年需要进行背调，不超过两年需要根据result和disputeStatus进行判断
                if (current.getTime().after(backgroundScreening.getUpdateTime())) {
                    backgroundStatusDto.setNeedBackgroundCheck(true);
                    backgroundStatusDto.setPhase(BackgroundPhase.START.getVal());
                    backgroundStatusDto.setResult("");

                } else {
                    String backgroundResult = backgroundScreening.getResult();
                    String disputeStatus = backgroundScreening.getDisputeStatus();
                    if (null != backgroundResult) {
                        switch (backgroundResult) {
                            //开始背调，背调结果结果为N/A
                            case "n/a":
                                backgroundStatusDto.setNeedBackgroundCheck(true);
                                backgroundStatusDto.setPhase(BackgroundPhase.PENDING.getVal());
                                backgroundStatusDto.setResult(BackgroundResult.NA.getVal());
                                break;
                            //背调结果为CLEAR，不再需要进行背调
                            case "clear":
                                backgroundStatusDto.setNeedBackgroundCheck(false);
                                backgroundStatusDto.setResult(BackgroundResult.CLEAR.getVal());
                                backgroundStatusDto.setPhase(BackgroundPhase.CLEAR.getVal());
                                break;
                            //背调结果为ALERT，需根据disputeStatus进行判断
                            case "alert":
                                //disputeStatus为null
                                if (null == disputeStatus) {
                                    //在5天内可以进行dispute，超过5天不允许在进行dispute自动FAIL
                                   // if (in5Days) {

                                        backgroundStatusDto.setPhase(BackgroundPhase.PREADVERSE.getVal());
                                        backgroundStatusDto.setResult(BackgroundResult.ALERT.getVal());
                                        backgroundStatusDto.setNeedBackgroundCheck(true);

                                   /* } else {
                                        backgroundStatusDto.setNeedBackgroundCheck(false);
                                        backgroundStatusDto.setPhase(BackgroundPhase.DIDNOTDISPUTE.getVal());
                                        backgroundStatusDto.setResult(BackgroundResult.FAIL.getVal());
                                    }*/

                                } else {
                                    //diSputeStatus为ACTIVE表明正在进行dispute，为DEACTIVATED表明disputed失败
                                    if (StringUtils.equalsIgnoreCase(disputeStatus, DisputeStatus.ACTIVE.toString())) {
                                        backgroundStatusDto.setPhase(BackgroundPhase.DISPUTE.getVal());
                                        backgroundStatusDto.setResult(BackgroundResult.ALERT.getVal());
                                        backgroundStatusDto.setNeedBackgroundCheck(true);
                                    } else {
                                        backgroundStatusDto.setNeedBackgroundCheck(false);
                                        backgroundStatusDto.setPhase(BackgroundPhase.FAIL.getVal());
                                        backgroundStatusDto.setResult(BackgroundResult.FAIL.getVal());
                                    }
                                }
                                break;
                        }
                    }else{
                        backgroundStatusDto.setPhase(BackgroundPhase.START.getVal());
                        backgroundStatusDto.setNeedBackgroundCheck(true);
                        backgroundStatusDto.setResult("");
                    }
                }

            }
            logger.info("获取美国老师: {} 背调状态信息 {}",teacher.getId(),backgroundStatusDto);
            return backgroundStatusDto;
        }else {
            backgroundStatusDto.setNeedBackgroundCheck(false);
            backgroundStatusDto.setPhase("");
            backgroundStatusDto.setResult("");
        }
        logger.info("获取美国老师: {} 背调状态信息 {}",teacher.getId(),backgroundStatusDto);
        return backgroundStatusDto;
    }

    public BackgroundStatusDto getCanadabackgroundStatus(Teacher teacher){
        BackgroundStatusDto backgroundStatusDto = new BackgroundStatusDto();
        Calendar current = Calendar.getInstance();
        Date  contractEndDate = teacher.getContractEndDate();
        Calendar  remindTime = Calendar.getInstance();
        remindTime.setTime(contractEndDate);
        remindTime.add(Calendar.MONTH,-1);
        //合同即将到期需进行背调,提前一个月进行弹窗提示
        if (remindTime.getTime().before(current.getTime()) ) {
            CanadaBackgroundScreening canadaBackgroundScreening = canadaBackgroundScreeningDao.findByTeacherId(teacher.getId());
            //第一次进行背调
            if (null == canadaBackgroundScreening) {
                backgroundStatusDto.setNeedBackgroundCheck(true);
                backgroundStatusDto.setPhase(BackgroundPhase.START.getVal());
                backgroundStatusDto.setResult("");

                return backgroundStatusDto;
            }
            current.add(Calendar.YEAR, -2);
            //超过两年需要背调，
            if (current.getTime().after(canadaBackgroundScreening.getUpdateTime())) {
                backgroundStatusDto.setNeedBackgroundCheck(true);
                backgroundStatusDto.setPhase(BackgroundPhase.START.getVal());
                backgroundStatusDto.setResult("");
            } else { //不超过两年显示result
                if (StringUtils.equalsIgnoreCase(canadaBackgroundScreening.getResult(),BackgroundResult.CLEAR.getVal())){
                    backgroundStatusDto.setPhase(BackgroundPhase.CLEAR.getVal());
                    backgroundStatusDto.setResult(BackgroundResult.CLEAR.getVal());
                    backgroundStatusDto.setNeedBackgroundCheck(false);
                }else if (StringUtils.equalsIgnoreCase(canadaBackgroundScreening.getResult(),BackgroundResult.ALERT.getVal())){
                    backgroundStatusDto.setNeedBackgroundCheck(false);
                    backgroundStatusDto.setPhase(BackgroundPhase.FAIL.getVal());
                    backgroundStatusDto.setResult(BackgroundResult.FAIL.getVal());
                }else {
                    backgroundStatusDto.setNeedBackgroundCheck(true);
                    backgroundStatusDto.setPhase(BackgroundPhase.PENDING.getVal());
                    backgroundStatusDto.setResult(BackgroundResult.NA.getVal());
                }
            }
            logger.info("获取加拿大老师: {} 背调状态信息 {}",teacher.getId(),backgroundStatusDto);
            return backgroundStatusDto;
        } else {
            backgroundStatusDto.setPhase("");
            backgroundStatusDto.setResult("");
            backgroundStatusDto.setNeedBackgroundCheck(false);
        }
        logger.info("获取加拿大老师: {} 背调信息 {}",teacher.getId(),backgroundStatusDto);
        return backgroundStatusDto;
    }

    public BackgroundFileStatusDto getBackgroundFileStatus(long teacherId, String nationality) {
        BackgroundFileStatusDto backgroundFileStatusDto = new BackgroundFileStatusDto();
        List<TeacherContractFile> teacherContractFiles = teacherContractFileDao.findBackgroundFileByTeacherId(teacherId);
        boolean hasFile = false;
        if (CollectionUtils.isNotEmpty(teacherContractFiles)) {
            hasFile = true;
            if (StringUtils.equalsIgnoreCase(nationality, "United States")) {
                backgroundFileStatusDto.setNationality("USA");

                for (TeacherContractFile contractFile : teacherContractFiles) {
                    Long screeningId = contractFile.getScreeningId();
                    String fileResult = contractFile.getResult();
                    if (null == screeningId || StringUtils.equalsIgnoreCase(fileResult,FileResult.FAIL.getValue())){
                        backgroundFileStatusDto.setFileStatus(FileStatus.SAVE.getValue());
                    }else {
                        backgroundFileStatusDto.setFileStatus(FileStatus.SUBMIT.getValue());
                    }
                    if (StringUtils.equalsIgnoreCase(fileResult, FileResult.PASS.getValue())) {
                        backgroundFileStatusDto.setFileResult(FileResult.PASS.getValue());
                    } else if (StringUtils.equalsIgnoreCase(fileResult, FileResult.FAIL.getValue())) {
                        backgroundFileStatusDto.setFileResult(FileResult.FAIL.getValue());
                    } else {
                        backgroundFileStatusDto.setFileResult(FileResult.PENDING.getValue());
                    }
                }
            } else if (StringUtils.equalsIgnoreCase(nationality, "CANADA")) {
                backgroundFileStatusDto.setNationality("CANADA");
                String canadaFirstFileResult = "";
                String canadaSecondFileResult = "";
                for (TeacherContractFile contractFile : teacherContractFiles) {
                    Long screeningId = contractFile.getScreeningId();
                    if (null == screeningId || StringUtils.equalsIgnoreCase(contractFile.getResult(),FileResult.FAIL.getValue())){
                        backgroundFileStatusDto.setFileStatus(FileStatus.SAVE.getValue());
                    }else {
                        backgroundFileStatusDto.setFileStatus(FileStatus.SUBMIT.getValue());
                    }
                    int fileType = contractFile.getFileType();
                    //美国的只有一个文件fileType为 9 ，加拿大有两个文件fileType为10 、 11
                    if (fileType == ContractFileType.CANADA_BACKGROUND_CHECK_CPIC_FORM.val()){
                        canadaFirstFileResult = getCanadaFileResult(contractFile.getResult());
                    }else if (fileType == ContractFileType.CANADA_BACKGROUND_CHECK_ID2.val()){
                        canadaSecondFileResult = getCanadaFileResult(contractFile.getResult());
                    }
                }if (StringUtils.equalsIgnoreCase(canadaFirstFileResult,FileResult.FAIL.getValue()) ||
                        StringUtils.equalsIgnoreCase(canadaSecondFileResult,FileResult.FAIL.getValue())){
                    backgroundFileStatusDto.setFileResult(FileResult.FAIL.getValue());
                }else if (StringUtils.equalsIgnoreCase(canadaFirstFileResult,FileResult.PASS.getValue()) &&
                        StringUtils.equalsIgnoreCase(canadaSecondFileResult,FileResult.PASS.getValue())){
                    backgroundFileStatusDto.setFileResult(FileResult.PASS.getValue());
                }else {
                    backgroundFileStatusDto.setFileResult(FileResult.PENDING.getValue());
                }
            }
        } else {
            backgroundFileStatusDto.setNationality("");
            backgroundFileStatusDto.setFileResult("");
            backgroundFileStatusDto.setFileStatus("");
            backgroundFileStatusDto.setHasFile(false);
        }
        backgroundFileStatusDto.setHasFile(hasFile);
        logger.info("获取老师: {} 背调文件信息 {}",teacherId,backgroundFileStatusDto);
        return backgroundFileStatusDto;

    }

    public String getCanadaFileResult(String result){
        if (StringUtils.equalsIgnoreCase(result,FileResult.PASS.getValue())){
            return FileResult.PASS.getValue();
        }else if (StringUtils.equalsIgnoreCase(result,FileResult.FAIL.getValue())){
            return FileResult.FAIL.getValue();
        }else {
            return FileResult.PENDING.getValue();
        }

    }


}
