package com.vipkid.trpm.entity.personal;

import java.io.Serializable;

import com.vipkid.rest.validation.annotation.Ignore;
import com.vipkid.rest.validation.annotation.NotNull;

@NotNull
public class TeacherBankVO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6274308012663278430L;
    
    private String beneficiaryAccountName;
    private String beneficiaryAccountNumber;
    private String beneficiaryBankName;
    
    private Integer bankCountryId;
    private Integer bankStateId;
    private Integer bankCityId;
    private String bankStreetAddress;
    private String bankZipCode;
    
    
    private Integer beneficiaryCountryId;
    private Integer beneficiaryStateId;
    private Integer beneficiaryCityId;
    private String beneficiaryStreetAddress;
    private String beneficiaryZipCode;

    /**
     * swiftCode and bankABARoutingNumber: 1 in 2
     */
    @Ignore
    private String swiftCode;
    @Ignore
    private String bankABARoutingNumber;

    @Ignore
    private String bankACHNumber;
    
    private Integer idType;
    private String passportURL;
    
    private String idNumber;
    
    private Integer issuanceCountryId;

    public String getBeneficiaryAccountName() {
        return beneficiaryAccountName;
    }

    public void setBeneficiaryAccountName(String beneficiaryAccountName) {
        this.beneficiaryAccountName = beneficiaryAccountName;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public String getBeneficiaryBankName() {
        return beneficiaryBankName;
    }

    public void setBeneficiaryBankName(String beneficiaryBankName) {
        this.beneficiaryBankName = beneficiaryBankName;
    }

    public Integer getBankCountryId() {
        return bankCountryId;
    }

    public void setBankCountryId(Integer bankCountryId) {
        if(bankCountryId == null) bankCountryId = 0;
        this.bankCountryId = bankCountryId;
    }

    public Integer getBankStateId() {
        return bankStateId;
    }

    public void setBankStateId(Integer bankStateId) {
        if(bankStateId == null) bankStateId = 0;
        this.bankStateId = bankStateId;
    }

    public Integer getBankCityId() {
        return bankCityId;
    }

    public void setBankCityId(Integer bankCityId) {
        if(bankCityId == null) bankCityId = 0;
        this.bankCityId = bankCityId;
    }

    public String getBankStreetAddress() {
        return bankStreetAddress;
    }

    public void setBankStreetAddress(String bankStreetAddress) {
        this.bankStreetAddress = bankStreetAddress;
    }

    public String getBankZipCode() {
        return bankZipCode;
    }

    public void setBankZipCode(String bankZipCode) {
        this.bankZipCode = bankZipCode;
    }

    public Integer getBeneficiaryCountryId() {
        return beneficiaryCountryId;
    }

    public void setBeneficiaryCountryId(Integer beneficiaryCountryId) {
        if(beneficiaryCountryId == null) beneficiaryCountryId = 0;
        this.beneficiaryCountryId = beneficiaryCountryId;
    }

    public Integer getBeneficiaryStateId() {
        return beneficiaryStateId;
    }

    public void setBeneficiaryStateId(Integer beneficiaryStateId) {
        if(beneficiaryStateId == null) beneficiaryStateId = 0;
        this.beneficiaryStateId = beneficiaryStateId;
    }

    public Integer getBeneficiaryCityId() {
        return beneficiaryCityId;
    }

    public void setBeneficiaryCityId(Integer beneficiaryCityId) {
        if(beneficiaryCityId == null) beneficiaryCityId = 0;
        this.beneficiaryCityId = beneficiaryCityId;
    }

    public String getBeneficiaryStreetAddress() {
        return beneficiaryStreetAddress;
    }

    public void setBeneficiaryStreetAddress(String beneficiaryStreetAddress) {
        this.beneficiaryStreetAddress = beneficiaryStreetAddress;
    }

    public String getBeneficiaryZipCode() {
        return beneficiaryZipCode;
    }

    public void setBeneficiaryZipCode(String beneficiaryZipCode) {
        this.beneficiaryZipCode = beneficiaryZipCode;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
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

	public Integer getIdType() {
        return idType;
    }

    public void setIdType(Integer idType) {
        if(idType == null) idType = 0;
        this.idType = idType;
    }

    public String getPassportURL() {
        return passportURL;
    }

    public void setPassportURL(String passportURL) {
        this.passportURL = passportURL;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public Integer getIssuanceCountryId() {
        return issuanceCountryId;
    }

    public void setIssuanceCountryId(Integer issuanceCountryId) {
        if(issuanceCountryId == null) issuanceCountryId = 0;
        this.issuanceCountryId = issuanceCountryId;
    }
    
    
}
