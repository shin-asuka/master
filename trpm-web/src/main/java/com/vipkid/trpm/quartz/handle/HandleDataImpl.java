package com.vipkid.trpm.quartz.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.vipkid.trpm.proxy.redis.RedisClient;
import com.vipkid.trpm.proxy.redis.RedisLock;
import com.vipkid.trpm.quartz.HandleData;

@Component
public class HandleDataImpl implements HandleData {

    private Logger log = LoggerFactory.getLogger(HandleDataImpl.class);

    public String findTeacherIdByRedis() {
        Jedis jedis = RedisClient.me().getJedisPool().getResource();
        RedisLock rl = new RedisLock(HandleEngine.redisKey, jedis);
        try {
            if (rl.lock()) {
                String teacherId = jedis.lpop(HandleEngine.redisKey);
                return teacherId;
            }
            return HandleConfig.HE_WAIT_STR;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HandleConfig.HE_WAIT_STR;
        } finally {
            rl.unlock();
        }
    }

}
