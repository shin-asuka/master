package com.vipkid.email;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.email.handle.EmailHandle;

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
    public static void addMailPool(String toEmail, Map<String, String> map, EmailFormEnum emailForm) {
        logger.info("异步发送邮件：" + toEmail);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            EmailHandle.switchMail(toEmail,map.get("title"), map.get("content"), emailForm);
            logger.info("异步发送邮件结束：" + toEmail);
        });
        
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
    public static void addMail(String toEmail, Map<String, String> map, EmailFormEnum emailForm) {
        logger.info("同步发送邮件：" + toEmail);
        EmailHandle.switchMail(toEmail,map.get("title"), map.get("content"), emailForm);
        logger.info("同步发送邮件结束：" + toEmail);
    }
}
