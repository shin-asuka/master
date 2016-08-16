package com.vipkid.trpm.quartz;

/**
 * 主要负责线程池的创建，管理邮件发送线程池，以及线程创建，
 * 
 * @author Along(ZengWeiLong)
 * @ClassName: HandleEngine
 * @date 2016年4月23日 下午6:03:23
 *
 */
public interface EnginePlugin {

    /**
     * 初始化
     * 
     * @Author:ALong (ZengWeiLong)
     * @return boolean
     * @date 2016年4月23日
     */
    public boolean start();

    /**
     * 核心调用
     * 
     * @Author:ALong (ZengWeiLong)
     * @return boolean
     * @date 2016年4月23日
     */
    public boolean excute();
    
    /**
     * 终止任务
     * 
     * @Author:ALong (ZengWeiLong)
     * @return boolean
     * @date 2016年4月23日
     */
    public boolean stop();
    
}
