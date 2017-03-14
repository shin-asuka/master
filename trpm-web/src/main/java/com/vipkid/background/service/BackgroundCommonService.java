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
import com.vipkid.trpm.dao.BackgroundScreeningDao;
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
    private BackgroundScreeningDao backgroundScreeningDao;

    @Autowired
    private TeacherContractFileDao teacherContractFileDao;

    private static Logger logger = LoggerFactory.getLogger(BackgroundCommonService.class);

    public Map<String, Object> getBackgroundStatus(Teacher teacher) {
        Map<String, Object> result = Maps.newHashMap();
        Calendar currnet = Calendar.getInstance();
        currnet.add(Calendar.MONTH, 1);
        BackgroundScreening backgroundScreening = backgroundScreeningDao.findByTeacherId(teacher.getId());
        //没有背调结果，即第一次开始背调
        if (null == backgroundScreening){
            result.put("needBackgroundCheck",true);
            result.put("phase",BackgroundPhase.START);
            return result;
        }
        if (teacher.getContractEndDate().after(currnet.getTime()) || StringUtils.equals(teacher.getLifeCycle(), LifeCycle.CONTRACT_INFO.toString())) {
            boolean in5Days = false;
            currnet.add(Calendar.MONTH, -1);
            //TODO
            Date adverseTime = backgroundScreening.getUpdateTime();
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
                        if (null == disputeStatus ){
                            //在5天内可以进行dispute，超过5天不允许在进行dispute
                            if (in5Days){
                                result.put("needBackgroundCheck",true);
                                result.put("phase",BackgroundPhase.PREADVERSE.getVal());
                                result.put("result",BackgroundResult.ALERT.getVal());
                            }else{
                                result.put("needBackgroundCheck",false);
                                result.put("result",BackgroundResult.ALERT.getVal());
                                result.put("phase",BackgroundPhase.FALSE.getVal());
                            }

                        }else {
                            //diaputeStatus为ACTIVE表明正在进行dispute，为DEACTIVATED表明disputed失败
                            if (StringUtils.equalsIgnoreCase(disputeStatus, DisputeStatus.ACTIVE.toString())) {
                                result.put("needBackgroundCheck", true);
                                result.put("phase", BackgroundPhase.DISPUTE.getVal());
                            } else {
                                result.put("needBackgroundCheck", false);
                                result.put("phase", BackgroundPhase.FALSE.getVal());
                            }
                        }
                        result.put("result", BackgroundResult.ALERT.getVal());
                        break;
                }
            }
        }

        return result;
    }
    public Map<String,Object> getBackgroundFileStatus(long teacherId){
        Map<String,Object> result = Maps.newHashMap();
        List<TeacherContractFile> teacherContractFiles = teacherContractFileDao.findBackgroundFileByTeacherId(teacherId);
        Calendar current = Calendar.getInstance();
        current.add(Calendar.YEAR,1);
        boolean hasFile = false;
        boolean fileResult = false;
        boolean firstFileResult = false;
        boolean secondFileResult = false;
        List<TeacherContractFile> teacherContractFile = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(teacherContractFiles)) {
            for (TeacherContractFile contractFile:teacherContractFiles){
                int fileType = contractFile.getFileType();
                switch(fileType){
                    case 9:
                        if(current.getTime().after(contractFile.getUpdateTime())){
                            fileResult = StringUtils.equalsIgnoreCase(contractFile.getResult(),"PASS");
                            hasFile = false;
                        }else{
                            hasFile = true;
                        }
                        break;
                    case 10:
                        if(current.getTime().after(contractFile.getUpdateTime())){
                            hasFile = false;
                            firstFileResult = StringUtils.equalsIgnoreCase(contractFile.getResult(),"PASS");
                        }else{
                            hasFile = true;
                        }
                    case 11:
                        if(current.getTime().after(contractFile.getUpdateTime())){
                            secondFileResult = StringUtils.equalsIgnoreCase(contractFile.getResult(),"PASS");
                            hasFile = false;
                        }else{
                            hasFile = true;
                        }
                        break;
                }
                if (firstFileResult && secondFileResult){
                    fileResult = true;
                }
                if (hasFile)
                    break;

            }

        }
        result.put("hasFile",hasFile);
        result.put("fileResult",fileResult);
        return result;

    }

}
