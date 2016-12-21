package com.vipkid.recruitment.entity;

import java.util.List;


/**
 * Created by zhangzhaojun on 2016/11/15.
 */
public class ContractFile{

    //Teacherpassport  文件类型1-other_degrees  2-certificationFiles   3-Identification  4-Diploma 5-Contract  6-Passport   7-Driver's license
    private TeacherContractFile identification;

    //Teacher最高学历 bachelorDiploma
    private TeacherContractFile diploma;

    private List<TeacherContractFile> certification;

    //其他证明  新建一个表
    private List<TeacherContractFile> degrees;
    //w9税收
    private TeacherContractFile tax;
    //Teacher表contract
    private TeacherContractFile contract;


    public TeacherContractFile getIdentification() {
        return identification;
    }

    public void setIdentification(TeacherContractFile identification) {
        this.identification = identification;
    }

    public List<TeacherContractFile> getCertification() {
        return certification;
    }

    public void setCertification(List<TeacherContractFile> certification) {
        this.certification = certification;
    }

    public List<TeacherContractFile> getDegrees() {
        return degrees;
    }

    public void setDegrees(List<TeacherContractFile> degrees) {
        this.degrees = degrees;
    }

    public TeacherContractFile getTax() {
        return tax;
    }

    public void setTax(TeacherContractFile tax) {
        this.tax = tax;
    }

    public TeacherContractFile getDiploma() {
        return diploma;
    }

    public void setDiploma(TeacherContractFile diploma) {
        this.diploma = diploma;
    }

    public TeacherContractFile getContract() {
        return contract;
    }

    public void setContract(TeacherContractFile contract) {
        this.contract = contract;
    }

}
