package com.vipkid.trpm.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.community.config.PropertyConfigurer;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.vipkid.trpm.proxy.redis.RedisException;

@Component
public class RedisProxy implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(RedisProxy.class);

    private static final String HOST = PropertyConfigurer.stringValue("redis.host");
    private static final int PORT = PropertyConfigurer.intValue("redis.port");

    private static final int TIMEOUT = 8000;

    private JedisPool jedisPool;

    @Autowired
    private JedisPoolConfig jedisPoolConfig;

    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool = new JedisPool(jedisPoolConfig, HOST, PORT, TIMEOUT);
        logger.info("jedis-config,activeNum = {}", jedisPool.getNumActive());
        logger.info("jedis status,isClosed = {}", jedisPool.isClosed());
        if (null == jedisPool || jedisPool.isClosed()) {
            throw new IllegalArgumentException("JedisPool initializing failed in class [RedisProxy]");
        }
    }

    /**
     * 缓存字符串
     * 
     * @author John
     *
     * @param key
     * @param value
     * @param expireSecond
     * @return boolean
     */
    public boolean set(String key, String value, int expireSecond) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
            jedis.expire(key, expireSecond);
            return true;
        } catch (Exception e) {
            logger.error("Redis rpush key: [" + key + "] value: [" + value + "] failed", e);
            return false;
        } finally {
            jedis.close();
        }
    }

    /**
     * 缓存字符串
     * 
     * @author John
     *
     * @param key
     * @param value
     * @return boolean
     */
    public boolean set(String key, String value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("Redis rpush key: [" + key + "] value: [" + value + "] failed", e);
            return false;
        } finally {
            jedis.close();
        }
    }

    /**
     * 查询指定键的缓存值
     * 
     * @author John
     *
     * @param key
     * @return String
     */
    public String get(String key) {
        Preconditions.checkNotNull(key);
        logger.info("**************************jedispool,activeNum = {},isClosed = {} ",jedisPool.getNumActive(),jedisPool.isClosed());
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("Redis rpush key: [" + key + "] failed", e);
            return null;
        } finally {
            jedis.close();
        }
    }

    /**
     * 缓存对象到指定键的队列尾部。
     *
     * @param key
     * @param list
     * @return 返回操作成功执行的数量，返回 -1 代表操作失败
     */
    public long rpush(String key, List<Map<String, Object>> list) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(list);
        Jedis jedis = jedisPool.getResource();
        try {
            String[] strings = list.stream().map(o -> JsonTools.getJson(o)).toArray(String[]::new);
            return jedis.rpush(key, strings);
        } catch (Exception e) {
            logger.error("Redis rpush key: [" + key + "] value: [" + list + "] failed", e);
            return -1;
        } finally {
            jedis.close();
        }
    }

    /**
     * 获取指定键的子队列
     * 
     * @author John
     *
     * @param key
     * @param start
     * @param end
     * @return List<T>
     */
    public List<Map<String, Object>> lrange(String key, int start, int end) {
        Preconditions.checkNotNull(key);
        Preconditions.checkArgument(start < end, "Argument 'start' must be less than 'end'");
        Jedis jedis = jedisPool.getResource();
        try {
            List<String> strings = jedis.lrange(key, start, end - 1);
            List<Map<String, Object>> lists = Lists.newArrayList();

            if (null != strings) {
                for (String json : strings) {
                    lists.add(JsonTools.readValue(json, new TypeReference<HashMap<String, Object>>() {
                    }));
                }
            }

            return lists;
        } catch (Exception e) {
            logger.error("Redis lrange key: [" + key + "] start: [" + start + "]  end: [" + end + "] failed", e);
            return Lists.newArrayList();
        } finally {
            jedis.close();
        }
    }

    /**
     * 删除指定的key
     *
     * @param key
     * @return long
     */
    public long del(String key) {
        Preconditions.checkNotNull(key);
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.del(key);
        } catch (Exception e) {
            logger.error("Redis del key: [" + key + "] failed", e);
            return -1;
        } finally {
            jedis.close();
        }
    }

    public String setex(String key, int seconds, String value) {
        Jedis jedis = jedisPool.getResource();
        String ret = null;
        try {
            ret = jedis.setex(key, seconds, value);
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            jedis.close();
        }
        return ret;
    }
    
    public long setnx(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        Long ret = null;
        try {
            ret = jedis.setnx(key, value);
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            jedis.close();
        }
        return ret;
    }

    /**
     *
     * @param key
     * @return Integer reply, returns the remaining time to live in seconds of a key that has an EXPIRE. If the Key does
     *         not exists or does not have an associated expire, -1 is returned.
     */
    public Long ttl(String key) {
        Jedis jedis = jedisPool.getResource();
        Long value = null;
        try {
            value = jedis.ttl(key);
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            jedis.close();
        }
        return value;
    }

    @Override
    public void destroy() throws Exception {
        if (null != jedisPool) {
            jedisPool.destroy();
            logger.info("JedisPool destroyed in class [RedisProxy]");
        }
    }

}
