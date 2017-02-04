package com.vipkid.cache.utils;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.cache.CacheConfigConst;

import redis.clients.jedis.exceptions.JedisDataException;

/**
 * 分布式锁服务
 * @author zouqinghua
 * @date 2017年1月13日  下午6:59:23
 *
 */
public class DistributedLock {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLock.class);

    public static final String LOCK_STATUS = "1";
	public static final String ENABLE_LOCK = "1";
    private static String LOCK_VALUE = "lock";

    /**
     * 
     * @param key
     * @param expireTime (seconds)
     * @return
     */
    public static boolean lock(String key, Integer expireTime) {
    	
    	Boolean isLock = false;
    	if (!ENABLE_LOCK.equals(LOCK_STATUS)){
    		return true;
    	}
        if (TextUtils.isEmpty(key) || expireTime != null && expireTime <= 0) {
            logger.error("key:{}, expireTime:{} is invalidate.", key, expireTime);
            return false;
        }
        
        try {
        	Long currTime = System.currentTimeMillis(); //获取当前时间
            String result = RedisClient.getInstance().getSet(key , String.valueOf(currTime));
            
            logger.info("getlock key = {}, result = {}", key, result);
            if(null == result){
                isLock = true;
                if (expireTime != null){ //设置有效期，避免服务失败，造成锁一致存留在redis中
                	RedisClient.getInstance().expire(key, expireTime);
                }
            }else {
                long time = Long.valueOf(result);
                if (currTime - time >= CacheConfigConst.LOCK_TIME*1000){
                    isLock = true;
                }else{
                    int mulTime = CacheConfigConst.LOCK_TIME - Math.round((currTime - time)/1000);
                    int eTime = mulTime >0 ? mulTime : 1 ;
                    RedisClient.getInstance().setex(key , eTime , String.valueOf(time));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to lock the key={}", key, e);
            checkMasterDown(e);
        }
        return isLock;
    }

    public static boolean lock(String key) {
        return lock(key, 300);
    }

    public static boolean unlock(String key) {
    	if (!ENABLE_LOCK.equals(LOCK_STATUS)){
    		return true;
    	}
        if (TextUtils.isEmpty(key)) {
            logger.error("key:{} can not be null", key);
            return false;
        }
        try {
            RedisClient.getInstance().del(key);
            logger.info("release key={}", key);
            return true;
        } catch (Exception e) {
            logger.error("Failed to unlock the key={}", key, e);
            checkMasterDown(e);
        }
        return false;
    }

    private static void checkMasterDown(Exception e) {
        if (e.getMessage() != null && e.getMessage().contains("READONLY")) {
            if (e instanceof JedisDataException) {
                logger.error("master down now");
            } else {
                logger.error("unknow readonly error now");
            }
        }
    }

}
