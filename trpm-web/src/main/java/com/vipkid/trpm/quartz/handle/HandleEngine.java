package com.vipkid.trpm.quartz.handle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.vipkid.trpm.proxy.redis.RedisClient;
import com.vipkid.trpm.proxy.redis.RedisLock;
import com.vipkid.trpm.quartz.EnginePlugin;
import com.vipkid.trpm.quartz.HandleData;
import com.vipkid.trpm.service.passport.NoticeService;

@Component
public class HandleEngine implements EnginePlugin {

    private Logger log = LoggerFactory.getLogger(HandleEngine.class);

    //记录发送Email的数量
    public static  int emialCount;
    //记录应该发送给那些老师Id
    public static StringBuilder sendEmal;
    //记录发送失败的email日志中打印出来
    public static StringBuilder failEmail;
    //redis key 后缀
    public static String redisKey;
    //Email内容处理和发送
    @Autowired
    private NoticeService noticeService;
    //参数处理类
    @Autowired
    private HandleData handleData;
    //Online Class
    private Map<String,List<Map<String,Object>>> onlineClassMap;
    
    public boolean start() {
        //初始化参数
        emialCount = 0;
        sendEmal = new StringBuilder("");
        failEmail = new StringBuilder("");
        //初始化缓存
        redisKey = HandleConfig.KEY + new SimpleDateFormat("-yyyy-MM-dd").format(new Date());
        //2.将预发送的老师放入Redis
        Jedis jedis = RedisClient.me().getJedisPool().getResource();
        RedisLock rl = new RedisLock(redisKey, jedis);
        try {
            if (rl.lock()) {
                Long listlen = jedis.llen(redisKey);
                if (listlen == 0) {
                    List<String> list = noticeService.findAllRegular();
                    String[] strings = list.toArray(new String[] {});
                    jedis.rpush(redisKey, strings);
                    log.info("push redis success " + list.size());
                }
                return true;
            }
            return false;
        } finally {
            rl.unlock();
        }
    }

    @Override
    public boolean excute() {
        try {
            //从Jedi 获取数据
            Map<String,String> map = HandleTools.paramMap();
            onlineClassMap = noticeService.findBookedClass(map.get("startTime"),map.get("endTime"));
            int i = 0;
            while (i < HandleConfig.MAX_LIMIT) {
                i++;
                String teacherId = handleData.findTeacherIdByRedis();
                if (StringUtils.isEmpty(teacherId)) {
                    log.info("Completed!");
                    break;
                }                
                if (HandleConfig.HE_WAIT_STR.equals(teacherId)) {
                    Thread.sleep(HandleConfig.HE_WAIT_TIME);
                    //本次计数无效
                    i--;
                    log.info("取数redis据失败：" + i + " == " + teacherId);
                    continue;
                }
                List<Map<String,Object>> list = onlineClassMap.get(teacherId);
                if (list != null && list.size() > 0) {
                    emialCount++;
                    log.info("需要发送邮件通知的第"+emialCount+"位老师【"+map.get("startTime")+" - "+map.get("endTime")+"】:" + teacherId);
                    sendEmal.append(teacherId).append(",");
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.submit(()->{
                        if (noticeService.emailHandle(teacherId, list)) {
                            log.info("【第"+emialCount+"位，老师:"+teacherId+"】邮件发送完毕");
                            return teacherId;
                        } else {
                            HandleEngine.failEmail.append("【"+emialCount+"+"+teacherId+"】发送失败:" + teacherId);
                            log.warn("已经发送失败邮件列表如下：" + HandleEngine.failEmail);
                            return teacherId + ",ERROR!";
                        }
                    });
                }
                log.info("已检查了第【"+i+"】位老师【"+teacherId+"】，是否发送邮件");
                Thread.sleep(HandleConfig.HE_WAIT_TIME/4);
            }
            log.info("总共执行记录数：" + (i - 1));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    @Override
    public boolean stop() {
        log.info("一共应发邮件：" + emialCount + "封,发送给："+sendEmal);
        return true;
    }
}
