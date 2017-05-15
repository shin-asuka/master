package com.vipkid.trpm.entity.personal;

import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.util.DateUtils;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        startTime = DateUtils.formatDate(DateUtils.parseDate(one.getStartTime(),DateUtils.YYYY_MM_DD) ,DateUtils.MMMM_DD_YYYY,Locale.ENGLISH);//合同系统传过来的即是yyyy-MM-dd格式的;
        endTime = DateUtils.formatDate(DateUtils.parseDate(one.getEndTime(),DateUtils.YYYY_MM_DD),DateUtils.MMMM_DD_YYYY,Locale.ENGLISH);//合同系统传过来的即是yyyy-MM-dd格式的 DateUtils.YYYY_MM_DD
        contractNumber = one.getInstanceNumber();
    }

    public QueryContractByTeacherIdOutputDto(Date contractStartDate, Date contractEndDate,
            String contract) {
        startTime = DateUtils.formatDate(contractStartDate,DateUtils.MMMM_DD_YYYY,Locale.ENGLISH);
        endTime = DateUtils.formatDate(contractEndDate,DateUtils.MMMM_DD_YYYY,Locale.ENGLISH);
        if(StringUtils.isNotBlank(contract) && !contract.startsWith(ApplicationConstant.HTTP)){
            paperContractUrl =ApplicationConstant.ContractConstants.PDF_URL+ contract;//不包含http://resource.vipkid.com.cn
        }else {
            paperContractUrl = contract;//不包含http://resource.vipkid.com.cn
        }
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
