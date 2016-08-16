/**
 * 
 */
package com.vipkid.http.vo;

import java.io.Serializable;

/**
 * @author zouqinghua
 * @date 2016年3月25日 上午10:46:09
 *
 */
public class HttpResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * HTTP 请求成功
     */
    public final static Integer STATUS_SUCCESS = 200;

    /**
     * HTTP 网络请求失败
     */
    public final static Integer STATUS_NETWORK_FAIL = 0;

    /**
     * HTTP 网络请求客户端异常
     */
    public final static Integer STATUS_NETWORK_ERROR = 1;

    private Integer status;
    private String message;
    private Object response;
    private Exception e;

    public HttpResult() {

    }

    public HttpResult(Integer status, String message, Object response) {
        this.status = status;
        this.response = response;
        this.message = message;
    }

    public void setException(Exception e) {
        this.e = e;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }

}
