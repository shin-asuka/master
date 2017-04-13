package com.vipkid.trpm.entity.personal;

import com.vipkid.trpm.util.DateUtils;

import java.util.Date;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2017/4/10 下午2:20
 */
public class QueryContractByTeacherIdOutputDto {

    private String startTime;
    private String endTime;
    private String contractNumber;
    private String contractId;
    private String paperContractUrl;

    public QueryContractByTeacherIdOutputDto(APIQueryContractListByTeacherIdResult one) {
        contractId = one.getId();
        startTime = one.getStartTime();//合同系统传过来的即是yyyy-MM-dd格式的;
        endTime = one.getEndTime();//合同系统传过来的即是yyyy-MM-dd格式的 DateUtils.YYYY_MM_DD
        contractNumber = one.getInstanceNumber();
    }

    public QueryContractByTeacherIdOutputDto(Date contractStartDate, Date contractEndDate,
            String contract) {
        startTime = DateUtils.formatDate(contractStartDate,DateUtils.YYYY_MM_DD);
        endTime = DateUtils.formatDate(contractEndDate,DateUtils.YYYY_MM_DD);
        paperContractUrl = contract;//不包含http://resource.vipkid.com.cn
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getPaperContractUrl() {
        return paperContractUrl;
    }

    public void setPaperContractUrl(String paperContractUrl) {
        this.paperContractUrl = paperContractUrl;
    }
}
