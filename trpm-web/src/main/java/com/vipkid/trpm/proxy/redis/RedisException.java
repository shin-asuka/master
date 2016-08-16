package com.vipkid.trpm.proxy.redis;

public class RedisException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RedisException() {
		super();
	}

	public RedisException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RedisException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RedisException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RedisException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
