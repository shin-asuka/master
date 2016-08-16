package com.vipkid.trpm.weixin;

import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步发送消息到家长端
 * 
 * @param requestParam 请求Map&lt;String,String&gt; 包含<br>
 * @return boolean true 发送成功,false 发失败
 */
public class MessageThread implements Callable<Boolean> {
    
    private static Logger logger = LoggerFactory.getLogger(MessageThread.class);

    private Map<String, String> requestParam;

    public MessageThread(Map<String, String> requestParam) {
        this.requestParam = requestParam;
    }

    @Override
    public Boolean call() {
        logger.info("async:Request send to feedback data result:{}", requestParam);
        try {
            MessageHandle ms = new MessageHandle();
            return ms.sendMessage(requestParam);
        } catch (InterruptedException e) {
            logger.error("异步发送失败:"+e.getMessage()+";参数:"+requestParam, e);
            return false;
        }
    }
}
