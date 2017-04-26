package com.vipkid.background.service;

import com.vipkid.background.BackgroundAdverseDao;
import com.vipkid.background.BackgroundReportDao;
import com.vipkid.background.BackgroundScreeningDao;
import com.vipkid.background.CanadaBackgroundScreeningDao;
import com.vipkid.background.api.sterling.dto.BackgroundFileStatusDto;
import com.vipkid.background.api.sterling.dto.BackgroundStatusDto;
import com.vipkid.enums.BackgroundCheckEnum.*;
import com.vipkid.enums.TeacherApplicationEnum.ContractFileType;
import com.vipkid.recruitment.dao.TeacherContractFileDao;
import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.trpm.dao.TeacherGatedLaunchDao;
import com.vipkid.trpm.entity.*;
import com.vipkid.trpm.service.portal.PersonalInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


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

    @Autowired
    private TeacherGatedLaunchDao teacherGatedLaunchDao;

    @Autowired
    private BackgroundReportDao backgroundReportDao;

    @Autowired
    private PersonalInfoService personalInfoService;

    private static Logger logger = LoggerFactory.getLogger(BackgroundCommonService.class);
    private boolean  backgroundSwitch = PropertyConfigurer.booleanValue("background.sterling.switch");


    public BackgroundStatusDto getUsaBackgroundStatus(Teacher teacher){
        BackgroundStatusDto backgroundStatusDto = new BackgroundStatusDto();

        boolean needBackgroundCheck = needBackgroundCheck(teacher.getId());
        //不在灰度列表中
//        if (!needBackgroundCheck){
//            backgroundStatusDto.setNeedBackgroundCheck(needBackgroundCheck);
//            backgroundStatusDto.setPhase("");
//            backgroundStatusDto.setResult("");
//            return backgroundStatusDto;
//        }

        Calendar current = Calendar.getInstance();
        BackgroundScreening backgroundScreening = backgroundScreeningDao.findByTeacherIdTopOne(teacher.getId());
        Date  contractEndDate = teacher.getContractEndDate();
        Calendar  remindTime = Calendar.getInstance();
        remindTime.setTime(contractEndDate);
        //合同即将到期需进行背调,提前一个月进行弹窗提示
        remindTime.add(Calendar.MONTH,-1);
        if (remindTime.getTime().before(current.getTime()) ) {
            backgroundStatusDto.setContractEndWithInOneMonth(!personalInfoService.checkHasSignNext(teacher.getId(),contractEndDate));
            //没有背调结果，即第一次开始背调
            if (null == backgroundScreening) {
                backgroundStatusDto.setNeedBackgroundCheck(true);
                backgroundStatusDto.setPhase(BackgroundPhase.START.getVal());
                backgroundStatusDto.setResult("");
                return backgroundStatusDto;
            } else {
                //boolean in5Days = false;
                long screeningId = backgroundScreening.getId();

                /*current.add(Calendar.DATE, 5);
                if (null != adverseTime && adverseTime.before(current.getTime())) {
                    in5Days = true;
                }
                current.add(Calendar.DATE, -5);*/

                current = backgroundDateCondition(current);

                //上次背调超过两年需要进行背调，不超过两年需要根据result和disputeStatus进行判断
                if (current.getTime().after(backgroundScreening.getUpdateTime())) {
                    backgroundStatusDto.setNeedBackgroundCheck(true);
                    backgroundStatusDto.setPhase(BackgroundPhase.START.getVal());
                    backgroundStatusDto.setResult("");

                } else { //mod by rentj 2017-04-26 只要是进行过背调了，不论结果如何，都不在弹出框setNeedBackgroundCheck=false
                    String backgroundResult = backgroundScreening.getResult();
                    String disputeStatus = backgroundScreening.getDisputeStatus();
                    if (null != backgroundResult) {
                        switch (backgroundResult) {
                            //开始背调，背调结果结果为N/A
                            case "n/a":
                                backgroundStatusDto.setNeedBackgroundCheck(false);
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
                                /*if (null == disputeStatus) {
                                    //在5天内可以进行dispute，超过5天不允许在进行dispute自动FAIL
                                   // if (in5Days) {

                                        backgroundStatusDto.setPhase(BackgroundPhase.PREADVERSE.getVal());
                                        backgroundStatusDto.setResult(BackgroundResult.ALERT.getVal());
                                        backgroundStatusDto.setNeedBackgroundCheck(true);

                                   *//* } else {
                                        backgroundStatusDto.setNeedBackgroundCheck(false);
                                        backgroundStatusDto.setPhase(BackgroundPhase.DIDNOTDISPUTE.getVal());
                                        backgroundStatusDto.setResult(BackgroundResult.FAIL.getVal());
                                    }*//*

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
                                }*/
                                BackgroundAdverse backgroundAdverse = backgroundAdverseDao.findByScreeningIdTopOne(screeningId);

                                if (null == backgroundAdverse){
                                    backgroundStatusDto.setPhase(BackgroundPhase.PENDING.getVal());
                                    backgroundStatusDto.setResult(BackgroundResult.NA.getVal());
                                    backgroundStatusDto.setNeedBackgroundCheck(false);
                                } else {
                                    String actionsStatus = backgroundAdverse.getActionsStatus();
                                    if (StringUtils.equalsIgnoreCase(actionsStatus,AdverseStatus.CANCELLED.getValue())){
                                        //cancelled 暂时显示30天
                                        backgroundStatusDto.setPhase(BackgroundPhase.DISPUTE.getVal());
                                        backgroundStatusDto.setResult(BackgroundResult.ALERT.getVal());
                                        backgroundStatusDto.setNeedBackgroundCheck(false);
                                    } else if (StringUtils.equalsIgnoreCase(actionsStatus,AdverseStatus.COMPLETE.getValue())){
                                        //最终结果为fail
                                        backgroundStatusDto.setPhase(BackgroundPhase.FAIL.getVal());
                                        backgroundStatusDto.setResult(BackgroundResult.FAIL.getVal());
                                        backgroundStatusDto.setNeedBackgroundCheck(false);
                                    } else {
                                        //AdverseStatus 为 initiated
                                        if (StringUtils.equalsIgnoreCase(disputeStatus,DisputeStatus.ACTIVE.getVal())){
                                            //正在进行dispute
                                            backgroundStatusDto.setPhase(BackgroundPhase.DISPUTE.getVal());
                                            backgroundStatusDto.setResult(BackgroundResult.ALERT.getVal());
                                            backgroundStatusDto.setNeedBackgroundCheck(false);
                                        } else if (StringUtils.equalsIgnoreCase(disputeStatus,DisputeStatus.DEACTIVATED.getVal())){
                                            //n天内老师没有DISPUTE
                                            backgroundStatusDto.setPhase(BackgroundPhase.DIDNOTDISPUTE.getVal());
                                            backgroundStatusDto.setResult(BackgroundResult.FAIL.getVal());
                                            backgroundStatusDto.setNeedBackgroundCheck(false);
                                        } else {
                                            //dispute_status 为 null 等待老师进行dispute
                                            backgroundStatusDto.setPhase(BackgroundPhase.PREADVERSE.getVal());
                                            backgroundStatusDto.setResult(BackgroundResult.ALERT.getVal());
                                            backgroundStatusDto.setNeedBackgroundCheck(false);
                                        }
                                    }

                                }
                                //如果只是因为ssn问题导致的alert可以认为是clear
                                List<BackgroundReport> backgroundReports = backgroundReportDao.findByBgSterlingScreeningId(screeningId);
                                boolean multiCheck = false;
                                boolean offender = false;
                                boolean criminalCheck = true;
                                for (BackgroundReport backgroundReport:backgroundReports) {
                                    String reportType = backgroundReport.getType();
                                    String reportResult = backgroundReport.getResult();
                                    if (null != reportType){
                                        switch (reportType){
                                            case ("Multi-State Instant Criminal Check"):
                                                if (StringUtils.equalsIgnoreCase(reportResult,ReportResult.COMPLETE.getValue()) ||
                                                        StringUtils.equalsIgnoreCase(reportResult,ReportResult.CLEAR.getValue())){
                                                    multiCheck = true;
                                                }
                                                break;
                                            case ("DOJ Sex Offender"):
                                                if (StringUtils.equalsIgnoreCase(reportResult,ReportResult.CLEAR.getValue())){
                                                    offender = true;
                                                }
                                                break;
                                            case ("Criminal Check by County"):
                                                if (!StringUtils.equalsIgnoreCase(reportResult,ReportResult.CLEAR.getValue())){
                                                    criminalCheck = false;
                                                }
                                                break;

                                        }
                                    }
                                }
                                if (multiCheck && offender && criminalCheck){
                                    backgroundStatusDto.setNeedBackgroundCheck(false);
                                    backgroundStatusDto.setResult(BackgroundResult.CLEAR.getVal());
                                    backgroundStatusDto.setPhase(BackgroundPhase.CLEAR.getVal());
                                }
                                break;
                        }
                    } else {
                        backgroundStatusDto.setPhase(BackgroundPhase.START.getVal());
                        backgroundStatusDto.setNeedBackgroundCheck(true);
                        backgroundStatusDto.setResult("");
                    }
                }

            }
            logger.info("获取美国老师: {} 背调状态信息 {}",teacher.getId(),backgroundStatusDto);
            return backgroundStatusDto;
        }else {
            backgroundStatusDto.setContractEndWithInOneMonth(false);
            backgroundStatusDto.setNeedBackgroundCheck(false);
            backgroundStatusDto.setPhase("");
            backgroundStatusDto.setResult("");
        }
        logger.info("获取美国老师: {} 背调状态信息 {}",teacher.getId(),backgroundStatusDto);
        return backgroundStatusDto;
    }

    public BackgroundStatusDto getCanadaBackgroundStatus(Teacher teacher){
        BackgroundStatusDto backgroundStatusDto = new BackgroundStatusDto();
        boolean needBackgroundCheck = needBackgroundCheck(teacher.getId());
        //不在灰度列表中
//        if (!needBackgroundCheck){
//            backgroundStatusDto.setNeedBackgroundCheck(needBackgroundCheck);
//            backgroundStatusDto.setPhase("");
//            backgroundStatusDto.setResult("");
//            return backgroundStatusDto;
//        }
        Calendar current = Calendar.getInstance();
        Date  contractEndDate = teacher.getContractEndDate();
        Calendar  remindTime = Calendar.getInstance();
        remindTime.setTime(contractEndDate);
        remindTime.add(Calendar.MONTH,-1);
        //合同即将到期需进行背调,提前一个月进行弹窗提示
        if (remindTime.getTime().before(current.getTime()) ) {
            backgroundStatusDto.setContractEndWithInOneMonth(!personalInfoService.checkHasSignNext(teacher.getId(),contractEndDate));
            CanadaBackgroundScreening canadaBackgroundScreening = canadaBackgroundScreeningDao.findByTeacherId(teacher.getId());
            //第一次进行背调
            if (null == canadaBackgroundScreening) {
                backgroundStatusDto.setNeedBackgroundCheck(true);
                backgroundStatusDto.setPhase(BackgroundPhase.START.getVal());
                backgroundStatusDto.setResult("");

                return backgroundStatusDto;
            }
            current = backgroundDateCondition(current);
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
                } else if (StringUtils.equalsIgnoreCase(canadaBackgroundScreening.getResult(),BackgroundResult.ALERT.getVal())){
                    backgroundStatusDto.setNeedBackgroundCheck(false);
                    backgroundStatusDto.setPhase(BackgroundPhase.FAIL.getVal());
                    backgroundStatusDto.setResult(BackgroundResult.FAIL.getVal());
                } else {
                    backgroundStatusDto.setNeedBackgroundCheck(false);
                    backgroundStatusDto.setPhase(BackgroundPhase.PENDING.getVal());
                    backgroundStatusDto.setResult(BackgroundResult.NA.getVal());
                }
            }
            logger.info("获取加拿大老师: {} 背调状态信息 {}",teacher.getId(),backgroundStatusDto);
            return backgroundStatusDto;
        } else {
            backgroundStatusDto.setContractEndWithInOneMonth(false);
            backgroundStatusDto.setPhase("");
            backgroundStatusDto.setResult("");
            backgroundStatusDto.setNeedBackgroundCheck(false);
        }

        logger.info("获取加拿大老师: {} 背调信息 {}",teacher.getId(),backgroundStatusDto);
        return backgroundStatusDto;
    }

    public BackgroundFileStatusDto getBackgroundFileStatus(long teacherId, String nationality) {
        BackgroundFileStatusDto backgroundFileStatusDto = new BackgroundFileStatusDto();
        Calendar calendar = Calendar.getInstance();
        calendar = backgroundDateCondition(calendar);
        Date timeCondition = calendar.getTime();
        List<TeacherContractFile> teacherContractFiles = teacherContractFileDao.findBackgroundFileByTeacherId(teacherId,timeCondition);
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
                    } else {
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
                    if (null == screeningId ){
                        backgroundFileStatusDto.setFileStatus(FileStatus.SAVE.getValue());
                    } else {
                        backgroundFileStatusDto.setFileStatus(FileStatus.SUBMIT.getValue());
                    }
                    int fileType = contractFile.getFileType();
                    //美国的只有一个文件fileType为 9 ，加拿大有两个文件fileType为10 、 11
                    if (fileType == ContractFileType.CANADA_BACKGROUND_CHECK_CPIC_FORM.val()){
                        canadaFirstFileResult = getCanadaFileResult(contractFile.getResult());
                    }else if (fileType == ContractFileType.CANADA_BACKGROUND_CHECK_ID2.val()){
                        canadaSecondFileResult = getCanadaFileResult(contractFile.getResult());
                    }
                } if (StringUtils.equalsIgnoreCase(canadaFirstFileResult,FileResult.FAIL.getValue()) ||
                        StringUtils.equalsIgnoreCase(canadaSecondFileResult,FileResult.FAIL.getValue())){
                    backgroundFileStatusDto.setFileResult(FileResult.FAIL.getValue());
                    //如果审核结果有一个为FAIL，则FileStatus应为SAVE
                    backgroundFileStatusDto.setFileStatus(FileStatus.SAVE.getValue());
                } else if (StringUtils.equalsIgnoreCase(canadaFirstFileResult,FileResult.PASS.getValue()) &&
                        StringUtils.equalsIgnoreCase(canadaSecondFileResult,FileResult.PASS.getValue())){
                    backgroundFileStatusDto.setFileResult(FileResult.PASS.getValue());
                } else {
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
        } else if (StringUtils.equalsIgnoreCase(result,FileResult.FAIL.getValue())){
            return FileResult.FAIL.getValue();
        } else {
            return FileResult.PENDING.getValue();
        }

    }

    public boolean needBackgroundCheck(long teacherId){
        if (!backgroundSwitch){
            long count = teacherGatedLaunchDao.countByTeacherId(teacherId);
            if (count> 0 ){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public Calendar backgroundDateCondition(Calendar calendar){
        calendar.add(Calendar.YEAR,-2);
        calendar.add(Calendar.MONTH,1);
        return calendar;
    }
}
