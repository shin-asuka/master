package com.vipkid.recruitment.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangzhaojun on 2016/11/15.
 */
public class ContractFile{

    //Teacherpassport  文件类型1-other_degrees  2-certificationFiles   3-Identification  4-Diploma 5-Contract  6-Passport   7-Driver's license
    private  TeacherOtherDegrees identification;

    //Teacher最高学历 bachelorDiploma
    private TeacherOtherDegrees diploma;

    private List<TeacherOtherDegrees> certification;

    //其他证明  新建一个表
    private List<TeacherOtherDegrees> degrees;
    //w9税收
    private  TeacherOtherDegrees tax;
    //Teacher表contract
    private TeacherOtherDegrees contract;

    //返回结果
    private String result;

    public TeacherOtherDegrees getIdentification() {
        return identification;
    }

    public void setIdentification(TeacherOtherDegrees identification) {
        this.identification = identification;
    }

    public List<TeacherOtherDegrees> getCertification() {
        return certification;
    }

    public void setCertification(List<TeacherOtherDegrees> certification) {
        this.certification = certification;
    }

    public List<TeacherOtherDegrees> getDegrees() {
        return degrees;
    }

    public void setDegrees(List<TeacherOtherDegrees> degrees) {
        this.degrees = degrees;
    }

    public TeacherOtherDegrees getTax() {
        return tax;
    }

    public void setTax(TeacherOtherDegrees tax) {
        this.tax = tax;
    }

    public TeacherOtherDegrees getDiploma() {
        return diploma;
    }

    public void setDiploma(TeacherOtherDegrees diploma) {
        this.diploma = diploma;
    }

    public TeacherOtherDegrees getContract() {
        return contract;
    }

    public void setContract(TeacherOtherDegrees contract) {
        this.contract = contract;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
