package com.vipkid.task.job;

import com.vipkid.background.api.sterling.service.SterlingService;
import com.vipkid.background.BackgroundScreeningDao;
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
    private BackgroundScreeningDao backgroundScreeningDao;


    /**
     * 调查结果状态的查询补全机制
     *
     *
     * @param jobContext
     */
    @Vschedule
    public void screeningResultCheckJob(JobContext jobContext){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("开始获取教师背景调查，对正在进行的进行调查结果状态更新=======================================");
        List<Long> bgScreeningIds = backgroundScreeningDao.findIdByResultAndDisputeStatus("n/a",null);
        if(CollectionUtils.isNotEmpty(bgScreeningIds)){
            for(Long bgScreeningId:bgScreeningIds){
                sterlingService.repairDateScreening(bgScreeningId);
            }
        }


        List<Long> alertBgScreeningIds = backgroundScreeningDao.findIdByResultAndDisputeStatus("alert","active");
        if(CollectionUtils.isNotEmpty(alertBgScreeningIds)){
            for(Long bgScreeningId:alertBgScreeningIds){
                sterlingService.repairDateScreening(bgScreeningId);
            }
        }
        stopWatch.stop();
        logger.info(String.format("结束获取教师背景调查，对正在进行的进行调查结果状态更新=======================================用时%s ms",stopWatch.getTime()));
    }


    @Vschedule
    public void candidateCheckJob(JobContext jobContext){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("开始检查教师背景调查中补全候选人信息=======================================");
        List<Long> teacherIds = backgroundScreeningDao.findTeacherIdBycandidateIdNone();
        if(CollectionUtils.isNotEmpty(teacherIds)){
            for(Long teacherId : teacherIds){
                sterlingService.repairDataCandidate(teacherId);
            }
        }
        stopWatch.stop();
        logger.info(String.format("结束检查教师背景调查中补全候选人信息=======================================用时%s ms",stopWatch.getTime()));
    }
}
