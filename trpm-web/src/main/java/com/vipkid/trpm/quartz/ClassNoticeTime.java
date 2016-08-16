package com.vipkid.trpm.quartz;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClassNoticeTime {

    private Logger log = LoggerFactory.getLogger(ClassNoticeTime.class);

    @Autowired
    private EnginePlugin handlePlugin;
    
    /**
     * 
     * 每天11点开始执行 ,如果Redis中NoticeAdvance.KEY不存在或为Null的时候 
     * @Author:ALong (ZengWeiLong)
     * @return
     * @date 2016年4月21日
     */
    // TODO 定时任务发送邮件功能暂时注释
    // @Scheduled(cron = "0 0 23 * * ?")
    // @Scheduled(cron = "0 0 21 * * ?")
    public void excuteSend() {
        log.info("开始执行。。。");
        try {
            Thread.sleep(new Random().nextInt(500));
            //初始化
            handlePlugin.start();
            //开始执行
            handlePlugin.excute();
        } catch (InterruptedException e) {
           log.error(e.getMessage(),e);
        }finally{
            handlePlugin.stop();
        }
    }

}
