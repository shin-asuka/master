package com.vipkid.background.api.sterling.service;



import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.vipkid.background.api.sterling.dto.*;

import com.vipkid.trpm.dao.BackgroundAdverseDao;
import com.vipkid.trpm.dao.BackgroundReportDao;
import com.vipkid.trpm.dao.BackgroundScreeningDao;
import com.vipkid.trpm.entity.BackgroundAdverse;
import com.vipkid.trpm.entity.BackgroundReport;
import com.vipkid.trpm.entity.BackgroundScreening;
import org.apache.commons.collections.CollectionUtils;
import org.community.config.PropertyConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by liyang on 2017/3/11.
 */

@Service
public class SterlingService {
    private static ScreeningInputDto.CallBack callback = null;

    static{
        callback = new ScreeningInputDto.CallBack();
        callback.setUri(PropertyConfigurer.stringValue("background.sterling.callback.uri"));
    }

    @Autowired
    private BackgroundScreeningDao backgroundScreeningDao;

    @Autowired
    private BackgroundReportDao backgroundReportDao;

    @Autowired
    private BackgroundAdverseDao backgroundAdverseDao;




    public Long createCandidate(CandidateInputDto candidateInputDto) {
        SterlingCandidate sterlingCandidate = SterlingApiUtils.createCandidate(candidateInputDto);
        BackgroundScreening backgroundScreening = transformBackgroundScreening(candidateInputDto);
        backgroundScreening.setCandidateId(sterlingCandidate.getId());

        return backgroundScreeningDao.insert(backgroundScreening);
    }

    public int updateCandidate(CandidateInputDto candidateInputDto) {
        SterlingCandidate sterlingCandidate = SterlingApiUtils.updateCandidate(candidateInputDto);
        BackgroundScreening backgroundScreening = transformBackgroundScreening(candidateInputDto);

        return backgroundScreeningDao.update(backgroundScreening);
    }

    @Transactional(readOnly = false)
    public Long createScreening(Long teacherId) {
        BackgroundScreening backgroundScreening = backgroundScreeningDao.findByTeacherIdTopOne(teacherId);
        if(backgroundScreening == null ){
            return null;
        }
        ScreeningInputDto screeningInputDto = new ScreeningInputDto();
        screeningInputDto.setPackageId(PropertyConfigurer.stringValue("background.sterling.pakcageId"));
        screeningInputDto.setCandidateId(backgroundScreening.getCandidateId());
        screeningInputDto.setCallback(callback);


        SterlingScreening sterlingScreening =SterlingApiUtils.createScreening(screeningInputDto);
        //返回字段保存
        backgroundScreening.setUpdateAt(Timestamp.valueOf(sterlingScreening.getUpdatedAt()));
        backgroundScreening.setUpdateTime(new Date());
        backgroundScreening.setResult(sterlingScreening.getResult());
        backgroundScreening.setStatus(sterlingScreening.getStatus());
        backgroundScreening.setScreeningId(sterlingScreening.getId());
        //更新 bg_sterling_screening 表的数据
        backgroundScreeningDao.update(backgroundScreening);
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
                backgroundReport.setUpdateTime(Timestamp.valueOf(reportItem.getUpdatedAt()));
                backgroundReport.setUpdateTime(new Date());
                backgroundReport.setCreateTime(new Date());
                backgroundReport.setType(reportItem.getType());
                backgroundReportList.add(backgroundReport);
            }
            int row = backgroundReportDao.batchInsert(backgroundReportList);
        }


        return backgroundScreening.getId();
    }

    public Integer createPreAdverse(Long teacherId) {

        BackgroundScreening backgroundScreening = backgroundScreeningDao.findByTeacherIdTopOne(teacherId);
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
                backgroundAdverse.setActionsUpdatedAt(Timestamp.valueOf(adverseAction.getUpdatedAt()));
                backgroundAdverseList.add(backgroundAdverse);
            }
            backgroundAdverseDao.batchInsert(backgroundAdverseList);
        }
        return 0;
    }




    private BackgroundScreening transformBackgroundScreening(CandidateInputDto candidateInputDto){

        return null;
    }
}
