package com.vipkid.task.job;

import com.google.common.collect.Lists;
import com.vipkid.background.api.sterling.service.SterlingService;
import com.vipkid.trpm.dao.BackgroundScreeningV2Dao;
import com.vipkid.vschedule.client.common.Vschedule;
import com.vipkid.vschedule.client.schedule.JobContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Created by liyang on 2017/3/17.
 */
@Component
@Vschedule
public class BackgroundCheckJob {

    private static final Logger logger = LoggerFactory.getLogger(BackgroundCheckJob.class);

    @Autowired
    private SterlingService sterlingService;

    @Autowired
    private BackgroundScreeningV2Dao backgroundScreeningV2Dao;


    @Vschedule
    public void screeningResultCheckJob(JobContext jobContext){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("开始获取教师背景调查中调查结果状态更新=======================================");
        List<Long> bgScreeningIds = backgroundScreeningV2Dao.findIdByResult("n/a");
        if(CollectionUtils.isNotEmpty(bgScreeningIds)){
            for(Long bgScreeningId:bgScreeningIds){
                sterlingService.repairDateScreeing(bgScreeningId);
            }
        }

        stopWatch.stop();
        logger.info(String.format("结束获取教师背景调查中调查结果状态更新=======================================用时%s ms",stopWatch.getTime()));
    }


    @Vschedule
    public void candidateCheckJob(JobContext jobContext){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("开始检查教师背景调查中补全候选人信息=======================================");
        List<Long> teacherIds = backgroundScreeningV2Dao.findTeacherIdBycandidateIdNone();
        if(CollectionUtils.isNotEmpty(teacherIds)){
            for(Long teacherId : teacherIds){
                sterlingService.repairDataCandidate(teacherId);
            }
        }
        stopWatch.stop();
        logger.info(String.format("结束检查教师背景调查中补全候选人信息=======================================用时%s ms",stopWatch.getTime()));
    }
}
