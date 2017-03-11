package com.vipkid.background.api.sterling.dto;

import java.io.Serializable;

/**
 * Created by liyang on 2017/3/11.
 * 此类用于接收Sterling 接口返回候选人信息
 */
public class SterlingCandidate implements Serializable {

    private static final long serialVersionUID = 8448794210314339734L;
    public  String candidateId;
    private String email;
    private String givenName;
    private String familyName;
    private boolean confirmedNoMiddleName;
    private String dob;
    private String ssn;
    private String phone;
    private CandidateInputDto.Address address;


    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public boolean isConfirmedNoMiddleName() {
        return confirmedNoMiddleName;
    }

    public void setConfirmedNoMiddleName(boolean confirmedNoMiddleName) {
        this.confirmedNoMiddleName = confirmedNoMiddleName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CandidateInputDto.Address getAddress() {
        return address;
    }

    public void setAddress(CandidateInputDto.Address address) {
        this.address = address;
    }

    public static class Address implements  java.io.Serializable{

        private static final long serialVersionUID = -7648552882770232691L;
        private String addressLine;
        private String municipality;
        private String regionCode;
        private String postalCode;
        private String countryCode;

        public String getAddressLine() {
            return addressLine;
        }

        public void setAddressLine(String addressLine) {
            this.addressLine = addressLine;
        }

        public String getMunicipality() {
            return municipality;
        }

        public void setMunicipality(String municipality) {
            this.municipality = municipality;
        }

        public String getRegionCode() {
            return regionCode;
        }

        public void setRegionCode(String regionCode) {
            this.regionCode = regionCode;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }
    }
}
