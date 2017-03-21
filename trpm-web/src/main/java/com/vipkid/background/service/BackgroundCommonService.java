package com.vipkid.background.service;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
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
import com.vipkid.trpm.dao.BackgroundScreeningV2Dao;
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
    private BackgroundScreeningV2Dao backgroundScreeningV2Dao;

    @Autowired
    private TeacherContractFileDao teacherContractFileDao;

    @Autowired
    private BackgroundAdverseDao backgroundAdverseDao;

    @Autowired
    private CanadaBackgroundScreeningDao canadaBackgroundScreeningDao;

    private static Logger logger = LoggerFactory.getLogger(BackgroundCommonService.class);


    public Map<String,Object> getUsaBackgroundStatus(Teacher teacher){
        logger.info("获取老师: {} 背调信息",teacher.getId());
        Map<String, Object> result = Maps.newHashMap();
        Calendar current = Calendar.getInstance();
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacher.getId());
        Date  contractEndDate = teacher.getContractEndDate();
        Calendar  remindTime = Calendar.getInstance();
        remindTime.setTime(contractEndDate);
        //合同即将到期需进行背调,提前一个月进行弹窗提示
        remindTime.add(Calendar.MONTH,-1);
        if (remindTime.getTime().before(current.getTime()) ) {
            //没有背调结果，即第一次开始背调
            if (null == backgroundScreening) {
                result.put("needBackgroundCheck", true);
                result.put("phase", BackgroundPhase.START);
                return result;
            } else {
                boolean in5Days = false;
                long screeningId = backgroundScreening.getId();
                Date adverseTime = backgroundAdverseDao.findUpdateTimeByScreeningIdTopOne(screeningId);
                current.add(Calendar.DATE, 5);
                if (null != adverseTime && adverseTime.before(current.getTime())) {
                    in5Days = true;
                }
                current.add(Calendar.DATE, -5);
                current.add(Calendar.YEAR, -2);

                //上次背调超过两年需要进行背调，不超过两年需要根据result和disputeStatus进行判断
                if (current.getTime().after(backgroundScreening.getUpdateTime())) {
                    result.put("needBackgroundCheck", true);
                    result.put("phase", BackgroundPhase.START);
                } else {
                    String backgroundResult = backgroundScreening.getResult();
                    String disputeStatus = backgroundScreening.getDisputeStatus();
                    if (null != backgroundResult) {
                        switch (backgroundResult) {
                            //开始背调，背调结果结果为N/A
                            case "n/a":
                                result.put("needBackgroundCheck", true);
                                result.put("phase", BackgroundPhase.PENDING);
                                result.put("result", BackgroundResult.NA);
                                break;
                            //背调结果为CLEAR，不再需要进行背调
                            case "clear":
                                result.put("needBackgroundCheck", false);
                                result.put("phase", BackgroundPhase.CLEAR.getVal());
                                result.put("result", BackgroundResult.CLEAR);
                                break;
                            //背调结果为ALERT，需根据disputeStatus进行判断
                            case "alert":
                                //disputeStatus为null
                                if (null == disputeStatus) {
                                    //在5天内可以进行dispute，超过5天不允许在进行dispute自动FAIL
                                    if (in5Days) {
                                        result.put("needBackgroundCheck", true);
                                        result.put("phase", BackgroundPhase.PREADVERSE);
                                        result.put("result", BackgroundResult.ALERT);
                                    } else {
                                        result.put("needBackgroundCheck", false);
                                        result.put("result", BackgroundResult.FAIL);
                                        result.put("phase", BackgroundPhase.DIDNOTDISPUTE);
                                    }

                                } else {
                                    //diaputeStatus为ACTIVE表明正在进行dispute，为DEACTIVATED表明disputed失败
                                    if (StringUtils.equalsIgnoreCase(disputeStatus, DisputeStatus.ACTIVE.toString())) {
                                        result.put("needBackgroundCheck", true);
                                        result.put("phase", BackgroundPhase.DISPUTE.getVal());
                                        result.put("result", BackgroundResult.ALERT.getVal());
                                    } else {
                                        result.put("needBackgroundCheck", false);
                                        result.put("phase", BackgroundPhase.FAIL.getVal());
                                        result.put("result", BackgroundResult.FAIL.getVal());
                                    }
                                }
                                break;
                        }
                    }else{
                        result.put("result",BackgroundResult.NA);
                        result.put("phase",BackgroundPhase.START);
                    }
                }

            }
            return result;
        }else {
            result.put("needBackgroundCheck", false);
            result.put("phase","");
            result.put("result","");
        }
        return result;
    }

    public Map<String,Object> getCanadabackgroundStatus(Teacher teacher){
        logger.info("获取老师: {} 背调信息",teacher.getId());
        Map<String, Object> result = Maps.newHashMap();
        Calendar current = Calendar.getInstance();
        Date  contractEndDate = teacher.getContractEndDate();
        Calendar  remindTime = Calendar.getInstance();
        remindTime.setTime(contractEndDate);
        remindTime.add(Calendar.MONTH,-1);
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacher.getId());
        //合同即将到期需进行背调,提前一个月进行弹窗提示
        if (remindTime.getTime().before(current.getTime()) ) {
            CanadaBackgroundScreening canadaBackgroundScreening = canadaBackgroundScreeningDao.findByTeacherId(teacher.getId());
            //第一次进行背调
            if (null == canadaBackgroundScreening) {
                result.put("needBackgroundCheck", true);
                result.put("phase", BackgroundPhase.START);
                result.put("result",BackgroundResult.NA);
                return result;
            }
            current.add(Calendar.YEAR, -2);
            //超过两年需要背调，
            if (current.getTime().after(backgroundScreening.getUpdateTime())) {
                result.put("needBackgroundCheck", true);
                result.put("phase", BackgroundPhase.START);
                result.put("result",BackgroundResult.NA);
            } else { //不超过两年显示result
                if (StringUtils.equalsIgnoreCase(canadaBackgroundScreening.getResult(),"PASS")){
                    result.put("needBackgroundCheck",false);
                    result.put("result",BackgroundResult.CLEAR);
                    result.put("phase",BackgroundPhase.CLEAR.getVal());
                }else if (StringUtils.equalsIgnoreCase(canadaBackgroundScreening.getResult(),"FAIL")){
                    result.put("needBackgroundCheck",true);
                    result.put("result",BackgroundResult.ALERT);
                    result.put("phase",BackgroundPhase.FAIL.getVal());
                }else {
                    result.put("needBackgroundCheck",true);
                    result.put("result",BackgroundResult.NA);
                    result.put("phase",BackgroundPhase.PENDING.getVal());
                }
            }
            return result;
        } else {
            result.put("result","");
            result.put("phase","");
            result.put("needBackgroundCheck", false);
        }

        return result;
    }

    public Map<String, Object> getBackgroundFileStatus(long teacherId, String nationality) {
        logger.info("获取老师: {} 背调文件信息",teacherId);
        Map<String, Object> result = Maps.newHashMap();
        List<TeacherContractFile> teacherContractFiles = teacherContractFileDao.findBackgroundFileByTeacherId(teacherId);
        boolean hasFile = false;
        if (CollectionUtils.isNotEmpty(teacherContractFiles)) {
            hasFile = true;
            if (StringUtils.equalsIgnoreCase(nationality, "United States")) {
                result.put("nationality", "USA");

                for (TeacherContractFile contractFile : teacherContractFiles) {
                    Long screeningId = contractFile.getScreeningId();
                    if (null == screeningId){
                        result.put("fileStatus",FileStatus.SAVE);
                    }else {
                        result.put("fileStatus",FileStatus.SUBMIT);
                    }
                    if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "PASS")) {

                        result.put("fileResult", FileResult.PASS);
                    } else if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "FAIL")) {

                        result.put("fileResult", FileResult.FAIL);
                    } else {
                        result.put("fileResult", FileResult.PENDING);
                    }
                }
            } else if (StringUtils.equalsIgnoreCase(nationality, "CANADA")) {
                result.put("nationality", "CANADA");
                String canadaFirstFileResult = "";
                String canadaSecondFileResult = "";
                for (TeacherContractFile contractFile : teacherContractFiles) {
                    Long screeningId = contractFile.getScreeningId();
                    if (null == screeningId){
                        result.put("fileStatus",FileStatus.SAVE);
                    }else {
                        result.put("fileStatus",FileStatus.SUBMIT);
                    }
                    int fileType = contractFile.getFileType();
                    //美国的只有一个文件fileType为 9 ，加拿大有两个文件fileType为10 、 11
                    switch (fileType) {
                        case 10:
                            if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "PASS")) {
                                canadaFirstFileResult = "PASS";
                            } else if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "FAIL")) {
                                canadaFirstFileResult = "FAIL";
                            } else {
                                canadaFirstFileResult = "PENDING";
                            }
                            break;
                        case 11:
                            if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "PASS")) {
                                canadaSecondFileResult = "PASS";
                            } else if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "FAIL")) {
                                canadaSecondFileResult = "FAIL";
                            } else {
                                canadaSecondFileResult = "PENDING";
                            }
                            break;
                    }
                }if (StringUtils.equalsIgnoreCase(canadaFirstFileResult,"FAIL") || StringUtils.equalsIgnoreCase(canadaSecondFileResult,"FAIL")){
                    result.put("fileResult",FileResult.FAIL);
                }else if (StringUtils.equalsIgnoreCase(canadaFirstFileResult,"PASS") && StringUtils.equalsIgnoreCase(canadaSecondFileResult,"PASS")){
                    result.put("fileResult",FileResult.PASS);
                }else {
                    result.put("fileResult",FileResult.PENDING);
                }
            }
        } else {
            result.put("nationality","");
            result.put("fileResult","");
            result.put("fileStatus","");
            hasFile = false;
        }
        result.put("hasFile", hasFile);
        return result;

    }

}
