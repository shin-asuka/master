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
 * 不需要外部对需要cache的对象转String啊，转json啊，转byte[] 呀！
 * 但是目前没有神马分布式锁之类的高级功能，
 * 不过以后会慢慢的完善
 */

public class RedisCacheUtils {
    private static final Logger logger = LoggerFactory.getLogger(RedisCacheUtils.class);


    private static RedisClient redisClient = RedisClient.me();

    private static final String PREFIX = "TP_%s";


    public static final int FIVE_MINUTES=5*60;


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
                byte[] keyByte = ProtostuffUtils.serializer(String.format(PREFIX,key));
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



    public static <T> boolean  lock(String key,T t,int seconds){
        Jedis jedis = null;
        Long setOK = -1l;
        try {
            jedis = redisClient.getJedisPool().getResource();
            if (jedis != null) {
                byte[] keyByte = ProtostuffUtils.serializer(String.format(PREFIX,key));
                byte[] valueByte = ProtostuffUtils.serializer(t);
                setOK = jedis.setnx(keyByte,valueByte);
                if (setOK == 1) {
                    jedis.expire(keyByte,seconds);
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
     * 解锁 无论是否加锁成功，都需要调用unlock 应该以： lock(); try { doSomething(); } finally { unlock()； } 的方式调用
     */
    public static void unlock(String key) {
        Jedis jedis = null;
        try {
            jedis = redisClient.getJedisPool().getResource();
            if (jedis != null) {
                byte[] keyByte = ProtostuffUtils.serializer(String.format(PREFIX, key));
                jedis.del(keyByte);
            }
        } catch (Exception e) {
            logger.info(String.format("Redis key:%s|value:%s",key, key),e);
        }  finally {
            if (jedis != null) {
                jedis.close();
            }
        }
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
                byte[] keyByte = ProtostuffUtils.serializer(String.format(PREFIX,key));
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


    public static Long del(String key){
        Jedis jedis = null;
        Long removedSize = 0l;
        try {
            jedis = redisClient.getJedisPool().getResource();
            if (jedis != null) {
                byte[] keyByte = ProtostuffUtils.serializer(String.format(PREFIX,key));
                removedSize = jedis.del(keyByte);
            }
            return removedSize;
        } catch (Exception e) {
            logger.info(String.format("Redis key:%s",key),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return removedSize;
    }

}
