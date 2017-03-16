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
        if(StringUtils.isNotBlank(candidateInputDto.getCandidateId())){
            //2年后参数背景调查 update信息，新插入记录
            SterlingCandidate sterlingCandidate = SterlingApiUtils.updateCandidate(candidateInputDto);
            if(CollectionUtils.isNotEmpty(sterlingCandidate.getErrors())){
                logger.warn("param:{},error:{}", JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(sterlingCandidate.getErrors()));
                return new CandidateOutputDto(null,Integer.valueOf(sterlingCandidate.getErrors().get(0).getErrorCode()),sterlingCandidate.getErrors().get(0).getErrorMessage());
            }
            BackgroundScreening backgroundScreening = transformBackgroundScreening(candidateInputDto,sterlingCandidate);
            Long row = backgroundScreeningV2Dao.insert(backgroundScreening);
            if(row>0){
                //优化一下
                BackgroundScreening newBackgoundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(candidateInputDto.getTeacherId());
                backgroundScreening.setId(newBackgoundScreening.getId());
            }
            return new CandidateOutputDto(backgroundScreening.getId(),null,null);
        }

        //新插入
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

        SterlingCandidate afterSterlingCandidate = SterlingApiUtils.getCandidate(candidateInputDto.getCandidateId());
        //diffCandidate(candidateInputDto,afterSterlingCandidate);

        SterlingCandidate sterlingCandidate = SterlingApiUtils.updateCandidate(candidateInputDto);
        if(CollectionUtils.isNotEmpty(sterlingCandidate.getErrors())){
            logger.warn("param:{},error:{}", JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(sterlingCandidate.getErrors()));
            return new CandidateOutputDto(null,Integer.valueOf(sterlingCandidate.getErrors().get(0).getErrorCode()),sterlingCandidate.getErrors().get(0).getErrorMessage());
        }
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(candidateInputDto.getTeacherId());

        return new CandidateOutputDto(backgroundScreening.getId(),null,null);
    }

    private Object diffCandidate(CandidateInputDto input,SterlingCandidate sterling){
        if(StringUtils.equals(input.getEmail(),sterling.getEmail())){
            input.setEmail(null);
        }
        return input;
    }



    @Transactional(readOnly = false)
    public CandidateOutputDto saveCandidate(CandidateInputDto candidateInputDto) {
        BackgroundScreening sterlingScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(candidateInputDto.getTeacherId());
        if(null == sterlingScreening){
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
     * @param backgroundSterlingId
     * @return
     */
    public CandidateOutputDto repairDataCandidate(Long teacherId,Long backgroundSterlingId){
        BackgroundScreening sterlingScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(teacherId);
        if(null == sterlingScreening){
            return new CandidateOutputDto(null,10000,String.format("teacherId:%s,在表中不存在",teacherId));
        }
        if(StringUtils.isNotBlank(sterlingScreening.getCandidateId())){
            return new CandidateOutputDto(null,10000,String.format("Candidate数据不需要修复,teacherId:%s,backgroundSterlingId:%s",teacherId,backgroundSterlingId));
        }
        CandidateFilterDto candidateFilterDto =new CandidateFilterDto();
        //TODO 调用雪林给的接口
        List<SterlingCandidate> sterlingCandidateList = SterlingApiUtils.getCandidateList(candidateFilterDto);
        if(CollectionUtils.isEmpty(sterlingCandidateList)){
            return new CandidateOutputDto(null ,10000,"没有查到");
        }

        for(SterlingCandidate sterlingCandidate:sterlingCandidateList){
            BackgroundScreening screening = new BackgroundScreening();
            screening.setCandidateId(sterlingCandidate.getId());
            screening.setId(backgroundSterlingId);
            backgroundScreeningV2Dao.update(screening);
        }
        return new CandidateOutputDto(backgroundSterlingId,null,null);
    }

    /**
     * 修复Screening 相关的数据
     * @param backgroundSterlingId
     * @return
     */
    public ScreeningOutputDto repairDateScreeing(Long backgroundSterlingId){
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findById(backgroundSterlingId);

        if(backgroundScreening == null){
            return new ScreeningOutputDto(null ,10000,String.format("id:%s 为ID的数据不存在",backgroundSterlingId));
        }

        if(StringUtils.isBlank(backgroundScreening.getScreeningId())){
            return new ScreeningOutputDto(null,10000,String.format("id:%s 为ID的数据没有screeningId",backgroundSterlingId));
        }

        SterlingScreening sterlingScreening = SterlingApiUtils.getScreening(backgroundScreening.getScreeningId());
        if(null == sterlingScreening){
            return new ScreeningOutputDto(null,10000,String.format("id:%s 为ID的Screening 在Sterling系统中没有查到",backgroundSterlingId));
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

        return new ScreeningOutputDto(backgroundSterlingId,null,null);

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

        boolean isSuccess = SterlingApiUtils.createScreeningDocument(sterlingScreening.getId(),documentUrl);
        if(isSuccess){
            return new ScreeningOutputDto(backgroundScreening.getId(),null,null);
        }

        return new ScreeningOutputDto(null,10000,"上传文档没有成功");
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





}
