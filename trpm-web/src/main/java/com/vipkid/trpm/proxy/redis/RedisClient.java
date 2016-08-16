package com.vipkid.trpm.proxy.redis;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClient {

    private static Logger log = LoggerFactory.getLogger(RedisClient.class);

    private JedisPool jedisPool;// 非切片连接池

    private static RedisClient jedisClient = null;

    private RedisClient() {
        if (jedisPool == null) {
            initialPool();
        }
    }

    public static RedisClient me() {
        if (jedisClient == null) {
            jedisClient = new RedisClient();
        }
        return jedisClient;
    }

    /**
     * 初始化非切片池
     */
    private void initialPool() {
        log.info("连接池初始化。。。");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(1000);
        config.setMaxIdle(10);
        config.setMaxWaitMillis(2000);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        log.info("Redis Ip:" + PropertyConfigurer.stringValue("redis.host") + "; Port:"
                + PropertyConfigurer.intValue("redis.port"));
        jedisPool = new JedisPool(config, PropertyConfigurer.stringValue("redis.host"),
                PropertyConfigurer.intValue("redis.port"));
    }

    public JedisPool getJedisPool() {
        if (jedisPool == null) {
            initialPool();
        }
        return jedisPool;
    }

    public String get(String key) {
        Jedis jedis = null;
        String returns = "";
        if (StringUtils.isBlank(key))
            return returns;
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                returns = jedis.get(key);
            }
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return returns;
    }

    public String set(String key, String value) {
        Jedis jedis = null;
        String returns = "";
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                returns = jedis.set(key, value);
            }
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return returns;
    }

    public Long rpushLock(String key, String[] values) {
        Jedis jedis = null;
        Long returns = 0L;
        jedis = jedisPool.getResource();
        if (jedis != null) {
            RedisLock rl = new RedisLock(key, jedis);
            try {
                if (rl.lock()) {
                    List<String> prelist = jedis.lrange(key, 0, 10);
                    if (prelist == null || prelist.isEmpty()) {
                        returns = jedis.rpush(key, values);
                        log.info("push redis success " + prelist.size());
                    }
                }
            } finally {
                rl.unlock();
            }
        }
        return returns;
    }

    public String lpopLock(String key) {
        Jedis jedis = null;
        String returns = "";
        if (StringUtils.isBlank(key))
            return returns;
        jedis = jedisPool.getResource();
        if (jedis != null) {
            RedisLock rl = new RedisLock(key, jedis);
            try {
                if (rl.lock()) {
                    returns = jedis.lpop(key);
                }
            } finally {
                rl.unlock();
            }
        }
        return returns;
    }

    public boolean lock(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (1 == jedis.setnx(key, RedisLock.LOCKED)) {
                jedis.expire(key, 10 * 60);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("redis lock error", e);
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean unlock(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
            return true;
        } catch (Exception e) {
            log.error("redis unlock error", e);
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public byte[] get(byte[] key) {
        Jedis jedis = null;
        byte[] value = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                value = jedis.get(key);
            }
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public String set(byte[] key, byte[] value) {
        Jedis jedis = null;
        String returns = "";
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                returns = jedis.set(key, value);
            }
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return returns;
    }

}
