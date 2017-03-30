package com.vipkid.background.api.sterling.dto;

import java.io.Serializable;

/**
 * Created by liyang on 2017/3/11.
 */
public class SterlingAccessToken implements Serializable{
    private static final long serialVersionUID = -8284266412381826119L;

    private String error;
    private String message;
    private String moreInfo;

    private String access_token;
    private String token_type;
    private Integer expires_in;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }
}
