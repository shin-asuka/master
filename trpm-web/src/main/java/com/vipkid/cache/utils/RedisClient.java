package com.vipkid.cache.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.rest.utils.SpringContextHolder;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.proxy.redis.RedisException;

import redis.clients.jedis.JedisPool;

/**
 * redis 客户端
 * @author zouqinghua
 * @date 2017年1月13日  下午6:28:50
 *
 */
public class RedisClient {

	private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);
	private static RedisProxy redisProxy;
	
    public static RedisProxy getInstance() {
    	if(redisProxy == null){
    		redisProxy = SpringContextHolder.getBean(RedisProxy.class); 
    	}
        return redisProxy;
    }

}
