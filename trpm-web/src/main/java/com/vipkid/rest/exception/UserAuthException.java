package com.vipkid.rest.exception;

/**
 * 用户身份认证异常
 * 
 * @author zouqinghua
 * @date 2016年7月23日  下午12:02:41
 *
 */
public class UserAuthException extends RuntimeException{

	
	private static final long serialVersionUID = 1L;

    public UserAuthException() {
        super();
    }

    public UserAuthException(String message) {
        super(message);
    }

    public UserAuthException(Throwable cause) {
        super(cause);
    }

    public UserAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
