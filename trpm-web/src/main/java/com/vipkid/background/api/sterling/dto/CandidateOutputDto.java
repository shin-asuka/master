package com.vipkid.background.api.sterling.dto;

import com.vipkid.background.enums.TeacherPortalCodeEnum;

/**
 * Created by liyang on 2017/3/15.
 */
public class CandidateOutputDto {

    private Long id;
    private Integer errorCode;
    private String errorMessage;
    private TeacherPortalCodeEnum resCode;

    public CandidateOutputDto(Integer errorCode,String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.resCode = TeacherPortalCodeEnum.SYS_FAIL;
    }

    public CandidateOutputDto(Long id){
        this.id = id;
        this.resCode = TeacherPortalCodeEnum.RES_SUCCESS;
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
