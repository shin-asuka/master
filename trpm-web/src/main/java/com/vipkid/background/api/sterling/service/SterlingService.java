package com.vipkid.background.api.sterling.service;



import com.google.common.collect.Lists;
import com.vipkid.background.api.sterling.controller.SterlingApiController;
import com.vipkid.background.api.sterling.dto.*;

import com.vipkid.http.utils.JacksonUtils;
import com.vipkid.trpm.dao.BackgroundAdverseDao;
import com.vipkid.trpm.dao.BackgroundReportDao;
import com.vipkid.trpm.dao.BackgroundScreeningV2Dao;
import com.vipkid.trpm.entity.BackgroundAdverse;
import com.vipkid.trpm.entity.BackgroundReport;
import com.vipkid.trpm.entity.BackgroundScreening;
import com.vipkid.trpm.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

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

//    @Resource
//    private BackgroundScreeningDao backgroundScreeningDao;

    @Resource
    private BackgroundReportDao backgroundReportDao;

    @Resource
    private BackgroundAdverseDao backgroundAdverseDao;

    @Resource
    private BackgroundScreeningV2Dao backgroundScreeningV2Dao;




    public Long createCandidate(CandidateInputDto candidateInputDto) {
        SterlingCandidate sterlingCandidate = SterlingApiUtils.createCandidate(candidateInputDto);
        if(CollectionUtils.isNotEmpty(sterlingCandidate.getErrors())){
            logger.warn("param:{},error:{}", JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(sterlingCandidate.getErrors()));
            return null;
        }

        BackgroundScreening backgroundScreening = transformBackgroundScreening(candidateInputDto,sterlingCandidate);

        return backgroundScreeningV2Dao.insert(backgroundScreening);
    }

    public Long updateCandidate(CandidateInputDto candidateInputDto) {
        SterlingCandidate sterlingCandidate = SterlingApiUtils.updateCandidate(candidateInputDto);
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(candidateInputDto.getTeacherId());

        return backgroundScreening.getId();
    }

    @Transactional(readOnly = false)
    public Long createScreening(Long teacherId,String documentUrl) {
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacherId);
        if(backgroundScreening == null ){
            return null;
        }
        ScreeningInputDto screeningInputDto = new ScreeningInputDto();
        screeningInputDto.setPackageId(PropertyConfigurer.stringValue("background.sterling.pakcageId"));
        screeningInputDto.setCandidateId(backgroundScreening.getCandidateId());
        screeningInputDto.setCallback(callback);


        SterlingScreening sterlingScreening =SterlingApiUtils.createScreening(screeningInputDto);
        if (sterlingScreening == null) {
            return null;
        }

        if (CollectionUtils.isNotEmpty(sterlingScreening.getErrors())) {
            return null;
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
            List<BackgroundReport> backgroundReportList = Lists.newArrayList();
            for(SterlingCallBack.ReportItem reportItem:sterlingScreening.getReportItems()){
                BackgroundReport backgroundReport =new BackgroundReport();
                backgroundReport.setBgSterlingScreeningId(backgroundScreening.getId());
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
            int row = backgroundReportDao.batchInsert(backgroundReportList);
        }

        boolean isSuccess = SterlingApiUtils.createScreeningDocument(sterlingScreening.getId(),documentUrl);
        if(isSuccess){
            return backgroundScreening.getId();
        }

        return null;
    }

    public Integer createPreAdverse(Long teacherId) {

        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacherId);
        if(backgroundScreening == null){
            return null;
        }
        List<BackgroundReport> backgroundReportList = backgroundReportDao.findByBgSterlingScreeningId(backgroundScreening.getId());
        if(CollectionUtils.isEmpty(backgroundReportList)){
            return null;
        }

        List<String> reportItemIdList = backgroundReportList.stream().map(map -> map.getReportId()).collect(Collectors.toList());

        boolean preAdverseAction =SterlingApiUtils.preAdverseAction(backgroundScreening.getScreeningId(),reportItemIdList);
        SterlingScreening sterlingScreening = SterlingApiUtils.getScreening(backgroundScreening.getScreeningId());
        if(sterlingScreening == null){
            return null;
        }
        if(CollectionUtils.isNotEmpty(sterlingScreening.getAdverseActions())){
            List<BackgroundAdverse> backgroundAdverseList = Lists.newArrayList();

            for(SterlingCallBack.AdverseAction adverseAction:sterlingScreening.getAdverseActions()){
                BackgroundAdverse backgroundAdverse =new BackgroundAdverse();
                backgroundAdverse.setBgSterlingScreeningId(backgroundScreening.getId());
                backgroundAdverse.setUpdateTime(new Date());
                backgroundAdverse.setCreateTime(new Date());
                backgroundAdverse.setScreeningId(sterlingScreening.getId());
                backgroundAdverse.setActionsId(adverseAction.getId());
                backgroundAdverse.setActionsStatus(adverseAction.getStatus());
                backgroundAdverse.setActionsUpdatedAt(DateUtils.convertzDateTime(adverseAction.getUpdatedAt()));
                backgroundAdverseList.add(backgroundAdverse);
            }
            backgroundAdverseDao.batchInsert(backgroundAdverseList);
        }
        return 0;
    }


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
}
