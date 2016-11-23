package com.vipkid.recruitment.entity;

import java.util.Map;

/**
 * Created by zhangzhaojun on 2016/11/15.
 */
public class ContractFile{

    //Teacherpassport
    private String identification;

    //Teacher最高学历 bachelorDiploma
    private String diploma;

    private Map<Integer,String> certification;

    //其他证明  新建一个表
    private Map<Integer,String> degrees;
    //w9税收
    private String tax;
    //Teacher表contract
    private String contract;

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getDiploma() {
        return diploma;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public void setDiploma(String diploma) {
        this.diploma = diploma;
    }

    public Map<Integer, String> getCertification() {
        return certification;
    }

    public void setCertification(Map<Integer, String> certification) {
        this.certification = certification;
    }

    public Map<Integer, String> getDegrees() {
        return degrees;
    }

    public void setDegrees(Map<Integer, String> degrees) {
        this.degrees = degrees;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }



}
