package com.vipkid.background.api.sterling.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liyang on 2017/3/11.
 */
public class SterlingCallBack implements Serializable{
    private static final long serialVersionUID = 7540815398033717416L;
    private String type;
    private Payload payload;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public static class Payload implements Serializable{


        private static final long serialVersionUID = 3711829936737900445L;
        private String id;
        private String packageId;
        private String candidateId;
        private String submittedAt;
        private String updatedAt;
        private String status;
        private String result;
        private List<ReportItem> reportItems;
        private List<AdverseAction> adverseActions;
        private Dispute dispute;
        private SterlingScreening.Link links;


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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<ReportItem> getReportItems() {
            return reportItems;
        }

        public void setReportItems(List<ReportItem> reportItems) {
            this.reportItems = reportItems;
        }

        public List<AdverseAction> getAdverseActions() {
            return adverseActions;
        }

        public void setAdverseActions(List<AdverseAction> adverseActions) {
            this.adverseActions = adverseActions;
        }

        public Dispute getDispute() {
            return dispute;
        }

        public void setDispute(Dispute dispute) {
            this.dispute = dispute;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public SterlingScreening.Link getLinks() {
            return links;
        }

        public void setLinks(SterlingScreening.Link links) {
            this.links = links;
        }
    }



    public static class ReportItem implements Serializable{
        private static final long serialVersionUID = 4268586107821851866L;
        private String id;
        private String type;
        private String status;
        private String result;
        private String updatedAt;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }


    public static class AdverseAction implements Serializable{
        private static final long serialVersionUID = 4372420208196773287L;
        private String id;
        private String status;
        private String updatedAt;
        private List<String> reportItemIds;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public List<String> getReportItemIds() {
            return reportItemIds;
        }

        public void setReportItemIds(List<String> reportItemIds) {
            this.reportItemIds = reportItemIds;
        }
    }

    public static class Dispute implements Serializable{
        private static final long serialVersionUID = -1569373881541946308L;
        private String status;
        private String createdAt;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}
