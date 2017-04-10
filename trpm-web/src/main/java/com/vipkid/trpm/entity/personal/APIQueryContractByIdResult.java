package com.vipkid.trpm.entity.personal;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2017/4/10 下午3:09
 */
public class APIQueryContractByIdResult {

    private String id;

    private String templateId;

    private String signerId;

    private String signerName;

    private Long signTime;

    private Long startTime;

    private Long endTime;

    private Long enableSignStartTime;

    private Long enableSignEndTime;

    private String channel;

    private String templateType;//合同类型即范本类型

    private String instanceStatus;

    private String instanceNumber;

    private String instanceContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getSignerId() {
        return signerId;
    }

    public void setSignerId(String signerId) {
        this.signerId = signerId;
    }

    public String getSignerName() {
        return signerName;
    }

    public void setSignerName(String signerName) {
        this.signerName = signerName;
    }

    public Long getSignTime() {
        return signTime;
    }

    public void setSignTime(Long signTime) {
        this.signTime = signTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getEnableSignStartTime() {
        return enableSignStartTime;
    }

    public void setEnableSignStartTime(Long enableSignStartTime) {
        this.enableSignStartTime = enableSignStartTime;
    }

    public Long getEnableSignEndTime() {
        return enableSignEndTime;
    }

    public void setEnableSignEndTime(Long enableSignEndTime) {
        this.enableSignEndTime = enableSignEndTime;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getInstanceStatus() {
        return instanceStatus;
    }

    public void setInstanceStatus(String instanceStatus) {
        this.instanceStatus = instanceStatus;
    }

    public String getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(String instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    public String getInstanceContent() {
        return instanceContent;
    }

    public void setInstanceContent(String instanceContent) {
        this.instanceContent = instanceContent;
    }
}
