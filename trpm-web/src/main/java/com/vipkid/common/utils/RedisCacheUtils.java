package com.vipkid.common.utils;

import com.vipkid.http.utils.JacksonUtils;
import com.vipkid.trpm.controller.h5.StudentCommentController;
import com.vipkid.trpm.proxy.redis.RedisClient;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by liyang on 2017/3/11.
 * 背景：因为这个项目里的Redis 相关的 工具类有三个，我也不知道都是干啥的貌似都还有用<br>
 * com.vipkid.trpm.proxy.RedisProxy
 * com.vipkid.cache.utils.RedisClient
 * com.vipkid.trpm.proxy.redis.RedisClient
 * 看到这三个就蒙逼了；也不敢改动；所以就搞了一个工具类，就是为了方便使用，这里提供了最为简单的使用方法<br>
 * 不需要外部对需要cache的对象转String啊，转json啊，转bety[] 呀！
 * 但是目前没有神马分布式锁之类的高级功能，
 * 不过以后会慢慢的完善
 */

public class RedisCacheUtils {
    private static final Logger logger = LoggerFactory.getLogger(RedisCacheUtils.class);
    private  static RedisClient redisClient = RedisClient.me();


    /**
     *
     * @param key
     * @param t
     * @param seconds
     * @param <T>
     * @return
     */
    public static <T> boolean  set(String key,T t,int seconds){
        Jedis jedis = null;
        String setOK = StringUtils.EMPTY;
        try {
            jedis = redisClient.getJedisPool().getResource();
            if (jedis != null) {
                byte[] keyByte = ProtostuffUtils.serializer(key);
                byte[] valueByte = ProtostuffUtils.serializer(t);
                setOK = jedis.setex(keyByte,seconds,valueByte);
                if (StringUtils.containsIgnoreCase(setOK, "ok")) {
                    return true;
                } else {
                    logger.info("Redis key:{}|value:{}",key, JacksonUtils.toJSONString(t));
                    return false;
                }
            }
        } catch (Exception e) {
            logger.info(String.format("Redis key:%s|value:%s",key, JacksonUtils.toJSONString(t)),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }


    /**
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T get(String key,Class<T> clazz){
        Jedis jedis = null;
        T t = null;
        try {
            jedis = redisClient.getJedisPool().getResource();
            byte[] valueByte = null;
            if (jedis != null) {
                byte[] keyByte = ProtostuffUtils.serializer(key);
                valueByte = jedis.get(keyByte);
            }
            if (ArrayUtils.isEmpty(valueByte)) {
                return t;
            }
            t = ProtostuffUtils.deserializer(valueByte,clazz);
            return t;
        } catch (Exception e) {
            logger.info(String.format("Redis key:%s|value:%s",key, JacksonUtils.toJSONString(t)),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return t;
    }

}
