package com.vipkid.recruitment.entity;

import java.util.Map;

/**
 * Created by zhangzhaojun on 2016/11/15.
 */
public class ContractFile{

    //Teacherpassport
    private Map<Integer,String> identification;

    //Teacher最高学历 bachelorDiploma
    private Map<Integer,String> diploma;

    private Map<Integer,String> certification;

    //其他证明  新建一个表
    private Map<Integer,String> degrees;
    //w9税收
    private String tax;
    //Teacher表contract
    private Map<Integer,String> contract;

    public Map<Integer, String> getIdentification() {
        return identification;
    }

    public void setIdentification(Map<Integer, String> identification) {
        this.identification = identification;
    }

    public Map<Integer, String> getDiploma() {
        return diploma;
    }

    public void setDiploma(Map<Integer, String> diploma) {
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

    public Map<Integer, String> getContract() {
        return contract;
    }

    public void setContract(Map<Integer, String> contract) {
        this.contract = contract;
    }
}
