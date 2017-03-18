package com.vipkid.background.api.sterling.service;



import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.vipkid.background.api.sterling.controller.SterlingApiController;
import com.vipkid.background.api.sterling.dto.*;

import com.vipkid.background.enums.TeacherPortalCodeEnum;
import com.vipkid.background.service.BackgroundCheckService;
import com.vipkid.background.vo.BackgroundCheckVo;
import com.vipkid.http.utils.JacksonUtils;
import com.vipkid.trpm.dao.BackgroundAdverseDao;
import com.vipkid.trpm.dao.BackgroundReportDao;
import com.vipkid.trpm.dao.BackgroundScreeningV2Dao;
import com.vipkid.trpm.entity.BackgroundAdverse;
import com.vipkid.trpm.entity.BackgroundReport;
import com.vipkid.trpm.entity.BackgroundScreening;
import com.vipkid.trpm.util.DateUtils;
import com.vipkid.trpm.util.Function;
import com.vipkid.trpm.util.MapUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.security.UnresolvedPermission;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by liyang on 2017/3/11.
 */

@Service
public class SterlingService {

    private static final Logger logger = LoggerFactory.getLogger(SterlingApiController.class);

    private static ScreeningInputDto.CallBack callback = null;

    static{
        callback = new ScreeningInputDto.CallBack();
        callback.setUri(PropertyConfigurer.stringValue("background.sterling.callback.uri"));
    }


    @Resource
    private BackgroundReportDao backgroundReportDao;

    @Resource
    private BackgroundAdverseDao backgroundAdverseDao;

    @Resource
    private BackgroundScreeningV2Dao backgroundScreeningV2Dao;

    @Resource
    private BackgroundCheckService backgroundCheckService;





    public CandidateOutputDto createCandidate(CandidateInputDto candidateInputDto) {

        BackgroundScreening newBackgroundScreening = new BackgroundScreening();
        newBackgroundScreening.setTeacherId(candidateInputDto.getTeacherId());
        newBackgroundScreening.setUpdateTime(new Date());
        newBackgroundScreening.setCreateTime(new Date());
        backgroundScreeningV2Dao.dynamicInsert(newBackgroundScreening);
        if(StringUtils.isNotBlank(candidateInputDto.getCandidateId())){
            //2年后参数背景调查 update信息，新插入记录
            SterlingCandidate sterlingCandidate = SterlingApiUtils.updateCandidate(candidateInputDto);
            if(CollectionUtils.isNotEmpty(sterlingCandidate.getErrors())){
                logger.warn("param:{},error:{}", JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(sterlingCandidate.getErrors()));
                return new CandidateOutputDto(Integer.valueOf(sterlingCandidate.getErrors().get(0).getErrorCode()),
                        sterlingCandidate.getErrors().get(0).getErrorMessage());
            }
            BackgroundScreening backgroundScreening = transformBackgroundScreening(candidateInputDto,sterlingCandidate);
            backgroundScreening.setId(newBackgroundScreening.getId());
            backgroundScreeningV2Dao.update(backgroundScreening);
            return new CandidateOutputDto(backgroundScreening.getId());
        }

        //新插入
        SterlingCandidate sterlingCandidate = SterlingApiUtils.createCandidate(candidateInputDto);
        if(CollectionUtils.isNotEmpty(sterlingCandidate.getErrors())){
            logger.warn("param:{},error:{}", JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(sterlingCandidate.getErrors()));
            //
            return new CandidateOutputDto(Integer.valueOf(sterlingCandidate.getErrors().get(0).getErrorCode()),
                    sterlingCandidate.getErrors().get(0).getErrorMessage());
        }

        BackgroundScreening backgroundScreening = transformBackgroundScreening(candidateInputDto,sterlingCandidate);
        backgroundScreening.setId(newBackgroundScreening.getId());
        backgroundScreeningV2Dao.update(backgroundScreening);
        return new CandidateOutputDto(newBackgroundScreening.getId());
    }


    @Transactional(readOnly = false)
    public CandidateOutputDto updateCandidate(CandidateInputDto candidateInputDto) {
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(candidateInputDto.getTeacherId());

        if(StringUtils.isBlank(candidateInputDto.getCandidateId())){
            if(backgroundScreening != null ){
                candidateInputDto.setCandidateId(backgroundScreening.getCandidateId());
            }
        }

        SterlingCandidate sterlingCandidate = SterlingApiUtils.updateCandidate(candidateInputDto);
        if(CollectionUtils.isNotEmpty(sterlingCandidate.getErrors())){
            logger.warn("param:{},error:{}", JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(sterlingCandidate.getErrors()));
            return new CandidateOutputDto(Integer.valueOf(sterlingCandidate.getErrors().get(0).getErrorCode()),sterlingCandidate.getErrors().get(0).getErrorMessage());
        }

        return new CandidateOutputDto(backgroundScreening.getId());
    }





    @Transactional(readOnly = false)
    public CandidateOutputDto saveCandidate(CandidateInputDto candidateInputDto) {
        BackgroundScreening sterlingScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(candidateInputDto.getTeacherId());
        if(null == sterlingScreening){
            return createCandidate(candidateInputDto);
        }

        if(StringUtils.isBlank(sterlingScreening.getCandidateId())){
            return createCandidate(candidateInputDto);
        }
        Date updateTime = sterlingScreening.getUpdateTime();
        Calendar lastTime = Calendar.getInstance();
        lastTime.setTimeInMillis(updateTime.getTime());
        Calendar currentTime = Calendar.getInstance();
        lastTime.add(Calendar.YEAR,2);
        candidateInputDto.setCandidateId(sterlingScreening.getCandidateId());

        if(currentTime.after(lastTime)){
            return createCandidate(candidateInputDto);
        }

        return updateCandidate(candidateInputDto);

    }

    /**
     * 修补候选人数据
     * @param teacherId
     * @return
     */
    public CandidateOutputDto repairDataCandidate(Long teacherId){
        BackgroundScreening sterlingScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacherId);
        if(null == sterlingScreening){
            return new CandidateOutputDto(10000,String.format("teacherId:%s,在表中不存在",teacherId));
        }
        if(StringUtils.isNotBlank(sterlingScreening.getCandidateId())){
            return new CandidateOutputDto(10000,String.format("Candidate数据不需要修复,teacherId:%s",teacherId));
        }
        CandidateFilterDto candidateFilterDto =new CandidateFilterDto();
        BackgroundCheckVo backgroundCheckVo =backgroundCheckService.getInfoForUs(teacherId);
        candidateFilterDto.setFamilyName(backgroundCheckVo.getLastName());
        candidateFilterDto.setGivenName(backgroundCheckVo.getFirstName());
        candidateFilterDto.setLimit(0);
        candidateFilterDto.setOffset(10);
        candidateFilterDto.setEmail(backgroundCheckVo.getEmail());

        List<SterlingCandidate> sterlingCandidateList = SterlingApiUtils.getCandidateList(candidateFilterDto);
        if(CollectionUtils.isEmpty(sterlingCandidateList)){
            return new CandidateOutputDto(10000,"没有查到");
        }

        for(SterlingCandidate sterlingCandidate:sterlingCandidateList){
            BackgroundScreening screening = new BackgroundScreening();
            screening.setCandidateId(sterlingCandidate.getId());
            screening.setId(sterlingScreening.getId());
            backgroundScreeningV2Dao.update(screening);
        }
        return new CandidateOutputDto(sterlingScreening.getId());
    }

    /**
     * 修复Screening 相关的数据
     * @param backgroundSterlingId
     * @return
     */
    public ScreeningOutputDto repairDateScreeing(Long backgroundSterlingId){
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findById(backgroundSterlingId);

        if(backgroundScreening == null){
            return new ScreeningOutputDto(10000,String.format("id:%s 为ID的数据不存在",backgroundSterlingId));
        }

        if(StringUtils.isBlank(backgroundScreening.getScreeningId())){
            return new ScreeningOutputDto(10000,String.format("id:%s 为ID的数据没有screeningId",backgroundSterlingId));
        }

        SterlingScreening sterlingScreening = SterlingApiUtils.getScreening(backgroundScreening.getScreeningId());
        if(null == sterlingScreening){
            return new ScreeningOutputDto(10000,String.format("id:%s 为ID的Screening 在Sterling系统中没有查到",backgroundSterlingId));
        }

        BackgroundScreening updateBackgroundScreening = new BackgroundScreening();
        updateBackgroundScreening.setId(backgroundSterlingId);
        updateBackgroundScreening.setStatus(sterlingScreening.getStatus());
        updateBackgroundScreening.setResult(sterlingScreening.getResult());
        updateBackgroundScreening.setUpdateAt(DateUtils.convertzDateTime(sterlingScreening.getUpdatedAt()));
        updateBackgroundScreening.setUpdateTime(new Date());

        backgroundScreeningV2Dao.update(updateBackgroundScreening);

        if(CollectionUtils.isNotEmpty(sterlingScreening.getAdverseActions())){
            List<BackgroundAdverse> backgroundAdverseList =  backgroundAdverseDao.findUpdateTimeByBgScreeningId(backgroundScreening.getId());
            if(CollectionUtils.isEmpty(backgroundAdverseList)){
                //数据库里没有
                batchInsertBackgroundAdverse(sterlingScreening,backgroundScreening.getId());
            }else{
                batchUpdateBackgroundAdverse(sterlingScreening,backgroundAdverseList);

            }
        }

        return new ScreeningOutputDto(backgroundSterlingId);

    }





    @Transactional(readOnly = false)
    public ScreeningOutputDto createScreening(Long teacherId,String documentUrl) {
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacherId);
        if(backgroundScreening == null ){
            return new ScreeningOutputDto(10000,"没有找到这个老师");
        }

        ScreeningInputDto screeningInputDto = new ScreeningInputDto();
        screeningInputDto.setPackageId(PropertyConfigurer.stringValue("background.sterling.pakcageId"));
        screeningInputDto.setCandidateId(backgroundScreening.getCandidateId());
        screeningInputDto.setCallback(callback);


        SterlingScreening sterlingScreening =SterlingApiUtils.createScreening(screeningInputDto);
        if (sterlingScreening == null) {
            return new ScreeningOutputDto(10000,"没有返回结果");
        }

        if (CollectionUtils.isNotEmpty(sterlingScreening.getErrors())) {
            return new ScreeningOutputDto(Integer.valueOf(sterlingScreening.getErrors().get(0).getErrorCode()),sterlingScreening.getErrors().get(0).getErrorMessage());
        }
        //返回字段保存
        backgroundScreening.setSubmittedAt(DateUtils.convertzDateTime(sterlingScreening.getSubmittedAt()));
        backgroundScreening.setUpdateAt(DateUtils.convertzDateTime(sterlingScreening.getUpdatedAt()));
        backgroundScreening.setUpdateTime(new Date());
        backgroundScreening.setResult(sterlingScreening.getResult());
        backgroundScreening.setStatus(sterlingScreening.getStatus());
        backgroundScreening.setScreeningId(sterlingScreening.getId());
        //更新 bg_sterling_screening 表的数据
        backgroundScreeningV2Dao.update(backgroundScreening);

        if(CollectionUtils.isNotEmpty(sterlingScreening.getReportItems())){
            //插入report表
            List<BackgroundReport> backgroundReportList = transformBackgroundReport(sterlingScreening,backgroundScreening.getId());
            int row = backgroundReportDao.batchInsert(backgroundReportList);
        }

        boolean isSuccess = SterlingApiUtils.createScreeningDocument(sterlingScreening.getId(),documentUrl);
        if(isSuccess){
            return new ScreeningOutputDto(backgroundScreening.getId());
        }

        return new ScreeningOutputDto(10000,"上传文档没有成功");
    }

    public AdverseOutputDto createPreAdverse(Long teacherId) {

        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacherId);
        if(backgroundScreening == null){
            return new AdverseOutputDto(null,10000,String.format("teacherId:%s screeing记录不存在",teacherId));
        }
        List<BackgroundReport> backgroundReportList = backgroundReportDao.findByBgSterlingScreeningId(backgroundScreening.getId());
        if(CollectionUtils.isEmpty(backgroundReportList)){
            return new AdverseOutputDto(null,10000,String.format("bgScreeingId:%s report记录不存在",backgroundScreening.getId()));
        }

        List<String> reportItemIdList = backgroundReportList.stream().map(map -> map.getReportId()).collect(Collectors.toList());

        boolean preAdverseAction =SterlingApiUtils.preAdverseAction(backgroundScreening.getScreeningId(),reportItemIdList);
        if(!preAdverseAction){
            return new AdverseOutputDto(null,10000,String.format("ScreeingId:%s 请求Sterling失败",backgroundScreening.getScreeningId()));
        }
        SterlingScreening sterlingScreening = SterlingApiUtils.getScreening(backgroundScreening.getScreeningId());
        if(sterlingScreening == null){
            return new AdverseOutputDto(null,10000,String.format("ScreeingId:%s 请求Sterling失败",backgroundScreening.getScreeningId()));
        }
        if(CollectionUtils.isNotEmpty(sterlingScreening.getAdverseActions())){
            batchInsertBackgroundAdverse(sterlingScreening,backgroundScreening.getId());
        }
        return new AdverseOutputDto(teacherId,null,null);
    }


    /**
     *
     * @param candidateInputDto
     * @param sterlingCandidate
     * @return
     */
    private BackgroundScreening transformBackgroundScreening(CandidateInputDto candidateInputDto, SterlingCandidate sterlingCandidate) {
        if (candidateInputDto == null) {
            return null;
        }
        BackgroundScreening backgroundScreening = new BackgroundScreening();
        backgroundScreening.setTeacherId(candidateInputDto.getTeacherId());
        backgroundScreening.setCreateTime(new Date());
        backgroundScreening.setUpdateTime(new Date());
        backgroundScreening.setCandidateId(sterlingCandidate.getId());

        return backgroundScreening;
    }

    /**
     * 转换report表
     * @param sterlingScreening
     * @param backgroundScreeningId
     * @return
     */
    private List<BackgroundReport> transformBackgroundReport(SterlingScreening sterlingScreening,Long backgroundScreeningId){
        List<BackgroundReport> backgroundReportList = Lists.newArrayList();
        for(SterlingCallBack.ReportItem reportItem:sterlingScreening.getReportItems()){
            BackgroundReport backgroundReport =new BackgroundReport();
            backgroundReport.setBgSterlingScreeningId(backgroundScreeningId);
            backgroundReport.setScreeningId(sterlingScreening.getId());
            backgroundReport.setReportId(reportItem.getId());
            backgroundReport.setStatus(reportItem.getStatus());
            backgroundReport.setResult(reportItem.getStatus());
            backgroundReport.setUpdateTime(DateUtils.convertzDateTime(reportItem.getUpdatedAt()));
            backgroundReport.setUpdateTime(new Date());
            backgroundReport.setCreateTime(new Date());
            backgroundReport.setType(reportItem.getType());
            backgroundReportList.add(backgroundReport);
        }
        return backgroundReportList;
    }


    /**
     * 批量插入Adverse表
     * @param sterlingScreening
     * @param backgroundScreeningId
     * @return
     */
    private int batchInsertBackgroundAdverse(SterlingScreening sterlingScreening,Long backgroundScreeningId){
        List<BackgroundAdverse> backgroundAdverseList = Lists.newArrayList();

        for(SterlingCallBack.AdverseAction adverseAction:sterlingScreening.getAdverseActions()){
            BackgroundAdverse backgroundAdverse =new BackgroundAdverse();
            backgroundAdverse.setBgSterlingScreeningId(backgroundScreeningId);
            backgroundAdverse.setUpdateTime(new Date());
            backgroundAdverse.setCreateTime(new Date());
            backgroundAdverse.setScreeningId(sterlingScreening.getId());
            backgroundAdverse.setActionsId(adverseAction.getId());
            backgroundAdverse.setActionsStatus(adverseAction.getStatus());
            backgroundAdverse.setActionsUpdatedAt(DateUtils.convertzDateTime(adverseAction.getUpdatedAt()));
            backgroundAdverseList.add(backgroundAdverse);
        }
        return backgroundAdverseDao.batchInsert(backgroundAdverseList);
    }


    /**
     * 批量修改Adverse 表
     * @param sterlingScreening
     * @param backgroundAdverseList
     */
    private void batchUpdateBackgroundAdverse(SterlingScreening sterlingScreening,List<BackgroundAdverse> backgroundAdverseList) {

        //数据库里有
        Map<String,SterlingCallBack.AdverseAction> adverseActionMap = MapUtils.transformListToMap(sterlingScreening.getAdverseActions(), new Function<String, SterlingCallBack.AdverseAction>() {
            @Override
            public String apply(SterlingCallBack.AdverseAction input) {
                return input.getId();
            }
        });
        List<BackgroundAdverse> updateBackgroundAdverseList =Lists.newArrayList();
        for(BackgroundAdverse backgroundAdverse :backgroundAdverseList){
            SterlingCallBack.AdverseAction remoteAdverseAction = adverseActionMap.get(backgroundAdverse.getActionsId());
            if(null == remoteAdverseAction){
                continue;
            }
            BackgroundAdverse updateBackgroundAdverse =new BackgroundAdverse();
            updateBackgroundAdverse.setUpdateTime(new Date());
            updateBackgroundAdverse.setId(backgroundAdverse.getId());
            updateBackgroundAdverse.setActionsStatus(remoteAdverseAction.getStatus());
            updateBackgroundAdverse.setActionsUpdatedAt(DateUtils.convertzDateTime(remoteAdverseAction.getUpdatedAt()));
            updateBackgroundAdverseList.add(updateBackgroundAdverse);
        }

        if(CollectionUtils.isEmpty(updateBackgroundAdverseList)){
            return ;
        }

        for(BackgroundAdverse backgroundAdverse:updateBackgroundAdverseList){
            backgroundAdverseDao.update(backgroundAdverse);
        }
    }



    @Deprecated
    public boolean  saveTestDate(){
        String json = "[{\"id\":\"001000062954785\",\"packageId\":\"182951\",\"candidateId\":\"3265042\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954785\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:51:27Z\"}],\"submittedAt\":\"2017-03-16T11:51:27Z\",\"updatedAt\":\"2017-03-16T11:51:27Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954785\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954785\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954786\",\"packageId\":\"182951\",\"candidateId\":\"3265043\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954786\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:51:33Z\"}],\"submittedAt\":\"2017-03-16T11:51:33Z\",\"updatedAt\":\"2017-03-16T11:51:33Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954786\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954786\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954787\",\"packageId\":\"182951\",\"candidateId\":\"3265044\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954787\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:51:38Z\"}],\"submittedAt\":\"2017-03-16T11:51:38Z\",\"updatedAt\":\"2017-03-16T11:51:38Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954787\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954787\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954789\",\"packageId\":\"182951\",\"candidateId\":\"3265045\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954789\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:51:43Z\"}],\"submittedAt\":\"2017-03-16T11:51:43Z\",\"updatedAt\":\"2017-03-16T11:51:43Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954789\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954789\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954790\",\"packageId\":\"182951\",\"candidateId\":\"3265046\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954790\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:51:49Z\"}],\"submittedAt\":\"2017-03-16T11:51:49Z\",\"updatedAt\":\"2017-03-16T11:51:49Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954790\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954790\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954791\",\"packageId\":\"182951\",\"candidateId\":\"3265047\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954791\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:51:54Z\"}],\"submittedAt\":\"2017-03-16T11:51:54Z\",\"updatedAt\":\"2017-03-16T11:51:54Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954791\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954791\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954792\",\"packageId\":\"182951\",\"candidateId\":\"3265048\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954792\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:51:59Z\"}],\"submittedAt\":\"2017-03-16T11:51:59Z\",\"updatedAt\":\"2017-03-16T11:51:59Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954792\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954792\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954793\",\"packageId\":\"182951\",\"candidateId\":\"3265049\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954793\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:52:03Z\"}],\"submittedAt\":\"2017-03-16T11:52:03Z\",\"updatedAt\":\"2017-03-16T11:52:03Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954793\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954793\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954794\",\"packageId\":\"182951\",\"candidateId\":\"3265050\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954794\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:52:08Z\"}],\"submittedAt\":\"2017-03-16T11:52:08Z\",\"updatedAt\":\"2017-03-16T11:52:08Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954794\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954794\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954795\",\"packageId\":\"182951\",\"candidateId\":\"3265051\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954795\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:52:14Z\"}],\"submittedAt\":\"2017-03-16T11:52:14Z\",\"updatedAt\":\"2017-03-16T11:52:14Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954795\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954795\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954796\",\"packageId\":\"182951\",\"candidateId\":\"3265052\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954796\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:52:18Z\"}],\"submittedAt\":\"2017-03-16T11:52:19Z\",\"updatedAt\":\"2017-03-16T11:52:19Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954796\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954796\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954797\",\"packageId\":\"182951\",\"candidateId\":\"3265053\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954797\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:52:24Z\"}],\"submittedAt\":\"2017-03-16T11:52:24Z\",\"updatedAt\":\"2017-03-16T11:52:24Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954797\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954797\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954798\",\"packageId\":\"182951\",\"candidateId\":\"3265054\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954798\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:52:31Z\"}],\"submittedAt\":\"2017-03-16T11:52:31Z\",\"updatedAt\":\"2017-03-16T11:52:31Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954798\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954798\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954799\",\"packageId\":\"182951\",\"candidateId\":\"3265055\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954799\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:52:35Z\"}],\"submittedAt\":\"2017-03-16T11:52:35Z\",\"updatedAt\":\"2017-03-16T11:52:35Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954799\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954799\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954800\",\"packageId\":\"182951\",\"candidateId\":\"3265057\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954800\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:52:43Z\"}],\"submittedAt\":\"2017-03-16T11:52:43Z\",\"updatedAt\":\"2017-03-16T11:52:43Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954800\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954800\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954801\",\"packageId\":\"182951\",\"candidateId\":\"3265058\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954801\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:52:52Z\"}],\"submittedAt\":\"2017-03-16T11:52:52Z\",\"updatedAt\":\"2017-03-16T11:52:52Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954801\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954801\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954802\",\"packageId\":\"182951\",\"candidateId\":\"3265061\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954802\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:52:57Z\"}],\"submittedAt\":\"2017-03-16T11:52:57Z\",\"updatedAt\":\"2017-03-16T11:52:57Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954802\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954802\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954803\",\"packageId\":\"182951\",\"candidateId\":\"3265062\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954803\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:53:01Z\"}],\"submittedAt\":\"2017-03-16T11:53:02Z\",\"updatedAt\":\"2017-03-16T11:53:02Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954803\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954803\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954804\",\"packageId\":\"182951\",\"candidateId\":\"3265063\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954804\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:53:06Z\"}],\"submittedAt\":\"2017-03-16T11:53:06Z\",\"updatedAt\":\"2017-03-16T11:53:06Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954804\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954804\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954805\",\"packageId\":\"182951\",\"candidateId\":\"3265064\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954805\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:53:11Z\"}],\"submittedAt\":\"2017-03-16T11:53:11Z\",\"updatedAt\":\"2017-03-16T11:53:11Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954805\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954805\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954806\",\"packageId\":\"182951\",\"candidateId\":\"3265065\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954806\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:53:18Z\"}],\"submittedAt\":\"2017-03-16T11:53:18Z\",\"updatedAt\":\"2017-03-16T11:53:18Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954806\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954806\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954807\",\"packageId\":\"182951\",\"candidateId\":\"3265066\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954807\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:53:23Z\"}],\"submittedAt\":\"2017-03-16T11:53:23Z\",\"updatedAt\":\"2017-03-16T11:53:23Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954807\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954807\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954808\",\"packageId\":\"182951\",\"candidateId\":\"3265067\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954808\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:53:28Z\"}],\"submittedAt\":\"2017-03-16T11:53:28Z\",\"updatedAt\":\"2017-03-16T11:53:28Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954808\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954808\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954809\",\"packageId\":\"182951\",\"candidateId\":\"3265068\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954809\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:53:35Z\"}],\"submittedAt\":\"2017-03-16T11:53:35Z\",\"updatedAt\":\"2017-03-16T11:53:35Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954809\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954809\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954811\",\"packageId\":\"182951\",\"candidateId\":\"3265069\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954811\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:53:40Z\"}],\"submittedAt\":\"2017-03-16T11:53:40Z\",\"updatedAt\":\"2017-03-16T11:53:40Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954811\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954811\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954812\",\"packageId\":\"182951\",\"candidateId\":\"3265070\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954812\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:53:44Z\"}],\"submittedAt\":\"2017-03-16T11:53:44Z\",\"updatedAt\":\"2017-03-16T11:53:44Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954812\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954812\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954813\",\"packageId\":\"182951\",\"candidateId\":\"3265071\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954813\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:53:50Z\"}],\"submittedAt\":\"2017-03-16T11:53:50Z\",\"updatedAt\":\"2017-03-16T11:53:50Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954813\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954813\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954814\",\"packageId\":\"182951\",\"candidateId\":\"3265072\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954814\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:53:54Z\"}],\"submittedAt\":\"2017-03-16T11:53:54Z\",\"updatedAt\":\"2017-03-16T11:53:54Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954814\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954814\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954815\",\"packageId\":\"182951\",\"candidateId\":\"3265073\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954815\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:53:59Z\"}],\"submittedAt\":\"2017-03-16T11:53:59Z\",\"updatedAt\":\"2017-03-16T11:53:59Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954815\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954815\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954816\",\"packageId\":\"182951\",\"candidateId\":\"3265074\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954816\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:54:03Z\"}],\"submittedAt\":\"2017-03-16T11:54:03Z\",\"updatedAt\":\"2017-03-16T11:54:03Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954816\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954816\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954817\",\"packageId\":\"182951\",\"candidateId\":\"3265075\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954817\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:54:08Z\"}],\"submittedAt\":\"2017-03-16T11:54:08Z\",\"updatedAt\":\"2017-03-16T11:54:08Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954817\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954817\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954818\",\"packageId\":\"182951\",\"candidateId\":\"3265076\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954818\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:54:12Z\"}],\"submittedAt\":\"2017-03-16T11:54:12Z\",\"updatedAt\":\"2017-03-16T11:54:12Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954818\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954818\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954819\",\"packageId\":\"182951\",\"candidateId\":\"3265077\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954819\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:54:17Z\"}],\"submittedAt\":\"2017-03-16T11:54:17Z\",\"updatedAt\":\"2017-03-16T11:54:17Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954819\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954819\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954820\",\"packageId\":\"182951\",\"candidateId\":\"3265078\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954820\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:54:22Z\"}],\"submittedAt\":\"2017-03-16T11:54:22Z\",\"updatedAt\":\"2017-03-16T11:54:22Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954820\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954820\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954821\",\"packageId\":\"182951\",\"candidateId\":\"3265079\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954821\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:54:27Z\"}],\"submittedAt\":\"2017-03-16T11:54:27Z\",\"updatedAt\":\"2017-03-16T11:54:27Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954821\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954821\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954822\",\"packageId\":\"182951\",\"candidateId\":\"3265080\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954822\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:54:31Z\"}],\"submittedAt\":\"2017-03-16T11:54:31Z\",\"updatedAt\":\"2017-03-16T11:54:31Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954822\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954822\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954823\",\"packageId\":\"182951\",\"candidateId\":\"3265081\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954823\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:54:38Z\"}],\"submittedAt\":\"2017-03-16T11:54:38Z\",\"updatedAt\":\"2017-03-16T11:54:38Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954823\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954823\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954824\",\"packageId\":\"182951\",\"candidateId\":\"3265082\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954824\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:54:42Z\"}],\"submittedAt\":\"2017-03-16T11:54:42Z\",\"updatedAt\":\"2017-03-16T11:54:42Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954824\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954824\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954825\",\"packageId\":\"182951\",\"candidateId\":\"3265083\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954825\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:54:47Z\"}],\"submittedAt\":\"2017-03-16T11:54:47Z\",\"updatedAt\":\"2017-03-16T11:54:47Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954825\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954825\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954826\",\"packageId\":\"182951\",\"candidateId\":\"3265084\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954826\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:54:51Z\"}],\"submittedAt\":\"2017-03-16T11:54:51Z\",\"updatedAt\":\"2017-03-16T11:54:51Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954826\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954826\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954827\",\"packageId\":\"182951\",\"candidateId\":\"3265085\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954827\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:54:56Z\"}],\"submittedAt\":\"2017-03-16T11:54:56Z\",\"updatedAt\":\"2017-03-16T11:54:56Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954827\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954827\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954828\",\"packageId\":\"182951\",\"candidateId\":\"3265086\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954828\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:00Z\"}],\"submittedAt\":\"2017-03-16T11:55:00Z\",\"updatedAt\":\"2017-03-16T11:55:00Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954828\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954828\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954829\",\"packageId\":\"182951\",\"candidateId\":\"3265087\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954829\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:04Z\"}],\"submittedAt\":\"2017-03-16T11:55:04Z\",\"updatedAt\":\"2017-03-16T11:55:04Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954829\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954829\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954830\",\"packageId\":\"182951\",\"candidateId\":\"3265088\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954830\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:09Z\"}],\"submittedAt\":\"2017-03-16T11:55:09Z\",\"updatedAt\":\"2017-03-16T11:55:09Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954830\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954830\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954831\",\"packageId\":\"182951\",\"candidateId\":\"3265089\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954831\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:13Z\"}],\"submittedAt\":\"2017-03-16T11:55:13Z\",\"updatedAt\":\"2017-03-16T11:55:13Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954831\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954831\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954832\",\"packageId\":\"182951\",\"candidateId\":\"3265090\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954832\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:17Z\"}],\"submittedAt\":\"2017-03-16T11:55:17Z\",\"updatedAt\":\"2017-03-16T11:55:17Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954832\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954832\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954833\",\"packageId\":\"182951\",\"candidateId\":\"3265092\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954833\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:22Z\"}],\"submittedAt\":\"2017-03-16T11:55:22Z\",\"updatedAt\":\"2017-03-16T11:55:22Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954833\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954833\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954834\",\"packageId\":\"182951\",\"candidateId\":\"3265093\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954834\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:26Z\"}],\"submittedAt\":\"2017-03-16T11:55:26Z\",\"updatedAt\":\"2017-03-16T11:55:26Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954834\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954834\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954835\",\"packageId\":\"182951\",\"candidateId\":\"3265094\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954835\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:31Z\"}],\"submittedAt\":\"2017-03-16T11:55:31Z\",\"updatedAt\":\"2017-03-16T11:55:31Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954835\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954835\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954836\",\"packageId\":\"182951\",\"candidateId\":\"3265095\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954836\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:36Z\"}],\"submittedAt\":\"2017-03-16T11:55:36Z\",\"updatedAt\":\"2017-03-16T11:55:36Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954836\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954836\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954837\",\"packageId\":\"182951\",\"candidateId\":\"3265096\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954837\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:42Z\"}],\"submittedAt\":\"2017-03-16T11:55:42Z\",\"updatedAt\":\"2017-03-16T11:55:42Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954837\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954837\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954838\",\"packageId\":\"182951\",\"candidateId\":\"3265097\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954838\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:46Z\"}],\"submittedAt\":\"2017-03-16T11:55:46Z\",\"updatedAt\":\"2017-03-16T11:55:46Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954838\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954838\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954839\",\"packageId\":\"182951\",\"candidateId\":\"3265098\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954839\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:51Z\"}],\"submittedAt\":\"2017-03-16T11:55:51Z\",\"updatedAt\":\"2017-03-16T11:55:51Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954839\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954839\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954840\",\"packageId\":\"182951\",\"candidateId\":\"3265099\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954840\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:55:55Z\"}],\"submittedAt\":\"2017-03-16T11:55:55Z\",\"updatedAt\":\"2017-03-16T11:55:55Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954840\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954840\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954841\",\"packageId\":\"182951\",\"candidateId\":\"3265100\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954841\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:00Z\"}],\"submittedAt\":\"2017-03-16T11:56:00Z\",\"updatedAt\":\"2017-03-16T11:56:00Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954841\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954841\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954842\",\"packageId\":\"182951\",\"candidateId\":\"3265101\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954842\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:05Z\"}],\"submittedAt\":\"2017-03-16T11:56:05Z\",\"updatedAt\":\"2017-03-16T11:56:05Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954842\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954842\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954843\",\"packageId\":\"182951\",\"candidateId\":\"3265102\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954843\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:10Z\"}],\"submittedAt\":\"2017-03-16T11:56:10Z\",\"updatedAt\":\"2017-03-16T11:56:10Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954843\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954843\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954844\",\"packageId\":\"182951\",\"candidateId\":\"3265103\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954844\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:14Z\"}],\"submittedAt\":\"2017-03-16T11:56:14Z\",\"updatedAt\":\"2017-03-16T11:56:14Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954844\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954844\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954845\",\"packageId\":\"182951\",\"candidateId\":\"3265104\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954845\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:18Z\"}],\"submittedAt\":\"2017-03-16T11:56:18Z\",\"updatedAt\":\"2017-03-16T11:56:18Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954845\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954845\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954846\",\"packageId\":\"182951\",\"candidateId\":\"3265105\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954846\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:23Z\"}],\"submittedAt\":\"2017-03-16T11:56:23Z\",\"updatedAt\":\"2017-03-16T11:56:23Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954846\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954846\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954847\",\"packageId\":\"182951\",\"candidateId\":\"3265106\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954847\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:28Z\"}],\"submittedAt\":\"2017-03-16T11:56:28Z\",\"updatedAt\":\"2017-03-16T11:56:28Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954847\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954847\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954848\",\"packageId\":\"182951\",\"candidateId\":\"3265107\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954848\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:33Z\"}],\"submittedAt\":\"2017-03-16T11:56:33Z\",\"updatedAt\":\"2017-03-16T11:56:33Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954848\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954848\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954849\",\"packageId\":\"182951\",\"candidateId\":\"3265108\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954849\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:37Z\"}],\"submittedAt\":\"2017-03-16T11:56:37Z\",\"updatedAt\":\"2017-03-16T11:56:37Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954849\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954849\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954850\",\"packageId\":\"182951\",\"candidateId\":\"3265109\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954850\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:42Z\"}],\"submittedAt\":\"2017-03-16T11:56:42Z\",\"updatedAt\":\"2017-03-16T11:56:42Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954850\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954850\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954851\",\"packageId\":\"182951\",\"candidateId\":\"3265110\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954851\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:48Z\"}],\"submittedAt\":\"2017-03-16T11:56:49Z\",\"updatedAt\":\"2017-03-16T11:56:49Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954851\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954851\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954852\",\"packageId\":\"182951\",\"candidateId\":\"3265111\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954852\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:54Z\"}],\"submittedAt\":\"2017-03-16T11:56:54Z\",\"updatedAt\":\"2017-03-16T11:56:54Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954852\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954852\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954853\",\"packageId\":\"182951\",\"candidateId\":\"3265112\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954853\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:56:59Z\"}],\"submittedAt\":\"2017-03-16T11:56:59Z\",\"updatedAt\":\"2017-03-16T11:56:59Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954853\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954853\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954854\",\"packageId\":\"182951\",\"candidateId\":\"3265113\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954854\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:57:04Z\"}],\"submittedAt\":\"2017-03-16T11:57:04Z\",\"updatedAt\":\"2017-03-16T11:57:04Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954854\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954854\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954855\",\"packageId\":\"182951\",\"candidateId\":\"3265114\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954855\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:57:08Z\"}],\"submittedAt\":\"2017-03-16T11:57:08Z\",\"updatedAt\":\"2017-03-16T11:57:08Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954855\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954855\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954856\",\"packageId\":\"182951\",\"candidateId\":\"3265115\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954856\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:57:13Z\"}],\"submittedAt\":\"2017-03-16T11:57:13Z\",\"updatedAt\":\"2017-03-16T11:57:13Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954856\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954856\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954857\",\"packageId\":\"182951\",\"candidateId\":\"3265116\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954857\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:57:18Z\"}],\"submittedAt\":\"2017-03-16T11:57:18Z\",\"updatedAt\":\"2017-03-16T11:57:18Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954857\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954857\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954858\",\"packageId\":\"182951\",\"candidateId\":\"3265117\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954858\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:57:22Z\"}],\"submittedAt\":\"2017-03-16T11:57:22Z\",\"updatedAt\":\"2017-03-16T11:57:22Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954858\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954858\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954859\",\"packageId\":\"182951\",\"candidateId\":\"3265118\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954859\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:57:27Z\"}],\"submittedAt\":\"2017-03-16T11:57:27Z\",\"updatedAt\":\"2017-03-16T11:57:27Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954859\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954859\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954860\",\"packageId\":\"182951\",\"candidateId\":\"3265119\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954860\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:57:32Z\"}],\"submittedAt\":\"2017-03-16T11:57:32Z\",\"updatedAt\":\"2017-03-16T11:57:32Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954860\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954860\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954861\",\"packageId\":\"182951\",\"candidateId\":\"3265120\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954861\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:57:36Z\"}],\"submittedAt\":\"2017-03-16T11:57:36Z\",\"updatedAt\":\"2017-03-16T11:57:36Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954861\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954861\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954862\",\"packageId\":\"182951\",\"candidateId\":\"3265121\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954862\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:57:40Z\"}],\"submittedAt\":\"2017-03-16T11:57:41Z\",\"updatedAt\":\"2017-03-16T11:57:41Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954862\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954862\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954863\",\"packageId\":\"182951\",\"candidateId\":\"3265122\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954863\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:57:45Z\"}],\"submittedAt\":\"2017-03-16T11:57:45Z\",\"updatedAt\":\"2017-03-16T11:57:45Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954863\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954863\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954864\",\"packageId\":\"182951\",\"candidateId\":\"3265123\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954864\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:57:51Z\"}],\"submittedAt\":\"2017-03-16T11:57:51Z\",\"updatedAt\":\"2017-03-16T11:57:51Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954864\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954864\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954865\",\"packageId\":\"182951\",\"candidateId\":\"3265124\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954865\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:57:56Z\"}],\"submittedAt\":\"2017-03-16T11:57:56Z\",\"updatedAt\":\"2017-03-16T11:57:56Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954865\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954865\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954866\",\"packageId\":\"182951\",\"candidateId\":\"3265125\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954866\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:58:01Z\"}],\"submittedAt\":\"2017-03-16T11:58:01Z\",\"updatedAt\":\"2017-03-16T11:58:01Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954866\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954866\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954867\",\"packageId\":\"182951\",\"candidateId\":\"3265126\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954867\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:58:06Z\"}],\"submittedAt\":\"2017-03-16T11:58:06Z\",\"updatedAt\":\"2017-03-16T11:58:06Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954867\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954867\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954868\",\"packageId\":\"182951\",\"candidateId\":\"3265127\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954868\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:58:11Z\"}],\"submittedAt\":\"2017-03-16T11:58:11Z\",\"updatedAt\":\"2017-03-16T11:58:11Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954868\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954868\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954869\",\"packageId\":\"182951\",\"candidateId\":\"3265128\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954869\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:58:16Z\"}],\"submittedAt\":\"2017-03-16T11:58:16Z\",\"updatedAt\":\"2017-03-16T11:58:16Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954869\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954869\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954870\",\"packageId\":\"182951\",\"candidateId\":\"3265129\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954870\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:58:20Z\"}],\"submittedAt\":\"2017-03-16T11:58:20Z\",\"updatedAt\":\"2017-03-16T11:58:20Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954870\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954870\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954871\",\"packageId\":\"182951\",\"candidateId\":\"3265130\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954871\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:58:25Z\"}],\"submittedAt\":\"2017-03-16T11:58:25Z\",\"updatedAt\":\"2017-03-16T11:58:25Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954871\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954871\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954873\",\"packageId\":\"182951\",\"candidateId\":\"3265131\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954873\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:58:29Z\"}],\"submittedAt\":\"2017-03-16T11:58:29Z\",\"updatedAt\":\"2017-03-16T11:58:29Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954873\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954873\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954874\",\"packageId\":\"182951\",\"candidateId\":\"3265132\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954874\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:58:34Z\"}],\"submittedAt\":\"2017-03-16T11:58:34Z\",\"updatedAt\":\"2017-03-16T11:58:34Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954874\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954874\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954875\",\"packageId\":\"182951\",\"candidateId\":\"3265133\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954875\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:58:38Z\"}],\"submittedAt\":\"2017-03-16T11:58:38Z\",\"updatedAt\":\"2017-03-16T11:58:38Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954875\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954875\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954887\",\"packageId\":\"182951\",\"candidateId\":\"3265134\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954887\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:58:44Z\"}],\"submittedAt\":\"2017-03-16T11:58:44Z\",\"updatedAt\":\"2017-03-16T11:58:44Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954887\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954887\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954889\",\"packageId\":\"182951\",\"candidateId\":\"3265135\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954889\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:58:49Z\"}],\"submittedAt\":\"2017-03-16T11:58:50Z\",\"updatedAt\":\"2017-03-16T11:58:50Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954889\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954889\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954890\",\"packageId\":\"182951\",\"candidateId\":\"3265136\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954890\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:58:56Z\"}],\"submittedAt\":\"2017-03-16T11:58:56Z\",\"updatedAt\":\"2017-03-16T11:58:56Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954890\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954890\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954892\",\"packageId\":\"182951\",\"candidateId\":\"3265137\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954892\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:59:01Z\"}],\"submittedAt\":\"2017-03-16T11:59:01Z\",\"updatedAt\":\"2017-03-16T11:59:01Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954892\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954892\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954893\",\"packageId\":\"182951\",\"candidateId\":\"3265138\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954893\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:59:06Z\"}],\"submittedAt\":\"2017-03-16T11:59:06Z\",\"updatedAt\":\"2017-03-16T11:59:06Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954893\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954893\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954896\",\"packageId\":\"182951\",\"candidateId\":\"3265139\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954896\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:59:11Z\"}],\"submittedAt\":\"2017-03-16T11:59:11Z\",\"updatedAt\":\"2017-03-16T11:59:11Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954896\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954896\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954898\",\"packageId\":\"182951\",\"candidateId\":\"3265140\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954898\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:59:15Z\"}],\"submittedAt\":\"2017-03-16T11:59:15Z\",\"updatedAt\":\"2017-03-16T11:59:15Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954898\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954898\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954901\",\"packageId\":\"182951\",\"candidateId\":\"3265141\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954901\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:59:20Z\"}],\"submittedAt\":\"2017-03-16T11:59:20Z\",\"updatedAt\":\"2017-03-16T11:59:20Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954901\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954901\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]},{\"id\":\"001000062954903\",\"packageId\":\"182951\",\"candidateId\":\"3265142\",\"status\":\"pending\",\"result\":\"n/a\",\"reportItems\":[{\"id\":\"62954903\",\"type\":\"Workers' Compensation\",\"status\":\"pending\",\"result\":\"n/a\",\"updatedAt\":\"2017-03-16T11:59:24Z\"}],\"submittedAt\":\"2017-03-16T11:59:24Z\",\"updatedAt\":\"2017-03-16T11:59:24Z\",\"links\":{\"admin\":{\"web\":\"https://integration.talentwise.com/screening/report.php?ApplicantID=62954903\",\"pdf\":\"https://integration.talentwise.com/screening/pdf-report.php?ApplicantID=62954903\"}},\"callback\":{\"uri\":\"https://a6-t.vipkid.com.cn/api/api/background/sterling/callback.json\"},\"adverseActions\":[]}]";
        List<SterlingScreening> sterlingScreenings = JacksonUtils.readJson(json, new TypeReference<List<SterlingScreening>>() {});
        if(CollectionUtils.isNotEmpty(sterlingScreenings)){
            BackgroundScreening backgroundScreening = new BackgroundScreening();
            for(SterlingScreening sterlingScreening : sterlingScreenings){
                backgroundScreening.setTeacherId(1l);
                backgroundScreening.setCandidateId(sterlingScreening.getCandidateId());
                backgroundScreening.setScreeningId(sterlingScreening.getId());
                backgroundScreening.setUpdateTime(new Date());
                backgroundScreening.setCreateTime(new Date());
                backgroundScreening.setSubmittedAt(DateUtils.convertzDateTime(sterlingScreening.getSubmittedAt()));
                backgroundScreening.setUpdateAt(DateUtils.convertzDateTime(sterlingScreening.getUpdatedAt()));
                backgroundScreening.setResult(sterlingScreening.getResult());
                backgroundScreening.setStatus(sterlingScreening.getStatus());
                backgroundScreeningV2Dao.insert(backgroundScreening);

                if(CollectionUtils.isNotEmpty(sterlingScreening.getReportItems())){
                    //插入report表
                    List<BackgroundReport> backgroundReportList = transformBackgroundReport(sterlingScreening,backgroundScreening.getId());
                    int row = backgroundReportDao.batchInsert(backgroundReportList);
                }
            }
        }
        return false;
    }



}
