package com.vipkid.email.handle;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Email 发送线程类
 * @author ALong
 *
 */
public class EmailHandleThread implements Callable<String> {
	
	private Logger logger = LoggerFactory.getLogger(EmailHandleThread.class);
	
	private EmailHandle emailHandle;
	
	public EmailHandleThread(EmailHandle emailHandle){
		this.emailHandle = emailHandle;
	}
	
	@Override
	public String call() {
	    try{
	        emailHandle.sendMail();
	    }catch(Exception e){
	        logger.error(e.getMessage(),e);
	    }
	    return "ERROR!"+emailHandle.email;
	}
}
