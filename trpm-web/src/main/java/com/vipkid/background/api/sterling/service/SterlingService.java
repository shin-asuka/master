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
import org.apache.commons.lang3.StringUtils;
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




    @Transactional(readOnly = false)
    public CandidateOutputDto createCandidate(CandidateInputDto candidateInputDto) {
        SterlingCandidate sterlingCandidate = SterlingApiUtils.createCandidate(candidateInputDto);
        if(CollectionUtils.isNotEmpty(sterlingCandidate.getErrors())){
            logger.warn("param:{},error:{}", JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(sterlingCandidate.getErrors()));
            return new CandidateOutputDto(null,Integer.valueOf(sterlingCandidate.getErrors().get(0).getErrorCode()),sterlingCandidate.getErrors().get(0).getErrorMessage());
        }

        BackgroundScreening backgroundScreening = transformBackgroundScreening(candidateInputDto,sterlingCandidate);
        Long id = backgroundScreeningV2Dao.insert(backgroundScreening);
        return new CandidateOutputDto(id,null,null);
    }

    @Transactional(readOnly = false)
    public CandidateOutputDto updateCandidate(CandidateInputDto candidateInputDto) {
        if(StringUtils.isBlank(candidateInputDto.getCandidateId())){
            BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(candidateInputDto.getTeacherId());
            if(backgroundScreening != null ){
                candidateInputDto.setCandidateId(backgroundScreening.getCandidateId());
            }
        }

        SterlingCandidate sterlingCandidate = SterlingApiUtils.updateCandidate(candidateInputDto);
        if(CollectionUtils.isNotEmpty(sterlingCandidate.getErrors())){
            logger.warn("param:{},error:{}", JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(sterlingCandidate.getErrors()));
            return new CandidateOutputDto(null,Integer.valueOf(sterlingCandidate.getErrors().get(0).getErrorCode()),sterlingCandidate.getErrors().get(0).getErrorMessage());
        }
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(candidateInputDto.getTeacherId());

        return new CandidateOutputDto(backgroundScreening.getId(),null,null);
    }

    @Transactional(readOnly = false)
    public ScreeningOutputDto createScreening(Long teacherId,String documentUrl) {
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacherId);
        if(backgroundScreening == null ){
            return new ScreeningOutputDto(null,10000,"没有找到这个老师");
        }
        ScreeningInputDto screeningInputDto = new ScreeningInputDto();
        screeningInputDto.setPackageId(PropertyConfigurer.stringValue("background.sterling.pakcageId"));
        screeningInputDto.setCandidateId(backgroundScreening.getCandidateId());
        screeningInputDto.setCallback(callback);


        SterlingScreening sterlingScreening =SterlingApiUtils.createScreening(screeningInputDto);
        if (sterlingScreening == null) {
            return new ScreeningOutputDto(null,10000,"没有返回结果");
        }

        if (CollectionUtils.isNotEmpty(sterlingScreening.getErrors())) {
            return new ScreeningOutputDto(null,Integer.valueOf(sterlingScreening.getErrors().get(0).getErrorCode()),sterlingScreening.getErrors().get(0).getErrorMessage());
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
//        else{
//            SterlingScreening getSterlingScreening = SterlingApiUtils.getScreening(backgroundScreening.getScreeningId());
//            if (null != getSterlingScreening) {
//                List<BackgroundReport> backgroundReportList = transformBackgroundReport(getSterlingScreening,backgroundScreening.getId());
//                if(CollectionUtils.isNotEmpty(backgroundReportList)) {
//                    int row = backgroundReportDao.batchInsert(backgroundReportList);
//                }
//            }
//        }

        boolean isSuccess = SterlingApiUtils.createScreeningDocument(sterlingScreening.getId(),documentUrl);
        if(isSuccess){
            return new ScreeningOutputDto(backgroundScreening.getId(),null,null);
        }

        return new ScreeningOutputDto(null,10000,"上传文档没有成功");
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
}
