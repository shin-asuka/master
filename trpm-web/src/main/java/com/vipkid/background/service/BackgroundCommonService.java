package com.vipkid.background.service;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.vipkid.enums.BackgroundCheckEnum.BackgroundResult;
import com.vipkid.enums.BackgroundCheckEnum.DisputeStatus;
import com.vipkid.enums.BackgroundCheckEnum.BackgroundPhase;
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

    public Map<String, Object> getBackgroundStatus(Teacher teacher) {
        String nationality = teacher.getCountry();
        Map<String, Object> result = Maps.newHashMap();
        Calendar currnet = Calendar.getInstance();
        currnet.add(Calendar.MONTH, 1);
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacher.getId());
        //合同即将到期或者处于CONTRACT_INFO阶段需要进行背调
        if (teacher.getContractEndDate().after(currnet.getTime()) || StringUtils.equals(teacher.getLifeCycle(), LifeCycle.CONTRACT_INFO.toString())) {
            //国籍为美国
            if (StringUtils.equalsIgnoreCase(nationality, "USA")) {

                //没有背调结果，即第一次开始背调
                if (null == backgroundScreening) {
                    result.put("needBackgroundCheck", true);
                    result.put("phase", BackgroundPhase.START);
                    return result;
                } else {
                    boolean in5Days = false;
                    long screeningId = backgroundScreening.getId();
                    currnet.add(Calendar.MONTH, -1);
                    //TODO
                    Date adverseTime = backgroundAdverseDao.findUpdateTimeByScreeningIdTopOne(screeningId);
                    currnet.add(Calendar.DATE, 5);
                    if (adverseTime.before(currnet.getTime())) {
                        in5Days = true;
                    }
                    currnet.add(Calendar.DATE, -5);
                    currnet.add(Calendar.YEAR, -2);
                    //上次背调超过两年需要进行背调，不超过两年需要根据result和disputeStatus进行判断
                    if (currnet.getTime().after(backgroundScreening.getUpdateTime())) {
                        result.put("needBackgroundCheck", true);
                        result.put("phase", BackgroundPhase.START.getVal());
                    } else {
                        String backgroundResult = backgroundScreening.getResult();
                        String disputeStatus = backgroundScreening.getStatus();
                        switch (backgroundResult) {
                            //开始背调，背调结果结果为N/A
                            case "N/A":
                                result.put("needBackgroundCheck", true);
                                result.put("phase", BackgroundPhase.PENDING.getVal());
                                result.put("result", BackgroundResult.NA.getVal());
                                break;
                            //背调结果为CLEAR，不再需要进行背调
                            case "CLEAR":
                                result.put("needBackgroundCheck", false);
                                result.put("phase", BackgroundPhase.CLEAR.getVal());
                                result.put("result", BackgroundResult.CLEAR.getVal());
                                break;
                            //背调结果为ALERT，需根据disputeStatus进行判断
                            case "ALERT":
                                //disputeStatus为null
                                if (null == disputeStatus) {
                                    //在5天内可以进行dispute，超过5天不允许在进行dispute
                                    if (in5Days) {
                                        result.put("needBackgroundCheck", true);
                                        result.put("phase", BackgroundPhase.PREADVERSE.getVal());
                                        result.put("result", BackgroundResult.ALERT.getVal());
                                    } else {
                                        result.put("needBackgroundCheck", false);
                                        result.put("result", BackgroundResult.ALERT.getVal());
                                        result.put("phase", BackgroundPhase.FAIL.getVal());
                                    }

                                } else {
                                    //diaputeStatus为ACTIVE表明正在进行dispute，为DEACTIVATED表明disputed失败
                                    if (StringUtils.equalsIgnoreCase(disputeStatus, DisputeStatus.ACTIVE.toString())) {
                                        result.put("needBackgroundCheck", true);
                                        result.put("phase", BackgroundPhase.DISPUTE.getVal());
                                    } else {
                                        result.put("needBackgroundCheck", false);
                                        result.put("phase", BackgroundPhase.FAIL.getVal());
                                    }
                                }
                                result.put("result", BackgroundResult.ALERT.getVal());
                                break;
                        }
                    }
                    return result;
                }
            } else if (StringUtils.equalsIgnoreCase(nationality, "CANADA")) {//国籍为加拿大
                CanadaBackgroundScreening canadaBackgroundScreening = canadaBackgroundScreeningDao.findByTeacherId(teacher.getId());
                currnet.add(Calendar.MONTH, -1);
                result.put("nationality","CANADA");
                //第一次进行背调
                if (null == canadaBackgroundScreening) {
                    result.put("needBackgroundCheck", true);
                    result.put("phase", BackgroundPhase.START);
                    return result;
                }
                //超过两年需要背调，不超过两年直接显示result
                if (currnet.getTime().after(backgroundScreening.getUpdateTime())) {
                    result.put("needBackgroundCheck", true);
                    result.put("phase", BackgroundPhase.START.getVal());
                } else {
                    result.put("needBackgroundCheck", false);
                    result.put("result", canadaBackgroundScreening.getResult());
                }
                return result;
            }

        } else {
            result.put("needBackgroundCheck", false);
        }

        return result;
    }


    public Map<String,Object> getUsaBackgroundStatus(Teacher teacher){
        Map<String, Object> result = Maps.newHashMap();
        Calendar currnet = Calendar.getInstance();
        currnet.add(Calendar.MONTH, -1);
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacher.getId());
        //合同即将到期需进行背调
        if (teacher.getContractEndDate().after(currnet.getTime()) ) {
            //没有背调结果，即第一次开始背调
            if (null == backgroundScreening) {
                result.put("needBackgroundCheck", true);
                result.put("phase", BackgroundPhase.START);
                return result;
            } else {
                boolean in5Days = false;
                long screeningId = backgroundScreening.getId();
                currnet.add(Calendar.MONTH, 1);
                //TODO
                Date adverseTime = backgroundAdverseDao.findUpdateTimeByScreeningIdTopOne(screeningId);
                currnet.add(Calendar.DATE, 5);
                if (adverseTime.before(currnet.getTime())) {
                    in5Days = true;
                }
                currnet.add(Calendar.DATE, -5);
                currnet.add(Calendar.YEAR, -2);
                //上次背调超过两年需要进行背调，不超过两年需要根据result和disputeStatus进行判断
                if (!currnet.getTime().after(backgroundScreening.getUpdateTime())) {
                    result.put("needBackgroundCheck", true);
                    result.put("phase", BackgroundPhase.START.getVal());
                } else {
                    String backgroundResult = backgroundScreening.getResult();
                    String disputeStatus = backgroundScreening.getStatus();
                    switch (backgroundResult) {
                        //开始背调，背调结果结果为N/A
                        case "N/A":
                            result.put("needBackgroundCheck", true);
                            result.put("phase", BackgroundPhase.PENDING.getVal());
                            result.put("result", BackgroundResult.NA.getVal());
                            break;
                        //背调结果为CLEAR，不再需要进行背调
                        case "CLEAR":
                            result.put("needBackgroundCheck", false);
                            result.put("phase", BackgroundPhase.CLEAR.getVal());
                            result.put("result", BackgroundResult.CLEAR.getVal());
                            break;
                        //背调结果为ALERT，需根据disputeStatus进行判断
                        case "ALERT":
                            //disputeStatus为null
                            if (null == disputeStatus) {
                                //在5天内可以进行dispute，超过5天不允许在进行dispute自动FAIL
                                if (in5Days) {
                                    result.put("needBackgroundCheck", true);
                                    result.put("phase", BackgroundPhase.PREADVERSE.getVal());
                                    result.put("result", BackgroundResult.ALERT.getVal());
                                } else {
                                    result.put("needBackgroundCheck", false);
                                    result.put("result", BackgroundResult.FAIL.getVal());
                                    result.put("phase", BackgroundPhase.FAIL.getVal());
                                }

                            } else {
                                //diaputeStatus为ACTIVE表明正在进行dispute，为DEACTIVATED表明disputed失败
                                if (StringUtils.equalsIgnoreCase(disputeStatus, DisputeStatus.ACTIVE.toString())) {
                                    result.put("needBackgroundCheck", true);
                                    result.put("phase", BackgroundPhase.DISPUTE.getVal());
                                    result.put("result",BackgroundResult.ALERT.getVal());
                                } else {
                                    result.put("needBackgroundCheck", false);
                                    result.put("phase", BackgroundPhase.FAIL.getVal());
                                    result.put("result",BackgroundResult.FAIL.getVal());
                                }
                            }
                            break;
                    }
                }

            }
            return result;
        }else {
            result.put("needBackgroundCheck", false);
        }
        return result;
    }

    public Map<String,Object> getCanadabackgroundStatus(Teacher teacher){
        Map<String, Object> result = Maps.newHashMap();
        Calendar currnet = Calendar.getInstance();
        currnet.add(Calendar.MONTH, -1);
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacher.getId());
        //合同即将到期需进行背调
        if (teacher.getContractEndDate().after(currnet.getTime()) ) {
            CanadaBackgroundScreening canadaBackgroundScreening = canadaBackgroundScreeningDao.findByTeacherId(teacher.getId());
            //第一次进行背调
            if (null == canadaBackgroundScreening) {
                result.put("needBackgroundCheck", true);
                result.put("phase", BackgroundPhase.START.getVal());
                result.put("result",BackgroundResult.NA.getVal());
                return result;
            }
            currnet.add(Calendar.MONTH,1);
            currnet.add(Calendar.YEAR, -2);
            //超过两年需要背调，
            if (!currnet.getTime().after(backgroundScreening.getUpdateTime())) {
                result.put("needBackgroundCheck", true);
                result.put("phase", BackgroundPhase.START.getVal());
                result.put("result",BackgroundResult.NA.getVal());
            } else { //不超过两年显示result
                if (StringUtils.equalsIgnoreCase(canadaBackgroundScreening.getResult(),"PASS")){
                    result.put("needBackgroundCheck",false);
                    result.put("result",BackgroundResult.CLEAR.getVal());
                    result.put("phase",BackgroundPhase.CLEAR.getVal());
                }else if (StringUtils.equalsIgnoreCase(canadaBackgroundScreening.getResult(),"FAIL")){
                    result.put("needBackgroundCheck",true);
                    result.put("result",BackgroundResult.ALERT.getVal());
                    result.put("phase",BackgroundPhase.FAIL.getVal());
                }else {
                    result.put("needBackgroundCheck",true);
                    result.put("result",BackgroundResult.NA.getVal());
                    result.put("phase",BackgroundPhase.PREADVERSE.getVal());
                }
            }
            return result;
        } else {
        result.put("needBackgroundCheck", false);
        }

        return result;
    }

    public Map<String, Object> getBackgroundFileStatus(long teacherId, String nationality) {
        Map<String, Object> result = Maps.newHashMap();
        List<TeacherContractFile> teacherContractFiles = teacherContractFileDao.findBackgroundFileByTeacherId(teacherId);
        boolean hasFile = false;

        if (CollectionUtils.isNotEmpty(teacherContractFiles)) {
            hasFile = true;
            if (StringUtils.equalsIgnoreCase(nationality, "United States")) {
                result.put("nationality", "USA");
                String fileResult = "";
                for (TeacherContractFile contractFile : teacherContractFiles) {

                    if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "PASS")) {
                        fileResult = "PASS";
                    } else if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "FAIL")) {
                        fileResult = "FAIL";
                    } else {
                        fileResult = "PENDING";
                    }
                }
                result.put("fileResult", fileResult);
            } else if (StringUtils.equalsIgnoreCase(nationality, "CANADA")) {
                result.put("nationality", "CANADA");
                String canadaFirstFileResult = "";
                String canadaSecondFileResult = "";
                for (TeacherContractFile contractFile : teacherContractFiles) {

                    int fileType = contractFile.getFileType();
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
                    result.put("fileResult","FAIL");
                }else if (StringUtils.equalsIgnoreCase(canadaFirstFileResult,"PASS") && StringUtils.equalsIgnoreCase(canadaSecondFileResult,"PASS")){
                    result.put("fileResult","PASS");
                }else {
                    result.put("fileResult","PENDING");
                }
            }
        } else {
            hasFile = false;
        }
        result.put("hasFile", hasFile);
        return result;

    }

}
