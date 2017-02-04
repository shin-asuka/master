package com.vipkid.cache.service;

/**
 * 教师锁服务
 * 
 * @author zouqinghua
 * @date 2017年1月13日  下午6:18:41
 *
 */
public interface TeacherLockService {

	public Boolean getTaxpayerLock(Long teacherId,Integer formType);
	public Boolean releaseTaxpayerLock(Long teacherId,Integer formType); 
	
}
