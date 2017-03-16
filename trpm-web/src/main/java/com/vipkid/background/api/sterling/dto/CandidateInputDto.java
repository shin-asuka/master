package com.vipkid.background.api.sterling.dto;

import java.util.UUID;

/**
 * Created by liyang on 2017/3/11.
 * 此类用于接收对VIPKID业务线的入参
 */
public class CandidateInputDto implements  java.io.Serializable{

    private static final long serialVersionUID = -7994566362552027623L;

    private Long teacherId;
    private String clientReferenceId = UUID.randomUUID().toString();
    private String candidateId;
    private String email;
    private String givenName;
    private String familyName;
    private boolean confirmedNoMiddleName;
    private String dob;
    private String ssn;
    private String phone;
    private Address address;
    private short retry =0;


    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getClientReferenceId() {
        return clientReferenceId;
    }

    public void setClientReferenceId(String clientReferenceId) {
        this.clientReferenceId = clientReferenceId;
    }

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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public short getRetry() {
        return retry;
    }

    public void setRetry(short retry) {
        this.retry = retry;
    }

    public static class Address implements  java.io.Serializable{

        private static final long serialVersionUID = 6245748039975109L;
        private String addressLine;
        private String municipality;
        private String regionCode;
        private String postalCode;
        private String countryCode ="US";

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




    public static class DriversLicense implements  java.io.Serializable{

        private static final long serialVersionUID = -5117238406635167485L;
        private String type;
        private String licenseNumber;
        private String issuingAgency;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLicenseNumber() {
            return licenseNumber;
        }

        public void setLicenseNumber(String licenseNumber) {
            this.licenseNumber = licenseNumber;
        }

        public String getIssuingAgency() {
            return issuingAgency;
        }

        public void setIssuingAgency(String issuingAgency) {
            this.issuingAgency = issuingAgency;
        }
    }

}
