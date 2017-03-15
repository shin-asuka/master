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
import com.vipkid.trpm.entity.BackgroundScreening;
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

    private static Logger logger = LoggerFactory.getLogger(BackgroundCommonService.class);

    public Map<String, Object> getBackgroundStatus(Teacher teacher) {
        Map<String, Object> result = Maps.newHashMap();
        Calendar currnet = Calendar.getInstance();
        currnet.add(Calendar.MONTH, 1);
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacher.getId());
        //没有背调结果，即第一次开始背调
        if (null == backgroundScreening) {
            result.put("needBackgroundCheck", true);
            result.put("phase", BackgroundPhase.START);
            return result;
        } else {
            if (teacher.getContractEndDate().after(currnet.getTime()) || StringUtils.equals(teacher.getLifeCycle(), LifeCycle.CONTRACT_INFO.toString())) {
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
            }

            return result;
        }
    }

    public Map<String, Object> getBackgroundFileStatus(long teacherId, String teacherCountry) {
        Map<String, Object> result = Maps.newHashMap();
        List<TeacherContractFile> teacherContractFiles = teacherContractFileDao.findBackgroundFileByTeacherId(teacherId);
        Calendar current = Calendar.getInstance();
        current.add(Calendar.YEAR, 1);
        boolean hasFile = false;
        String UsaFileResult = null;
        String CanadaFirstFileResult = null;
        String CanadaSecondFileResult = null;
        List<TeacherContractFile> teacherContractFile = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(teacherContractFiles)) {
            if (StringUtils.equalsIgnoreCase(teacherCountry, "USA")) {
                for (TeacherContractFile contractFile : teacherContractFiles) {
                    hasFile = true;
                    if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "PASS")) {
                        UsaFileResult = "pass";
                    } else if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "FAIL")) {
                        UsaFileResult = "fail";
                    } else {
                        UsaFileResult = "noResult";
                    }
                }
            } else {
                for (TeacherContractFile contractFile : teacherContractFiles) {
                    hasFile = true;
                    int fileType = contractFile.getFileType();
                    switch (fileType) {
                        case 10:
                            if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "PASS")) {
                                CanadaFirstFileResult = "pass";
                            } else if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "FAIL")) {
                                CanadaFirstFileResult = "fail";
                            } else {
                                CanadaFirstFileResult = "noResult";
                            }
                            break;
                        case 11:
                            if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "PASS")) {
                                CanadaSecondFileResult = "pass";
                            } else if (StringUtils.equalsIgnoreCase(contractFile.getResult(), "FAIL")) {
                                CanadaSecondFileResult = "fail";
                            } else {
                                CanadaSecondFileResult = "noResult";
                            }
                            break;
                    }
                }
            }
        } else {
            hasFile = false;
        }

        if (StringUtils.equalsIgnoreCase(teacherCountry, "USA")) {
            result.put("UsaFileResult", UsaFileResult);
        } else {
            result.put("CanadaFirstFileResult", CanadaFirstFileResult);
            result.put("CanadaSecondFileResult", CanadaSecondFileResult);
        }

        result.put("hasFile", hasFile);
        return result;

    }

}
