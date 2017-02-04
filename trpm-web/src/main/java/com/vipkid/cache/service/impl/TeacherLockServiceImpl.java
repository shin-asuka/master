package com.vipkid.cache.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.cache.CacheConfigConst;
import com.vipkid.cache.service.TeacherLockService;
import com.vipkid.cache.utils.DistributedLock;

/**
 * 教师锁服务
 * 
 * @author zouqinghua
 * @date 2017年1月13日  下午6:21:02
 *
 */
@Service
public class TeacherLockServiceImpl implements TeacherLockService {

	private static final Logger logger = LoggerFactory.getLogger(TeacherLockServiceImpl.class);

	@Override
	public Boolean getTaxpayerLock(Long teacherId, Integer type) {
		String key = getTaxpayerKey(teacherId, type);
		
		logger.info("获取锁  getLock key = {}",key);
		Boolean isLock = DistributedLock.lock(key);
		logger.info("锁状态  LockStatus key = {} , isLock = {}",key,isLock);
		return isLock;
	}

	@Override
	public Boolean releaseTaxpayerLock(Long teacherId, Integer type) {
		String key = getTaxpayerKey(teacherId, type);
		logger.info("释放锁 releaseLock key = {}",key);
		Boolean flag =  DistributedLock.unlock(key);
		logger.info("释放锁  releaseLockResult key = {} , flag = {}",key,flag);
		return flag;
	}
	
	public String getTaxpayerKey(Long teacherId, Integer type){
		String key = null;
		if(teacherId !=null && type !=null){
			key = CacheConfigConst.TEACHER_TAXPAYER_LOCK_KEY+"_"+teacherId+"_"+type;
		}
		return key;
	}

}
