package com.vipkid.background.api.sterling.service;




import com.google.common.collect.Lists;
import com.vipkid.background.api.sterling.controller.SterlingApiController;
import com.vipkid.background.api.sterling.dto.*;

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


    /**
     * 新建候选人  请求进来的时候，会分2个步骤：
     * 1）插入老师信息（teacherId）。
     * 2）向sterling发起请求并将返回的结果保存到数据库中
     * 还有一个种情况是，
     * @param candidateInputDto
     * @return
     */
    public CandidateOutputDto createCandidate(CandidateInputDto candidateInputDto) {


        BackgroundScreening newBackgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(candidateInputDto.getTeacherId());
        if(null == newBackgroundScreening){
            //如果没有就插入
            newBackgroundScreening= new BackgroundScreening();
            newBackgroundScreening.setTeacherId(candidateInputDto.getTeacherId());
            newBackgroundScreening.setUpdateTime(new Date());
            newBackgroundScreening.setCreateTime(new Date());
            backgroundScreeningV2Dao.dynamicInsert(newBackgroundScreening);
        }

        SterlingCandidate sterlingCandidate = SterlingApiUtils.createCandidate(candidateInputDto,SterlingApiUtils.MAX_RETRY);
        if(CollectionUtils.isNotEmpty(sterlingCandidate.getErrors())){
            logger.warn("param:{},error:{}", JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(sterlingCandidate.getErrors()));
            return new CandidateOutputDto(Integer.valueOf(sterlingCandidate.getErrors().get(0).getErrorCode()),
                    sterlingCandidate.getErrors().get(0).getErrorMessage());
        }
        BackgroundScreening backgroundScreening = transformBackgroundScreening(candidateInputDto,sterlingCandidate);
        backgroundScreening.setId(newBackgroundScreening.getId());
        backgroundScreeningV2Dao.update(backgroundScreening);
        return new CandidateOutputDto(newBackgroundScreening.getId());
    }


    /**
     * 老师2年之后要参加新的背景调查，哪么向sterling发起updateCandidate ，但是会在数据库中创建新的记录
     * @param candidateInputDto
     * @return
     */
    public CandidateOutputDto updateCandidateFor2years(CandidateInputDto candidateInputDto){
        if(StringUtils.isNotBlank(candidateInputDto.getCandidateId())){
            //2年后参数背景调查 update信息，但是在screening表中新插入记录
            SterlingCandidate sterlingCandidate = SterlingApiUtils.updateCandidate(candidateInputDto,SterlingApiUtils.MAX_RETRY);
            if(CollectionUtils.isNotEmpty(sterlingCandidate.getErrors())){
                logger.warn("param:{},error:{}", JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(sterlingCandidate.getErrors()));
                return new CandidateOutputDto(Integer.valueOf(sterlingCandidate.getErrors().get(0).getErrorCode()),
                        sterlingCandidate.getErrors().get(0).getErrorMessage());
            }
            BackgroundScreening backgroundScreening = transformBackgroundScreening(candidateInputDto,sterlingCandidate);
            backgroundScreeningV2Dao.insert(backgroundScreening);
            return new CandidateOutputDto(backgroundScreening.getId());
        }

        return new CandidateOutputDto(100010, "没有candidateId,没有办法做变更");
    }


    /**
     * 更新 Candidate 信息
     * @param candidateInputDto
     * @return
     */
    public CandidateOutputDto updateCandidate(CandidateInputDto candidateInputDto) {
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(candidateInputDto.getTeacherId());

        if(StringUtils.isBlank(candidateInputDto.getCandidateId())){
            if(backgroundScreening != null ){
                candidateInputDto.setCandidateId(backgroundScreening.getCandidateId());
            }
        }

        SterlingCandidate sterlingCandidate = SterlingApiUtils.updateCandidate(candidateInputDto,SterlingApiUtils.MAX_RETRY);
        if(CollectionUtils.isNotEmpty(sterlingCandidate.getErrors())){
            logger.warn("param:{},error:{}", JacksonUtils.toJSONString(candidateInputDto),JacksonUtils.toJSONString(sterlingCandidate.getErrors()));
            return new CandidateOutputDto(Integer.valueOf(sterlingCandidate.getErrors().get(0).getErrorCode()),sterlingCandidate.getErrors().get(0).getErrorMessage());
        }

        return new CandidateOutputDto(backgroundScreening.getId());
    }


    /**
     * 主要功能：
     * 1、创建
     * 2、超过2年后修改信息，创建新记录
     * 3、如果之前数据不完整，要看sterling是否有数据，如果有则补全，如果没有则创建
     * @param candidateInputDto
     * @return
     */
    public CandidateOutputDto saveCandidate(CandidateInputDto candidateInputDto) {
        BackgroundScreening sterlingScreening = backgroundScreeningV2Dao.findByTeacherIdTopOne(candidateInputDto.getTeacherId());
        //创建
        if(null == sterlingScreening){
            return createCandidate(candidateInputDto);
        }

        //修补
        if(StringUtils.isBlank(sterlingScreening.getCandidateId())){
            //有记录，但是没有sterling的数据
            CandidateOutputDto candidateOutputDto = repairDataCandidate(candidateInputDto.getTeacherId());
            if(candidateOutputDto.getErrorCode() == 12000){
                //sterling不存在要重新创建candidate
                candidateOutputDto = createCandidate(candidateInputDto);
            }
            return candidateOutputDto;
        }
        Date updateTime = sterlingScreening.getUpdateTime();
        Calendar lastTime = Calendar.getInstance();
        lastTime.setTimeInMillis(updateTime.getTime());
        Calendar currentTime = Calendar.getInstance();
        lastTime.add(Calendar.YEAR,2);
        candidateInputDto.setCandidateId(sterlingScreening.getCandidateId());

        //2年后修改信息，创建新记录
        if(currentTime.after(lastTime)){
            return updateCandidateFor2years(candidateInputDto);
        }

        //修改信息
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
        if(backgroundCheckVo == null){
            return new CandidateOutputDto(10000,String.format("没有teacherId:%s",teacherId));
        }
        candidateFilterDto.setFamilyName(backgroundCheckVo.getLastName());
        candidateFilterDto.setGivenName(backgroundCheckVo.getFirstName());
        candidateFilterDto.setLimit(0);
        candidateFilterDto.setOffset(10);
        candidateFilterDto.setEmail(backgroundCheckVo.getEmail());

        List<SterlingCandidate> sterlingCandidateList = SterlingApiUtils.getCandidateList(candidateFilterDto);
        if(CollectionUtils.isEmpty(sterlingCandidateList)){
            return new CandidateOutputDto(12000,"没有查到");
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
    @Transactional(readOnly = false)
    public ScreeningOutputDto repairDateScreening(Long backgroundSterlingId){
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
                batchInsertBackgroundAdverse(sterlingScreening.getAdverseActions(),backgroundScreening.getId(),sterlingScreening.getId());
            }else{
                //数据库里有
                Map<String,SterlingCallBack.AdverseAction> adverseActionMap = MapUtils.transformListToMap(sterlingScreening.getAdverseActions(), new Function<String, SterlingCallBack.AdverseAction>() {
                    @Override
                    public String apply(SterlingCallBack.AdverseAction input) {
                        return input.getId();
                    }
                });
                batchUpdateBackgroundAdverse(adverseActionMap,backgroundAdverseList);

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


        SterlingScreening sterlingScreening =SterlingApiUtils.createScreening(screeningInputDto,SterlingApiUtils.MAX_RETRY);
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
            List<BackgroundReport> backgroundReportList = transformBackgroundReport(sterlingScreening.getReportItems(),backgroundScreening.getId(),backgroundScreening.getScreeningId());
            int row = backgroundReportDao.batchInsert(backgroundReportList);
        }

        boolean isSuccess = SterlingApiUtils.createScreeningDocument(sterlingScreening.getId(),documentUrl);
        if(isSuccess){
            return new ScreeningOutputDto(backgroundScreening.getId());
        }

        return new ScreeningOutputDto(10000,"上传文档没有成功");
    }

    @Transactional(readOnly = false)
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
            batchInsertBackgroundAdverse(sterlingScreening.getAdverseActions(),backgroundScreening.getId(),sterlingScreening.getId());
        }
        return new AdverseOutputDto(teacherId,null,null);
    }


    /**
     * 转换方法
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
     * @param reportItemList
     * @param backgroundScreeningId
     * @param screeingId
     * @return
     */
    private List<BackgroundReport> transformBackgroundReport(List<SterlingCallBack.ReportItem> reportItemList,Long backgroundScreeningId,String screeingId){
        if(CollectionUtils.isEmpty(reportItemList)){
            return Lists.newArrayList();
        }
        List<BackgroundReport> backgroundReportList = Lists.newArrayList();
        for(SterlingCallBack.ReportItem reportItem:reportItemList){
            BackgroundReport backgroundReport =new BackgroundReport();
            backgroundReport.setBgSterlingScreeningId(backgroundScreeningId);
            backgroundReport.setScreeningId(screeingId);
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
     * @param adverseActionList
     * @param backgroundScreeningId
     * @param sterlingScreeningId
     * @return
     */
    private int batchInsertBackgroundAdverse(List<SterlingCallBack.AdverseAction> adverseActionList,Long backgroundScreeningId,String sterlingScreeningId){
        List<BackgroundAdverse> backgroundAdverseList = Lists.newArrayList();
        if(CollectionUtils.isEmpty(adverseActionList)){
            return 0;
        }
        for(SterlingCallBack.AdverseAction adverseAction:adverseActionList){
            BackgroundAdverse backgroundAdverse =new BackgroundAdverse();
            backgroundAdverse.setBgSterlingScreeningId(backgroundScreeningId);
            backgroundAdverse.setUpdateTime(new Date());
            backgroundAdverse.setCreateTime(new Date());
            backgroundAdverse.setScreeningId(sterlingScreeningId);
            backgroundAdverse.setActionsId(adverseAction.getId());
            backgroundAdverse.setActionsStatus(adverseAction.getStatus());
            backgroundAdverse.setActionsUpdatedAt(DateUtils.convertzDateTime(adverseAction.getUpdatedAt()));
            backgroundAdverseList.add(backgroundAdverse);
        }
        return backgroundAdverseDao.batchInsert(backgroundAdverseList);
    }


    /**
     * 批量修改Adverse 表
     * @param adverseActionMap
     * @param backgroundAdverseList
     */
    private void batchUpdateBackgroundAdverse(Map<String,SterlingCallBack.AdverseAction> adverseActionMap,List<BackgroundAdverse> backgroundAdverseList) {

        if(CollectionUtils.isEmpty(backgroundAdverseList)){
            return ;
        }
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
            adverseActionMap.remove(backgroundAdverse.getActionsId());
        }

        if(CollectionUtils.isEmpty(updateBackgroundAdverseList)){
            return ;
        }

        for(BackgroundAdverse backgroundAdverse:updateBackgroundAdverseList){
            backgroundAdverseDao.update(backgroundAdverse);
        }

    }


    /**
     * callback 处理
     * @param payload
     */
    @Transactional(readOnly = false)
    public void updateBackgroundScreening(SterlingCallBack.Payload payload) {
        BackgroundScreening backgroundScreening = backgroundScreeningV2Dao.findByScreeningIdAndCandidateId(payload.getId(),payload.getCandidateId());
        if(null == backgroundScreening){
            return ;
        }

        BackgroundScreening updateBackgroundScreening = new BackgroundScreening();

        if(!StringUtils.equals(backgroundScreening.getResult(),payload.getResult())){
            updateBackgroundScreening.setResult(payload.getResult());
        }

        if(!StringUtils.equals(backgroundScreening.getStatus(),payload.getStatus())){
            updateBackgroundScreening.setStatus(payload.getStatus());
        }

        if(null != payload.getDispute() && !StringUtils.equals(backgroundScreening.getDisputeStatus(),payload.getDispute().getStatus())){
            updateBackgroundScreening.setDisputeStatus(payload.getDispute().getStatus());
            updateBackgroundScreening.setDisputeCreatedAt(DateUtils.convertzDateTime(payload.getDispute().getCreatedAt()));
        }

        if(null != payload.getLinks()){
            String web = payload.getLinks().getAdmin().getWeb();
            String pdf = payload.getLinks().getAdmin().getPdf();
            updateBackgroundScreening.setPdfLink(pdf);
            updateBackgroundScreening.setWebLink(web);
        }

        Date updateAt = DateUtils.convertzDateTime(payload.getUpdatedAt());
        updateBackgroundScreening.setUpdateAt(updateAt);


        if(null != updateBackgroundScreening.getDisputeStatus() ||
                null != updateBackgroundScreening.getStatus() || null != updateBackgroundScreening.getResult()){
            updateBackgroundScreening.setId(backgroundScreening.getId());
            backgroundScreeningV2Dao.update(updateBackgroundScreening);
        }


        if(CollectionUtils.isNotEmpty(payload.getReportItems())){
            List<BackgroundReport> backgroundReportList = backgroundReportDao.findByBgSterlingScreeningId(backgroundScreening.getId());
            if(CollectionUtils.isNotEmpty(backgroundReportList)){
                //变更
                Map<String,SterlingCallBack.ReportItem> remoteBackgroundReportMap = MapUtils.transformListToMap(payload.getReportItems(), new Function<String, SterlingCallBack.ReportItem>() {
                    @Override
                    public String apply(SterlingCallBack.ReportItem input) {
                        return input.getId();
                    }
                });
                updateBackgroundReport(backgroundReportList,remoteBackgroundReportMap);
                List<BackgroundReport> newBackgroundReportList = transformBackgroundReport(Lists.newArrayList(remoteBackgroundReportMap.values()),backgroundScreening.getId(),backgroundScreening.getScreeningId());
                if(CollectionUtils.isNotEmpty(newBackgroundReportList)) {
                    backgroundReportDao.batchInsert(newBackgroundReportList);
                }
            }else{
                //插入
                List<BackgroundReport> newBackgroundReportList = transformBackgroundReport(payload.getReportItems(),backgroundScreening.getId(),backgroundScreening.getScreeningId());
                if(CollectionUtils.isNotEmpty(newBackgroundReportList)) {
                    backgroundReportDao.batchInsert(newBackgroundReportList);
                }


            }
        }

        if(CollectionUtils.isNotEmpty(payload.getAdverseActions())){
            List<BackgroundAdverse> backgroundAdverseList =  backgroundAdverseDao.findUpdateTimeByBgScreeningId(backgroundScreening.getId());

            if(CollectionUtils.isNotEmpty(backgroundAdverseList)){
                Map<String,SterlingCallBack.AdverseAction> adverseActionMap = MapUtils.transformListToMap(payload.getAdverseActions(), new Function<String, SterlingCallBack.AdverseAction>() {
                    @Override
                    public String apply(SterlingCallBack.AdverseAction input) {
                        return input.getId();
                    }
                });
                //数据库里有，修改
                batchUpdateBackgroundAdverse(adverseActionMap,backgroundAdverseList);
                batchInsertBackgroundAdverse(Lists.newArrayList(adverseActionMap.values()),backgroundScreening.getId(),payload.getId());
            }else{
                //数据库里没有，插入
                batchInsertBackgroundAdverse(payload.getAdverseActions(),backgroundScreening.getId(),payload.getId());

            }

        }


    }

    private void updateBackgroundReport(List<BackgroundReport> backgroundReportList, Map<String, SterlingCallBack.ReportItem> remoteBackgroundReportMap) {
        if(CollectionUtils.isEmpty(backgroundReportList)){
            return ;
        }

        for(BackgroundReport backgroundReport:backgroundReportList){
            SterlingCallBack.ReportItem reportItem = remoteBackgroundReportMap.get(backgroundReport.getReportId());
            if(reportItem == null){
                continue;
            }
            remoteBackgroundReportMap.remove(backgroundReport.getReportId());
            backgroundReport.setResult(reportItem.getResult());
            backgroundReport.setStatus(reportItem.getStatus());
            backgroundReport.setUpdatedAt(DateUtils.convertzDateTime(reportItem.getUpdatedAt()));
            backgroundReport.setUpdateTime(new Date());
            backgroundReportDao.update(backgroundReport);
        }

    }
}
