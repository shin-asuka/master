package com.vipkid.email;

import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.email.handle.EmailHandle;
import com.vipkid.email.handle.EmailHandleThread;

/**
 * <b>邮件发送线程调度引擎类</b> <br/>
 * <br/>
 * addMailPool 方法:发送异步邮件，立刻返回<br/>
 * <br/>
 * addMail 方法:发送同步邮件，执行完成返回结果<br/>
 * <br/>
 *
 * @author Along(ZengWeiLong)
 * @ClassName: EmailEngine
 * @date 2016年4月23日 下午3:26:25
 *
 */
public class EmailEngine {

    private static Logger logger = LoggerFactory.getLogger(EmailEngine.class);
    // 起步线程 100
    private static int corePoolSize = 10;
    // 最大线程 1000
    private static int maximumPoolSize = 50;
    // 非活跃线程等待时间10分钟
    private static int keepAliveTime = 10 * 60;
    // 排队线程
    private static int workQueue = 500; // 自建线程池

    private static ThreadPoolExecutor executorService =
            new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(workQueue));

    /* JDK自建线程池 */
    // private static ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 使用线程池发送邮件
     *
     * @Author:ALong (ZengWeiLong)
     * @param toEmail 发送到目标地址
     * @param map （必须包含邮件标题 <span style="color:red;"> title</span>，邮件内容 <span style="color:red;">
     *        content</span>） 通过 {@link com.vipkid.email.templete.TempleteUtils} 的静态方法 readTemplete可以得到
     * @param emailForm 使用发送者
     * @date 2016年4月23日
     */
    public void addMailPool(String toEmail, Map<String, String> map, EmailFormEnum emailForm) {
        logger.info("异步发送邮件：" + toEmail);
        // String teacherHttp = PropertyConfigurer.stringValue("teacher.www");
        int threadCount = executorService.getActiveCount();
        if (threadCount > EmailConfig.THREAD_POOL_LIMIT) {
            logger.info(
                    "邮件发送线程池活跃线程数已经超过峰值【" + EmailConfig.THREAD_POOL_LIMIT + "】达到：" + threadCount);
            try {
                Thread.sleep(EmailConfig.WAIT_TIME);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        // if (EmailConfig.SEND_EMIAL_EVN.equals(teacherHttp)) {
        EmailHandle emailHandle =
                new EmailHandle(toEmail, map.get("title"), map.get("content"), emailForm);
        FutureTask<String> futureTask = new FutureTask<String>(new EmailHandleThread(emailHandle));
        executorService.execute(futureTask);
        /*
         * } else { EmailHandle emailHandle = new EmailHandle(EmailConfig.TEST_EMIAL_TO,
         * map.get("title"), map.get("content"), emailForm); FutureTask<String> futureTask = new
         * FutureTask<String>(new EmailHandleThread(emailHandle));
         * executorService.execute(futureTask); }
         */
    }

    /**
     * 同步发送邮件
     *
     * @Author:ALong (ZengWeiLong)
     * @param toEmail 发送到目标地址
     * @param map （必须包含邮件标题 <span style="color:red;"> title</span>，邮件内容 <span style="color:red;">
     *        content</span>） 通过 {@link com.vipkid.email.templete.TempleteUtils} 的静态方法 readTemplete可以得到
     * @param emailForm 使用发送者
     * @throws InterruptedException void
     * @date 2016年4月23日
     */
    public void addMail(String toEmail, Map<String, String> map, EmailFormEnum emailForm) {
        logger.info("同步发送邮件：" + toEmail);
        // String teacherHttp = PropertyConfigurer.stringValue("teacher.www");
        // if (EmailConfig.SEND_EMIAL_EVN.equals(teacherHttp)) {
        EmailHandle emailHandle =
                new EmailHandle(toEmail, map.get("title"), map.get("content"), emailForm);
        emailHandle.sendMail();
        // } else {
        // EmailHandle emailHandle = new EmailHandle(EmailConfig.TEST_EMIAL_TO, map.get("title"),
        // map.get("content"),emailForm);
        // emailHandle.sendMail();
        // }
        logger.info("同步发送邮件结束：" + toEmail);
    }
}
