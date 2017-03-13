package com.vipkid.background.api.sterling.dto;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created by liyang on 2017/3/12.
 */
public class SterlingError implements Serializable{

    private static final long serialVersionUID = -1057972474144370894L;
    private String message;
    private String code;
    private String errorCode;
    private String errorMessage;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        if(StringUtils.isNotBlank(this.code) && StringUtils.contains(this.code,"#")){
            String[] errors= StringUtils.split(this.code,"#");
            this.setErrorCode(errors[0]);
            this.setErrorMessage(errors[1]);
        }
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
