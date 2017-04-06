package com.vipkid.background.api.sterling.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liyang on 2017/3/11.
 * 此类用于接收 Sterling的Screening 相关的接口响应数据
 */
public class SterlingScreening implements Serializable {
    private static final long serialVersionUID = -8753695057275978954L;
    private String id;
    private String packageId;
    private String candidateId;
    private String status;
    private String result;
    private List<SterlingCallBack.ReportItem> reportItems;
    private String submittedAt;
    private String updatedAt;
    private ScreeningInputDto.CallBack callback;
    private List<SterlingCallBack.AdverseAction> adverseActions;
    private Link links;
    private List<SterlingError> errors;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<SterlingCallBack.ReportItem> getReportItems() {
        return reportItems;
    }

    public void setReportItems(List<SterlingCallBack.ReportItem> reportItems) {
        this.reportItems = reportItems;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ScreeningInputDto.CallBack getCallback() {
        return callback;
    }

    public void setCallback(ScreeningInputDto.CallBack callback) {
        this.callback = callback;
    }

    public List<SterlingCallBack.AdverseAction> getAdverseActions() {
        return adverseActions;
    }

    public void setAdverseActions(List<SterlingCallBack.AdverseAction> adverseActions) {
        this.adverseActions = adverseActions;
    }

    public Link getLinks() {
        return links;
    }

    public void setLinks(Link links) {
        this.links = links;
    }

    public List<SterlingError> getErrors() {
        return errors;
    }

    public void setErrors(List<SterlingError> errors) {
        this.errors = errors;
    }

    public static class Link implements Serializable{
        private static final long serialVersionUID = -300761723926346679L;
        private Admin admin;

        public Admin getAdmin() {
            return admin;
        }

        public void setAdmin(Admin admin) {
            this.admin = admin;
        }
    }


    public static class Admin implements Serializable{
        private static final long serialVersionUID = 7540530175501712073L;
        private String web;
        private String pdf;

        public String getWeb() {
            return web;
        }

        public void setWeb(String web) {
            this.web = web;
        }

        public String getPdf() {
            return pdf;
        }

        public void setPdf(String pdf) {
            this.pdf = pdf;
        }
    }
}
