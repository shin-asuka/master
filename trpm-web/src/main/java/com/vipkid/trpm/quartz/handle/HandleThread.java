package com.vipkid.trpm.quartz.handle;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.trpm.service.passport.NoticeService;

public class HandleThread implements Callable<String> {

    private Logger log = LoggerFactory.getLogger(HandleThread.class);

    private int emialCount;
    
    private String teacherId;

    private List<Map<String,Object>> list;

    private NoticeService noticeService;
    
    public HandleThread(String teacherId, NoticeService noticeService,List<Map<String,Object>> list) {
        this.emialCount = HandleEngine.emialCount;
        this.teacherId = teacherId;
        this.noticeService = noticeService;
        this.list = list;
    }

    @Override
    public String call() {
        long start = System.currentTimeMillis();
        try{
            if (noticeService.emailHandle(teacherId, list)) {
                log.info("【第"+this.emialCount+"位，老师:"+this.teacherId+"】邮件发送完毕");
                return teacherId;
            } else {
                HandleEngine.failEmail.append("【"+this.emialCount+"+"+this.teacherId+"】发送失败:" + teacherId);
                log.warn("已经发送失败邮件列表如下：" + HandleEngine.failEmail);
                return teacherId + ",ERROR!";
            }
        }catch(Exception e){
            log.error("【"+this.emialCount+"+"+this.teacherId+"】"+e.getMessage(),e);
            return teacherId + ",ERROR!";
        }finally{
            log.info("【"+this.emialCount+"+"+this.teacherId+"】:thread END共花费时间(秒)：" + (System.currentTimeMillis() - start)/1000);
        }
    }

}
