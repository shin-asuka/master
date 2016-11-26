package com.vipkid.recruitment.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangzhaojun on 2016/11/15.
 */
public class ContractFile{

    //Teacherpassport  文件类型1-other_degrees  2-certificationFiles   3-Identification  4-Diploma 5-Contract  6-Passport   7-Driver's license
    private  Map<String,TeacherOtherDegrees> identification;

    //Teacher最高学历 bachelorDiploma
    private List<TeacherOtherDegrees> diploma;

    private List<TeacherOtherDegrees> certification;

    //其他证明  新建一个表
    private List<TeacherOtherDegrees> degrees;
    //w9税收
    private String tax;
    //Teacher表contract
    private List<TeacherOtherDegrees> contract;

    private String result;

    public Map<String, TeacherOtherDegrees> getIdentification() {
        return identification;
    }

    public void setIdentification(Map<String, TeacherOtherDegrees> identification) {
        this.identification = identification;
    }

    public List<TeacherOtherDegrees> getDiploma() {
        return diploma;
    }

    public void setDiploma(List<TeacherOtherDegrees> diploma) {
        this.diploma = diploma;
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

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public List<TeacherOtherDegrees> getContract() {
        return contract;
    }

    public void setContract(List<TeacherOtherDegrees> contract) {
        this.contract = contract;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
