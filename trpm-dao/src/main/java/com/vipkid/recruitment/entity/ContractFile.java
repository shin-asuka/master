package com.vipkid.recruitment.entity;

import java.util.List;

/**
 * Created by zhangzhaojun on 2016/11/15.
 */
public class ContractFile{

    //Teacherpassport
    private String identification;

    //Teacher最高学历 highestLevelOfEdu
    private String diploma;

    //Teacher教师证明  certificates
    private List<String> certification;

    //其他证明  新建一个表
    private List<String> degrees;
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

    public List<String> getCertification() {
        return certification;
    }

    public void setCertification(List<String> certification) {
        this.certification = certification;
    }

    public List<String> getDegrees() {
        return degrees;
    }

    public void setDegrees(List<String> degrees) {
        this.degrees = degrees;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }



}
