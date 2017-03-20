/**
 * 微支付
 * com.ninefbank.smallpay.common.util
 * ThreadPool.java
 * 
 * 2015年12月23日-下午7:23:00
 *  2015 9FBank.com 玖富公司-版权所有
 *
 */
package com.vipkid.background.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkThreadPool {
	// 工作线程池
	public static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

	/**
	 * getNamedThread 获取线程并命名 便于在jmap中查看各系统使用线程池的情况<br/>
	 * 
	 */
	public static Thread getNamedThread(Runnable command, String name) {
		Thread thread = new Thread(command);
		// 设置线程name为"Acount."+方法名
		thread.setName(name + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
		return thread;
	}
}
