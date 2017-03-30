
package com.vipkid.rest.exception;

/**
 * Service层公用的Exception, 从由Spring管理事务的函数中抛出时会触发事务回滚.
 * 
 */
public class ServiceException extends RuntimeException {

    private String errCode;
    private static final long serialVersionUID = 1L;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String errCode, String message) {
        this(errCode, message, null);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String errCode, String message, Throwable cause) {
        super(message, cause);
        this.errCode = errCode;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
