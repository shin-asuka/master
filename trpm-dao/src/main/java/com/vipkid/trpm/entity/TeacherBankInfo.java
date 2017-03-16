package com.vipkid.trpm.entity;

import org.community.dao.support.Entity;

import java.io.Serializable;

public class TeacherBankInfo extends Entity implements Serializable {

    private static final long serialVersionUID = -4902625160957594059L;
    /*  */
    private long id;
    /*  */
    private long teacherId;
    /*  */
    private String bankAccountName;
    /*  */
    private String bankAddress;
    /*  */
    private String bankCardNumber;
    /*  */
    private String bankName;
    /*  */
    private String bankSwiftCode;
    /*  */
    private String bankABARoutingNumber;
    /*  */
    private String bankACHNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String getBankCardNumber() {
        return bankCardNumber;
    }

    public void setBankCardNumber(String bankCardNumber) {
        this.bankCardNumber = bankCardNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankSwiftCode() {
        return bankSwiftCode;
    }

    public void setBankSwiftCode(String bankSwiftCode) {
        this.bankSwiftCode = bankSwiftCode;
    }

    public String getBankABARoutingNumber() {
        return bankABARoutingNumber;
    }

    public void setBankABARoutingNumber(String bankABARoutingNumber) {
        this.bankABARoutingNumber = bankABARoutingNumber;
    }

    public String getBankACHNumber() {
        return bankACHNumber;
    }

    public void setBankACHNumber(String bankACHNumber) {
        this.bankACHNumber = bankACHNumber;
    }

}
