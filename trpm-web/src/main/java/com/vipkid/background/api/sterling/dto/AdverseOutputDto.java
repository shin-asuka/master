package com.vipkid.background.api.sterling.dto;

/**
 * Created by liyang on 2017/3/16.
 */
public class AdverseOutputDto {
    private Long id;
    private Integer errorCode;
    private String errorMessage;

    public AdverseOutputDto(Long id,Integer errorCode,String errorMessage){
        this.id = id;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
